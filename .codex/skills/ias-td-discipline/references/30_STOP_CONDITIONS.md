# STOP Conditions (Codex Coder)

## Normative Rules (MUST / MUST NOT)
- Include a `STOP Conditions` section whenever `Role = Codex Coder`.
- Insert the **Canonical Minimum STOP Conditions** block below verbatim.
- Add extra STOP conditions only after the canonical block; do not remove or weaken the canonical block.
- Stop work immediately when any STOP condition is met and request clarification.

## Canonical Minimum STOP Conditions (copy verbatim)

- STOP if a task instruction conflicts with a Design Decision, skill, or architecture intent; escalate the conflict before proceeding.
- STOP if required TD sections are missing (Role, Execution & Verification, Acceptance Criteria, or Steps).
- STOP if targeted test commands are missing or ambiguous for production code changes.
- STOP if required inputs/files are missing or steps cannot be executed deterministically.
- STOP if the task requires architectural choices not explicitly defined in the TD.

## Mandatory Additional STOP Conditions (append after canonical block)

- STOP if tests would need to be weakened, deleted, or bypassed to satisfy the TD unless explicitly authorized by the TD.
- STOP if execution would require modifying deprecated code or artifacts that the TD scope excludes.

## Advisory Guidance (SHOULD)
- Add STOP conditions for known project-specific risks (e.g., missing infra, unavailable credentials) when relevant.
- Keep STOP conditions short and unambiguous.
