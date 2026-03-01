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
  - configuration is guarded against accidental double invocation
  - all created routes must define explicit `routeId`
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

RoutePolicy bean scope rule:
- `RoutePolicy` implementations should be `@Scope("prototype")` unless fully stateless.

Deterministic outcome:
- sync sender routes: fail-contract policy, then logging policy
- non-sync routes: logging policy only

## PoC simulation boundary

`FailureInjector` is **not** part of the RoutePolicy architecture or production design.

- Architectural behavior (termination, mapping, logging) is implemented by RouteBuilders, RoutePolicy modules, and fail-contract policies.
- `FailureInjector` is a PoC/testing utility used only to trigger controlled failures for demonstrations.
- It is activated only through PoC wiring (`PocFailureStepProcessorFactory` under `poc` profile); non-`poc` profiles use no-op processors.

## Monitoring metadata model

Logging is annotation-driven and split into two concerns:
- concrete `RouteBuilder` classes use `@Monitored(service = "...", critical = ...)`
- `service` is an explicit stable identifier
- `critical` marks externally exposed ingress routes (`true`) vs internal orchestration routes (`false`)
- optional `@LoggingStatus(onStart = ..., onComplete = ..., onFailure = ...)` configures exchange status semantics
  - defaults: `onStart=RUNNING`, `onComplete=RUNNING`, `onFailure=FAILURE`
  - ingress technical sender routes override defaults to `STARTED/COMPLETED/FAILED`

`LoggingRoutePolicyFactory` falls back to class name + `critical=false` when annotation metadata is absent.

For every exchange, the logging policy emits canonical `event=process_status` records:
- status is logged only when it changed (so the last log per `correlationId` is the current process status)
- log payload is intentionally minimal for monitoring: `status`, `routeId`, `correlationId`

Token refresh retry flow emits additional `event=process_detail` logs from retry decider:
- `detailState=RUNNING recoveryAction=TOKEN_REFRESH_START`
- `detailState=RUNNING recoveryAction=TOKEN_REFRESH_OK`
- `detailState=RUNNING recoveryAction=RETRY_AFTER_REFRESH`
- or `detailState=FAILURE recoveryAction=TOKEN_REFRESH_FAILED`

`correlationId` is always logged. It is resolved in this order:
- existing exchange property `integration.correlation.id`
- incoming header `X-Correlation-Id`
- generated UUID (when missing)

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

PoC profile behavior:
- `poc` profile wires `PocFailureStepProcessorFactory` (active failure injection).
- non-`poc` profiles wire `NoopFailureStepProcessorFactory` (no-op processors).
- `TokenManager` is also profile-bound (`FakeTokenManager` for `poc`, `NoopTokenManager` otherwise).

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
