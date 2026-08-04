# AGENTS.md

This file provides guidance to coding agents working in this repository.

## What this repository is

ThermoWrapperK is a background Spring Boot / Kotlin service that
periodically polls a remote endpoint for version information, downloads
new versions of two external jars (`ThermoSmartSpring`, `ThermoTools`)
when available, and launches/relaunches them as child processes. It
persists version/state info in a Postgres-backed JPA entity (`Info`).
The artefacts in this repo (`adr/`, `plan/`, `_agent/`) are the
documentation-led layer added on top of the existing codebase and its
prior, informal commit history.

## Repository structure

- `adr/0000-template.md` — canonical ADR template (single shape).
- `adr/NNNN-<kebab-slug>.md` — one ADR per decision, contiguous
  numbering, no gaps.
- `INDEX.md` — table regenerated from every ADR's metadata block.
- `CONVENTIONS.md` — authoring rules (read before editing anything).
- `plan/todo/NNNN-<slug>.md` — pending work, lower numbers run first.
- `plan/done/<YYYY-MM-DD>-<slug>.md` — shipped work, chronological.
- `_agent/` — single-agent coordination: `ROLES.md`, `WORKLOG.md`,
  `CURRENT_FOCUS.md`, `HANDOFF.md`, `prompts/autonomous.md`.

## Hard rules when editing ADRs

These come from `CONVENTIONS.md` and override default behaviour:

- **One decision per ADR.** Splits become new ADRs that supersede;
  never expand scope inside an existing one.
- **Status lifecycle:** `Proposed → Accepted → Implemented →
  (Superseded | Deprecated)`.
- **ADR section order:** metadata → Context → Capability statement →
  User stories / scenarios → Acceptance criteria → Out of scope → Open
  questions → References → Revision History → Approvals.
- **Acceptance criteria are testable and numbered.**
- **ADRs are internal artefacts — never user-visible.** ADR numbers,
  ADR titles, and the existence of the ADR catalogue must NEVER appear
  in any string the product emits to users: UI copy, API response
  bodies, error messages, customer-visible log lines, public
  documentation, release notes, marketing copy, or support
  communications. The catalogue is a builder's tool, not a user-facing
  surface. References ARE allowed in: code comments
  (`// see adr/0042-foo.md`), commit messages, PR descriptions,
  internal docs, `AGENTS.md`, `CONVENTIONS.md`, `INDEX.md`, and the
  `plan/` queue. Rule of thumb: if a non-builder could ever see the
  string, the ADR reference comes out.

## Implementation work

- Start from the ADRs. Identify which ADRs a code change implements or
  affects before changing behaviour.
- If implementation reveals a capability gap or changed decision, update
  the relevant ADR rather than silently diverging.
- Add or update tests for implemented behaviour. Map tests back to ADR
  acceptance criteria where practical. Note: the existing
  `ThermoWrapperKApplicationTests.contextLoads()` test requires a local
  Postgres instance on `localhost:5432`; it is expected to fail without
  one and is excluded from the verify gate below for that reason.
- **Do not leak ADR identifiers into user-visible surfaces.** When
  writing error messages, log lines, or any output a non-builder could
  see, refer to the behaviour by its product-level name — never by ADR
  number, ADR title, or phrases like "per the ADR catalogue". The ADR
  link belongs in the commit message and (optionally) an inline code
  comment, not in the string the user reads.

## Audit trail and revision discipline

- Substantive ADR changes append a row to the Revision History table.
  Editorial changes (typos, formatting, link fixes) are excluded but
  flagged `editorial` in the commit message.
- Approvals table populates when an ADR is Accepted and updates on each
  later substantive revision.
- Regenerate `INDEX.md` from ADR metadata after any ADR status change
  or new ADR.

## Multi-agent workflow

A single agent owns this repo. The `_agent/` directory tracks live
state and history; LOCKS discipline is not in use.

## Plan folder

- A pending item gets a `plan/todo/NNNN-<slug>.md` file BEFORE work
  starts, naming the owning ADR(s), scope, and exit criteria.
- The completion event is: fast-forwarded to `main` + remote push
  succeeded. On completion, `git mv` the file to
  `plan/done/<YYYY-MM-DD>-<slug>.md` with a footer naming the HEAD SHA.
- The owning ADR(s) advance `Accepted → Implemented` on the same
  commit. Regenerate `INDEX.md`.

## Git contract

- Commit messages follow **Conventional Commits**.
- Mandatory `Rationale:` footer on any commit touching an ADR.
- Signed commits: yes.
- ADR-revision tags `adr-NNNN-rN`: no.
- Co-Authored-By trailer: no, unless the user explicitly asks for one.
- Cross-references between ADRs use relative paths (`adr/NNNN-*.md`).
- **Integration:** direct-to-main, **fast-forward only**. No merge
  commits on `main`. The verify gate (`./gradlew build`) runs locally
  and must pass before push. Completion event: fast-forwarded to
  `main` + remote push succeeded. Pushing to `main` still requires the
  user's explicit go-ahead each time, per this repo's standing
  push-authorization practice — this convention fixes the *shape* of
  integration (no PRs, ff-only), not a blanket authorization to push
  without asking.
