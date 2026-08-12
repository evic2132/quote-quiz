# ADR-0001 — Initial Architecture and Scope

**Status:** Accepted  
**Date:** 2026-08-12

## Context

The Famous Quote Quiz is a small interview assignment intended to demonstrate Kotlin Multiplatform, Compose Multiplatform, Spring Boot, REST integration, application architecture, testing, and engineering judgment.

The assignment is deliberately small. The implementation should therefore be production-minded without becoming production-sized.

## Decision

### Repository

Use a Gradle monorepo with:

- `:api-contract`
- `:server`
- `:client:shared`
- thin platform launchers where required

Feature separation will primarily be package-level rather than one Gradle module per feature.

### API contract

Share only serializable wire DTOs and API enums between client and server.

Do not share persistence models, repositories, domain services, or ViewModels.

### Backend

Use:

- Spring Boot 4.1.x
- Kotlin
- Java 21 toolchain
- Spring MVC
- Spring Security
- Spring Data JPA
- H2
- BCrypt
- JWT bearer authentication

A single JWT session token is sufficient for this assignment.

Deliberately excluded:

- refresh-token flows
- static client API-key security layer
- OAuth/OIDC
- Redis
- WebFlux/R2DBC
- PostgreSQL

These can be discussed as production evolutions but are not required to demonstrate the requested skills.

### Quiz authority

The backend owns quiz generation and answer correctness.

The backend creates a ten-question session and returns question data without revealing correct answers in advance.

Each submitted answer is verified by the backend.

This supports:

- authoritative scoring
- duplicate-answer prevention
- correct distractor generation
- clean client/server responsibility boundaries

### Client

Use:

- Kotlin Multiplatform
- Compose Multiplatform
- shared ViewModels
- StateFlow
- pragmatic `UiState` + `UiAction` + optional `UiEffect`
- Ktor Client
- Koin
- Multiplatform Settings
- Navigation 3

Do not use Room unless relational local persistence becomes a real requirement.

Authentication state is represented at the app root as:

- Loading
- Unauthenticated
- Authenticated

This drives Splash/Login/Main rendering.

### Navigation

Use Navigation 3.

The app has auth-level navigation plus three top-level tabs:

- Quiz
- Settings
- Profile

Multiple back stacks may be used if they clearly improve tab state preservation; they are not a goal by themselves.

### Platform order

1. Android API 26+ — required
2. Desktop JVM — bonus
3. iOS 15+ — bonus
4. Web/Wasm — optional showcase only

Bonus targets must not delay or destabilize Android.

### Testing

Use test-first development for business rules and state machines where useful.

Prioritize:

- quiz generation invariants
- session progression
- duplicate-answer prevention
- auth behavior
- login validation
- session restoration
- ViewModel state transitions

Do not force artificial TDD around trivial configuration or presentation-only edits.

### Delivery additions

CI, Docker, screenshots, and a strong README are valuable and in scope as engineering add-ons.

Hosted backend and Web/Wasm deployment are optional bonuses, not assignment completion criteria.

## Consequences

This architecture intentionally favors:

- clarity
- reviewer experience
- testability
- shared Kotlin value
- proportionate complexity

over showcasing the maximum possible number of frameworks or infrastructure patterns.
