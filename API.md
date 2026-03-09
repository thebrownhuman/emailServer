# AtlasID — API Reference

Complete list of backend API endpoints needed to replace all mocked/hardcoded data in the frontend.

Base URL: `https://api.atlasid.com/v1` (or your backend URL)

All authenticated routes require: `Authorization: Bearer <token>`

---

## Table of Contents

- [Authentication](#authentication)
- [Signup (KYC Flow)](#signup-kyc-flow)
- [Dashboard](#dashboard)
- [Identity](#identity)
- [Activity](#activity)
- [Linked Services](#linked-services)
- [Security](#security)
- [Settings](#settings)
- [Documents](#documents)
- [Request & Response Conventions](#request--response-conventions)

---

## Authentication

### POST `/auth/login`
Authenticate an existing user.

**Body:**
```json
{
  "email": "string",
  "password": "string",
  "rememberMe": "boolean"
}
```

**Response:**
```json
{
  "token": "string",
  "maskedEmail": "a***@example.com",
  "expiresIn": 3600
}
```

**Notes:** On success, frontend transitions to OTP verification screen.

---

### POST `/auth/verify-otp`
Verify the 6-digit one-time code sent to the user's email.

**Body:**
```json
{
  "email": "string",
  "otp": "string (6 digits)"
}
```

**Response:**
```json
{
  "success": true,
  "accessToken": "string",
  "refreshToken": "string",
  "user": {
    "id": "string",
    "name": "string",
    "email": "string",
    "initials": "string"
  }
}
```

**Error (wrong code):**
```json
{
  "success": false,
  "message": "Incorrect code. Please try again."
}
```

---

### POST `/auth/resend-otp`
Resend a new OTP to the user's email. Max 3 attempts, 30-second cooldown enforced on frontend.

**Body:**
```json
{
  "email": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Code sent to a***@example.com"
}
```

---

### POST `/auth/logout`
Invalidate the current session/token.

**Headers:** `Authorization: Bearer <token>`

**Response:**
```json
{
  "success": true
}
```

---

### POST `/auth/refresh`
Exchange a refresh token for a new access token.

**Body:**
```json
{
  "refreshToken": "string"
}
```

**Response:**
```json
{
  "accessToken": "string",
  "expiresIn": 3600
}
```

---

## Signup (KYC Flow)

The 5-step signup flow. Steps can be submitted individually (save progress) or all at once on final submit.

### POST `/auth/signup/init`
Start a new signup session. Returns a `signupToken` used across all steps.

**Body:**
```json
{}
```

**Response:**
```json
{
  "signupToken": "string"
}
```

---

### POST `/auth/signup/personal`
Step 1 — Personal information.

**Body:**
```json
{
  "signupToken": "string",
  "firstName": "string",
  "lastName": "string",
  "email": "string",
  "phone": "string (optional)",
  "dateOfBirth": "YYYY-MM-DD"
}
```

**Response:**
```json
{
  "success": true,
  "step": 0
}
```

**Validation errors:**
```json
{
  "success": false,
  "errors": {
    "email": "Email is already in use",
    "dateOfBirth": "Must be at least 18 years old"
  }
}
```

---

### POST `/auth/signup/address`
Step 2 — Address information.

**Body:**
```json
{
  "signupToken": "string",
  "country": "GB",
  "streetLine1": "string",
  "streetLine2": "string (optional)",
  "city": "string",
  "state": "string (optional)",
  "postalCode": "string"
}
```

**Response:**
```json
{
  "success": true,
  "step": 1
}
```

---

### POST `/auth/signup/security`
Step 3 — Password and security questions.

**Body:**
```json
{
  "signupToken": "string",
  "password": "string (min 8 chars, 1 uppercase, 1 digit)",
  "question1": "string (question ID)",
  "answer1": "string",
  "question2": "string (different question ID)",
  "answer2": "string"
}
```

**Response:**
```json
{
  "success": true,
  "step": 2
}
```

**Notes:** Hash passwords and answers (bcrypt/Argon2) before storing. Never store plaintext.

---

### GET `/auth/signup/totp-secret`
Generate a TOTP secret and QR code URI for Google Authenticator setup.

**Query:** `?signupToken=<token>`

**Response:**
```json
{
  "secret": "JBSWY3DPEHPK3PXP",
  "qrCodeUri": "otpauth://totp/AtlasID:user@email.com?secret=...&issuer=AtlasID",
  "backupCodes": ["xxxx-xxxx", "xxxx-xxxx", "..."]
}
```

---

### POST `/auth/signup/totp-verify`
Step 4 — Verify the Google Authenticator TOTP code.

**Body:**
```json
{
  "signupToken": "string",
  "totpCode": "string (6 digits)"
}
```

**Response:**
```json
{
  "success": true,
  "verified": true,
  "step": 3
}
```

---

### POST `/auth/signup/submit`
Step 5 (Review) — Complete registration with all collected data.

**Body:**
```json
{
  "signupToken": "string",
  "totpEnabled": "boolean",
  "agreedToTerms": true
}
```

**Response:**
```json
{
  "success": true,
  "userId": "string",
  "accessToken": "string",
  "refreshToken": "string",
  "user": {
    "id": "string",
    "name": "string",
    "email": "string",
    "initials": "string",
    "idNumber": "ATL-2026-XXXXXX"
  }
}
```

---

## Dashboard

### GET `/dashboard`
Overview data for the main dashboard screen.

**Response:**
```json
{
  "user": {
    "id": "string",
    "name": "string",
    "email": "string",
    "initials": "string",
    "avatarUrl": "string | null"
  },
  "idCard": {
    "idNumber": "ATL-2026-884921",
    "issuedDate": "2026-03-01",
    "expiryDate": "2029-02-28",
    "status": "verified",
    "verificationScore": 94
  },
  "stats": [
    {
      "icon": "string",
      "label": "string",
      "value": "string",
      "subtext": "string",
      "trend": "up | down | neutral",
      "trendText": "string",
      "variant": "default | highlight | success | warning"
    }
  ],
  "recentActivity": [ /* same shape as /activity events */ ]
}
```

---

## Identity

### GET `/identity`
Full identity profile for the "My Identity" page.

**Response:**
```json
{
  "idCard": {
    "fullName": "string",
    "idNumber": "string",
    "email": "string",
    "nationality": "string",
    "issuedDate": "string",
    "expiryDate": "string",
    "lastVerified": "string",
    "status": "verified | pending | expired",
    "verificationScore": 94
  },
  "strengthBreakdown": {
    "documentVerification": 100,
    "biometricMatch": 98,
    "addressConfirmed": 85,
    "phoneVerified": 90
  },
  "personalInfo": {
    "fullName": "string",
    "dateOfBirth": "string",
    "nationality": "string",
    "gender": "string"
  },
  "contactInfo": {
    "email": "string",
    "phone": "string",
    "city": "string",
    "postalCode": "string"
  }
}
```

---

### POST `/identity/request-physical-card`
Request a physical ID card to be mailed.

**Response:**
```json
{
  "success": true,
  "requestId": "string",
  "estimatedDelivery": "7-10 business days",
  "message": "Your physical ID card has been requested."
}
```

---

## Activity

### GET `/activity`
Paginated activity log with stats.

**Query params:**
| Param | Type | Default | Description |
|---|---|---|---|
| `days` | number | 7 | Filter last N days |
| `type` | string | all | `login \| verification \| document \| service \| security \| logout` |
| `page` | number | 0 | Page index |
| `limit` | number | 10 | Items per page |

**Response:**
```json
{
  "stats": {
    "totalEvents": 47,
    "verifications": 12,
    "failedAttempts": 0
  },
  "events": [
    {
      "id": "string",
      "type": "login | verification | document | service | security | logout",
      "title": "string",
      "description": "string",
      "icon": "string (Material Symbol name)",
      "timestamp": "ISO string",
      "timeAgo": "2 hours ago",
      "location": "London, UK | Automated | null"
    }
  ],
  "hasMore": true,
  "total": 47
}
```

---

## Linked Services

### GET `/linked-services`
List all linked services for the user.

**Query params:** `?category=Finance` (optional filter)

**Response:**
```json
{
  "activeCount": 4,
  "services": [
    {
      "id": "string",
      "name": "NovaPay Bank",
      "category": "Finance",
      "icon": "account_balance",
      "color": "#2e77ff",
      "connectedDate": "15 Jan 2026",
      "permissions": ["Identity Verification", "KYC Check"],
      "status": "connected | expired",
      "expiryDate": "string | null"
    }
  ],
  "discover": [
    {
      "id": "string",
      "name": "Revolut",
      "category": "Finance",
      "icon": "payments",
      "color": "#0075eb"
    }
  ]
}
```

---

### DELETE `/linked-services/:serviceId`
Revoke a linked service's access.

**Response:**
```json
{
  "success": true,
  "message": "Access revoked for NovaPay Bank"
}
```

---

### POST `/linked-services/connect`
Initiate OAuth/connection flow with a new service.

**Body:**
```json
{
  "serviceId": "string"
}
```

**Response:**
```json
{
  "redirectUrl": "string (OAuth URL)"
}
```

---

## Security

### GET `/security`
All security settings and active sessions.

**Response:**
```json
{
  "twoFactorAuth": {
    "enabled": true,
    "method": "google_authenticator",
    "linkedDate": "15 Jan 2026",
    "backupCodesRemaining": 8
  },
  "password": {
    "lastChanged": "30 days ago",
    "strength": "strong"
  },
  "activeSessions": [
    {
      "id": "string",
      "device": "Chrome on Windows",
      "location": "London, UK",
      "loginTime": "2 hours ago",
      "isCurrent": true
    }
  ],
  "loginAlerts": {
    "newDevice": true,
    "failedAttempts": true,
    "passwordReset": true,
    "newIpAddress": false
  },
  "securityScore": 94,
  "recommendations": [
    { "text": "Two-factor authentication enabled", "completed": true },
    { "text": "Strong password in use", "completed": true },
    { "text": "Add a recovery email address", "completed": false }
  ]
}
```

---

### POST `/security/change-password`
Change the user's password.

**Body:**
```json
{
  "currentPassword": "string",
  "newPassword": "string",
  "confirmPassword": "string",
  "totpCode": "string (if 2FA enabled)"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Password changed successfully."
}
```

---

### PATCH `/security/login-alerts`
Update login alert preferences.

**Body:**
```json
{
  "newDevice": true,
  "failedAttempts": true,
  "passwordReset": true,
  "newIpAddress": false
}
```

**Response:**
```json
{
  "success": true
}
```

---

### DELETE `/security/sessions/:sessionId`
Revoke an active session (cannot revoke current session).

**Response:**
```json
{
  "success": true,
  "message": "Session revoked."
}
```

---

## Settings

### GET `/settings`
All user account settings.

**Response:**
```json
{
  "profile": {
    "displayName": "Alex Chen",
    "email": "alex@atlasid.com",
    "avatarUrl": "string | null",
    "language": "en-GB",
    "timezone": "Europe/London"
  },
  "notifications": {
    "emailAlerts": true,
    "pushNotifications": true,
    "securityAlerts": true,
    "marketingUpdates": false
  },
  "privacy": {
    "anonymousDataSharing": false,
    "usageAnalytics": true
  },
  "appearance": {
    "darkMode": true,
    "compactView": false
  }
}
```

---

### PATCH `/settings`
Update one or more settings fields.

**Body (all fields optional):**
```json
{
  "displayName": "string",
  "language": "string",
  "timezone": "string",
  "notifications": {
    "emailAlerts": "boolean",
    "pushNotifications": "boolean",
    "securityAlerts": "boolean",
    "marketingUpdates": "boolean"
  },
  "privacy": {
    "anonymousDataSharing": "boolean",
    "usageAnalytics": "boolean"
  },
  "appearance": {
    "darkMode": "boolean",
    "compactView": "boolean"
  }
}
```

**Response:**
```json
{
  "success": true,
  "updatedFields": ["displayName", "notifications.emailAlerts"]
}
```

---

### POST `/settings/avatar`
Upload a profile photo.

**Body:** `multipart/form-data` with field `avatar` (image file)

**Response:**
```json
{
  "success": true,
  "avatarUrl": "https://cdn.atlasid.com/avatars/user-id.jpg"
}
```

---

### DELETE `/account`
Permanently delete the user's account.

**Body:**
```json
{
  "confirmationPhrase": "delete my account",
  "password": "string"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Your account has been scheduled for deletion.",
  "deletionDate": "2026-04-01T00:00:00Z"
}
```

---

## Documents

### GET `/documents`
List all uploaded documents.

**Query params:** `?status=verified` (optional: `verified | pending | expired`)

**Response:**
```json
{
  "documents": [
    {
      "id": "string",
      "name": "Passport",
      "category": "Travel",
      "icon": "travel_explore",
      "status": "verified | pending | expired",
      "issuedDate": "15 Jan 2026",
      "expiryDate": "14 Jan 2030",
      "uploadedAt": "ISO string",
      "fileUrl": "string"
    }
  ]
}
```

---

### POST `/documents/upload`
Upload a new document for verification.

**Body:** `multipart/form-data`
| Field | Type | Required |
|---|---|---|
| `file` | File | Yes |
| `type` | string | Yes (e.g. `passport`, `drivers_license`, `utility_bill`) |
| `expiryDate` | string | No (YYYY-MM-DD) |

**Response:**
```json
{
  "success": true,
  "document": {
    "id": "string",
    "status": "pending",
    "estimatedVerification": "1-2 business days"
  }
}
```

---

### GET `/documents/:documentId/download`
Download a document file.

**Response:** Binary file stream with appropriate `Content-Type`.

---

### DELETE `/documents/:documentId`
Delete an uploaded document.

**Response:**
```json
{
  "success": true
}
```

---

## Request & Response Conventions

### Authentication
All endpoints except `/auth/*` and `/auth/signup/*` require:
```
Authorization: Bearer <accessToken>
```

### Error Format
All errors follow this shape:
```json
{
  "success": false,
  "error": "ERROR_CODE",
  "message": "Human-readable message",
  "fields": {
    "fieldName": "Field-specific error"
  }
}
```

### Common HTTP Status Codes
| Code | Meaning |
|---|---|
| 200 | OK |
| 201 | Created |
| 400 | Bad request / validation error |
| 401 | Unauthenticated (token missing or expired) |
| 403 | Forbidden (no permission) |
| 404 | Resource not found |
| 409 | Conflict (e.g. email already in use) |
| 422 | Unprocessable entity |
| 429 | Rate limited |
| 500 | Internal server error |

### Mocked Timeouts to Replace in Frontend
| File | Mock delay | Replace with |
|---|---|---|
| `useLoginForm.ts` | 1500ms | `POST /auth/login` |
| `useSignupForm.ts` | 2000ms | `POST /auth/signup/submit` |
| `VerificationForm.tsx` | 1800ms | `POST /auth/verify-otp` |
| `VerificationForm.tsx` | 800ms | `POST /auth/resend-otp` |
| `GoogleAuthSetupStep.tsx` | 1400ms | `POST /auth/signup/totp-verify` |
| `DashboardPage.tsx` | hardcoded | `GET /dashboard` |
| `MyIdentityPage.tsx` | hardcoded | `GET /identity` |
| `ActivityPage.tsx` | hardcoded | `GET /activity` |
| `LinkedServicesPage.tsx` | hardcoded | `GET /linked-services` |
| `SecurityPage.tsx` | hardcoded | `GET /security` |
| `SettingsPage.tsx` | hardcoded | `GET /settings` |
| `DocumentsPage.tsx` | hardcoded | `GET /documents` |

### Security Recommendations
- Hash passwords with **bcrypt** or **Argon2** before storing
- Hash security question answers before storing
- Use **httpOnly cookies** for refresh tokens, memory for access tokens
- Enforce **rate limiting** on login and OTP endpoints
- Implement **account lockout** after 5 failed login attempts
- Validate all inputs on the backend (never trust frontend-only validation)
- Use **HTTPS** for all endpoints
- Implement **CORS** to allow only your frontend origin
- Generate TOTP secrets server-side, never expose the raw secret after setup

---

## Full Endpoint Summary

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/auth/login` | No | Login with email + password |
| POST | `/auth/verify-otp` | No | Verify email OTP |
| POST | `/auth/resend-otp` | No | Resend OTP |
| POST | `/auth/logout` | Yes | Sign out |
| POST | `/auth/refresh` | No | Refresh access token |
| POST | `/auth/signup/init` | No | Start signup session |
| POST | `/auth/signup/personal` | No | Submit personal info |
| POST | `/auth/signup/address` | No | Submit address |
| POST | `/auth/signup/security` | No | Submit password + questions |
| GET | `/auth/signup/totp-secret` | No | Get TOTP QR + secret |
| POST | `/auth/signup/totp-verify` | No | Verify TOTP code |
| POST | `/auth/signup/submit` | No | Complete registration |
| GET | `/dashboard` | Yes | Dashboard overview |
| GET | `/identity` | Yes | Full identity profile |
| POST | `/identity/request-physical-card` | Yes | Request physical card |
| GET | `/activity` | Yes | Activity log + stats |
| GET | `/linked-services` | Yes | Connected services |
| DELETE | `/linked-services/:id` | Yes | Revoke service access |
| POST | `/linked-services/connect` | Yes | Connect new service |
| GET | `/security` | Yes | Security settings |
| POST | `/security/change-password` | Yes | Change password |
| PATCH | `/security/login-alerts` | Yes | Update alert prefs |
| DELETE | `/security/sessions/:id` | Yes | Revoke session |
| GET | `/settings` | Yes | Account settings |
| PATCH | `/settings` | Yes | Update settings |
| POST | `/settings/avatar` | Yes | Upload avatar |
| DELETE | `/account` | Yes | Delete account |
| GET | `/documents` | Yes | List documents |
| POST | `/documents/upload` | Yes | Upload document |
| GET | `/documents/:id/download` | Yes | Download document |
| DELETE | `/documents/:id` | Yes | Delete document |
