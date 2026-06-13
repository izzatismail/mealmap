# AGENTS.md — Recipe Planner Project Reference

> ⚠️ **AI AGENT INSTRUCTION:** Read this file at the start of EVERY session before writing any code,
> suggesting any architecture, or making any decisions. All decisions made here are final unless
> the developer explicitly overrides them with a reason. Do not deviate from the agreed stack,
> structure, or security rules without confirmation.

---

## 1. Project Overview

**App:** Recipe / Meal Planner
**Purpose:** Browse recipes, plan weekly meals, auto-generate shopping lists, track pantry inventory.
**Developer:** Solo project. Personal use. Portfolio-focused.
**Stage:** MVP

---

## 2. Agreed Tech Stack

| Layer | Technology | Notes |
|-------|-----------|-------|
| Backend | Spring Boot 3.x (Kotlin or Java) | REST API |
| Database | PostgreSQL | Local + Railway (later) |
| Mobile | Kotlin Multiplatform (KMP) | Shared logic |
| Android UI | Jetpack Compose | Native |
| iOS UI | SwiftUI | Native |
| Local HTTP | Ktor Client | In KMP shared module |
| Serialization | Kotlinx Serialization | In KMP shared module |
| Local DB | Room (Android) / SQLite (iOS) | Caching |
| Image Loading | Coil (Android) | Recipe images |
| Auth | Spring Security + JWT | Backend |
| Recipe Data | Spoonacular API | 150 req/day free tier |
| Containerization | Docker + docker-compose | Local dev |
| CI/CD | GitHub Actions | Build + test only for now |
| Deployment | Railway | Deferred to post-MVP |

---

## 3. Non-Negotiable Security Rules

> ⛔ **AI AGENT:** These are hard rules. Never suggest, write, or generate code that violates
> any of the following. If a feature request conflicts with these rules, flag it immediately
> before proceeding.

### 3.1 Secrets & Credentials

- **NEVER** hardcode API keys, passwords, or secrets in any source file
- **NEVER** commit `.env` files — always add to `.gitignore`
- **ALWAYS** use environment variables for all secrets
- **ALWAYS** reference Spoonacular API key as `${SPOONACULAR_API_KEY}` in config
- **ALWAYS** reference DB password as `${DB_PASSWORD}` in config
- GitHub Actions secrets must use `${{ secrets.SECRET_NAME }}` syntax — never inline values
- Railway environment variables must be set in dashboard — never in code

```yaml
# ✅ CORRECT - application.yml
spoonacular:
  api:
    key: ${SPOONACULAR_API_KEY}

spring:
  datasource:
    password: ${DB_PASSWORD}

# ❌ NEVER DO THIS
spoonacular:
  api:
    key: abc123myrealapikey
```

### 3.2 Authentication & Authorization

- **ALWAYS** use JWT for user authentication — never session cookies
- **ALWAYS** validate JWT on every protected endpoint
- **ALWAYS** use Spring Security's `BCryptPasswordEncoder` for password hashing
- **NEVER** store plain text passwords — not even temporarily
- **NEVER** return passwords or sensitive fields in API responses
- **ALWAYS** apply `@JsonIgnore` or exclude sensitive fields from DTOs
- JWT secret must be stored as environment variable `${JWT_SECRET}` — minimum 256-bit key

```java
// ✅ CORRECT - Always hash passwords
passwordEncoder.encode(rawPassword);

// ❌ NEVER store raw passwords
user.setPassword(rawPassword);
```

### 3.3 API Security

- **ALWAYS** validate and sanitize all incoming request parameters
- **ALWAYS** use `@Valid` and `@Validated` on controller method parameters
- **ALWAYS** return proper HTTP status codes (401 Unauthorized, 403 Forbidden, 404 Not Found)
- **NEVER** expose internal stack traces in API error responses
- **ALWAYS** use a global exception handler (`@ControllerAdvice`) for consistent error responses
- **ALWAYS** apply CORS configuration explicitly — never use wildcard `*` in production
- Rate-limit Spoonacular calls — never call API in a loop without throttle guard

```java
// ✅ CORRECT - Use validation annotations
@PostMapping("/register")
public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) { ... }

// ✅ CORRECT - Global exception handler
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handle(Exception ex) {
        // Return safe error message, not stack trace
    }
}
```

### 3.4 Database Security

- **ALWAYS** use Spring Data JPA parameterized queries — never raw string concatenation in queries
- **NEVER** use `ddl-auto: create-drop` or `ddl-auto: create` outside of test profiles
- Use `ddl-auto: update` for local dev, `ddl-auto: validate` for production
- **ALWAYS** use the least-privilege database user for the app connection
- **NEVER** expose the database port publicly in production (Railway internal only)

```java
// ✅ CORRECT - Parameterized JPA query
@Query("SELECT r FROM Recipe r WHERE r.title LIKE %:title%")
List<Recipe> findByTitle(@Param("title") String title);

// ❌ NEVER do string concatenation in queries
"SELECT * FROM recipes WHERE title = '" + title + "'"
```

### 3.5 Docker & Environment Security

- **NEVER** bake API keys or secrets into Docker images
- **ALWAYS** pass secrets as environment variables at runtime via `docker-compose.yml`
- **NEVER** run containers as root — use a non-root user in Dockerfile
- `.env` file for docker-compose is for local dev only — must be in `.gitignore`
- **ALWAYS** use multi-stage Docker builds to minimize image size and attack surface

```dockerfile
# ✅ CORRECT - Non-root user in Dockerfile
FROM eclipse-temurin:21-jre
RUN addgroup --system appgroup && adduser --system appuser --ingroup appgroup
USER appuser
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 3.6 KMP Mobile Security

- **NEVER** hardcode backend base URL — use a config file or build config
- **NEVER** store JWT tokens in plain SharedPreferences — use Android Keystore / iOS Keychain
- **ALWAYS** use HTTPS for all API calls — never HTTP in any environment
- **NEVER** log sensitive data (tokens, passwords, personal info) in Logcat/console
- **ALWAYS** obfuscate release builds (ProGuard/R8 for Android)

```kotlin
// ✅ CORRECT - Store token securely
// Android: EncryptedSharedPreferences or Keystore
// iOS: Keychain

// ❌ NEVER do this
sharedPreferences.putString("jwt_token", token)
```

### 3.7 .gitignore Minimum Requirements

The following must always be present in `.gitignore`:

```
# Secrets
.env
*.env
application-prod.yml
secrets.properties

# Build outputs
build/
.gradle/
*.jar
*.class

# IDE
.idea/
*.iml
.DS_Store

# Docker
docker-compose.override.yml
```

---

## 4. Project Structure

```
recipe-planner/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── kotlin/com/recipeplanner/
│   │   │   │   ├── controller/        # REST controllers
│   │   │   │   ├── service/           # Business logic
│   │   │   │   ├── entity/            # JPA entities
│   │   │   │   ├── dto/               # Request/response models
│   │   │   │   ├── repository/        # JPA repositories
│   │   │   │   ├── config/            # Security, CORS, beans
│   │   │   │   └── exception/         # Global exception handler
│   │   │   └── resources/
│   │   │       ├── application.yml    # Default config (env vars only)
│   │   │       └── application-test.yml
│   │   └── test/
│   ├── build.gradle
│   └── Dockerfile
│
├── mobile/
│   ├── shared/
│   │   └── src/commonMain/kotlin/
│   │       ├── models/                # Shared data models
│   │       ├── api/                   # Ktor HTTP client
│   │       ├── repository/            # Shared repositories
│   │       └── viewmodel/             # Shared ViewModels
│   ├── androidApp/
│   │   └── src/main/kotlin/
│   │       ├── screens/               # Compose UI screens
│   │       └── ui/                    # UI components
│   └── iosApp/                        # SwiftUI
│
├── docker-compose.yml
├── .env.example                       # Template only, no real values
├── .gitignore
├── AGENTS.md                          # This file
└── README.md
```

---

## 5. Architecture Decisions

| Decision | Choice | Reason |
|----------|--------|--------|
| Environments | Single env for MVP | Solo project, no real users yet |
| Staging/Prod split | Deferred post-MVP | Adds complexity with no benefit now |
| Caching strategy | Save Spoonacular results to PostgreSQL | Avoid hitting 150 req/day limit |
| Deployment platform | Railway | Simple, free tier, PostgreSQL included, Docker support |
| CI/CD scope | Build + test only (no deploy yet) | Deploy manually when ready |
| Docker scope | Local dev only for now | Deploy to Railway later |
| ORM | Spring Data JPA | Standard, type-safe, parameterized queries |

---

## 6. Spoonacular API Usage Rules

- **Rate limit:** 150 requests/day on free tier
- **Always cache** responses to PostgreSQL — never fetch the same recipe ID twice
- Check local DB before calling Spoonacular API
- Log every API call for debugging quota usage
- If quota is hit, return cached data gracefully — never crash
- Endpoints in use:
  - `GET /recipes/complexSearch` — search by query
  - `GET /recipes/{id}/information` — recipe details
  - `GET /recipes/findByIngredients` — pantry-based search

---

## 7. Phase Checkpoints

> ⚠️ **AI AGENT:** Check the current phase at the start of each session.
> Ask the developer which phase they are on if unclear.
> Do not suggest features or tasks from future phases unless asked.

---

### ✅ Phase 1 — Backend Foundation (Weeks 1-2)

**Goal:** Working Spring Boot API with Spoonacular integration

- [x] Spring Boot project initialized
- [ ] PostgreSQL connected locally
- [x] `application.yml` using environment variables only
- [ ] `Recipe` entity + `RecipeDto` created
- [ ] `SpoonacularService` — search, details, by-ingredients
- [ ] `RecipeRepository` (JPA)
- [ ] `RecipeController` with endpoints:
  - `GET /api/recipes/search?query=&limit=`
  - `GET /api/recipes/{id}`
  - `GET /api/recipes/by-ingredients?ingredients=`
  - `GET /api/recipes/cached`
- [ ] Caching logic (save to DB on first fetch)
- [ ] `GlobalExceptionHandler` in place
- [ ] API tested with Postman or curl
- [ ] Unit tests for `SpoonacularService`

**Phase 1 Complete When:** API returns recipe data, results are cached in PostgreSQL, no secrets in code

---

### ✅ Phase 2 — Database & Core Services (Week 2 continued)

**Goal:** Full data model + shopping list algorithm

- [ ] `User` entity (no plain text passwords)
- [ ] `MealPlan` entity
- [ ] `PlannedMeal` entity
- [ ] `ShoppingList` entity
- [ ] `ShoppingItem` entity
- [ ] `Ingredient` entity
- [ ] `ShoppingListGeneratorService` (combine + normalize ingredients from meal plan)
- [ ] `PantryItem` entity + service
- [ ] All relationships defined (OneToMany, ManyToMany)
- [ ] `ddl-auto: update` confirmed for local dev
- [ ] Unit tests for `ShoppingListGeneratorService`

**Phase 2 Complete When:** Full data model migrated, shopping list generates from a mock meal plan

---

### ✅ Phase 3 — Docker & CI/CD (Week 3)

**Goal:** Everything runs in Docker locally + CI validates builds

- [x] `Dockerfile` (multi-stage, non-root user)
- [x] `docker-compose.yml` with:
  - PostgreSQL service with health check
  - Backend service with env vars (no hardcoded secrets)
  - Volume for PostgreSQL data
- [x] `.env.example` committed (no `.env`)
- [x] `.gitignore` covers all secrets and build outputs
- [ ] `docker-compose up` runs successfully
- [ ] API reachable at `localhost:8080` via Docker
- [ ] `.github/workflows/build.yml` created:
  - Triggers on push to `main` and `develop`
  - Runs `./gradlew test`
  - Builds Docker image
  - Verifies build success (no push yet)
- [ ] GitHub Actions pipeline passes

**Phase 3 Complete When:** `docker-compose up` works, CI passes on every push

---

### ✅ Phase 4 — Mobile Foundation (Weeks 4-5)

**Goal:** Both platforms browse recipes from backend API

- [ ] KMP project initialized
- [ ] Shared module structure set up
- [ ] Shared: `Recipe`, `MealPlan`, `ShoppingItem` models
- [ ] Shared: Ktor Client configured (HTTPS only)
- [ ] Shared: `RecipeRepository` (network + local cache)
- [ ] Shared: `RecipeViewModel`
- [ ] Android: Recipe list screen (Compose)
- [ ] Android: Recipe detail screen (Compose)
- [ ] Android: Navigation
- [ ] Android: Coil image loading
- [ ] Android: Room local caching
- [ ] iOS: Mirror screens in SwiftUI
- [ ] iOS: Local SQLite caching
- [ ] JWT token stored securely (Keystore / Keychain)
- [ ] Base URL in build config (not hardcoded)
- [ ] No sensitive data in logs

**Phase 4 Complete When:** Both platforms fetch and display real recipe data

---

### ✅ Phase 5 — Complete MVP Features (Weeks 6-7)

**Goal:** Full working MVP — auth, meal planning, shopping lists, pantry

**Backend:**
- [ ] User registration + login (JWT, BCrypt passwords)
- [ ] Protected endpoints with Spring Security
- [ ] Favorite recipes endpoints
- [ ] Meal plan CRUD
- [ ] Shopping list generation endpoint
- [ ] Pantry management CRUD
- [ ] CORS configured explicitly (no wildcard)
- [ ] Integration tests for auth endpoints

**Mobile:**
- [ ] Login / Registration screens
- [ ] Secure token storage + refresh logic
- [ ] Save/unsave favorite recipes
- [ ] Weekly meal planner UI
- [ ] Shopping list screen (check off items)
- [ ] Pantry tracker screen

**Phase 5 Complete When:** Full end-to-end flow works — login → browse → plan → generate shopping list

---

### ✅ Phase 6 — Polish & Deploy (Week 8+)

**Goal:** Live app + documented repo

- [ ] Unit + integration tests passing
- [ ] README with setup instructions
- [ ] API documentation (Swagger/OpenAPI optional)
- [ ] Architecture diagram
- [ ] Railway account set up
- [ ] PostgreSQL deployed to Railway
- [ ] Spring Boot deployed to Railway
- [ ] Environment variables set in Railway dashboard
- [ ] CI/CD `deploy.yml` added to GitHub Actions (deploy on merge to `main`)
- [ ] Production `ddl-auto` set to `validate`
- [ ] No debug logs in production
- [ ] ProGuard/R8 enabled for Android release build

**Phase 6 Complete When:** App is live, accessible via public URL, CI auto-deploys on push to main

---

## 8. Key Interview Talking Points

**On Architecture:**
> "Spring Boot REST API with PostgreSQL for the backend. KMP shares business logic (models, API client, ViewModels) across iOS and Android while keeping native UI with Compose and SwiftUI."

**On Spoonacular Rate Limiting:**
> "150 requests/day on the free tier. Backend caches all results in PostgreSQL so the same recipe is never fetched twice. For production, I'd add request throttling and upgrade to a paid plan as usage grows."

**On CI/CD:**
> "GitHub Actions runs tests and builds the Docker image on every commit. This ensures the build is always valid. Deployment to Railway is automated on merge to main."

**On Environments:**
> "For MVP I kept it simple — one environment locally with docker-compose, one production on Railway. I'd add staging once there are real users and the risk of breaking production becomes real."

**On Security:**
> "All secrets are environment variables — never in source code. Passwords are BCrypt hashed. JWT protects all user endpoints. Docker containers run as non-root. Sensitive fields are excluded from API responses."

---

## 9. What NOT to Do (Common Pitfalls)

> ⚠️ **AI AGENT:** Flag these immediately if spotted in any code review or suggestion.

| ❌ Never | ✅ Instead |
|----------|-----------|
| Hardcode API keys | Use `${ENV_VAR}` |
| Store plain text passwords | Use BCryptPasswordEncoder |
| Return stack traces in API errors | Use GlobalExceptionHandler |
| Use `SELECT *` with raw string concat | Use JPA parameterized queries |
| `ddl-auto: create-drop` in dev/prod | `update` for dev, `validate` for prod |
| Wildcard CORS in production | Explicit allowed origins |
| Log JWT tokens or passwords | Never log sensitive data |
| Bake secrets into Docker image | Pass secrets as runtime env vars |
| Store JWT in plain SharedPreferences | Use Keystore / Keychain |
| Call Spoonacular without checking cache | Always check DB first |
| Skip `@Valid` on controller inputs | Always validate request bodies |
| Push `.env` to GitHub | `.gitignore` + `.env.example` |

---

## 10. Git Workflow & Conventions

### 10.1 Branch Strategy

```
main            ← Production-ready (only developer merges here)
develop         ← Integration branch
  ├── phase-<n>/<slug>     e.g. phase-1/recipe-entity
  ├── feature/<slug>       e.g. feature/meal-plan-crud
  ├── fix/<slug>           e.g. fix/shopping-list-dedup
  └── chore/<slug>         e.g. chore/update-deps
```

- `main` is protected — no direct pushes ever
- `develop` is the integration branch — AI Agents must not push here
- All work flows: feature branch → PR into `develop` → developer merges to `main`
- Every phase and every big change gets its own branch

### 10.2 Commit Message Format

```
<type>(<scope>): <description>
```

| Type | Usage |
|------|-------|
| `feat` | New feature |
| `fix` | Bug fix |
| `chore` | Maintenance, deps, tooling |
| `docs` | Documentation (AGENTS.md, README) |
| `refactor` | Restructuring code |
| `test` | Adding/fixing tests |

`scope` is optional — examples: `api`, `entity`, `docker`, `agents`, `auth`

Examples:
```
feat(api): add recipe search endpoint
feat(entity): create Recipe entity with DTO mapping
chore(docker): update PostgreSQL to 15-alpine
docs(agents): add branch and commit conventions
```

### 10.3 AI Agent Rules

- ⛔ **NEVER** commit or push directly to `main` or `develop`
- ⛔ **NEVER** open PRs targeting `main` or `develop`
- ✅ **ALWAYS** create a feature/phase/fix branch and push there
- ✅ **ALWAYS** let the developer review and merge to `develop`/`main`

---

## 11. Session Start Checklist for AI Agent

At the start of every session, confirm:

1. **Which phase** is the developer currently working on?
2. **What was completed** last session?
3. **What is the goal** of this session?
4. **Are all security rules** being followed in the code we are about to write?
5. **Is the current task within scope** of the active phase?

If any of the above is unclear — **ask before writing code.**

---

*Last updated: Jun 13, 2026 — Phase 1 scaffolding complete, git workflow added.*
*Stack, phases, and security rules are agreed and locked for MVP.*