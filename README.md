## Project Overview

SocialDating is a dating/social app consisting of:

- `server/` — Spring Boot microservices backend
- `client/` — Android app

---

## Server

### Commands (run from `server/`)

#### Running in Docker

1. Build JARs: `./gradlew build -x test`
2. Start containers: `docker compose up -d --build`
3. Wait for containers health: `./check_containers_health.sh`
4. Log unhealthy containers (if needed): `./log_unhealthy_containers.sh`

#### Testing

- Run unit and local integration tests: `./gradlew test`
- Run unit and local integration tests for a single service: `./gradlew :{service-name}:test`
- Run a specific test class: `./gradlew :{service-name}:test --tests {test-class-name}`
- Run a specific test method: `./gradlew :{service-name}:test --tests {test-class-name}.{test-method-name}`
- Run global integration tests (requires server running in Docker): `./gradlew integrationTest`

### Architecture

Spring Boot microservices, entry point is `gateway-service/` (Spring Cloud Gateway):

- `gateway-service/` — routes requests, validates JWT via `JwtAuthFilter`, creates auth headers, publishes Kafka topics
- `users-service/`, `categories-service/`, `defining-themes-service/`, `statements-service/` — domain services, each
  with `controller/` → `service/` → `repository/` layers and `model/` classes
- `common/` — shared module for domain services: general security config, verification of auth headers via
  `HeaderAuthFilter`, exception handling, model classes, utilities

**Security** — JWT Bearer tokens issued by `users-service` (with auth exception handling)

**Inter-service communication** — Kafka async events (producers/consumers in `service/` layer)

**Database** — single PostgreSQL instance `social_dating_db`, separate schemas per service:

| Schema                   | Entities                             |
|--------------------------|--------------------------------------|
| `users_schema`           | `User`                               |
| `categories_schema`      | `Category`, `UserCategory`           |
| `defining_themes_schema` | `DefiningTheme`, `UserDefiningTheme` |
| `statements_schema`      | `Statement`, `UserStatement`         |

**Migrations** — no migration tool, Hibernate DDL auto is `create-drop`

**Testing:**

- Unit and local integration tests: JUnit 5 + MockK + SpringMockK + TestContainers
- Global integration tests live only in `common/src/integrationTest/kotlin/`

---

## Client

### Commands (run from `client/`)

#### Running

1. Build APK: `./gradlew installDebug`
2. Launch the app on emulator:
   `~/AppData/Local/Android/Sdk/platform-tools/adb.exe shell am start -n xelagurd.socialdating.client/.MainActivity`

#### Testing

- Run unit tests: `./gradlew test`
- Run a specific test class: `./gradlew testDebugUnitTest --tests {test-class-name}`
- Run a specific test method: `./gradlew testDebugUnitTest --tests {test-class-name}.{test-method-name}`
- Run instrumented tests: `./gradlew connectedAndroidTest`

### Architecture

MVVM + Repository pattern, Jetpack Compose UI, Dagger Hilt DI (`AppModule`).

**UI layer** (`ui/`):

- `navigation/` — Navigation graph
- `viewmodel/` — ViewModels
- `screen/` — Composable screens and components. `ScreenComponents` contains extensions of standard elements, and
  `AppComponents` uses them to create complex components. When adding/changing UI components, you should **always** try
  to change existing components in `ScreenComponents`/`AppComponents` first, and only then add new components to
  `ScreenComponents`/`AppComponents`. You should **only** add components to screens if they are unique to screen and
  cannot be reused.
- `state/` — UI state classes
- `form/` — Form-related composables
- `theme/` — Material theme

**Data layer** (`data/`) — dual-source (Room local + Retrofit remote), offline-first:

- `local/` — Room DAOs, local repositories, `AppDatabase`
- `remote/` — Retrofit API interfaces and remote repositories; all calls go through `ApiUtils.safeApiCall`
- `model/` — models shared between local/remote layers

**Auth and config:**

- Two Retrofit instances — one without auth (login/register), one with
- `AuthInterceptor` — injects JWT into Retrofit requests, refreshes access token
- `AccountManager` — wraps `CredentialManager`, manages credentials
- `PreferencesRepository` — wraps `DataStore`, stores access + refresh JWT tokens

**Testing:**

- Unit tests: JUnit 4 + MockK + Coroutines Test
- Instrumented tests cover screen rendering and navigation

**Network** — emulator connects to server at `http://10.0.2.2:8080`