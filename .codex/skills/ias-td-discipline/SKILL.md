---
name: ias-td-discipline
description: "TD authoring and execution discipline for integration-services. Use when drafting, reviewing, or executing Technical Debt (TD) tasks, defining required Role/STOP Conditions, or specifying build/test execution requirements and Architect vs Coder responsibilities."
---

# IAS TD Discipline Skill

Use this skill to author, review, or execute Technical Debt (TD) tasks with deterministic boundaries and explicit execution discipline. This skill is **primarily for Codex Architect**; Codex Coder uses it to enforce STOP Conditions and non-negotiable execution rules.

**Intent:** Normative (TD authoring + execution discipline).

## When to use
- You are writing or revising a TD task file.
- You are reviewing TD quality, determinism, or role separation.
- You are executing a TD and need STOP Conditions, build/test requirements, or task boundaries.

## Quick start
- Read `references/00_PURPOSE.md` for scope, precedence, and role separation.
- Use `references/10_TD_TEMPLATE_AND_ROLE.md` for mandatory template usage and Role field rules.
- Use `references/20_EXECUTION_DISCIPLINE.md` for build/test requirements and execution gates.
- Use `references/30_STOP_CONDITIONS.md` for the mandatory STOP Conditions block.
- Use `references/90_ACCEPTANCE_CHECKLISTS.md` for author/reviewer checklists.
- For TDs touching error handling, sender/receiver boundaries, transport calls, or retry/recovery logic, validate the
  TD against DD-016 chained normalization rules; TDs relying on `onException(Exception.class)` alone as a catch-all
  MUST be rejected or escalated.
- For TDs touching technical transport routes (HTTP/SOAP/FTP/etc.), require an explicit **Propagation Proof** section
  describing where the exception is thrown and how it reaches the canonical sender boundary. TDs that rely on
  “DefaultErrorHandler will propagate anyway” MUST be rejected unless proven by a deterministic test.

## Exclusions
- Naming conventions, package taxonomy, and error-handling semantics.
- Test design details (profiles, fixtures, isolation). See `ias-testing`.
- Build tool configuration or CI enforcement.

## Explicit Non-Scope
- Implementing TD work items or refactors.
- Modifying existing Design Decisions (DDs).
- Replacing the canonical TD template content; this skill augments it.

## Change History
- v1.1 (2026-01-26): Require unique TD numbers across `.codex/tasks/`.
- v1.0 (2026-01-26): Initial TD authoring & execution discipline skill.
