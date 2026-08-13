# Famous Quote Quiz — Implementation Plan

**Date:** 2026-08-12  
**Status:** Execution-ready  
**Primary target:** Android API 26+  
**Bonus targets:** Desktop JVM, iOS 15+, optional Web/Wasm

## Goal

Build a polished Kotlin full-stack implementation of the Famous Quote Quiz assignment using a Spring Boot Kotlin backend
and a Kotlin Multiplatform client with Compose Multiplatform shared UI.

The implementation must optimize for:

1. Exact assignment compliance
2. Android quality first
3. Clear Kotlin Multiplatform boundaries
4. Shared Compose Multiplatform UI
5. Simple, idiomatic Spring Boot backend
6. Testability
7. Reviewer-friendly setup
8. Deliberate, proportionate architecture
9. Clean Git history
10. Bonus platform support only after the required product is stable

Do not overengineer the assignment.

## Progress

- Task 1 — Repository and build scaffolding: `Completed`
- Task 2 — Shared API contract: `Completed`
- Task 3 — Server persistence and seed data: `Completed`
- Task 4 — Server authentication and profile: `Completed`
- Task 5 — Server quiz engine and API: `Completed`
- Task 6 — Client core infrastructure: `Completed`
- Task 7 — App shell and Navigation 3: `Completed`
- Task 8 — Login feature: `Completed`
- Task 9 — Quiz feature: `Completed`
- Task 10 — Settings and Profile: `Pending`
- Task 11 — Android polish and compliance hardening: `Pending`
- Task 12 — Comprehensive test hardening: `Pending`
- Task 13 — Bonus platforms: `Pending`
- Task 14 — CI, Docker, and documentation: `Pending`
- Task 15 — Optional deployment/showcase: `Pending`
- Task 16 — Final assignment compliance audit: `Pending`

---

# 1. Acceptance Criteria

The following are mandatory unless explicitly marked bonus.

## 1.1 Startup

- Show Splash while initial session state is resolved.
- No fake splash delay.
- If no persisted valid session exists -> Login.
- If a persisted valid session exists -> Main.
- Successful login persists session.
- Logout clears session and returns to Login.
- Reopening after logout must require login.

## 1.2 Login

Fields:

- Email
- Password
- Login button

Behavior:

- Inputs have purpose-appropriate hints.
- Empty email shows error below email field.
- Empty password shows error below password field.
- Errors hidden initially.
- Error color exactly `#FF0000`.
- Editing a field clears that field's validation error.
- Backend authentication failure produces a clear user-facing error.
- Prevent duplicate login submission while login is running.
- Successful login transitions root session state to authenticated.
- Login must not remain reachable through Back after successful authentication.

## 1.3 Main tabs

Provide:

1. Quiz
2. Settings
3. Profile

Phone form factor is primary.

Use bottom navigation unless implementation evidence strongly favors another phone-appropriate tab control.

## 1.4 Quiz session

Each session:

- Contains exactly 10 questions.
- Uses currently selected mode.
- Tracks current progress.
- Tracks score.
- Does not score the same question twice; duplicate submissions are treated as idempotent retries.
- Shows useful progress such as `Question 3 of 10`.

### Binary mode

Default mode.

Display:

- Quote
- Proposed author
- Yes
- No

Backend determines whether the proposed author is correct.

### Multiple-choice mode

Display:

- Quote
- Exactly three author choices
- Exactly one correct author

The backend generates the options and distractors.

## 1.5 Feedback dialog

After backend answer verification:

Correct:

`Correct! The right answer is: {author}`

Incorrect:

`Sorry, you are wrong! The right answer is: {author}`

Requirements:

- Display as modal dialog.
- Do not advance before `OK`.
- Pressing `OK` advances to the next pre-generated question.
- Feedback-dialog state belongs in `QuizUiState` so configuration change/rotation does not lose workflow state.

## 1.6 Results

After question 10:

- Show results/statistics.
- Show score.
- Show percentage.
- Show correct count.
- Show incorrect count.
- Show quiz mode.
- Include `Start again`.
- Starting again creates a fresh 10-question session.

## 1.7 Settings

Provide one mode-switching control:

- Binary
- Multiple Choice

Binary is default.

Changing mode:

- updates selected mode
- restarts the active quiz session
- creates a new server session in the newly selected mode

## 1.8 Profile

Read-only profile.

Display at least:

- Name
- Email

Data comes from authenticated backend identity.

Include Logout.

Logout:

- clears persisted token/session
- clears in-memory quiz state
- moves root session state to unauthenticated
- makes Login the active flow

## 1.9 Orientation

Android required:

- Portrait
- Landscape

Primary screens must remain usable in both.

Avoid fixed layouts that clip content.

---

# 2. Platform Priority

## Tier 1 — Required

Android API 26+

Must be complete, polished, tested, and reviewer-ready.

## Tier 2 — Bonus

Desktop JVM.

Use the same shared Compose UI and business logic.

## Tier 3 — Bonus

iOS 15+.

Use the shared Compose UI. Do not build a duplicate SwiftUI product.

## Tier 4 — Optional showcase

Web/Wasm.

Only attempt after required Android and higher-value bonus work are stable.

Web must never become a blocker for submission.

## Platform architecture rule

All client platforms consume the same shared `:app:shared` KMP
module.

Platform application modules are thin entry points:

- `androidApp` -> shared `shared`
- `desktopApp` -> shared `shared`
- `iosApp` -> shared `shared`

Platform entry-point modules must not contain duplicate feature
implementations.

Android is packaged exclusively by `androidApp`; `shared` is a KMP
library, not an Android application.

---

# 3. Repository Structure

Use a Gradle monorepo with explicit platform entry-point modules.

Target structure:

```text
quote-quiz/
├── AGENTS.md
├── .github/
│   └── workflows/
│       └── ci.yml
├── api-contract/
│   └── src/commonMain/
├── build-logic/
│   └── convention/                  # only if genuinely reducing duplication
├── client/
│   ├── shared/
│   │   └── src/
│   │       ├── commonMain/
│   │       ├── commonTest/
│   │       ├── androidMain/
│   │       ├── iosMain/
│   │       ├── jvmMain/
│   │       └── wasmJsMain/          # only when enabled
│   ├── androidApp/
│   │   └── src/
│   │       └── main/
│   └── iosApp/
├── server/
│   └── src/
│       ├── main/kotlin/
│       ├── main/resources/
│       └── test/kotlin/
├── docs/
│   ├── assignment/
│   ├── decisions/
│   └── plans/
├── gradle/
│   └── libs.versions.toml
├── Dockerfile
├── docker-compose.yml
├── .env.example
├── .gitignore
├── README.md
├── build.gradle.kts
└── settings.gradle.kts
```

Guidance:

- Do not create a Gradle module per feature.
- Package-level feature boundaries are sufficient for this assignment.
- `build-logic` is optional and must remain minimal.
- Do not create agent-framework files beyond what has a concrete purpose.

## 3.1 Module responsibilities

### `:api-contract`

Kotlin Multiplatform library containing only shared REST wire contracts.

Consumed by:

- `:server`
- `:app:shared`

It must not contain client or server business logic.

### `:server`

Standalone Spring Boot JVM application.

Contains:

- REST controllers
- services
- Spring Security
- persistence
- quiz generation
- authentication
- database entities

The server is not a KMP application target.

### `:app:shared`

The shared Kotlin Multiplatform client application library.

This is NOT an Android application module.

It owns the shared client implementation:

- Compose Multiplatform UI
- application shell
- Navigation 3
- shared ViewModels
- repositories
- Ktor Client APIs
- session management
- settings
- dependency injection
- shared business logic

It exposes the shared Compose application entry point used by platform launchers.

Example conceptual API:

@Composable fun App ()

Because all client platforms use Compose Multiplatform, shared business logic and shared UI remain together in this
single KMP module.

Do not create separate `sharedLogic` and `sharedUI` modules for this assignment.

### `:app:androidApp`

Standalone Android APPLICATION module.

Uses the Android application plugin and owns Android application packaging and the Android entry point.

Contains Android-specific application concerns such as:

- AndroidManifest.xml
- MainActivity
- Android application configuration
- application ID
- Android build configuration
- Android packaging

Depends on:

`:app:shared`

Its `MainActivity` should remain intentionally thin and render the shared Compose application.

Conceptually:

setContent { App ()
}

### `:app:desktopApp`

Thin JVM Desktop application launcher.

Depends on:

`:app:shared`

It owns only Desktop application entry-point and packaging concerns.

The application UI and business logic remain in `shared`.

### `:app:iosApp`

iOS/Xcode application consuming the framework produced from the shared KMP client.

The iOS application should host the shared Compose Multiplatform UI rather than reimplementing the product in SwiftUI.

iOS-specific bootstrap code should remain thin.

## 3.2 AGP 9+ module rule

The project must use the modern AGP 9+ compatible KMP structure.

`:app:shared` must use:

- `org.jetbrains.kotlin.multiplatform`
- `com.android.kotlin.multiplatform.library`
- Compose Multiplatform
- Compose compiler plugin

It must NOT apply:

- `com.android.application`
- legacy `com.android.library`

`:app:androidApp` is the standalone Android application module and owns Android application packaging.

Do not apply the Kotlin Multiplatform plugin and Android application plugin to the same Gradle module.

Do not enable legacy AGP variant APIs as a workaround.

Use the current Android-KMP library DSL supported by the selected stable AGP version rather than copying deprecated
`androidLibrary {}` syntax from older examples.

## 3.3 Module design rule

Do not create a Gradle module per feature.

Feature boundaries inside `shared` should initially be package-level.

Do not create additional modules unless there is a concrete technical boundary that justifies them.

The desired dependency direction is:

                 api-contract
                  /         \
                 /           \
              server      shared
                              ↑
                    ┌─────────┼─────────┐
                    │         │         │
                androidApp desktopApp iosApp

Platform entry-point modules depend on the shared client module.

The shared client module must never depend on platform application modules.

---

# 4. Version Policy

At implementation start:

- Use latest compatible stable Kotlin.
- Use Spring Boot 4.1.x stable.
- Use latest compatible stable Compose Multiplatform.
- Use latest compatible stable Ktor Client.
- Use latest compatible stable Navigation 3.
- Prefer stable dependencies over RCs unless a concrete blocker requires otherwise.
- Record deliberate non-stable dependency choices in README/architecture notes.

Do not novelty-chase.

---

# 5. Shared API Contract

Create `:api-contract`.

Use `kotlinx.serialization`.

It contains only shared wire models.

Suggested DTOs:

```text
auth/
├── LoginRequest.kt
├── LoginResponse.kt
└── UserDto.kt

quiz/
├── QuizMode.kt
├── QuizSessionStartRequest.kt
├── QuizSessionDto.kt
├── QuizQuestionDto.kt
├── QuizOptionDto.kt
├── SubmitAnswerRequest.kt
├── SubmitAnswerResponse.kt
└── QuizResultDto.kt
```

Rules:

- Use `@Serializable`.
- Do not expose JPA entities.
- Do not put repositories/services/ViewModels/client domain logic in this module.
- Keep transport DTOs explicit even when they resemble client/server domain models.

---

# 6. Backend

## 6.1 Technology

Use:

- Spring Boot 4.1.x
- Kotlin
- Java 21 toolchain
- Spring MVC
- Spring Security
- Spring Data JPA
- H2
- Kotlin Serialization
- BCrypt
- Gradle Kotlin DSL

Do not use WebFlux/R2DBC for this assignment.

Blocking JPA remains blocking. Do not add `suspend` merely to imply reactive persistence.

## 6.2 Package structure

Prefer feature-oriented packages:

```text
com.example.quotequiz
├── auth/
│   ├── AuthController.kt
│   ├── AuthService.kt
│   ├── JwtTokenService.kt
│   └── SecurityConfig.kt
├── user/
│   ├── User.kt
│   ├── UserRepository.kt
│   └── UserController.kt
├── quote/
│   ├── Quote.kt
│   └── QuoteRepository.kt
├── quiz/
│   ├── QuizSession.kt
│   ├── QuizQuestion.kt
│   ├── QuizAnswer.kt
│   ├── QuizController.kt
│   ├── QuizService.kt
│   └── QuizGenerator.kt
├── config/
│   ├── DataSeeder.kt
│   └── AppProperties.kt
└── QuoteQuizApplication.kt
```

Rules:

- Controllers are thin.
- Services own business/application logic.
- Repositories own persistence.
- Entities do not cross the HTTP boundary.
- Constructor injection only.
- Avoid interfaces that provide no real seam or boundary.

## 6.3 Database

### User

Fields:

- id
- name
- normalized unique email
- passwordHash

Requirements:

- Passwords stored only as BCrypt hashes.
- Seed one or more demo users.
- Document demo credentials.

### Quote

Fields:

- id
- text
- author

Seed at least 20–30 quotes so ten-question sessions and distractors have enough variety.

### Quiz session

Persist enough server-authoritative state to support:

- session ID
- user ID
- mode
- generated ten-question set
- correct answer metadata
- answered status per question
- correct count
- completion state

Do not expose correct-answer metadata in session-creation responses.

Use stable ID generation such as `kotlin.uuid.Uuid` where compatible and clear.

---

# 7. Authentication

Keep assignment authentication intentionally simple.

## 7.1 Login

`POST /api/v1/auth/login`

Request:

```json
{
  "email": "demo@example.com",
  "password": "password"
}
```

Response:

```json
{
  "token": "<jwt>",
  "user": {
    "id": 1,
    "name": "Demo User",
    "email": "demo@example.com"
  }
}
```

Use:

- BCrypt password verification
- JWT bearer token

## 7.2 Protected profile

`GET /api/v1/me`

Requires:

`Authorization: Bearer <token>`

Return authenticated server identity.

## 7.3 Logout

Client logout is sufficient for assignment scope:

- clear local token
- clear session state
- return to Login

Server-side token revocation is not required.

## 7.4 Explicit exclusions

Do not implement unless requirements change:

- refresh tokens
- token rotation
- static client API key/header
- OAuth/OIDC
- Redis session stores

Production improvements may be documented in README.

---

# 8. Quiz API

The backend is authoritative for quiz generation and answer verification.

## 8.1 Create session

`POST /api/v1/quiz/sessions`

Request:

```json
{
  "mode": "BINARY"
}
```

Response contains:

- session ID
- mode
- totalQuestions = 10
- answered = 0
- correctAnswers = 0
- completed = false
- all ten generated questions

Correct answers must not be revealed.

### Binary question

Contains:

- question ID
- quote
- proposedAuthor

Must not contain a boolean indicating correctness.

### Multiple-choice question

Contains:

- question ID
- quote
- exactly three distinct author options

Must not mark the correct choice.

## 8.2 Submit answer

`POST /api/v1/quiz/sessions/{sessionId}/questions/{questionId}/answer`

Binary request:

```json
{
  "binaryAnswer": true
}
```

Multiple-choice request:

```json
{
  "selectedAuthorId": "..."
}
```

Response:

```json
{
  "correct": true,
  "correctAuthor": "Albert Einstein",
  "answered": 3,
  "correctAnswers": 2,
  "totalQuestions": 10,
  "completed": false
}
```

Final answer response may also include final result summary.

Server rules:

- treat duplicate answers as idempotent retries and return the stored accepted result
- ensure question belongs to session
- ensure session belongs to authenticated user
- ensure selected option belongs to the generated question
- derive correctness from authoritative session data

## 8.3 Generation invariants

Session:

- exactly 10 questions
- unique quotes within one session

Binary:

- proposed author correct approximately half the time
- incorrect proposed author must differ from real author

Multiple choice:

- exactly 3 options
- all options distinct
- exactly 1 correct author
- two incorrect authors
- options shuffled

Unit-test these invariants.

---

# 9. Client Architecture

Use pragmatic unidirectional state flow.

```text
UI
  -> UiAction
ViewModel
  -> repository/API
  -> state update
UI observes StateFlow<UiState>
```

Use shared ViewModels in `commonMain`.

Preferred conventions:

- immutable `UiState`
- sealed `UiAction` when useful
- `onAction(action)`
- optional `UiEffect` for genuine one-shot commands

Durable workflow state belongs in `UiState`.

Examples:

State:

- authenticated/unauthenticated
- current quiz question
- score/progress
- feedback dialog visible
- final results

Effect:

- snackbar
- open external link

Do not use one-shot effects for durable navigation/session state.

---

# 10. Client Packages

Suggested package layout:

```text
commonMain/
├── app/
│   ├── App.kt
│   ├── AppViewModel.kt
│   ├── navigation/
│   └── theme/
├── core/
│   ├── network/
│   ├── storage/
│   ├── session/
│   ├── result/
│   └── ui/
├── auth/
│   ├── data/
│   ├── domain/
│   └── presentation/
├── quiz/
│   ├── data/
│   ├── domain/
│   └── presentation/
├── settings/
│   ├── data/
│   └── presentation/
└── profile/
    ├── data/
    └── presentation/
```

Do not create feature Gradle modules.

---

# 11. Session and Persistence

Use Multiplatform Settings behind small abstractions.

Required persistence:

- JWT token
- selected quiz mode

Optional:

- lightweight cached user summary if useful

Suggested flow:

```text
Multiplatform Settings
    -> TokenStorage / SettingsStorage
    -> SessionRepository
    -> StateFlow<SessionState>
    -> App root
```

`SessionState`:

- Loading
- Unauthenticated
- Authenticated

Startup:

```text
Splash
  -> read persisted token
  -> no token => Unauthenticated
  -> token => validate with GET /me
      -> success => Authenticated
      -> 401 => clear token => Unauthenticated
```

Do not add Room for these values.

---

# 12. Networking

Use Ktor Client in `commonMain`.

Install as appropriate:

- ContentNegotiation
- kotlinx.serialization
- HttpTimeout
- Logging in development
- default request configuration
- bearer authentication/token attachment

Networking boundaries:

- `AuthApi`
- `QuizApi`
- `ProfileApi`

Do not expose `HttpClient` to ViewModels.

Do not leak raw Ktor exceptions into presentation state.

Map transport failures into a small application error model.

Example:

```kotlin
sealed interface AppError {
    data object Network : AppError
    data object Unauthorized : AppError
    data class Server(val message: String) : AppError
    data class Unknown(val cause: Throwable?) : AppError
}
```

On terminal `401`:

- clear local session
- emit `SessionState.Unauthenticated`
- root app renders Login

No automatic refresh-token flow is required.

---

# 13. Dependency Injection

Use Koin pragmatically.

Suggested modules:

- coreModule
- authModule
- quizModule
- settingsModule
- profileModule

Inject:

- HttpClient/APIs
- repositories
- storage abstractions
- ViewModels

Do not inject trivial pure utilities.

Prefer constructor injection.

---

# 14. Navigation

Use Compose Multiplatform Navigation 3.

Root session-driven rendering:

```text
Loading         -> Splash
Unauthenticated -> Login
Authenticated   -> Main
```

Main contains:

- Quiz
- Settings
- Profile

Quiz contains:

- Active quiz
- Results

Navigation principles:

- ViewModels do not own NavController/Navigator.
- Auth navigation is state-driven.
- Login is not left in the authenticated back stack.
- Logout discards authenticated navigation state.
- Preserve tab state when useful.
- Multiple back stacks are optional if complexity exceeds product value.

Implement navigation foundation before most feature UI so later screens plug into the intended app shell rather than
requiring a late rewrite.

---

# 15. UI and Responsive Design

Use Material 3 and create a cohesive quote-focused visual identity.

Do not submit a generic tutorial-looking app.

Quiz hierarchy:

- app/top bar where appropriate
- progress text
- progress indicator
- quote card
- author prompt
- answer controls
- assignment-required modal feedback

Login:

- keyboard-aware
- correct IME actions
- password field behavior
- inline validation

Landscape:

- use width effectively
- use scrolling where needed
- consider two-pane quote/answers layout if it improves usability
- avoid fixed dimensions that clip

Accessibility:

- touch targets
- contrast
- semantic labels
- logical focus order
- errors communicated by text, not only color

---

# 16. Testing Strategy

Tests are written during feature tasks, not postponed until the end.

## Server

Test-first where valuable:

- auth success
- bad password
- unknown user
- BCrypt verification
- protected `/me`
- exactly ten quiz questions
- unique quotes
- binary correctness/distractor rules
- multiple-choice three-option invariant
- exactly one correct author
- duplicate-answer rejection
- session ownership
- final result calculation

Use JUnit 5, Spring Boot Test, and MockK only where mocking is appropriate.

Prefer real integration boundaries when they provide stronger confidence than mocking everything.

## Client shared tests

- login validation
- login loading/error/success state
- session restoration with no token
- session restoration with valid token
- invalid persisted token -> unauthenticated
- logout
- quiz session load
- correct answer
- incorrect answer
- feedback dialog visible before advance
- `OK` advances
- tenth answer -> results
- Start again
- mode change -> fresh session
- error + retry

Use Kotlin coroutine test utilities.

## Android UI tests

Prioritize a small set of high-value flows:

- login validation
- basic tab navigation
- quiz happy path
- critical rotation/state preservation where feasible

Do not optimize for coverage percentage alone.

---

# 17. CI/CD

Add GitHub Actions after core behavior is stable.

CI should run understandable checks such as:

- server tests
- shared client tests
- Android compile/build
- Android lint where configured
- formatting/static analysis
- secret scanning if lightweight

Optional:

- Desktop build
- Docker image build
- Web build if enabled

CI must remain readable.

---

# 18. Docker

Provide:

- `Dockerfile` for server
- `docker-compose.yml` for convenient local server startup

H2 is acceptable and keeps reviewer setup simple.

Document:

- direct Gradle server startup
- Docker startup
- Android emulator backend host mapping
- desktop/iOS simulator/local host configuration

Do not scatter environment URLs throughout code.

---

# 19. README

Final README must include:

- project overview
- assignment summary
- screenshots/GIF if available
- architecture diagram
- module layout
- technology stack
- demo credentials
- how to run server
- how to run Android
- bonus target status
- API endpoint summary
- test commands
- CI status
- Docker instructions
- design decisions/trade-offs
- intentionally omitted production features
- optional hosted demo URLs

Reviewer goal:

Within five minutes a reviewer should understand the project.

Within ten minutes a reviewer should be able to run the backend, launch Android, login, and play the quiz.

---

# 20. Git Strategy

Use small focused commits.

Suggested sequence:

- `chore: scaffold multiplatform monorepo`
- `feat(contract): add shared API contracts`
- `feat(server): add persistence and seed data`
- `feat(server): implement JWT authentication and BCrypt password verification`
- `feat(server): implement quiz session API`
- `feat(client): add networking and session infrastructure`
- `feat(client): add app shell and navigation`
- `feat(auth): implement login flow`
- `feat(quiz): implement quiz flow`
- `feat(settings): add quiz mode selection`
- `feat(profile): add profile and logout`
- `test: harden assignment-critical flows`
- `feat: add desktop support`
- `feat: add ios support`
- `ci: add automated verification`
- `docs: finalize reviewer documentation`

Do not create one giant final commit.

---

# 21. Task Execution Plan

Codex must execute one numbered task at a time.

## Task 1 — Repository and build scaffolding

Goal:

Create a modern AGP 9+ compatible Kotlin full-stack monorepo foundation with explicit platform application entry points.

### Required modules

Deliverables:

- Gradle wrapper
- root Gradle configuration
- version catalog
- `:api-contract`
- `:server`
- `:app:shared`
- `:app:androidApp`
- `:app:desktopApp`
- `:app:iosApp` - The iOS project may be represented by an Xcode application directory rather than a conventional Gradle application module
- required Android launcher/module arrangement
- iOS project skeleton if generated naturally by the selected KMP template
- minimal `.gitignore`

### `:app:shared`
Configure as the shared Kotlin Multiplatform application/UI library.

It must:

- use Kotlin Multiplatform
- use `com.android.kotlin.multiplatform.library`
- use Compose Multiplatform
- use the Compose compiler plugin
- contain `commonMain`
- contain `commonTest`
- configure Android as a KMP library target
- configure JVM Desktop
- configure required iOS targets
- expose the shared Compose application UI

It must NOT apply:

- `com.android.application`
- legacy `com.android.library`

It must NOT contain:

- Android `MainActivity`
- Android application ID
- Android application packaging configuration

### `:app:androidApp`

Configure as a standalone Android application module.

It must:

- use the Android application plugin
- depend on `:app:shared`
- own `AndroidManifest.xml`
- own `MainActivity`
- own application ID
- own Android application packaging
- target Android API 26+

`MainActivity` must remain a thin platform entry point that renders the
shared Compose application.

Do not apply the Kotlin Multiplatform plugin to this module.

Follow current AGP 9 built-in Kotlin requirements rather than copying
pre-AGP-9 Android Gradle configuration.

### `:app:desktopApp`

Create a thin JVM Desktop launcher.

It must:

- depend on `:app:shared`
- contain only Desktop entry-point/application packaging concerns
- render the shared Compose application

Do not duplicate screens or business logic.

### `:app:iosApp`

Prepare the iOS application to consume the shared KMP framework and host
the shared Compose UI.

Keep platform bootstrap code thin.

Do not create duplicate SwiftUI feature screens.

### `:api-contract`

Create a KMP library skeleton suitable for shared serializable REST
contracts.

### `:server`

Create a standalone Spring Boot Kotlin/JVM application skeleton.

The server must not be configured as a KMP target.

### Versioning

Use a root version catalog.

Select mutually compatible stable versions of:

- Gradle
- AGP 9+
- Kotlin
- Compose Multiplatform
- Spring Boot 4.1.x
- Ktor
- Koin
- kotlinx.serialization

Do not use deprecated AGP/KMP compatibility workarounds.

Verification:

1. Inspect the generated Gradle project and available tasks.
2. Verify `:api-contract` compiles.
3. Verify `:server` compiles/starts sufficiently to validate its skeleton.
4. Verify `:app:shared` compiles as a KMP library.
5. Verify `:app:androidApp` builds as the Android application.
6. Verify the Desktop launcher compiles/runs.
7. Verify Android application code depends on `shared`, not vice versa.
8. Verify `shared` does not apply `com.android.application`.
9. Verify no legacy AGP variant API workaround has been enabled.
10. Record the actual canonical Gradle verification commands for later
    tasks.

The repository foundation is not complete merely because Gradle sync
succeeds.

Run the Android application and Desktop application if the environment
supports them.

### Commit

`chore: scaffold multiplatform monorepo and platform entry points`

## Task 2 — Shared API contract

Goal:

Create serializable auth/profile/quiz wire models.

Tests:

Serialization smoke tests only if useful; compilation is the primary verification.

Verification:

- build contract module
- ensure both server and client can depend on it

Commit:

`feat(contract): add shared API contracts`

## Task 3 — Server persistence and seed data

Goal:

Create users, quotes, repositories, H2 setup, and startup seed data.

Tests first:

- quote seed/service assumptions where valuable
- email uniqueness/lookup behavior where valuable

Verification:

- server tests
- server starts successfully
- H2 schema/data initialization succeeds

Commit:

`feat(server): add persistence and seed data`

## Task 4 — Server authentication and profile

Goal:

Implement BCrypt login, JWT issuance, Spring Security, and `/me`.

Tests first:

- successful login
- bad password
- unknown user
- password hash verification
- protected `/me`
- authenticated `/me`

Verification:

- targeted auth tests
- full server test suite

Commit:

`feat(server): implement JWT authentication and profile API`

## Task 5 — Server quiz engine and API

Goal:

Implement server-authoritative ten-question sessions and answer verification.

Tests first:

- exactly 10 questions
- unique quotes
- binary invariants
- MCQ three-option invariant
- exactly one correct option
- duplicate-answer rejection
- session ownership
- final score/result

Verification:

- targeted quiz tests
- full server tests
- API smoke test

Commit:

`feat(server): implement quiz session and answer APIs`

## Task 6 — Client core infrastructure

Goal:

Implement:

- Ktor Client
- serialization
- API boundaries
- error mapping
- Koin
- Multiplatform Settings
- TokenStorage
- SessionRepository
- SessionState

Tests first:

- no-token startup
- valid-token startup
- invalid-token startup
- logout/session clear
- storage behavior

Verification:

- relevant common tests
- shared client compilation

Commit:

`feat(client): add networking and session infrastructure`

## Task 7 — App shell and Navigation 3

Goal:

Implement root session-driven rendering:

- Splash
- Login flow
- Main shell
- Quiz/Settings/Profile top-level navigation

No feature-heavy UI yet.

Tests where practical:

- app/session navigation state behavior
- authenticated vs unauthenticated root selection

Verification:

- Android build
- run app shell manually
- rotate basic screens

Commit:

`feat(client): add app shell and navigation`

## Task 8 — Login feature

Goal:

Implement complete assignment login behavior.

Tests first:

- empty email
- empty password
- both empty
- editing clears field error
- loading
- backend auth error
- successful login
- duplicate submission prevention

UI requirements:

- exact `#FF0000`
- inline errors
- hints
- keyboard behavior

Verification:

- common/ViewModel tests
- Android manual flow
- restart persistence test

Commit:

`feat(auth): implement login flow`

## Task 9 — Quiz feature

Goal:

Implement binary and multiple-choice quiz presentation and workflow.

Suggested execution breakdown:

### Task 9A — Quiz client data flow

Scope:

- wire client quiz API usage into a repository/service layer
- load/start a quiz session from the authenticated client
- define shared quiz UI state, actions, and ViewModel contract
- keep the implementation mode-aware but screen-agnostic

Verification:

- narrow common/ViewModel tests for initial load
- narrow common/ViewModel tests for load failure + retry
- shared module compile/tests

### Task 9B — Shared quiz screen and binary mode

Scope:

- build the shared quiz screen structure using the design system patterns already used in login
- implement common quiz chrome: progress, quote card, loading, error, and submit state
- implement binary answer UI inside the shared screen

Verification:

- common/ViewModel tests for binary submit path
- Android manual binary session flow
- shared + Android compile

### Task 9C — Multiple-choice mode

Scope:

- implement the multiple-choice answer section inside the same quiz screen
- render exactly three options from backend data
- submit selected option through the existing quiz flow

Verification:

- common/ViewModel tests for multiple-choice submit path
- Android manual multiple-choice flow
- shared + Android compile

### Task 9D — Feedback dialog and progression

Scope:

- show correct/incorrect modal using the provided design direction
- keep feedback-dialog state in `QuizUiState`
- block advancement until `OK`
- advance to the next pre-generated question after `OK`
- preserve behavior across configuration changes

Verification:

- common/ViewModel tests for:
  - correct/incorrect feedback state
  - no advance before `OK`
  - advance after `OK`
- Android manual rotation check while feedback dialog is visible

### Task 9E — Results and restart

Scope:

- replace the current placeholder result route with the real result screen
- show score, percentage, correct count, incorrect count, and mode
- implement `Start again` with a fresh server session

Verification:

- common/ViewModel tests for question 10 -> results
- common/ViewModel tests for `Start again`
- Android manual full 10-question flow

### Task 9F — Final quiz polish and verification

Scope:

- align the final quiz UI against the binary, multiple-choice, modal, and result HTML mockups
- remove temporary quiz placeholders
- review landscape usability and error/retry states

Verification:

- shared module tests
- Android manual portrait + landscape checks
- targeted compile/build verification

Tests first:

- initial load
- answer submission
- correct/incorrect state
- feedback dialog state
- no advance before OK
- advance after OK
- question 10 -> results
- Start again
- mode-specific rendering state
- failure/retry

Verification:

- common tests
- Android manual 10-question flow
- rotation while feedback dialog visible
- portrait + landscape

Commit:

`feat(quiz): implement quiz flow and results`

## Task 10 — Settings and Profile

Goal:

Settings:

- mode control
- Binary default
- changing mode restarts session

Profile:

- backend identity
- read-only display
- logout

Tests first:

- mode change behavior
- fresh session after mode change
- logout state clearing

Verification:

- relevant common tests
- Android manual flows

Commit:

`feat(client): add settings profile and logout`

## Task 11 — Android polish and compliance hardening

Goal:

Make the required Android experience submission-ready.

Verify and improve:

- API 26
- portrait
- landscape
- loading
- offline/server errors
- keyboard
- accessibility
- responsive layout
- no clipping
- back behavior
- exact assignment text
- visual consistency

Add a small number of Android UI tests for the highest-value flows.

Commit:

`feat(android): polish required assignment experience`

## Task 12 — Comprehensive test hardening

Goal:

Fill remaining high-value coverage gaps.

Focus:

- server integration tests
- client state-machine tests
- Android UI tests
- regression issues found during manual testing

Run wider verification.

Commit:

`test: harden assignment-critical flows`

## Task 13 — Bonus platforms

Order:

1. Desktop JVM
2. iOS 15+

Goal:

Reuse the same client logic and Compose UI.

Do not rewrite features.

Web/Wasm is deferred to optional post-core work.

Commits:

- `feat(desktop): enable desktop client`
- `feat(ios): enable ios client`

## Task 14 — CI, Docker, and documentation

Goal:

Add:

- GitHub Actions
- Dockerfile
- docker-compose
- final README
- API docs
- architecture summary
- screenshots/demo assets if available

Verification:

- CI-equivalent local commands
- Docker server startup
- clean-clone setup rehearsal

Commit:

`ci: add automated verification and delivery tooling`

and/or focused docs commit.

## Task 15 — Optional deployment/showcase

Only if all previous tasks are stable.

Possible work:

- hosted Spring Boot backend
- Web/Wasm target
- static Web deployment

These are portfolio bonuses, not assignment requirements.

## Task 16 — Final assignment compliance audit

Mandatory before submission.

Re-read original assignment.

Produce a requirement-by-requirement audit.

Verify:

- Android API 26+
- splash/login
- persisted login
- logout
- exact validation behavior/color
- three tabs
- ten-question session
- Binary default
- MCQ exactly three options
- backend distractors
- mode-switch restart
- exact modal messages
- no advance before OK
- results
- Start again
- read-only profile
- portrait
- landscape
- backend DB
- seeded quotes
- quote list not hard-coded in client
- API docs
- clean run instructions
- no committed secrets

Run final build/test suite.

Report any remaining deviation instead of hiding it.

---

# 22. Definition of Done

Core assignment is done when:

- Android API 26+ is complete and polished.
- Splash/Login/Main flow works.
- Session persists across restart.
- Logout returns to Login.
- Login validation matches assignment exactly.
- Three tabs work.
- Binary is default.
- Multiple choice shows exactly three backend-generated options.
- Each session contains ten questions.
- Changing mode creates a fresh session.
- Server verifies answers.
- Duplicate answers are prevented.
- Exact correct/incorrect dialog behavior works.
- Dialog waits for OK before advancing.
- Results screen works.
- Start again works.
- Profile comes from authenticated backend identity.
- Portrait and landscape are usable.
- Users and quotes are database-backed.
- Quotes are seeded server-side.
- Quotes are not hard-coded in the client.
- Critical business behavior is tested.
- Required documentation is complete.
- Final compliance audit passes.

Recommended engineering add-ons:

- CI
- Docker
- Desktop
- iOS

Optional showcase:

- Hosted backend
- Web/Wasm
- Public live demo

The project must prefer coherence and complete execution over additional technology count.
