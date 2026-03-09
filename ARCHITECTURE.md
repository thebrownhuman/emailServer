# 📧 Email Service — Architecture

## System Overview

```mermaid
graph LR
    A["Client (Postman / AtlasID / ShopVerse)"] -->|HTTPS| B["Cloudflare Tunnel"]
    B -->|port 8085| C["Spring Boot Container"]
    C -->|SMTP| D["Gmail API"]
    D -->|Email| E["User Inbox"]
```

---

## Request Flow — What happens when you call `/send`

```mermaid
sequenceDiagram
    participant Client
    participant Filter as ApiKeyAuthFilter
    participant Controller as EmailController
    participant Sender as EmailSenderService
    participant RateLimit as RateLimitConfig
    participant Template as TemplateService
    participant Thymeleaf as Thymeleaf Engine
    participant Mail as JavaMailSender
    participant Gmail as Gmail SMTP

    Client->>Filter: POST /api/v1/email/send + X-API-Key
    
    Note over Filter: Step 1 — Auth Check
    Filter->>Filter: Look up API key in CallerProperties
    alt Invalid/Missing Key
        Filter-->>Client: 401 INVALID_API_KEY
    end
    
    Filter->>Controller: Request passes through
    
    Note over Controller: Step 2 — Validation
    Controller->>Controller: Jakarta @Valid checks (email format, required fields)
    alt Validation fails
        Controller-->>Client: 400 VALIDATION_ERROR
    end
    
    Controller->>Sender: sendEmail(request)
    
    Note over Sender: Step 3 — Rate Limit
    Sender->>RateLimit: tryConsume(callerApp)
    alt Rate limited
        Sender-->>Client: 429 RATE_LIMIT_EXCEEDED
    end
    
    Note over Sender: Step 4 — Render Template
    Sender->>Template: renderTemplate(type, data, displayName)
    Template->>Template: Validate required fields present
    Template->>Thymeleaf: process("otp.html", context)
    Thymeleaf-->>Sender: "<html>...rendered email...</html>"
    
    Note over Sender: Step 5 — Resolve Subject
    alt Subject provided
        Sender->>Sender: Use provided subject
    else No subject
        Sender->>Template: generateSubject(type, data, displayName)
    end
    
    Note over Sender: Step 6 — Send or Log
    alt Dev Mode
        Sender->>Sender: Log to console (no email sent)
    else Prod Mode
        Sender->>Mail: doSendWithRetry(to, displayName, subject, html)
        Mail->>Gmail: SMTP TLS on port 587
        Note over Mail,Gmail: Retries 3x with exponential backoff on failure
    end
    
    Sender-->>Client: {"success": true, "messageId": "uuid"}
```

---

## Component Map

```mermaid
graph TB
    subgraph "🌐 Incoming Request"
        REQ["HTTP Request"]
    end

    subgraph "🔐 Security Layer"
        AKF["ApiKeyAuthFilter"]
        CP["CallerProperties"]
    end

    subgraph "🎮 Controller Layer"
        EC["EmailController"]
    end

    subgraph "⚙️ Service Layer"
        ESS["EmailSenderService"]
        TS["TemplateService"]
    end

    subgraph "🛡️ Protection"
        RLC["RateLimitConfig"]
    end

    subgraph "📧 Email Infrastructure"
        JMS["JavaMailSender"]
        MC["MailConfig"]
        THY["Thymeleaf Engine"]
    end

    subgraph "📄 Templates (11 total)"
        T1["otp.html"]
        T2["welcome.html"]
        T3["login-alert.html"]
        T4["...8 more"]
    end

    subgraph "🚨 Error Handling"
        GEH["GlobalExceptionHandler"]
    end

    REQ --> AKF
    AKF --> CP
    AKF --> EC
    EC --> ESS
    ESS --> RLC
    ESS --> TS
    TS --> THY
    THY --> T1 & T2 & T3 & T4
    ESS --> JMS
    JMS --> MC
    EC -.->|on error| GEH
```

---

## File-by-File Breakdown

### Security Layer

| File | Purpose |
|------|---------|
| [ApiKeyAuthFilter](file:///c:/Users/Shivansh/Desktop/PROzz/emailService/src/main/java/com/shivansh/emailservice/filter/ApiKeyAuthFilter.java) | Intercepts every request, checks `X-API-Key` header against registered callers. Skips check in dev mode. |
| [CallerProperties](file:///c:/Users/Shivansh/Desktop/PROzz/emailService/src/main/java/com/shivansh/emailservice/config/CallerProperties.java) | Loads caller app registrations from `application.yml` (name, API key, display name, rate limit). |
| [SecurityConfig](file:///c:/Users/Shivansh/Desktop/PROzz/emailService/src/main/java/com/shivansh/emailservice/config/SecurityConfig.java) | Registers the `ApiKeyAuthFilter` into the filter chain, excludes `/health` from auth. |

### Controller Layer

| File | Purpose |
|------|---------|
| [EmailController](file:///c:/Users/Shivansh/Desktop/PROzz/emailService/src/main/java/com/shivansh/emailservice/controller/EmailController.java) | 4 endpoints: `POST /send`, `POST /send-batch`, `GET /types`, `GET /health`. Delegates to `EmailSenderService`. |

### Service Layer

| File | Purpose |
|------|---------|
| [EmailSenderService](file:///c:/Users/Shivansh/Desktop/PROzz/emailService/src/main/java/com/shivansh/emailservice/service/EmailSenderService.java) | Orchestrates the full flow: rate limit → render template → resolve subject → send/log. Contains `@Retryable` for SMTP retries. |
| [TemplateService](file:///c:/Users/Shivansh/Desktop/PROzz/emailService/src/main/java/com/shivansh/emailservice/service/TemplateService.java) | Validates required fields, renders Thymeleaf HTML templates, auto-generates subjects. |

### Config

| File | Purpose |
|------|---------|
| [MailConfig](file:///c:/Users/Shivansh/Desktop/PROzz/emailService/src/main/java/com/shivansh/emailservice/config/MailConfig.java) | Configures `JavaMailSender` bean for Gmail SMTP (TLS, port 587, auth). |
| [RateLimitConfig](file:///c:/Users/Shivansh/Desktop/PROzz/emailService/src/main/java/com/shivansh/emailservice/config/RateLimitConfig.java) | In-memory Bucket4j rate limiter. Per-caller limits configurable in YAML. |
| [SwaggerConfig](file:///c:/Users/Shivansh/Desktop/PROzz/emailService/src/main/java/com/shivansh/emailservice/config/SwaggerConfig.java) | OpenAPI docs with API key security scheme. Disabled in prod. |

### Models

| File | Purpose |
|------|---------|
| [EmailType](file:///c:/Users/Shivansh/Desktop/PROzz/emailService/src/main/java/com/shivansh/emailservice/model/EmailType.java) | Enum of 11 types, each defining template name + required/optional fields. |
| [EmailRequest](file:///c:/Users/Shivansh/Desktop/PROzz/emailService/src/main/java/com/shivansh/emailservice/model/EmailRequest.java) | DTO: `to`, `type`, `callerApp`, `subject`, `templateData`. |
| [EmailResponse](file:///c:/Users/Shivansh/Desktop/PROzz/emailService/src/main/java/com/shivansh/emailservice/model/EmailResponse.java) | DTO: `success`, `messageId`, `timestamp`, `error`. |

### Error Handling

| File | Purpose |
|------|---------|
| [GlobalExceptionHandler](file:///c:/Users/Shivansh/Desktop/PROzz/emailService/src/main/java/com/shivansh/emailservice/exception/GlobalExceptionHandler.java) | Catches all exceptions and returns clean `EmailResponse` — never leaks stack traces. |

---

## Infrastructure

```mermaid
graph LR
    subgraph "Your PC"
        CODE["Source Code"] -->|git push| GH["GitHub"]
    end

    subgraph "GitHub"
        GH -->|triggers| GA["GitHub Actions"]
        GA -->|builds + pushes| GHCR["ghcr.io/thebrownhuman/emailserver:latest"]
    end

    subgraph "NUC Homelab"
        WT["Watchtower"] -->|auto-pulls| GHCR
        WT -->|restarts| DOCK["Docker: email-service :8085"]
        CF["cloudflared"] -->|routes| DOCK
    end

    subgraph "Internet"
        USER["Any Client"] -->|HTTPS| CFL["Cloudflare CDN"]
        CFL -->|tunnel| CF
    end
