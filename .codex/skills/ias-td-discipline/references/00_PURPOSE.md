# Purpose, Scope, and Precedence

Define how to author and execute Technical Debt (TD) tasks with explicit boundaries and deterministic execution.

## Normative Rules (MUST / MUST NOT)
- Treat Design Decisions as binding. If a TD conflicts with a DD, correct the TD or escalate; do not proceed under conflict.
- Make architecture explicit. State intent, boundaries, and verification steps in the TD; do not infer or redesign architecture during execution.
- Prioritize determinism. Write steps and acceptance criteria so they are actionable and free of interpretation.
- Enforce role separation:
  - Codex Architect defines intent, scope, verification, and explicit test commands.
  - Codex Coder executes mechanically and stops on ambiguity.
- TD numbers MUST be unique across `.codex/tasks/`. A new TD MUST NOT reuse an existing number; verify uniqueness before creating or renaming task files.
- Any TD touching error handling, sender/receiver boundaries, transport calls, or retry/recovery logic MUST be checked
  against the DD-016 chained normalization rules. TDs that rely on `onException(Exception.class)` alone as a universal
  catch-all MUST be rejected or escalated.
- Any TD that modifies a technical call route MUST include at least one deterministic test that would fail if the
  technical route absorbs delivery failures (e.g., by leaving `DefaultErrorHandler` active and not propagating).
- Author TDs against the latest version of this skill unless the TD explicitly pins an older version and states why.
- Follow precedence: Task file > this skill > `.codex/skills/GOVERNANCE.md` > other documentation.
- Avoid enforcement changes. Do not require tooling/CI updates inside this skill; open a TD for enforcement changes.

## Advisory Guidance (SHOULD)
- Keep TD scope small and atomic to reduce interpretation risk.
- Prefer explicit inclusions/exclusions over descriptive prose.
- If architectural changes are required, create or reference a DD before assigning to Codex Coder.
