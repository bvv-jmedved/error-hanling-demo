# Acceptance Checklists

## TD Author Checklist (Codex Architect)
- [ ] Use the canonical TD template (`doc/templates/workflow-templates.md`).
- [ ] Include a `Role` section with value `Codex Architect` or `Codex Coder`.
- [ ] Include an `Execution & Verification` section (exact name) with explicit commands.
- [ ] List targeted tests for production code changes.
- [ ] Insert the canonical STOP Conditions block and mandatory additions when `Role = Codex Coder`.
- [ ] Make steps deterministic, ordered, and unambiguous.
- [ ] List explicit Included and Excluded items in Scope.
- [ ] Make Acceptance Criteria objective and verifiable.
- [ ] Validate no conflicts with Design Decisions or skills.
- [ ] Confirm the TD targets the latest `ias-td-discipline` version, or explicitly documents a pinned older version.

## TD Execution Checklist (Codex Coder)
- [ ] Validate the Role and required TD sections are present.
- [ ] Run `./mvnw test -DskipTests=true` for production code changes.
- [ ] Run all targeted tests listed in the TD.
- [ ] Run every modified or newly created test unless explicitly waived.
- [ ] Stop and escalate on any STOP condition.
- [ ] Report any deviations or blockers explicitly.
