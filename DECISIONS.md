# Email Service — Decisions Log

> **Purpose:** Every major decision made during implementation, logged with reasoning and justification. Portable context for switching between Antigravity, Claude Code, or any other AI assistant.
>
> **Started:** 2026-03-09
> **Conversation ID:** `439b9586-af52-40ef-9083-7821b427fbbf`

---

### Decision 1 — Source of Truth for Design

**What:** Used `MEMORY.md` (Sections 8 & 13) + `email_service_design.md` from conversation `cd937268` as the authoritative design sources. Did NOT redesign anything from scratch.

**Why:** The user explicitly stated "the design is already finalized." Both documents were written in a prior session with extensive back-and-forth. Redesigning would contradict agreed-upon decisions.

**Was it correct? Yes.** The existing design is comprehensive (517 lines covering API, email types, caller config, retry, rate limiting, Docker, project structure). No gaps were found that required new design work.

---

### Decision 2 — Implementation Plan Scope

**What:** Created a single, exhaustive `implementation_plan.md` covering all 20 Java classes, every field/method/annotation, all YAML configs, Thymeleaf template structure, Docker setup, and a 9-phase implementation order.

**Why:** The user asked for "every class, method, function, how they connect, and the full Spring Boot project structure." A surface-level plan would have been insufficient. The detailed plan also serves as a reference during coding — no ambiguity about what to build.

**Was it correct? Yes.** User approved the plan without changes ("it looks good lets start with development").

---

### Decision 3 — Spring Boot 3.4.3 + Java 21

**What:** Chose Spring Boot `3.4.3` (latest stable in the 3.4.x line) and Java 21 (LTS).

**Why:** Matches the tech stack decision in `MEMORY.md` Section 4: "Java 21 (LTS)" and "Spring Boot 3.4.x". Java 21 gives us virtual threads and records. Spring Boot 3.4.x is the latest stable release line with full Java 21 support.

**Was it correct? Yes.** This is the same stack as the AtlasID backend, so the two services will have identical build tooling and can share knowledge.

---

### Decision 4 — No Spring Security Dependency

**What:** Used a plain `OncePerRequestFilter` for API key auth instead of pulling in `spring-boot-starter-security`.

**Why:** Spring Security is extremely powerful but adds significant complexity (SecurityFilterChain, CSRF, session management) that's overkill for a simple API key check on a microservice with 4 endpoints. A lightweight filter registered via `FilterRegistrationBean` is simpler, easier to understand, and has zero configuration surprises.

**Was it correct? Yes.** The email service has no user sessions, no OAuth, no role-based access — just "does this API key match a registered caller?" A filter does this in ~40 lines.

---

### Decision 5 — In-Memory Rate Limiting (Bucket4j, No Redis)

**What:** Used `bucket4j-core` with `ConcurrentHashMap` for rate limiting. No Redis.

**Why:** MEMORY.md Section 8 explicitly says the email service has "no Redis." The service runs as a single instance (one Docker container on a NUC). In-memory Bucket4j is sufficient for single-instance rate limiting. Adding Redis would contradict the "stateless, no persistence" design principle and add an unnecessary dependency.

**Was it correct? Yes.** With a single instance, in-memory buckets are perfectly accurate. If the service ever scales to multiple instances (unlikely for a personal ecosystem), Redis-backed buckets could be added later.

---

### Decision 6 — Thymeleaf for Email Templates (Not Freemarker, Not Raw Strings)

**What:** Used Thymeleaf as the template engine for HTML emails.

**Why:** Explicitly specified in `MEMORY.md` Section 4 ("Thymeleaf (HTML email templates)") and the original design. Thymeleaf integrates natively with Spring Boot (`spring-boot-starter-thymeleaf`), supports natural templating (templates are valid HTML even without processing), and is the de facto standard for Spring email templates.

**Was it correct? Yes.** Thymeleaf templates can be previewed in a browser during design, which is important for email formatting. FreeMarker would also work but wasn't specified.

---

### Decision 7 — `@Retryable` for SMTP Retry (Not Manual Retry Loop)

**What:** Used Spring Retry's `@Retryable` annotation on the `doSendWithRetry()` method with exponential backoff (2s, 5s, 15s).

**Why:** Spring Retry is already a dependency and provides declarative retry with `@Retryable` + `@Recover`. This is cleaner than a manual for-loop with Thread.sleep(). The backoff parameters (delay=2000, multiplier=2.5, maxDelay=15000) produce the exact sequence specified in the design: ~2s → ~5s → ~12.5s (capped to 15s).

**Was it correct? Yes.** Declarative retry keeps the business logic clean. The `@Recover` method provides a clear fallback when all retries are exhausted.

---

### Decision 8 — Separate `TemplateService` and `EmailSenderService`

**What:** Split email functionality into two services: `TemplateService` (renders HTML) and `EmailSenderService` (sends via SMTP).

**Why:** Single Responsibility Principle. `TemplateService` is a pure function (data in → HTML out) that's easy to unit test. `EmailSenderService` handles the impure side effects (rate limiting, SMTP, retry). This separation also matches the original design document's architecture diagram.

**Was it correct? Yes.** This makes testing straightforward: `TemplateServiceTest` can verify template output without touching SMTP, and `EmailSenderServiceTest` can mock the template service.

---

### Decision 9 — Dev Mode as a Boolean Flag (Not a Separate Implementation)

**What:** Used a single `email.dev-mode` boolean in YAML to switch between "log to console" and "send via SMTP." Same code path, just an if/else in `EmailSenderService`.

**Why:** Simpler than creating separate dev/prod implementations with a Strategy pattern. The behavior difference is trivial (log vs send). The YAML profile (`application-dev.yml`) sets `dev-mode: true`, `rate-limit.enabled: false`, and enables Swagger.

**Was it correct? Yes.** A Strategy/interface pattern would be overengineering for a single if/else. The codebase stays simple and the dev/prod split is clear from the YAML profiles.

---

### Decision 10 — Creating This Decisions File in the Workspace (Not Brain)

**What:** Created `DECISIONS.md` in `c:\Users\Shivansh\Desktop\PROzz\emailService\` (the project workspace), not in the Antigravity brain directory.

**Why:** The user explicitly wants this file to be portable — "if I ever go from Antigravity to Claude Code, I have all my context." Files in the Antigravity brain are tool-specific and not easily accessible. Files in the workspace are version-controllable, editor-visible, and tool-agnostic.

**Was it correct? Yes.** This file is now alongside `MEMORY.md` in the project root, making it easy to find and share with any AI tool.

---

---

### Decision 11 — JAVA_HOME Discovery and Build Verification

**What:** Maven wrapper failed initially because `JAVA_HOME` wasn't set in the shell. Found Java 21 at `C:\Program Files\Java\jdk-21` and ran `$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"; .\mvnw.cmd compile` which completed with exit code 0.

**Why:** The `mvnw.cmd` wrapper script requires `JAVA_HOME` to find the JDK. Maven wasn't installed system-wide (no `mvn` on PATH), but Java 21 JDK was installed at the expected location.

**Was it correct? Yes.** Build compiled cleanly on first try with Java 21 + Spring Boot 3.4.3. All 20 Java source files compiled without errors.

---

---

### Decision 12 — Service is API-Only (No UI, Thymeleaf ≠ Web Pages)

**What:** Confirmed the email service has zero web UI. Thymeleaf is used **only** as an email template engine — it renders variables into HTML strings that become email bodies. It does NOT serve web pages.

**Why:** The controller is annotated with `@RestController` (not `@Controller`), which means every method returns JSON responses, never HTML views. Thymeleaf's `SpringTemplateEngine.process()` is called programmatically inside `TemplateService.java` to generate email HTML — it has nothing to do with web page rendering.

**Was it correct? Yes.** This is a pure REST API microservice. The only "UI" is Swagger UI (`/swagger-ui.html`), which is a built-in API documentation/testing tool provided by the `springdoc-openapi` library — not something we built.

---

### Decision 13 — Gmail Sender Address: `shivanshmailserver@gmail.com`

**What:** Set `shivanshmailserver@gmail.com` as the default SMTP sender in `application.yml` (`${GMAIL_USERNAME:shivanshmailserver@gmail.com}`). This is the "From" address on all outgoing emails.

**Why:** The user created a dedicated Gmail account for the email service (not their personal email). This is best practice — a dedicated sender avoids hitting personal Gmail sending limits and keeps the service isolated.

**Was it correct? Yes.** Dedicating a Gmail account to the service means the user's personal inbox isn't affected, and the App Password is scoped to this service account only.

---

### Decision 14 — Gmail App Password via Environment Variable (Never Hardcoded)

**What:** The Gmail App Password (`ddua spnf fnxr cdfg`) is stored as a permanent Windows environment variable (`GMAIL_APP_PASSWORD`) and referenced in YAML as `${GMAIL_APP_PASSWORD:changeme}`. The password is **never** committed to code.

**Why:** App Passwords are credentials. Hardcoding them in YAML or Java files means they'd end up in Git history. Using `[System.Environment]::SetEnvironmentVariable('GMAIL_APP_PASSWORD', '...', 'User')` stores them securely at the OS level. Spring Boot's `${}` syntax resolves them at runtime.

**Was it correct? Yes.** This follows the 12-Factor App principle (config in environment). The `changeme` default ensures the app starts but fails gracefully if the real password isn't set.

---

### Decision 15 — Running Default Profile for Real Email Testing (Not Dev)

**What:** When testing real email sending, ran with no profile (default) instead of `dev`. This enables API key auth, rate limiting, and actual SMTP sending.

**Why:** Dev mode (`spring-boot.run.profiles=dev`) logs emails to console but doesn't send them. To verify the Gmail App Password and SMTP flow, the app must run in default/prod mode. The user explicitly wanted to "test sending real emails."

**Was it correct? Yes.** Both OTP and Welcome emails were sent and received successfully, confirming the full SMTP pipeline works end-to-end.

---

### Decision 16 — Stitch for Email Template UI Design (Visual Reference)

**What:** Used Google's Stitch MCP to generate 6 premium email template UI designs (OTP, Welcome, Login Alert, Transaction Receipt, Order Confirmation, Identity Link Request) in a project titled "Email Service — Email Templates UI."

**Why:** The user asked for UI designs "inspired by AtlasID." Stitch generates high-fidelity screen designs from text prompts. All 6 designs use a consistent dark mode theme (#0d1117 background, #161b26 card, #2e77ff electric blue accent, glassmorphism borders) matching the AtlasID design language. These serve as visual references and can optionally replace the current Thymeleaf template HTML.

**Was it correct? Yes.** The designs maintain brand consistency across AtlasID (identity), ShopVerse (e-commerce), and the shared email service. Stitch also generates downloadable HTML for each screen.

---

### Decision 17 — .gitignore and .dockerignore (What Gets Tracked vs Built)

**What:** Created `.gitignore` (excludes `target/`, `.env`, IDE files, Maven wrapper JAR, logs) and `.dockerignore` (excludes `.git`, IDE, docs, secrets, build output from Docker build context).

**Why:** `.gitignore` ensures no build artifacts, credentials, or IDE-specific files end up in the repo. `.dockerignore` keeps the Docker build context small and fast — no point copying `target/` into Docker since it rebuilds inside the container.

**Was it correct? Yes.** The Maven wrapper `.properties` file IS tracked (tells Docker which Maven version to download), but the `.jar` is NOT (it's downloaded at build time). Secrets (`.env`) are excluded from both git and Docker.

---

### Decision 18 — GitHub Repo: `thebrownhuman/emailServer`

**What:** Pushed the email service code to `git@github.com:thebrownhuman/emailServer.git` on branch `main`. Git user configured as `thebrownhuman` / `Thebrownhuman@gmail.com`.

**Why:** The user wants the code version-controlled as a standalone project on GitHub. This also enables cloning on the remote deployment machine as an alternative to Docker Hub.

**Was it correct? Yes.** The repo contains only source code, configs, templates, Docker files, and docs — no credentials, no build output, no IDE files.

---

### Decision 19 — GitHub Container Registry (ghcr.io) Over Docker Hub

**What:** Used GitHub Container Registry (`ghcr.io/thebrownhuman/emailserver`) instead of Docker Hub for hosting Docker images.

**Why:** The user already has a GitHub account (`thebrownhuman`). ghcr.io is free, integrated with GitHub, and requires no separate account. Auth uses the built-in `GITHUB_TOKEN` — zero setup. Docker Hub would require creating a separate account and managing separate credentials.

**Was it correct? Yes.** ghcr.io is the path of least resistance for GitHub-hosted projects. Images are linked to the repo automatically.

---

### Decision 20 — GitHub Actions CI/CD (Auto Build + Push on Every Push to Main)

**What:** Created `.github/workflows/docker-publish.yml` — a GitHub Actions workflow that triggers on every push to `main`, builds the Docker image, and pushes it to `ghcr.io/thebrownhuman/emailserver:latest`.

**Why:** The user wants "whenever new code is pushed, Docker should get updated." GitHub Actions is free for public repos and has generous limits for private repos. No external CI service needed.

**Was it correct? Yes.** The workflow uses official GitHub Actions (`checkout@v4`, `docker/login-action@v3`, `docker/build-push-action@v5`), tags images with both `latest` and the git SHA for traceability, and requires no secrets configuration since `GITHUB_TOKEN` is auto-provided.

---

### Decision 21 — Unit Tests (18 Tests, 3 Classes)

**What:** Created `TemplateServiceTest` (7 tests, `@SpringBootTest` with real Thymeleaf), `EmailSenderServiceTest` (5 tests, Mockito), and `EmailControllerTest` (6 tests, MockMvc). All 18 pass.

**Why:** The original plan included a Phase 9 for tests. Tests cover: template rendering for all 11 types, required field validation, subject generation, dev/prod mode switching, rate limiting enforcement, batch processing, and all 4 REST endpoints with validation errors.

**Was it correct? Yes.** Two bugs were found and fixed during testing: (1) `BatchEmailResponse` getters were named `getTotal()` not `getTotalCount()`, (2) `LOGIN_ALERT` requires field `time` not `loginTime`. Tests also needed `@TestPropertySource(properties = "email.dev-mode=true")` to bypass the API key auth filter.

---

### Decision 22 — README.md for GitHub Repo

**What:** Created a comprehensive `README.md` with quick start, API reference, email types table, architecture diagram, test instructions, and CI/CD info.

**Why:** The GitHub repo looked bare without a README. A good README is essential for any open-source project and helps future contributors (or the user's other AI sessions) understand the project quickly.

**Was it correct? Yes.** Covers all essential sections without being bloated.

---

*This document will be appended after each major decision during implementation.*
