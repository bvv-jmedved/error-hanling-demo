---
apply: by model decision
instructions: Use when creating or updating Use Case or Technical Debt specification
---

# Workflow Templates

**Intent:** Normative (templates and checklists).

**Purpose:**
This document provides reusable templates for writing use-cases, technical tasks, and project documentation.
They ensure consistency and clarity for both humans and AI contributors.

---

## Use Case Template (UC-xxx)

**Purpose:**
Defines a functional requirement (business-driven).

### User Story

_As a <actor>_
_I want <goal>_
_So that <benefit>_

### Actors

- Primary Actor: …
- Secondary Actor: …

### Preconditions

- …

### Triggers

- …

### Main Flow

1. …
2. …
3. …

### Alternate Flows

- …

### Acceptance Criteria

- [ ] …

---

### Test Metadata (for E2E Automation)

| Field                | Description                                                                                        |
|----------------------|----------------------------------------------------------------------------------------------------|
| **Process ID**       | The exact `@FlowDirection.process` value from `ProcessIds.*` (lowercase-hyphen).                   |
| **Sender**           | External source system (e.g., GOP, K2, LN). Transport (FTP/REST/etc.) is not a ProcessId identity. |
| **Receiver**         | Target system (e.g., K2, GIS, LN).                                                                 |
| **Profile**          | Always `e2e` for Codex-generated tests.                                                            |
| **Preconditions**    | External infra setup required (WireMock, RabbitMQ, FTP, etc.).                                     |
| **Input Source**     | Path to test payload or trigger condition (e.g., `src/test/resources/e2e/gop/customer.json`).      |
| **Expected Output**  | Expected HTTP status, file, or message body.                                                       |
| **Validation Steps** | How the test verifies the outcome (e.g., `assertJsonEquals`, `verifyQueueMessage`).                |
| **Dependencies**     | Required route builders or infra containers.                                                       |

#### Example (E2E-ready metadata)

| Field                | Example                                                             |
|----------------------|---------------------------------------------------------------------|
| **Process ID**       | gop-k2-customer                                                     |
| **Sender**           | GOP                                                                 |
| **Receiver**         | K2                                                                  |
| **Profile**          | e2e                                                                 |
| **Preconditions**    | RabbitMQ and K2 mock server running                                 |
| **Input Source**     | `src/test/resources/e2e/customer/createCustomer.json`               |
| **Expected Output**  | HTTP 200, contains `"status":"OK"`                                  |
| **Validation Steps** | Verify `K2PostReceiverRouteBuilder` was called with correct headers |
| **Dependencies**     | `CustomersProcessRouteBuilder`, `K2PostReceiverRouteBuilder`        |

### Manifest-driven E2E workflow

- Declare fixtures under `src/test/resources/fixtures/<source>/<target>/<process>/manifest.json`, where source/target
  are
  external systems (not transports or internal layers). New fixtures MUST use `manifestVersion: 3`; older manifests
  remain supported for gradual migration.
- Companion assets (`request.json`, `headers.json`, `query.json`, downstream WireMock/FTP payloads) must stay inside the
  same directory; `ManifestLoader` resolves paths relative to the manifest. For HTTP receivers, prefer a unified
  snapshot (`receivers[].request.snapshot`) capturing both request + expected response; legacy `expectedBody` remains
  supported only for flows not yet migrated.
- HTTP senders run through `ManifestTestExecutor.run(..)` / `runWithTokens(..)` and verify downstream expectations with
  `HttpReceiverAdapter`; FTP scenarios leverage `runFtp(..)`. WireMock mappings now match on method + URL only—body
  validation is handled in Java via `ValidationMode` (`EXACT` or `REGEX`).
- Use receiver entries to model downstream stubs: `type` (`http`/`ftp`), optional `id`, `validate` (defaults to `true`),
  request-specific `validationMode`, and `auxiliary` (skip verification unless explicitly overridden). Expected
  request/response bodies should stay concise; prefer `REGEX` when only a subset of the structure matters.
- Keep manifests and payloads under version control to document the expected integration contract.

---

### Retrospective

After implementation and commit, create a retrospective file in
`../../.codex/retrospective/UC-xxx-<shortname>-retrospective.md`.
Follow the standard retrospective prompt in `../../.codex/prompts/RETROSPECTIVE_PROMPT.md`.

The retrospective MUST capture:

- What slowed it down
- What conventions or assumptions were unclear
- What could be automated, clarified, or standardized next time

---

## Technical Debt Task Template (TD-xxx)

**Purpose:**
Defines a technical, non-functional change such as refactoring, cleanup, or modernization.

### Context

Describe the problem or source of technical debt.

### Goal

What is the target state after the task is completed?

### Scope

Clarify what is included and excluded.

### Steps

1. …
2. …
3. …

### Acceptance Criteria

- [ ] All tests remain green.
- [ ] No business functionality is changed.
- [ ] Code readability/maintainability improves.
- [ ] Documentation updated if boundaries or modules are affected.

#### Specialized technical-debt templates

- **Config refactor tasks:** use `../../.codex/templates/TD-Template-ConfigRefactor.md` and replace `<role>`,
  `<system>`, and `<legacy-key>` placeholders when migrating hyphenated Camel configuration keys.

### Retrospective

After implementation and commit, create a retrospective file in
`../../.codex/retrospective/TD-xxx-<shortname>-retrospective.md`.
Follow the standard retrospective prompt in `../../.codex/prompts/RETROSPECTIVE_PROMPT.md`.

The retrospective MUST capture:

- What slowed it down
- What conventions or assumptions were unclear
- What could be automated, clarified, or standardized next time

---

## Design Decision Entry

When a task leads to an architectural change, add an entry in `../design-decision/`:

- **ID:** DD-xxx
- **Date:** YYYY-MM-DD
- **Decision:** …
- **Alternatives:** …
- **Rationale:** …
- **Consequences:** …

---

## Commit Message Template

[optional body explaining what and why, not how]
Closes: #issue

- **Types:** feat, fix, refactor, test, docs, chore.
- **Scope:** module or package (e.g., gop-sender, logging, mapper).

---

## Pull Request Checklist

- [ ] Canonical AI rules followed (`../../.codex/README.md` and relevant skills).
- [ ] Code follows naming conventions (`../NamingConventions*.md`).
- [ ] Tests written/updated (unit, integration, and E2E).
- [ ] Correct test profile used (`integration` for integration, `e2e` for end-to-end).
- [ ] Logging with correlation ID and status/step enums.
- [ ] Test-only mapping helpers end with `Stub`, not `Mapper` or `Mapping`.
- [ ] MapStruct scanning restricted to production packages.
- [ ] Documentation updated (`../architecture/MODULE_OVERVIEW.md`, `../architecture/DESIGN_DECISIONS.md`,
  `../../.codex/README.md` and relevant skills).
- [ ] Acceptance criteria met.
- [ ] Retrospective file created.
- [ ] `mvn verify` executed before merge; all tests green.
