# Execution & Verification Discipline

## Execution Gate Levels

TDs MAY define different verification gates depending on role and scope.

### Coder Execution Gate (local, mandatory for Codex Coder)
- Must be deterministic and bounded in time.
- Uses targeted compilation and test commands only.
- Must NOT require full `./mvnw verify` unless explicitly stated.
- Is the authoritative gate for Codex Coder task completion.

### Long-Running Verification Commands
If a TD requires long-running commands (e.g. `./mvnw verify`):

- The TD MUST explicitly mark them as long-running.
- The TD MUST state an expected duration range (e.g. 7–15 minutes).
- Codex Coder MUST wait for completion and MUST NOT treat lack of output as a hang within the stated range.
- Absence of an expected duration is a TD authoring defect.

### CI / Full Verification Gate (authoritative, external)
- Typically executed by CI or human reviewer.
- May include `./mvnw verify` or broader profiles.
- Failure here invalidates the change even if Coder Gate was green.

## Normative Rules (MUST / MUST NOT)
- Run a compilation check for production code changes: `./mvnw test -DskipTests=true`.
- List explicit targeted test commands in `Execution & Verification` when production code changes. Codex Coder must execute them.
- Execute every modified or newly created test unless the TD explicitly documents a waiver and reason.
- Use only the commands listed in the TD or mandated by `ias-testing`; do not invent alternative commands.
- Diagnostic or exploratory commands MAY be run to investigate failures, but MUST NOT replace required execution commands or be used to satisfy acceptance criteria.
- Apply stricter rules when `ias-testing` or `ias-methodology` requires additional verification steps.
- Stop and report if any required command cannot run (missing profile, infra, or ambiguity).

## Advisory Guidance (SHOULD)
- Prefer the smallest targeted test set that validates the change.
- Document preconditions (profiles, infra, fixtures) alongside each command.
- For docs-only changes, state `Execution & Verification: No build/test required (docs-only)` explicitly.
