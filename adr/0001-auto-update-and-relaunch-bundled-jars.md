---
adr: 0001
title: Automatic update and relaunch of bundled ThermoSmartSpring and ThermoTools jars
status: Implemented
date: 2020-12-05
owner: human (backfilled by docflow bootstrap)
supersedes:
superseded-by:
depends-on: ["0002", "0003", "0004"]
tags: [capability, core]
---

# ADR 0001 — Automatic update and relaunch of bundled ThermoSmartSpring and ThermoTools jars

## Context

ThermoWrapperK exists to keep two companion JVM applications
(`ThermoSmartSpring`, a web app, and `ThermoTools`) always running the
latest published build on a host, without a human redeploying them
manually. The wrapper is the always-on parent process; the two
companion jars are its supervised children.

## Capability statement

The system periodically checks a remote endpoint for the latest
available version of both jars, downloads a jar when a newer version
is published, and (re)launches it as a child JVM process — replacing
any already-running process for that jar.

## User stories / scenarios

- As the maintainer, I want the two companion jars to update
  themselves without me redeploying manually, so that I don't have to
  log into the host for every release.
- As the wrapper service, I want to detect a version change on a
  schedule, so that new releases roll out within a bounded time
  window (currently every 12 hours).

## Acceptance criteria

1. On startup, the wrapper compares the last known installed version
   (persisted per [ADR 0003](0003-persist-version-state-in-postgres-jpa.md))
   against the remote version-check response (per
   [ADR 0004](0004-ktor-jackson-remote-version-check-client.md)); if no
   prior state exists, it downloads and records the current remote
   version as a baseline.
2. A scheduled check runs every 12 hours (`Runner.scheduledRunner`)
   and re-queries the remote endpoint.
3. When the remote `webversion` or `toolsversion` differs from the
   last recorded value, the corresponding jar is downloaded and the
   old process is destroyed and replaced with a freshly started one.
4. Each jar runs as an independent child JVM process
   (`java -Xms512m -Xmx2g -jar <name>.jar`) supervised by a dedicated
   `Runnable` (`WebRunner`, `ToolsRunner`).
5. A failed jar execution (`JarExecutorException`) is caught and
   logged without crashing the wrapper process itself.

## Out of scope

- Rollback to a previous jar version if the new one fails to start.
- Health-checking the child processes beyond their exit code.
- Zero-downtime handoff between the old and new process (there is a
  fixed ~1.5s sleep before restart).

## Open questions

- None — this capability has been running in production since 2020;
  it was Accepted and Implemented long before this ADR was written
  down.

## References / cross-links

- `src/main/kotlin/it/srv/thermoWrapperK/Runner.kt`
- `src/main/kotlin/it/srv/thermoWrapperK/JarExecutor.kt`
- [ADR 0002](0002-spring-boot-kotlin-platform.md) — application platform
- [ADR 0003](0003-persist-version-state-in-postgres-jpa.md) — version-state persistence
- [ADR 0004](0004-ktor-jackson-remote-version-check-client.md) — remote version-check client

## Revision History

| Date | Revision | Author | Change |
|------|----------|--------|--------|
| 2020-12-05 | r1 | human | Initial implementation: scheduled check, download, relaunch (`ac0405a`). |
| 2020-12-14 | r2 | human | Fixed scheduler timing (`d7bb894`). |
| 2020-12-24 | r3 | human | Reworked the `Info`/state structure used to detect version changes (`975b2e8`, `3003462`, `cdc8d2d`). |
| 2026-08-04 | r4 | human+agent | Fixed the startup `@Bean fun executor()` — an implicit-`Unit` (void) bean method rejected by Spring Framework 6.2's stricter bean validation — by wrapping the startup logic in an `ApplicationRunner`. Behaviour unchanged (`7eb89ac`). |

## Approvals

| Role | Name | Date | Signature |
|------|------|------|-----------|

_No formal approval process existed before docflow adoption
(2026-08-04); shipping to `master` was the de-facto approval for every
revision listed above._
