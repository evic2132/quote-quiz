# AGENTS.md

## Repository Purpose

This repository implements the **Famous Quote Quiz** technical assignment.

The primary goal is to satisfy the assignment completely with a polished, maintainable Kotlin implementation while demonstrating strong Kotlin Multiplatform, Compose Multiplatform, Spring Boot, testing, and engineering judgment.

## Sources of Truth

Read these before making architectural or behavioral changes:

1. `docs/assignment/requirements.md` — normalized assignment requirements.
2. `docs/assignment/Test_Task.pdf` — original assignment document.
3. `docs/plans/2026-08-12-famous-quote-quiz-plan.md` — implementation plan.
4. `docs/decisions/0001-initial-architecture.md` — initial architecture decisions and deliberate scope limits.

Priority when sources conflict:

1. Original assignment PDF
2. Normalized requirements
3. Implementation plan
4. Architecture decision notes
5. Existing implementation

Do not silently reinterpret or weaken an assignment requirement.

---

## Working Style

Work **one numbered plan task at a time**.

Before starting a task:

- Read the task in `docs/plans/2026-08-12-famous-quote-quiz-plan.md`.
- Read the acceptance criteria related to that task.
- Inspect the current codebase before creating new abstractions.
- State the exact task being executed and the intended verification commands.

Do not implement future tasks early unless a minimal prerequisite is required for the current task.

Do not perform broad unrelated refactors while implementing a feature.

---

## Test-Driven Development

Use test-first development where it provides engineering value.

Strong test-first candidates:

- Quiz generation rules
- Quiz session progression
- Duplicate-answer prevention
- Authentication service behavior
- Login validation
- Session restoration
- ViewModel state transitions
- Quiz feedback-dialog workflow
- Logout/session invalidation
- Error mapping

Do not force artificial tests for:

- Version catalog edits
- Basic Gradle scaffolding
- README text
- Dockerfile formatting
- Simple theme/spacing changes
- Trivial declarative Compose layout

For business behavior:

1. Write or update the smallest relevant failing test.
2. Run the narrow test and confirm the expected failure when practical.
3. Implement the minimum production code required.
4. Run the narrow test again.
5. Refactor only while tests remain green.
6. Run the relevant module verification before declaring the task complete.

---

## Verification Rules

Never claim a task is complete unless the relevant verification passes.

Prefer the narrowest useful command during iteration.

Examples:

- A single server test class while implementing server logic.
- Server test suite when a server feature is complete.
- Relevant KMP/common tests while implementing ViewModels or repositories.
- Android compile/lint/tests for Android-facing changes.
- Full repository verification at milestone boundaries and before final delivery.

After project scaffolding, inspect available Gradle tasks and document the canonical commands in the README and/or this file.

Do not assume a Gradle task exists before checking the generated task graph.

If a command fails:

- Report the failure accurately.
- Fix it if it belongs to the current task.
- Do not hide, bypass, or delete legitimate failing tests merely to obtain a green build.

---

## Git Rules

Keep changes scoped and reviewable.

Preferred commit format:

- `chore: ...`
- `feat(contract): ...`
- `feat(server): ...`
- `feat(client): ...`
- `feat(auth): ...`
- `feat(quiz): ...`
- `feat(settings): ...`
- `feat(profile): ...`
- `test: ...`
- `ci: ...`
- `docs: ...`

Before committing:

- Review the diff.
- Verify no secrets or generated junk are included.
- Ensure relevant tests/builds pass.

At the end of each numbered task:

1. Summarize what changed.
2. List verification commands and results.
3. List important design decisions or deviations.
4. Show the proposed commit message.
5. Commit only after the task is verified.
6. Stop before starting the next numbered task unless explicitly instructed to continue.

Do not rewrite existing Git history unless explicitly requested.

---

## Scope and Priority

### Required platform

Android API 26+ is the primary required client target and must remain the highest priority.

### Bonus platforms

Implement only after the Android assignment is complete and stable:

1. Desktop JVM
2. iOS 15+
3. Web/Wasm

Bonus-platform work must never destabilize or delay required Android behavior.

### Required backend

Use Spring Boot with Kotlin.

### Deliberate scope limits

Do not add these unless the assignment changes or there is a concrete demonstrated need:

- Refresh-token rotation
- OAuth/OIDC server
- Static client API-key security layer
- Redis
- PostgreSQL for the assignment implementation
- Kubernetes
- Microservices
- Per-feature Gradle modules
- Heavy Clean Architecture ceremony
- WebFlux/R2DBC
- Room solely to store a login token
- Complex custom Gradle plugin ecosystems

The project should demonstrate mature judgment through proportional architecture.

---

## Architecture Guardrails

### AGP 9+ client module structure

This repository uses the modern AGP 9+ Kotlin Multiplatform structure.

The shared client and Android application MUST be separate Gradle
modules.

#### `:app:shared`

This is a Kotlin Multiplatform library containing shared client logic
and shared Compose Multiplatform UI.

It must use:

- `org.jetbrains.kotlin.multiplatform`
- `com.android.kotlin.multiplatform.library`
- Compose Multiplatform
- Compose compiler plugin

It must NOT use:

- `com.android.application`
- legacy `com.android.library`

It must NOT own:

- `MainActivity`
- Android application ID
- Android application packaging

Use the current Android-KMP library DSL supported by the selected stable
AGP version. Do not copy deprecated DSL from old KMP examples.

#### `:app:androidApp`

This is the standalone Android application module.

It:

- owns `MainActivity`
- owns `AndroidManifest.xml`
- owns the application ID and Android packaging
- depends on `:app:shared`
- renders the shared `App()` Compose entry point

Do not apply Kotlin Multiplatform to `androidApp`.

Follow current AGP 9 built-in Kotlin configuration.

#### `:app:desktopApp`

This is a thin JVM Desktop application entry point.

It depends on `:app:shared`.

Do not duplicate shared UI or business logic in this module.

#### `:app:iosApp`

This is the iOS/Xcode application consuming the shared KMP framework.

It hosts the shared Compose UI.

Do not create duplicate SwiftUI feature implementations.

#### Dependency direction

Platform applications depend on the shared KMP client:

`androidApp -> shared`

`desktopApp -> shared`

`iosApp -> shared`

Never reverse this dependency.

The server is independent of the client and shares only API wire
contracts through `:api-contract`.

Do not use deprecated AGP legacy-variant compatibility flags to make an
old KMP structure compile.

### Shared contract

`:api-contract` contains only shared wire/API models.

It may contain:

- `@Serializable` request/response DTOs
- Shared API enums such as `QuizMode`

It must not contain:

- JPA entities
- Repositories
- Server services
- ViewModels
- Client domain logic
- Database models

### Server

Use simple feature-oriented packages.

Rules:

- Controllers handle HTTP concerns.
- Services own business/application logic.
- Repositories own persistence.
- JPA entities never leave the server.
- Constructor injection only.
- Passwords are stored only as BCrypt hashes.
- JWT is sufficient for assignment authentication.
- Correct quiz answers remain authoritative on the server.
- Do not expose correct answers in question-creation payloads.
- Prevent duplicate answers server-side.

Use Spring MVC + Spring Data JPA + H2 unless a real requirement demands otherwise.

Do not pretend blocking JPA becomes non-blocking merely because a function is `suspend`.

### Client

Use shared Compose Multiplatform UI and shared ViewModels in `commonMain`.

Preferred flow:

`UI -> UiAction -> ViewModel -> Repository/API -> StateFlow<UiState> -> UI`

Rules:

- Composables do not call network APIs directly.
- ViewModels do not depend on platform navigation controllers.
- ViewModels do not receive `HttpClient` directly.
- Transport exceptions are mapped before reaching presentation logic.
- Durable workflow state belongs in `UiState`.
- Use `UiEffect` only for genuine one-shot commands.
- Authentication navigation is driven by root `SessionState`.
- Quiz feedback-dialog visibility/result belongs in `QuizUiState` so rotation does not lose it.
- Use Ktor Client for REST networking.
- Use Koin for pragmatic dependency injection.
- Use Multiplatform Settings behind storage abstractions for token/mode persistence.
- Do not add Room unless actual relational local data appears.

### Navigation

Use Compose Multiplatform Navigation 3.

Auth state sits above main tab navigation:

- Loading -> Splash
- Unauthenticated -> Login
- Authenticated -> Main

Main tabs:

- Quiz
- Settings
- Profile

Use multiple back stacks only when they provide clear state-preservation value. Do not make navigation architecture more complicated than the product requires.

---

## Assignment-Specific Behavior That Must Not Drift

- Login fields: email and password.
- Validation is non-empty only unless explicitly adding harmless UX validation that does not violate the task.
- Validation error text appears below its field.
- Validation errors are hidden initially.
- Validation color is exactly `#FF0000`.
- Editing a field clears that field's validation error.
- Successful login opens the main application.
- Logged-in state survives restart.
- Logout returns to login.
- Three main tabs: Quiz, Settings, Profile.
- Default quiz mode is Binary/Yes-No.
- Multiple-choice mode has exactly three answer choices and exactly one correct choice.
- Multiple-choice distractors come from the backend.
- Each quiz session has exactly ten questions.
- Changing mode restarts the quiz session.
- Correct feedback text: `Correct! The right answer is: …`
- Incorrect feedback text: `Sorry, you are wrong! The right answer is: …`
- Next question appears only after feedback dialog `OK`.
- After question ten show statistics/results and `Start again`.
- Profile is read-only and comes from authenticated backend identity.
- Portrait and landscape must both work.
- Quote list is not hard-coded in the client.
- Backend stores users and quotes in a database and seeds quotes at startup.
- Documentation includes how to run client/backend and a short API overview.

---

## Code Quality

Prefer:

- Idiomatic Kotlin
- Immutable UI state
- `StateFlow`
- Structured concurrency
- Clear names
- Small focused functions
- Explicit domain invariants
- Minimal public API surfaces
- Stable library versions

Avoid:

- `GlobalScope`
- swallowed cancellation
- giant god classes
- unnecessary interfaces with one trivial implementation
- speculative abstractions
- duplicated DTO/domain/entity roles without a reason
- experimental language features merely for novelty
- hard-coded environment URLs scattered through source files

---

## Security and Secrets

This repository is intended to be public.

Never commit:

- JWT signing secrets
- Real passwords beyond documented demo credentials intended for seeded local data
- `.env`
- keystores
- signing files
- production tokens
- private certificates
- local machine configuration

Use environment variables for server secrets.

Client-bundled configuration must be treated as public.

---

## Final Review Requirement

Before final delivery, perform an explicit assignment compliance audit.

The final audit must:

1. Re-read `docs/assignment/Test_Task.pdf` or `requirements.md`.
2. Map every requirement to implemented code or documentation.
3. Run the final verification suite.
4. Verify Android API 26+.
5. Verify portrait and landscape.
6. Verify fresh login, persisted login, and logout.
7. Verify both quiz modes.
8. Verify mode change restarts the session.
9. Verify exact modal behavior/text.
10. Verify final results and Start again.
11. Verify backend-generated distractors.
12. Verify profile.
13. Verify README/run instructions.
14. Verify no secrets are committed.
15. Report any remaining deviations instead of hiding them.
