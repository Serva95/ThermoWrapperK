# Conventions

## Project

Project name: ThermoWrapperK.

Language: en-US throughout. All ADRs, plan items, and `_agent/` files
are written in English, regardless of the language used in
conversation with an agent.

## Project-Specific Practices

- **Version string encodes key dependency versions.** The `version`
  field in `build.gradle.kts` follows
  `<semver>-<springBootVersion>_<ktorVersion>_<postgresVersion>` (e.g.
  `1.3.0-3.5.16_2.3.12_42.7.4`). Bump the semver segment for any
  release and update the three trailing version tokens whenever the
  corresponding dependency changes, so the artifact version alone
  identifies its Spring Boot/Ktor/Postgres-driver combination.
- **`src/main/resources/application.properties` is intentionally
  untracked.** It holds local datasource credentials in plain text
  and was deliberately removed from git tracking (commit `790a781`),
  even though it is not listed in `.gitignore`. Never `git add` this
  file, nor the `src/main/resources/` directory as a whole, when it
  appears untracked in `git status`.

## ADR Files

ADR filenames use `NNNN-kebab-case-slug.md`, zero-padded to 4 digits,
with contiguous numbering and no reserved gaps.

Each ADR describes one decision. If a decision splits, supersede the
original ADR and create new ADRs rather than expanding scope inside a
single document.

Status lifecycle: `Proposed → Accepted → Implemented → (Superseded | Deprecated)`.

| Status | Meaning |
|---|---|
| Proposed | Draft. Decision authored but not yet approved. |
| Accepted | Decision approved; implementation authorised. Work item lives in `plan/todo/`. |
| Implemented | Code shipped (fast-forwarded to `main` + pushed). Work item moved to `plan/done/`. ADR is the authoritative spec the running system matches. |
| Superseded | Replaced by another ADR. The successor is named in `superseded-by:` metadata. |
| Deprecated | Was real; the world moved on; no successor. Capability is not being rebuilt. |

Terminal states (Superseded / Deprecated) are reachable from any prior
state.

Cross-references link by relative path to `adr/NNNN-*.md`.

## ADR Shapes

This project uses a single ADR shape. ADRs use `adr/0000-template.md`
and contain these sections in order: Context, Capability statement,
User stories / scenarios, Acceptance criteria, Out of scope, Open
questions, References, Revision History, Approvals.

## ADR Privacy

ADRs are internal artefacts. ADR numbers, ADR titles, and the
existence of the ADR catalogue must never appear in any string the
product emits to users: UI copy, API response bodies, error messages,
customer-visible log lines, public documentation, release notes,
marketing copy, or support communications.

Allowed references:
- Inline code comments tying a non-obvious choice to its ADR
  (`// see adr/0042-foo.md`).
- Commit messages and PR descriptions.
- Internal documents: `AGENTS.md`, `INDEX.md`, the `plan/` queue,
  `_agent/` files, internal runbooks.

Rule of thumb: if a non-builder could ever read the string, the ADR
reference comes out. Refer to the behaviour by its product-level name
instead.

## Multi-Agent Rules

A single agent owns this repo. The `_agent/` directory tracks live
state and history; no LOCKS discipline.

## Plan Folder

Pending and shipped work live in `plan/` at the repository root:

- `plan/todo/NNNN-<slug>.md` — pending work, lower numbers run first.
  Each file names the owning ADR(s), scope, and exit criteria.
- `plan/done/<YYYY-MM-DD>-<slug>.md` — shipped work, chronological. A
  `git mv` from `todo/` to `done/` is the completion event.

The completion event is: fast-forwarded to `main` + remote push
succeeded (see §Git Contract).

When a `plan/todo/` item ships, the file moves to `plan/done/` AND the
owning ADR(s)' `status:` advances from `Accepted` to `Implemented`.
`INDEX.md` is regenerated to match.

## Audit Trail Policy

Every substantive change to an Accepted ADR appends a new row to its
Revision History table.

Editorial changes — typos, formatting, link fixes — are excluded from
Revision History but must be flagged "editorial" in the commit message.

The Approvals table is populated when an ADR transitions to Accepted
and updated on every subsequent substantive revision.

## Git Contract

Commit messages follow Conventional Commits with a mandatory
`Rationale:` footer for any commit that touches an ADR.

- Signed commits: yes.
- ADR-revision tags `adr-NNNN-rN`: no.
- Co-Authored-By trailer on agent commits: no, unless the user
  explicitly asks for one on a given commit.

**Integration model:** direct-to-main, fast-forward only. Changes are
fast-forwarded onto `main`; no merge commits. The verify gate
(`./gradlew build`) runs locally and must pass before push. A change
is "shipped" when it is on `main` and pushed. Direct pushes to `main`
still require the user's explicit go-ahead per this repo's normal
push-authorization rules — this convention describes the *shape* of
integration, not a standing authorization to push unattended.
