---
adr: 0005
title: Migrate ThermoWrapperK to Spring Boot 4.1.2
status: Proposed
date: 2026-10-30
owner: agent
supersedes:
superseded-by:
depends-on: [ThermoSmartSpring/adr/0001]
tags: [infrastructure, upgrade, spring]
---

# ADR 0005 — Migrate ThermoWrapperK to Spring Boot 4.1.2

## Context

The wrapper currently runs on **Spring Boot 3.5.16** (Kotlin 2.4.10,
Java 17, Ktor 2.3.12, Postgres driver 42.7.4). In late 2026 the 3.x
line has passed end of open-source support, and the sibling components
have already moved on: `ThermoSmartSpring` is on **Spring Boot 4.1.2**
(see `ThermoSmartSpring/adr/0001-migrate-to-spring-boot-4-1-2.md`,
Implemented) and `ThermoTools` is queued for the same target. Spring
Boot 4.1.2 was released 2026-10-22; the 4.0 line reaches EOL
2026-12-31 and the 4.1 line 2027-07-31, so 4.1.x is now the supported
maintenance line.

Keeping the wrapper on a 3.x base means a fleet that mixes a 2.7-line
backend with a 4.1-line app, with three different framework bases in
one deployment — a security and maintainability risk on a machine with
auto-updating jars.

## Capability statement

Upgrade the framework of the `ThermoWrapperK` service from Spring Boot
3.5.16 to **Spring Boot 4.1.2**, with no change to the observable
behaviour of the service: periodic remote version check, jar download,
child-process launch/relaunch of the two bundled jars, and Postgres
persistence of version state all behave exactly as specified by
[ADR 0001](0001-auto-update-and-relaunch-bundled-jars.md),
[ADR 0002](0002-spring-boot-kotlin-platform.md),
[ADR 0003](0003-persist-version-state-in-postgres-jpa.md), and
[ADR 0004](0004-ktor-jackson-remote-version-check-client.md).

## User stories / scenarios

- As a maintainer, I want all three components on the same supported
  Spring Boot line, so a new CVE has one upgrade path to follow.
- As the operator of the Raspberry Pi, I want the wrapper (the process
  that relaunches everything else) to run on a maintained framework,
  so the auto-update chain itself is not the fragile link.
- As a developer, I want the existing test suite to pass unchanged, so
  the migration is verified rather than assumed.

## Acceptance criteria

1. `build.gradle.kts` declares `org.springframework.boot` version
   `4.1.2` and the `io.spring.dependency-management` plugin is at a
   version compatible with Spring Boot 4 (or removed if the 3.5-era
   plugin is no longer needed).
2. The `version` string in `build.gradle.kts` is updated in the same
   commit to keep its `<springBootVersion>` token in sync
   (e.g. `1.3.0-4.1.2_2.3.12_42.7.4`, or with new Ktor/Postgres tokens
   if those are bumped to achieve criterion 1/4).
3. The Ktor client and Jackson deserialization path (ADR 0004) works
   unmodified, or with the minimum version bump required for
   Spring Boot 4 compatibility — `VersionManagerTest` passes with the
   mocked client and no real network call.
4. All `javax.*` imports that Spring Boot 4 no longer carries are
   migrated to `jakarta.*`; the JPA `Info` entity and repository layer
   (ADR 0003) load and persist against the same Postgres schema with
   no migration script required.
5. `./gradlew build` passes, including
   `ThermoWrapperKApplicationTests.contextLoads()` (verified against a
   reachable local Postgres, per the repo's verify-gate note).
6. No behaviour change: the 12-hourly version-check schedule, download
   URL handling, child-process launch/relaunch logic, and error
   logging are unchanged apart from what the framework migration
   mechanically requires.

## Out of scope

- Migration of `ThermoSmartSpring` (already done — `ThermoSmartSpring`
  ADR 0001) or `ThermoTools` (queued — `ThermoSmartSpring` ADR 0006).
- Any change to the remote version-check endpoint contract
  (`https://serva.click/prove/thermo.php`).
- Database schema changes or data migration.
- Upgrading Java beyond 17, Kotlin beyond what Spring Boot 4
  compatibility requires, or restructuring the codebase.
- Fleet-wide version alignment of the `<semver>` token in the wrapper
  `version` string (that follows the component's own release cadence).

## Open questions

- Whether Ktor 2.3.12 is usable as-is on Spring Boot 4.1.2 or needs a
  minor bump — decide at implementation time against the new BOM.
- Whether the `io.spring.dependency-management` 1.1.x plugin remains
  required on Spring Boot 4 or can be dropped.

## References / cross-links

- `build.gradle.kts`
- [ADR 0001](0001-auto-update-and-relaunch-bundled-jars.md) — capability preserved by this migration
- [ADR 0002](0002-spring-boot-kotlin-platform.md) — platform choice this ADR keeps
- [ADR 0003](0003-persist-version-state-in-postgres-jpa.md) — persistence this ADR must not break
- [ADR 0004](0004-ktor-jackson-remote-version-check-client.md) — HTTP client this ADR must keep working
- `ThermoSmartSpring/adr/0001-migrate-to-spring-boot-4-1-2.md` — precedent + target version (separate repository)
- `ThermoSmartSpring/adr/0006-migrate-thermotools-to-spring-boot-4-1-2.md` — the sibling component's migration (separate repository)
- [Spring Boot 4.1.2 release notes](https://spring.io/blog/2026/10/23/spring-boot-4-1-2-available-now)

## Revision History

| Date | Revision | Author | Change |
|------|----------|--------|--------|
| 2026-10-30 | r1 | agent | Initial draft. |

## Approvals

| Role | Name | Date | Signature |
|------|------|------|-----------|
