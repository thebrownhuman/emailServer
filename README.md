# 📧 Email Service

A production-ready, reusable email microservice built with **Java 21** and **Spring Boot 3.4.3**. Sends beautifully templated HTML emails via Gmail SMTP with API key authentication, rate limiting, and automatic retries.

Built as a shared service for the **AtlasID** ecosystem — used by AtlasID (identity), ShopVerse (e-commerce), IndianExpress (banking), and any future projects.

---

## ✨ Features

- **11 Email Templates** — OTP, Welcome, Login Alert, Password Changed, Identity Link, Transaction Receipt, Order Confirmation, Shipping Update, Account Statement, Link Approved, Generic
- **API Key Authentication** — Each caller app gets its own key
- **Rate Limiting** — Per-caller + global limits using Bucket4j
- **Automatic Retries** — 3 retries with exponential backoff on SMTP failures
- **Batch Sending** — Send up to 50 emails in one request
- **Dev Mode** — Logs emails to console instead of sending (for local development)
- **Swagger UI** — Interactive API docs at `/swagger-ui.html`
- **Docker Ready** — Multi-stage Dockerfile + CI/CD via GitHub Actions
- **Dark-Themed Templates** — Premium HTML emails with inline CSS

---

## 🚀 Quick Start

### Prerequisites
- Java 21 (LTS)
- Gmail account with [App Password](https://myaccount.google.com/apppasswords) enabled

### Run Locally
```bash
# Set environment variables
export GMAIL_USERNAME=your-email@gmail.com
export GMAIL_APP_PASSWORD=your-app-password

# Start the service
./mvnw spring-boot:run

# Or with dev profile (no real emails sent)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Run with Docker
```bash
# Create .env file (see .env.example)
cp .env.example .env
# Edit .env with your real credentials

# Pull and run
docker compose pull
docker compose up -d
```

Service starts on **port 8085**.

---

## 📡 API Endpoints

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| `GET` | `/api/v1/email/health` | ❌ | Health check |
| `GET` | `/api/v1/email/types` | ✅ | List all email types |
| `POST` | `/api/v1/email/send` | ✅ | Send single email |
| `POST` | `/api/v1/email/send-batch` | ✅ | Send batch emails (up to 50) |

### Auth Header
```
X-API-Key: your-api-key
```

### Example: Send OTP Email
```bash
curl -X POST http://localhost:8085/api/v1/email/send \
  -H "Content-Type: application/json" \
  -H "X-API-Key: ems_atlasid_dev_key" \
  -d '{
    "to": "user@example.com",
    "type": "OTP",
    "callerApp": "atlasid",
    "templateData": {
      "otpCode": "482917",
      "userName": "Shivansh",
      "expiryMinutes": 3
    }
  }'
```

---

## 📋 Supported Email Types

| Type | Required Fields | Optional Fields |
|------|----------------|-----------------|
| `OTP` | otpCode, userName, expiryMinutes | appName |
| `WELCOME` | userName | appName, dashboardUrl |
| `LOGIN_ALERT` | userName, device, location, time | ipAddress |
| `PASSWORD_CHANGED` | userName, time | appName |
| `LINK_APPROVAL` | userName, serviceName, scopes | approveUrl, expiryMinutes |
| `LINK_APPROVED` | userName, serviceName | — |
| `TRANSACTION_RECEIPT` | userName, amount, merchant, date | cardLast4, referenceId |
| `ACCOUNT_STATEMENT` | userName, month, openingBalance, closingBalance | statementUrl |
| `ORDER_CONFIRMATION` | userName, orderId, items, total | deliveryDate, trackingUrl |
| `SHIPPING_UPDATE` | userName, orderId, status | trackingUrl, deliveryDate |
| `GENERIC` | userName, body | htmlBody |

---

## 🏗️ Architecture

```
src/main/java/com/shivansh/emailservice/
├── config/          # CallerProperties, MailConfig, RateLimitConfig, SwaggerConfig
├── controller/      # EmailController (REST endpoints)
├── exception/       # Custom exceptions + GlobalExceptionHandler
├── filter/          # ApiKeyAuthFilter
├── model/           # DTOs (EmailRequest, EmailResponse, EmailType, etc.)
└── service/         # TemplateService, EmailSenderService
```

---

## 🧪 Tests

```bash
./mvnw test
```

- **TemplateServiceTest** — Template rendering, field validation, subject generation
- **EmailSenderServiceTest** — Dev/prod mode, rate limiting, batch processing
- **EmailControllerTest** — MockMvc integration tests for all endpoints

---

## 🐳 CI/CD

Every push to `main` triggers a GitHub Actions workflow that:
1. Builds the Docker image
2. Pushes to `ghcr.io/thebrownhuman/emailserver:latest`

Pull the latest image on your server:
```bash
docker compose pull && docker compose up -d
```

---

## 📄 License

MIT

---

Built with ❤️ by [@thebrownhuman](https://github.com/thebrownhuman)
