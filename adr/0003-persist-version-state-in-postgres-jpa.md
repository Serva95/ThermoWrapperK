---
adr: 0003
title: Persist update and version state in Postgres via Spring Data JPA
status: Implemented
date: 2020-12-05
owner: human (backfilled by docflow bootstrap)
supersedes:
superseded-by:
depends-on: ["0002"]
tags: [technology, persistence]
---

# ADR 0003 — Persist update and version state in Postgres via Spring Data JPA

## Context

The wrapper must remember, across restarts, which jar versions are
currently installed and when the last check/update happened, so it
does not re-download or re-launch unnecessarily on every boot. State
needs to survive host redeployments and disk wipes, ruling out a
local file.

## Capability statement

Version/state facts (`webversion`, `toolsversion`, `weburl`,
`toolsurl`, `lastsearch`, `lastupdate`, and optional `*extra` fields)
are stored as key/value rows in a single Postgres table (`info`) via a
generic `Info` JPA entity and Spring Data `CrudRepository`.

## User stories / scenarios

- As the wrapper, I want to load the last known state on startup, so
  that a restart doesn't trigger a spurious re-download.
- As the maintainer, I want state in a real database rather than a
  local file, so it survives disk wipes or redeployment to a new host.

## Acceptance criteria

1. `Info` is mapped as a JPA `@Entity` with `id`, `value`, `time`
   columns; `id` is the primary key.
2. `InfoRepository` (`CrudRepository<Info, String>`) provides
   key-based lookup; `InfoDAO` exposes the higher-level
   `findLastTemporal()` / `saveHash()` helpers used by `Runner` and
   `VersionManager`.
3. `spring.jpa.hibernate.ddl-auto=none` — the schema is managed
   outside the application, not by Hibernate auto-DDL.
4. Entity persistence annotations resolve against the
   `jakarta.persistence` namespace under Spring Boot 3 / Hibernate 6.

## Out of scope

- Schema migration tooling (Flyway/Liquibase) — none in use, schema
  changes are applied manually.
- Versioned/historical rows — only the latest value per key is kept,
  not a change history.

## Open questions

- None.

## References / cross-links

- `src/main/kotlin/it/srv/thermoWrapperK/model/Info.kt`
- `src/main/kotlin/it/srv/thermoWrapperK/repo/InfoRepository.kt`
- `src/main/kotlin/it/srv/thermoWrapperK/dao/InfoDAO.kt`
- [ADR 0002](0002-spring-boot-kotlin-platform.md) — platform this persistence layer runs on
- [ADR 0001](0001-auto-update-and-relaunch-bundled-jars.md) — the capability this state serves

## Revision History

| Date | Revision | Author | Change |
|------|----------|--------|--------|
| 2020-12-05 | r1 | human | Initial `Info` entity + repository/DAO (`ac0405a`). |
| 2020-12-24 | r2 | human | Restructured the `Info`/DB schema (`975b2e8`). |
| 2020-12-24 | r3 | human | Fixed nullable `Info` handling in `HashMap`-based state (`3003462`). |
| 2020-12-27 | r4 | human | Fixed `Info` data writes to the DB (`cdc8d2d`). |
| 2026-08-04 | r5 | human+agent | Migrated the entity from `javax.persistence` to `jakarta.persistence` for Spring Boot 3 / Hibernate 6 (`7eb89ac`). |

## Approvals

| Role | Name | Date | Signature |
|------|------|------|-----------|

_No formal approval process existed before docflow adoption
(2026-08-04); shipping to `master` was the de-facto approval for every
revision listed above._
