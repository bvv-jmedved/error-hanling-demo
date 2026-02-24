# Error Handling Demo (Apache Camel + Spring Boot)

This project demonstrates:
- out-of-route exception handling in Apache Camel routes,
- centralized failure-contract mapping for REST responses,
- sender/process/receiver route layering for clean orchestration.

## Tech stack
- Java 21
- Spring Boot 3.5.x
- Apache Camel 4.17.x
- Maven

## Purpose
The demo exposes a REST endpoint and routes the request through:
1. sender route,
2. process route,
3. receiver route,
4. technical receiver route (simulated downstream call).

Failures are mapped into a stable JSON REST error contract by a route policy (outside main business DSL).

## Route flow
- REST entry: `POST /demo`
- Route chain:
  - `DemoTechnicalSender`
  - `DemoSenderRouteBuilder`
  - `DemoProcessRouteBuilder`
  - `DemoReceiverRouteBuilder`
  - `DemoTechnicalReceiverRouteBuilder`

Main components:
- `FailureContractRoutePolicy` (base failure mapping hook)
- `RestFailureContractRoutePolicy` (HTTP status/body contract mapping)
- `IntegrationException` + `IntegrationError` (integration error model)
- `DefaultIntegrationExceptionMapperImpl` (maps technical exceptions to integration contract)

## Error response contract
Errors are returned as JSON:

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

Fallback body when contract mapping/serialization fails:

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

Run with PoC profile (enables failure injection headers):

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=poc
```

Default endpoint:
- `http://localhost:8080/demo`

## Quick usage examples
### 1) Happy path
```bash
curl -i -X POST http://localhost:8080/demo \
  -H 'Content-Type: application/json' \
  -d '{}'
```

Expected:
- `HTTP 200`
- body: `{"status":"ok"}`

### 2) Simulate downstream HTTP 500 (PoC profile)
```bash
curl -i -X POST http://localhost:8080/demo \
  -H 'Content-Type: application/json' \
  -H 'X_THROW_IN: technical-call' \
  -H 'X_THROW_TYPE: http' \
  -H 'X_THROW_STATUS: 500' \
  -d '{}'
```

Expected:
- mapped to `HTTP 502`
- body code: `DOWNSTREAM_HTTP_500`

### 3) Simulate business error inside HTTP 200 (PoC profile)
```bash
curl -i -X POST http://localhost:8080/demo \
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

Test suite includes:
- happy path and sender completion behavior,
- retry exhaustion and token refresh edge cases,
- downstream HTTP status mapping alignment,
- failure contract fallback behavior,
- unit tests for exception mapping and PoC injectors.
