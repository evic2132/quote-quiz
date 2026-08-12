# Famous Quote Quiz — Normalized Assignment Requirements

> This file is a normalized, implementation-friendly rendering of the supplied test assignment.
> The original PDF at `docs/assignment/Test_Task.pdf` remains the highest-priority source of truth.

## Product

Build a tabbed cross-platform **Famous Quote Quiz** application.

The system asks quote-related questions and the user chooses the correct answer.

Two quiz modes are required:

- **Binary / Yes-No** — answer whether the proposed person said the quote.
- **Multiple choice** — choose the correct author from three possible answers.

---

## Technology Requirements

### Client

- Kotlin Multiplatform (KMP)
- Compose Multiplatform shared UI
- Primary required target: Android 8.0 / API 26+
- Phone form factor
- Portrait and landscape orientations

### Bonus targets

Welcome but not required:

- iOS 15+
- Desktop JVM 21+

### Backend

- Spring Boot
- Kotlin
- REST/JSON API
- Database for users and quotes
- Embedded database such as H2 is acceptable

### Client/server boundary

- Client communicates with backend through REST API.
- Quote list must not be hard-coded in the client.
- Backend provides quotes and authors.
- Backend handles login/authentication.
- Backend exposes profile information.
- Backend produces multiple-choice answer options, including distractors.

---

## Startup and Login

On application start:

- Show a splash screen.
- First-time or logged-out users must see Login.
- After successful login, redirect to the application main screen.
- A previously logged-in user must not be asked to log in again after reopening the application unless the user logged out.

Login screen contains:

- Email input
- Password input
- Login button
- Hint text appropriate for each field

Validation:

- Simple non-empty validation for both fields.
- Error text appears below the corresponding input.
- Error text hidden by default.
- Error text color is red: `#f00` / `#FF0000`.
- When the user types new input into a field, that field's validation error becomes hidden.

---

## Main Application

Provide three tabs:

1. Quiz
2. Settings
3. User Profile

---

## Quiz

A single quiz session contains exactly **10 quotes/questions**.

Changing quiz mode restarts the quiz session.

### Correct answer

Display a modal box containing:

`Correct! The right answer is: …`

### Incorrect answer

Display a modal box containing:

`Sorry, you are wrong! The right answer is: …`

Only after the user presses `OK` should the next question be shown.

### Completion

After the ten-question session completes:

- Show a statistics/results screen.
- Include a `Start again` button.

No result-screen wireframe is prescribed.

---

## Settings

Provide a single control switching between quiz modes.

Modes:

- Binary / Yes-No — default
- Multiple choice

Changing mode restarts the quiz session.

Multiple-choice mode:

- Exactly three possible answers
- Exactly one correct answer
- Backend produces the correct option plus distractors

---

## User Profile

Profile:

- Read-only
- No editing required
- Based on authenticated/login user information
- Includes a Logout button

Logout:

- Returns the user to Login.
- Subsequent application start must require login again.

---

## Backend Data

Backend must:

- Store users in a database.
- Store quotes in a database.
- Seed a set of famous quotes on startup.
- Serve quotes/authors through REST.
- Handle authentication.
- Expose profile information.
- Produce multiple-choice answer choices.

---

## Client Persistence

Client-side persistence is optional in general, but the required "stay logged in across reopen unless logged out" behavior means some session state must be preserved.

Implementation choice is free.

Do not introduce a local relational database merely because it is allowed.

---

## Deliverables

Provide:

- Client source code
- Backend source code
- Documentation explaining how to run backend
- Documentation explaining how to run client
- Short overview of API endpoints

Additional useful add-ons are allowed.
