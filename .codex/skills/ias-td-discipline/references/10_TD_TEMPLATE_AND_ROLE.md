# TD Template and Role Requirements

## Normative Rules (MUST / MUST NOT)
- Use the canonical TD template in `doc/templates/workflow-templates.md` under “Technical Debt Task Template (TD-xxx)”.
- For configuration refactors, use `.codex/templates/TD-Template-ConfigRefactor.md` and fill all placeholders. Add new specialized templates only via a dedicated TD.
- Preserve template sections. Include all of the following in every TD:
  - Context
  - Goal
  - Scope (explicit Included/Excluded)
  - Steps (deterministic, ordered)
  - Acceptance Criteria
  - Retrospective (pointer only; content comes after implementation)
- Add a `Role` section directly below the title with exactly one value:
  - `Codex Architect`
  - `Codex Coder`
- Add an `Execution & Verification` section (exact section name) listing required build/test commands and checkpoints.
- If `Role = Codex Coder`, add a `STOP Conditions` section and include the canonical minimum block from `references/30_STOP_CONDITIONS.md` verbatim. Append any mandatory additions from that reference; do not remove or weaken the canonical block.

## Advisory Guidance (SHOULD)
- Keep the Role singular; split into multiple TDs if both Architect and Coder responsibilities exist.
- Place `Execution & Verification` after `Steps` for readability.
- Include explicit ownership (package/layer) in Scope when relevant.
