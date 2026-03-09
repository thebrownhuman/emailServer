# AtlasID — Cross-Session Memory Document

> **Purpose:** This document captures every decision, requirement, design choice, and architectural context for the AtlasID project. It is designed to be read by **any session** (including sessions working on the emailService or AtlasID backend) to understand the full project state, avoid contradictory decisions, and continue work seamlessly.
>
> **Last updated:** 2026-03-09
> **Workspace:** `c:\Users\Shivansh\Desktop\PROzz\AtlasID`

---

## 1. What Is AtlasID?

AtlasID is a **global digital identity platform** — the identity backbone of the Shivansh Projects ecosystem. Users complete KYC onboarding once, receive a cryptographically-signed digital ID card, and then use their single AtlasID to authenticate across 200+ linked services (banks, government portals, platforms).

**Elevator pitch:** "One world. One identity."

**Key value propositions:**
- Military-grade AES-256 encryption
- Zero-knowledge architecture (raw identity data is never stored)
- Bank-grade SOC 2 Type II compliance
- Accepted by 500+ institutions across 150+ countries
- One-tap authentication for linked services

---

## 2. Ecosystem Overview

AtlasID is **not a standalone project** — it's part of a larger ecosystem of learning projects, all under the `PROzz/` directory:

```
PROzz/
├── AtlasID/          ← This project (frontend + future backend)
├── emailService/     ← Shared email microservice (separate workspace)
├── (future) Indian Express/  ← Banking app
└── (future) Shopping App/    ← Amazon-like marketplace
```

### Ecosystem Relationships

| Project | Description | Depends On |
|---|---|---|
| **AtlasID** | Global digital identity platform. Provides identity verification APIs for all other apps. | Email Service |
| **Email Service** | Shared email microservice. All apps send emails through it. One Gmail account, multiple sender display names. | Gmail SMTP |
| **Indian Express** | Banking app (Axis Bank-like features, American Express premium UI). Credit cards, accounts, transfers. | AtlasID (identity verification), Email Service |
| **Shopping App** | Amazon-like marketplace. Users spend Indian Express money here. | AtlasID (identity verification), Email Service, Indian Express (payments) |

### Inter-Service Communication

```
AtlasID Backend ──HTTP──> Email Service (port 8085)
Indian Express  ──HTTP──> Email Service (port 8085)
Indian Express  ──HTTP──> AtlasID Backend (Identity Linking API)
Shopping App    ──HTTP──> Email Service (port 8085)
Shopping App    ──HTTP──> AtlasID Backend (Identity Linking API)
```

All services communicate via a **shared Docker network** (`shivansh-network`).

---

## 3. Current State of the Project

### Frontend — COMPLETE ✅

The frontend is a fully-built, production-grade React SPA. **Zero external UI libraries** — every component is handcrafted.

**Status:**
- ✅ Landing page (Hero, Features, How It Works, Trust, Footer)
- ✅ Login screen (two-column split layout)
- ✅ OTP verification (6-digit with auto-advance, paste, resend timer)
- ✅ 5-step KYC signup flow (Personal Info → Address → Security → 2FA → Review)
- ✅ Success screen (animated holographic ID card reveal with countdown)
- ✅ Full dashboard (6 sub-pages: Overview, My Identity, Activity, Linked Services, Security, Settings)
- ✅ All forms with validation, error states, dark-themed autofill fix
- ✅ Custom date picker (3-view: day/month/year grid, portal-rendered)

**What remains for the frontend:**
- [ ] PricingSection (3-tier card grid — Free / Pro / Enterprise)
- [ ] Wire up to real backend APIs (currently all data is mocked in `src/data/mockData.ts`)
- [ ] Dashboard notification badge for pending link requests (Flow A)
- [ ] Persistent pop-up on Linked Services page for approval-based linking
- [ ] OAuth redirect consent page for Flow B (when user is redirected from external service)

### Backend — NOT STARTED ❌

The backend system design is **fully finalized** (see Section 7 below). No code has been written yet.

### Email Service — DESIGN COMPLETE, NOT STARTED ❌

The email service design is **fully finalized** (see Section 8 below). The folder `PROzz/emailService/` exists but only contains a skeleton `emailServiceDesign.md`. The full design document is in the Antigravity brain for conversation `cd937268`.

---

## 4. Tech Stack

### Frontend

| Category | Tool | Version |
|---|---|---|
| Framework | React | ^19.2.0 |
| Language | TypeScript | ~5.9.3 |
| Build Tool | Vite | ^7.3.1 |
| Compiler | SWC (via @vitejs/plugin-react) | ^1.15.17 |
| Styling | Tailwind CSS | ^4.2.1 |
| CSS Processing | PostCSS + @tailwindcss/postcss | ^4.2.1 |
| Form Plugin | @tailwindcss/forms | ^0.5.11 |
| Icons | Material Symbols (Google Fonts, CDN) | — |
| Fonts | Manrope (Google Fonts, CDN) | — |
| Linting | ESLint + typescript-eslint | ^9.39.1 |

> **No external UI component libraries.** No Headless UI, Radix, Chakra, etc.

### Backend (Planned)

| Layer | Technology | Version | Why |
|---|---|---|---|
| Language | Java | 21 (LTS) | Latest LTS, virtual threads, records |
| Framework | Spring Boot | 3.4.x | Industry standard, massive ecosystem |
| Build | Maven | 3.9+ | Standard for enterprise Java |
| Database | PostgreSQL | 16 | Rock-solid, open source |
| ORM | Spring Data JPA + Hibernate | 6.x | Reduces boilerplate |
| Cache/Session | Redis | 7.x | OTP storage, rate limiting, sessions |
| Auth | Spring Security + JJWT | 0.12.x | JWT-based stateless auth |
| Email | Shared Email Service | — | Separate microservice, HTTP calls |
| TOTP | `dev.samstevens.totp` | 1.7.x | Real Google Authenticator support |
| API Docs | SpringDoc OpenAPI (Swagger) | 2.x | Auto-generated interactive docs |
| Rate Limiting | Bucket4j + Redis | — | Token-bucket per IP/user |
| Containerization | Docker + Docker Compose | — | Single-command deployment |
| Reverse Proxy | Nginx | — | SSL termination, static assets |
| Migration | Flyway | 10.x | Versioned schema migrations |

### Email Service (Planned)

| Layer | Technology |
|---|---|
| Language/Framework | Java 21 + Spring Boot 3.x |
| Template Engine | Thymeleaf (HTML email templates) |
| SMTP | Gmail SMTP (smtp.gmail.com:587) |
| Auth | API key per caller app |
| Rate Limiting | Bucket4j (per-caller limits) |
| Retry | Spring Retry (3 retries: 2s, 5s, 15s) |
| Containerization | Docker |

---

## 5. Design System Summary

**Theme:** Deep Space Fintech — "Authoritative, Luminous, Secure, and Global"

### Color Palette

| Role | Token | Hex |
|---|---|---|
| Canvas | `void-black` | `#060810` |
| App Background | `deep-navy` | `#0d1117` |
| Card Surface | `graphite-navy` | `#161b26` |
| Modal Surface | `slate-navy` | `#1e2535` |
| Brand Primary | `atlas-blue` | `#2e77ff` |
| Brand Hover | `atlas-blue-dim` | `#1a5fe0` |
| Success | `status-success` | `#22c55e` |
| Warning | `status-warning` | `#f59e0b` |
| Error | `status-error` | `#ef4444` |

### Typography

- **Font:** Manrope (Google Fonts) — geometric sans-serif
- **Headings:** Bold (700) / SemiBold (600)
- **Body:** Medium (500) / Regular (400)
- **Labels:** SemiBold (600), uppercase, letter-spacing 0.08em

### Glass Surfaces (Signature Design Element)

| Level | Background | Blur | Use |
|---|---|---|---|
| Glass 1 — Frosted | `rgba(255,255,255,0.06)` | 12px | Cards, nav items |
| Glass 2 — Deep Modal | `rgba(14,20,36,0.85)` | 20px | Auth forms, modals |
| Glass 3 — Sidebar | `rgba(6,8,16,0.90)` | 8px | Sidebar panel |

### Key Design Rules

- **8px base grid** for all spacing (4px micro-adjustments allowed)
- **8px border-radius** on all interactive controls
- All form controls styled as "high-security terminal interfaces"
- Buttons: 48px minimum height, Manrope SemiBold 15-16px
- No external CSS libraries — all custom via Tailwind CSS v4 `@theme` tokens in `src/index.css`

> **Full design system:** See `DESIGN.md` in the AtlasID workspace for exhaustive details (407 lines).

---

## 6. Frontend Architecture

### Routing — State-Driven (No Router Library)

```typescript
type Screen = 'landing' | 'login' | 'otp' | 'success' | 'signup' | 'dashboard'
```

All routing is in `App.tsx`. Screen transitions via callback props passed down the tree. No react-router.

### Component Tree

```
App.tsx
├── LandingPage          (screen = 'landing')
├── HeroLayout           (screen = 'login' | 'otp' | 'signup')
│   ├── LoginForm        (screen = 'login')
│   ├── VerificationForm (screen = 'otp')
│   └── SignupPage       (screen = 'signup')
│       └── StepperLayout
│           ├── PersonalInfoStep
│           ├── AddressStep
│           ├── SecurityQuestionsStep
│           ├── GoogleAuthSetupStep
│           └── ReviewStep
├── SuccessScreen        (screen = 'success')
└── DashboardPage        (screen = 'dashboard')
    └── AppShell
        ├── Sidebar
        ├── TopNavigation
        └── [active sub-page]
```

### State Management

All state is local `useState` within custom hooks. **No Redux, Zustand, or Context API.** Each domain area has its own hook:

| Hook | Purpose |
|---|---|
| `useSignupForm` | 5-step signup state, per-step validation, navigation |
| `useLoginForm` | Login form state, email/password validation |
| `useOtpInput` | 6-digit OTP array, auto-advance, paste support |
| `useResendTimer` | 30-second cooldown, 3-attempt limit |
| `useAutoRedirectCountdown` | Success screen countdown ring (8s auto-redirect) |

### Key Files

| File | Purpose |
|---|---|
| `src/App.tsx` | Root screen router |
| `src/index.css` | Global styles + all design tokens |
| `src/data/mockData.ts` | All mock data (will be replaced by API calls) |
| `src/hooks/useSignupForm.ts` | 5-step form orchestration |
| `src/components/landing/LandingPage.tsx` | Landing page orchestrator |
| `src/components/signup/SignupPage.tsx` | Signup flow router |
| `src/components/dashboard/DashboardPage.tsx` | Dashboard page router |

---

## 7. Backend Architecture (Finalized Design)

### Architecture Decision: Modular Monolith

**Chosen over pure microservices and traditional monolith.** Single JVM (~512MB RAM), clean module boundaries, each module has its own controller/service/repository layer. Any module can later be extracted into its own microservice.

### Module Breakdown

| Module | Path | Responsibility |
|---|---|---|
| **Auth** | `/auth/**` | Registration, login, OTP, JWT lifecycle, TOTP 2FA |
| **Identity** | `/identity/**` | User profile, digital ID card, verification score |
| **Dashboard** | `/dashboard/**` | Aggregation layer for overview screen |
| **Security** | `/security/**` | Password changes, 2FA management, sessions, alerts |
| **Settings** | `/settings/**` | Profile preferences, notifications, privacy |
| **Linked Services** | `/linked-services/**` | Third-party service connections, Identity Linking API |
| **Common** | — | Shared: exceptions, utilities, JPA entities |
| **Infrastructure** | — | Cross-cutting: Redis, JWT filter, schedulers, file storage |

### Database: PostgreSQL

**Tables:** `users`, `user_addresses`, `security_questions`, `totp_secrets`, `user_settings`, `activity_log`, `linked_services`, `service_registrations`, `user_avatars`, `link_tokens`

The `activity_log` table will grow fastest (~500K rows/year at 20K users). Indexes on `(user_id, created_at DESC)` and `(user_id, type)` are critical.

### Redis Data Structures

| Key Pattern | TTL | Purpose |
|---|---|---|
| `otp:{email}` | 3 min | OTP codes |
| `signup:{token}` | 30 min | Multi-step signup session |
| `refresh:{tokenHash}` | 7/30 days | Refresh token for revocation |
| `session:{userId}:{sessionId}` | 30 days | Active session tracking |
| `rate:login:{ip}` | 15 min | Login rate limit |
| `rate:otp:{email}` | 1 hour | OTP rate limit |
| `dashboard:{userId}` | 5 min | Cached dashboard data |
| `link:pending:{userId}` | 15 min | Pending link requests |
| `link:notify:{userId}` | None | Unread notification count |

### JWT Strategy

- **Access token:** 15-minute expiry, memory-only (never localStorage), contains `userId`, `email`, `roles`
- **Refresh token:** 7-day (or 30-day with "remember me"), httpOnly cookie, stored in Redis for revocation
- **Session timeout:** 10-15 minutes inactivity is handled natively by access token expiry

### Identity Linking (Critical Feature)

Two flows for external services to verify users:

**Flow A — Approval-Based (Push Consent):**
1. External app sends `POST /api/v1/link/request` with API key + user's AtlasID number
2. AtlasID creates a dashboard notification (bell badge count + persistent pop-up on Linked Services page)
3. User approves with OTP/TOTP confirmation
4. External app polls `GET /api/v1/link/status/{requestId}` until APPROVED
5. Pending requests expire after 15 minutes

**Flow B — OAuth Redirect (Instant Linking):**
1. External app sends `POST /api/v1/link/initiate` → gets `redirectUrl`
2. User is redirected to AtlasID, logs in, sees consent screen, approves with OTP
3. AtlasID redirects back to external app with single-use `authCode` (5-min expiry)
4. External app exchanges `authCode` for verified user info via `POST /api/v1/link/verify`

### External-Facing API Endpoints (Identity Linking)

| Endpoint | Method | Auth | Description |
|---|---|---|---|
| `/api/v1/link/request` | POST | API Key | Request identity verification (Flow A) |
| `/api/v1/link/status/{requestId}` | GET | API Key | Poll approval status (Flow A) |
| `/api/v1/link/initiate` | POST | API Key | Start OAuth redirect (Flow B) |
| `/api/v1/link/verify` | POST | API Key | Exchange auth code for user info (Flow B) |
| `/api/v1/link/user-info` | GET | API Key | Get linked user details |
| `/api/v1/link/revoke` | POST | API Key | Revoke a linking |

### Development Phases

| Phase | Timeline | Scope |
|---|---|---|
| Phase 1 — Auth | Weeks 1–3 | Signup, login, OTP, JWT, TOTP, rate limiting, Swagger |
| Phase 2 — Dashboard & Identity | Weeks 4–5 | Profile, verification score, dashboard aggregation, activity log, avatar, settings, security |
| Phase 3 — Identity Linking | Weeks 6–8 | Service registration, linked services CRUD, Flow A, Flow B, frontend notifications |

---

## 8. Email Service Design (Finalized)

### Overview

A **standalone Java/Spring Boot microservice** whose only job is sending emails. Every project in the ecosystem calls this one service via HTTP.

**Key properties:**
- Stateless — no database, no persistence
- One Gmail account (`shivanshprojects@gmail.com`), multiple sender display names
- Template-based HTML emails (Thymeleaf)
- API key authentication per caller app (`X-API-Key` header)
- Rate limiting per caller
- ~128MB RAM footprint
- Port: **8085**

### API

| Endpoint | Method | Auth | Description |
|---|---|---|---|
| `POST /api/v1/email/send` | POST | API Key | Send a single email |
| `POST /api/v1/email/send-batch` | POST | API Key | Send batch emails (up to 50) |
| `GET /api/v1/email/types` | GET | API Key | List supported email types |
| `GET /api/v1/email/health` | GET | None | Health check |

### Request Format

```json
{
  "to": "user@example.com",
  "type": "OTP",
  "callerApp": "atlasid",
  "subject": "Your AtlasID verification code",
  "templateData": {
    "otpCode": "482917",
    "userName": "Shivansh",
    "expiryMinutes": 3
  }
}
```

### API Key Format

```
ems_{appName}_{randomString}
```
Example: `ems_atlasid_a1b2c3d4e5f6`

### Email Types

| Type | Description | Required Fields | Used By |
|---|---|---|---|
| `OTP` | Verification code | `otpCode`, `userName`, `expiryMinutes` | AtlasID, Indian Express |
| `WELCOME` | Post-registration welcome | `userName` | All apps |
| `LOGIN_ALERT` | New device/location login | `userName`, `device`, `location`, `time` | AtlasID |
| `PASSWORD_CHANGED` | Password change confirmation | `userName`, `time` | AtlasID, Indian Express |
| `LINK_APPROVAL` | Identity link request received | `userName`, `serviceName`, `scopes` | AtlasID |
| `LINK_APPROVED` | Identity link approved | `userName`, `serviceName` | AtlasID |
| `TRANSACTION_RECEIPT` | Payment receipt | `userName`, `amount`, `merchant`, `date` | Indian Express |
| `ACCOUNT_STATEMENT` | Monthly summary | `userName`, `month`, `openingBalance`, `closingBalance` | Indian Express |
| `ORDER_CONFIRMATION` | Order placed | `userName`, `orderId`, `items`, `total` | Shopping App |
| `SHIPPING_UPDATE` | Order shipped/delivered | `userName`, `orderId`, `status` | Shopping App |
| `GENERIC` | Custom email (no template) | `userName`, `body` | Any app |

### Caller App Configuration

```yaml
email:
  callers:
    atlasid:
      apiKey: ${ATLASID_API_KEY}
      displayName: "AtlasID"
      rateLimit: 100              # emails per hour
    indianexpress:
      apiKey: ${INDIANEXPRESS_API_KEY}
      displayName: "Indian Express"
      rateLimit: 200
    shopping:
      apiKey: ${SHOPPING_API_KEY}
      displayName: "ShopVerse"
      rateLimit: 150
```

### How Consumer Apps Connect

Each consumer app needs a simple `EmailServiceClient` bean:
- Inject `@Value("${email-service.url}")` for the base URL
- Inject `@Value("${email-service.api-key}")` for the API key
- Use Spring's `RestClient` to call `POST /api/v1/email/send`
- In Docker, the URL is `http://email-service:8085`

### Dev vs Prod Mode

| Behavior | Dev | Prod |
|---|---|---|
| Email sending | Logs to console (doesn't send) | Sends via Gmail SMTP |
| API key check | Accepts any key | Strict validation |
| Rate limiting | Disabled | Enabled |
| Swagger UI | Enabled | Disabled |

---

## 9. Deployment Architecture

**Target:** Self-hosted Intel NUC (i7-11th gen, 32GB RAM, 1TB storage, Ubuntu Server)

### Resource Estimates

| Service | RAM (est.) | Port |
|---|---|---|
| AtlasID Backend (Spring Boot) | 512MB – 1GB | 8080 |
| PostgreSQL 16 | 256MB – 512MB | 5432 |
| Redis 7 | 64MB | 6379 |
| Nginx | 16MB | 80/443 |
| Email Service | 128MB | 8085 |
| Indian Express (future) | 512MB | 8081 |
| Shopping App (future) | 512MB | 8082 |
| **Total** | ~2 – 3GB | — |

All services use `restart: unless-stopped` for auto-restart on power cuts.

### Docker Network

```bash
# Create once on NUC setup
docker network create shivansh-network

# All docker-compose.yml files reference this network
# Services call each other by service name:
#   http://email-service:8085/api/v1/email/send
#   http://atlasid-backend:8080/api/v1/link/request
```

---

## 10. API Contract Summary

The frontend's `API.md` (907 lines) defines the **exact request/response shapes** for all endpoints. Key conventions:

- **Base URL:** `https://api.atlasid.com/v1` (or `http://localhost:8080` locally)
- **Auth:** `Authorization: Bearer <token>` for authenticated routes
- **Error format:** `{ success: false, error: "ERROR_CODE", message: "Human-readable" }`
- **Validation errors:** `{ success: false, errors: { fieldName: "Error message" } }`

> **Full API reference:** See `API.md` in the AtlasID workspace.

---

## 11. Decisions Log

| # | Decision | Choice | Rationale | Session |
|---|---|---|---|---|
| 1 | Backend architecture | **Modular Monolith** | Single JVM (~512MB). Clean module boundaries. Microservice-ready. Best for solo dev on single NUC. | cd937268 |
| 2 | Email sending | **Shared microservice** | One Gmail account, one service. All ecosystem apps call via HTTP. Avoids duplicating SMTP config. | cd937268 |
| 3 | Identity linking | **Dual flow** (approval-based + OAuth redirect) | Flow A for high-security with dashboard notifications; Flow B for instant UX like "Login with Google." | cd937268 |
| 4 | Repository structure | **Separate repos** | Backend is its own project. Frontend calls it via HTTP through Nginx. Email service is its own repo. | cd937268 |
| 5 | Database | **PostgreSQL 16** | Rock-solid, open source, great SQL support. | cd937268 |
| 6 | Caching | **Redis 7** | Ephemeral data (OTP, sessions, rate limits). No Redis = no critical data loss. | cd937268 |
| 7 | Frontend routing | **State-driven (no router)** | Simple screen set, no complex nested routes needed. | Multiple sessions |
| 8 | State management | **Local useState + custom hooks** | No Redux/Zustand needed at current complexity. | Multiple sessions |
| 9 | CSS | **Tailwind CSS v4 with @theme tokens** | CSS-first design tokens, no arbitrary values. | Multiple sessions |
| 10 | Document uploads | **Removed** | AtlasID only requires email for creation. Can be added later. | cd937268 |
| 11 | Session timeout | **15-min JWT access token expiry** | Native inactivity timeout. No polling needed. | cd937268 |
| 12 | Password hashing | **BCrypt strength 12** | ~250ms per hash — slow enough for brute-force resistance, fast enough for UX. | cd937268 |
| 13 | Gmail limit | **500 emails/day** (free tier) | Rate limiter protects against accidental loops. Monitor usage with 3+ apps. | cd937268 |
| 14 | Frontend UI libraries | **None** | Everything handcrafted. No Headless UI, Radix, Chakra, etc. Every component matches the design system. | Multiple sessions |

---

## 12. File Reference

### Key documentation files in `PROzz/AtlasID/`

| File | Lines | Content |
|---|---|---|
| `README.md` | 539 | Full project overview, architecture, components, hooks, design system |
| `DESIGN.md` | 407 | Exhaustive design system (colors, typography, spacing, glass surfaces, buttons, forms, patterns) |
| `API.md` | 907 | Complete backend API reference (all endpoints, request/response shapes) |
| `METHODS_REFERENCE.md` | 1060 | Every component and hook documented with props, methods, usage examples |
| `SITE.md` | 71 | Site vision, component architecture rules, sitemap, roadmap |
| `MEMORY.md` | This file | Cross-session context document |

### Key design artifacts (in Antigravity brain, conversation `cd937268`)

| File | Content |
|---|---|
| `implementation_plan.md` | 1030-line full backend system design (architecture, modules, DB schema, Redis, security, Docker, phases) |
| `email_service_design.md` | 517-line complete email service design (API, email types, config, Docker, project structure) |

---

## 13. For the Email Service Session

If you're working in the **emailService workspace**, here's what you need to know:

1. **Your full design is in `implementation_plan.md` and `email_service_design.md`** from Antigravity brain conversation `cd937268-7b77-4f22-b498-0425451ff840`. Read those first.

2. **You are a standalone Spring Boot app** — no database, no Redis, just REST API + Thymeleaf templates + Gmail SMTP.

3. **Port 8085** — this is your port. AtlasID backend will be on 8080.

4. **Java package:** `com.shivansh.emailservice`

5. **API key format:** `ems_{appName}_{randomString}`, validated via `X-API-Key` header.

6. **11 email types** are defined (OTP, WELCOME, LOGIN_ALERT, PASSWORD_CHANGED, LINK_APPROVAL, LINK_APPROVED, TRANSACTION_RECEIPT, ACCOUNT_STATEMENT, ORDER_CONFIRMATION, SHIPPING_UPDATE, GENERIC).

7. **Gmail App Password** required (not regular password). Environment variable: `GMAIL_APP_PASSWORD`.

8. **Rate limits:** Per-caller (configurable, default 100/hour), global (500/hour). Gmail free tier limit: 500/day.

9. **Retry logic:** 3 retries (2s, 5s, 15s delays) via Spring Retry `@Retryable`.

10. **Dev mode:** Log emails to console instead of sending. Accept any API key. Disable rate limiting. Enable Swagger.

11. **Docker:** Join `shivansh-network`. Other apps call you by service name: `http://email-service:8085/api/v1/email/send`.

---

## 14. For the AtlasID Backend Session

If you're working on the **AtlasID backend**, here's what you need to know:

1. **Your full design is in `implementation_plan.md`** from Antigravity brain conversation `cd937268-7b77-4f22-b498-0425451ff840`. That's your bible.

2. **Java package:** `com.atlasid.backend`

3. **API contract:** `API.md` in the AtlasID workspace defines the exact request/response shapes the frontend expects. **Do not deviate** from these contracts.

4. **Email sending:** Call the shared email service via HTTP, not `JavaMailSender` directly. URL: `http://email-service:8085/api/v1/email/send`.

5. **Frontend is done.** All components, hooks, and data types are documented in `METHODS_REFERENCE.md`. The mock data in `src/data/mockData.ts` shows the exact shapes the frontend renders.

6. **Identity Linking** is the most complex feature. Two flows (approval-based + OAuth redirect). See Section 7 above.

7. **Flyway migrations** for database schema. Files: `V1__create_users_table.sql` through `V9__create_avatars.sql`.

8. **Background jobs:** Session cleanup (60s interval), activity log archival (daily 3 AM), metrics collection (5 min).

---

## 15. Stitch Project Reference

The AtlasID UI was originally designed in **Google Stitch** (AI design tool).

- **Stitch Project ID:** `7528742426841309900`
- **Screens designed:** 18 screens across mobile, tablet, and desktop breakpoints
- **Stitch prompting preamble:** *"Design in the AtlasID visual language: deep space dark UI with `#060810` as the canvas, `#2e77ff` as the electric blue accent, Manrope typeface across all weights, glass surfaces with 12px backdrop blur and `rgba(255,255,255,0.06)` fill, 8px border-radius on all interactive controls, 8pt spacing grid. The atmosphere is premium fintech — authoritative, luminous, globally trusted."*

---

## Appendix A — Quick Commands

```bash
# Start frontend dev server
cd c:\Users\Shivansh\Desktop\PROzz\AtlasID
npm run dev
# → http://localhost:5173/

# Build for production
npm run build

# Lint
npm run lint
```

---

*This document should be copied to any new workspace that needs context about the AtlasID ecosystem.*
