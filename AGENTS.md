# Repository Guidelines

## Project Structure & Module Organization
This is a single-module Maven project (`pom.xml`) using Spring Boot 3.5 and Apache Camel 4.
- Application entrypoint: `src/main/java/cz/bvv/errorhanlingdemo/ErrorHanlingDemoApplication.java`
- Route builders: `src/main/java/cz/bvv/errorhanlingdemo/builder/`
- Shared route base classes: `src/main/java/cz/bvv/errorhanlingdemo/builder/common/`
- Custom exceptions: `src/main/java/cz/bvv/errorhanlingdemo/exception/`
- Runtime config: `src/main/resources/application.yaml`
- Tests: `src/test/java/cz/bvv/errorhanlingdemo/`

Organize new Camel routes by role (`sender`, `process`, `receiver`, `technical*`) and keep shared behavior in `builder/common`.

## Build, Test, and Development Commands
Use the Maven wrapper to keep tool versions consistent.
- `./mvnw clean` removes build artifacts.
- `./mvnw test` runs unit/spring-context tests (currently includes `ErrorHanlingDemoApplicationTests`).
- `./mvnw verify` runs the full Maven verification lifecycle.
- `./mvnw spring-boot:run` starts the app locally.

For clean CI-style validation, prefer:
1. `./mvnw clean`
2. `./mvnw verify`

## Coding Style & Naming Conventions
- Java 21, 4-space indentation, UTF-8 source files.
- Follow standard Spring/Camel style: PascalCase classes, camelCase methods/fields, lowercase package names.
- Keep route classes named `*RouteBuilder` (example: `DemoReceiverRouteBuilder`).
- Keep Camel `routeId` values short, kebab-case, and role-oriented (example: `demo-sender`, `demo-process`).
- Put common error/completion behavior in base route builders instead of duplicating it in concrete routes.

## Testing Guidelines
- Frameworks: JUnit 5 + Spring Boot Test.
- Name tests `*Tests` for context tests and `*Test` for unit tests.
- Cover both happy path and failure/error-handling path for route changes.
- Run `./mvnw test` before every commit; run `./mvnw verify` before opening a PR.

## Commit & Pull Request Guidelines
Recent history favors short, imperative, sentence-style commits (example: `Refactor builders to use specific base route classes...`).
- Keep commit subjects focused and action-oriented.
- In PRs, include: purpose, affected routes/classes, test evidence (`./mvnw test`/`./mvnw verify`), and any behavioral changes to error handling.
- Link related issues/tasks when available.
