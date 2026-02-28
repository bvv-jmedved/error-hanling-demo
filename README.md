# Error Handling Demo (Apache Camel + Spring Boot)

This project demonstrates:
- layered sender/process/receiver route orchestration,
- centralized failure-contract mapping outside route DSL,
- deterministic RoutePolicy composition via Spring modules.

## Tech stack
- Java 21
- Spring Boot 3.5.x
- Apache Camel 4.17.x
- Maven

## Architecture at a glance

The current design follows the RoutePolicy architecture in `.codex/doc/Camel_RoutePolicy_Architecture.md`.

- `BaseRouteBuilder` owns orchestration:
  - `final configure()` calls `configureRoutes()`
  - then applies ordered `RouteConfigurationModule` beans
- Capability interfaces:
  - `MonitoringRoute` (marker for logging policy support)
  - `FailContractRoute` (provider of fail-contract policy)
- Policy contracts:
  - `FailContractRoutePolicy` interface (capability-facing type)
  - `BaseFailContractRoutePolicy` abstract base class (shared fail mapping behavior)
- Policy modules:
  - `FailContractModule` (`@Order(10)`) for `FailContractRoute` builders
  - `LoggingModule` (`@Order(100)`) for `MonitoringRoute` builders

Deterministic outcome:
- sync sender routes: fail-contract policy, then logging policy
- non-sync routes: logging policy only

## Monitoring metadata model

Logging is annotation-driven:
- concrete `RouteBuilder` classes use `@Monitored(service = "...", critical = ...)`
- `service` is an explicit stable identifier
- `critical` marks externally exposed ingress routes (`true`) vs internal orchestration routes (`false`)

`LoggingRoutePolicyFactory` falls back to class name + `critical=false` when annotation metadata is absent.

## Route topology

Ingress routes:
- REST:
  - `POST /demo1` -> `direct:demo-sender`
  - `POST /demo2` -> `direct:demo2-sender`
- SOAP:
  - CXF endpoint `/services/demo-soap` -> `direct:demo-soap-sender`
- FTP:
  - `ftp://user@localhost:<port>/test` (poll delay `10000 ms`) -> `direct:demo-ftp-sender`

Common orchestration path:
- sender route
- `direct:demo-process` (`DemoProcessRouteBuilder`)
- `direct:demo-receiver` (`DemoReceiverRouteBuilder`)
- `direct:technical-receiver` (`DemoTechnicalReceiverRouteBuilder`)

Main policy/error components:
- `FailContractRoutePolicy` (policy contract interface)
- `BaseFailContractRoutePolicy` (base failure mapping hook)
- `RestFailureContractRoutePolicy` / `SoapFailureContractRoutePolicy`
- `IntegrationException` + `IntegrationError`
- `DefaultIntegrationExceptionMapperImpl`

## Error response contract

REST errors are returned as JSON:

```json
{
  "errors": [
    {
      "code": "DOWNSTREAM_HTTP_500",
      "message": "Downstream Internal Server Error"
    }
  ]
}
```

Fallback body when mapping/serialization fails:

```json
{
  "errors": [
    {
      "code": "INTERNAL_ERROR",
      "message": "Internal error while preparing error response"
    }
  ]
}
```

## Run locally

Build:

```bash
./mvnw clean test
```

Run app:

```bash
./mvnw spring-boot:run
```

Run app with PoC profile (enables failure injection headers):

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=poc
```

Default local base URL:
- `http://localhost:8080`

## Quick usage examples (REST)

### 1) Happy path (`/demo2`)

```bash
curl -i -X POST http://localhost:8080/demo2 \
  -H 'Content-Type: application/json' \
  -d '{}'
```

Expected:
- `HTTP 200`
- body: `{"status":"ok"}`

### 2) Simulate downstream HTTP 500 (`/demo2`, PoC profile)

```bash
curl -i -X POST http://localhost:8080/demo2 \
  -H 'Content-Type: application/json' \
  -H 'X_THROW_IN: technical-call' \
  -H 'X_THROW_TYPE: http' \
  -H 'X_THROW_STATUS: 500' \
  -d '{}'
```

Expected:
- mapped to `HTTP 502`
- body code: `DOWNSTREAM_HTTP_500`

### 3) Simulate business error inside HTTP 200 (`/demo2`, PoC profile)

```bash
curl -i -X POST http://localhost:8080/demo2 \
  -H 'Content-Type: application/json' \
  -H 'X_THROW_IN: technical-call' \
  -H 'X_THROW_TYPE: http' \
  -H 'X_THROW_STATUS: 200' \
  -H 'X_THROW_BODY: {"errCode":"K2_123","errMsg":"Business validation failed"}' \
  -d '{}'
```

Expected:
- mapped to `HTTP 400`
- body code: `K2_123`

## Testing

Run all tests:

```bash
./mvnw test
```

Relevant architecture/composition coverage includes:
- `RouteConfigurationModuleOrchestrationTest`
- `RoutePolicyCompositionTest`
- `FailContractModuleTest`
- `LoggingRoutePolicyFactoryTest`
- integration tests for REST/SOAP/FTP flows and failure mapping.
