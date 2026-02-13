---
apply: by model decision
instructions: Use when creating or updating Use case specification
---

# UC-001: Example – User Registration Integration

**Purpose:**
This file serves as a template for user stories and use-cases.
Each use-case defines functional requirements and acceptance criteria.

---

## User Story

As a new user,
I want to register with email and password,
So that I can access the system.

---

## Actors

- **Primary Actor:** User registering into the system.
- **Secondary Actor:** CRM system storing user accounts.

---

## Preconditions

- CRM API is available.
- Email is not already registered.

---

## Triggers

- HTTP POST `/users/register` with payload `{ email, password }`.

---

## Main Flow

1. Receiver accepts the HTTP request.
2. Process validates email and password, hashes password.
3. Process prepares DTO for CRM API.
4. Sender calls CRM API with the DTO.
5. CRM responds with 200 OK.
6. Sender logs successful registration.

---

## Alternate Flows

- **Email already exists:** Process returns 409 Conflict.
- **CRM API unavailable:** Sender returns 503 Service Unavailable and pushes message to DLQ.

---

## Acceptance Criteria

- 201 Created is returned on success.
- Password is always stored as a hash.
- Correct HTTP status codes are returned on errors.
- Every request and response is logged with Correlation ID.

---

## Retrospective

See `../../.codex/retrospective/UC-001-user-registration-retrospective.md` for lessons learned.
