---
apply: by model decision
instructions: Use when creating or updating Technical Debt specification
---

# TD-001: Example – Refactor Legacy Logging

**Purpose:**
This file serves as a template for technical debt tasks.
Each task defines technical, non-functional improvements to the codebase.

---

## Context

Current logging is inconsistent:
- Some routes log raw strings without correlation IDs.
- Error messages are not structured, making monitoring harder.

---

## Goal

Refactor logging across all routes to use the unified structured logging approach with Correlation ID.

---

## Scope

**Included:**
- Updating all `log.info()` and `log.error()` calls in process routes.
- Adding structured logging in sender/receiver adapters.

**Excluded:**
- Business logic refactor.
- Monitoring dashboards (to be handled separately).

---

## Steps

1. Identify all legacy log statements.
2. Replace with structured logging API.
3. Verify correlation ID is always present.
4. Update unit tests where log output is asserted.

---

## Acceptance Criteria

- [ ] All logs use structured logging format.
- [ ] Correlation ID is present in every log entry.
- [ ] No regression in business functionality.
- [ ] All tests remain green.
- [ ] Documentation (`../../.codex/README.md` and relevant skills) updated.

---

## Retrospective

See `../../.codex/retrospective/TD-001-logging-refactor-retrospective.md` for lessons learned.
