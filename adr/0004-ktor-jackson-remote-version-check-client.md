---
adr: 0004
title: Ktor client with Jackson deserialization for the remote version-check endpoint
status: Implemented
date: 2020-12-05
owner: human (backfilled by docflow bootstrap)
supersedes:
superseded-by:
depends-on: []
tags: [technology, http-client]
---

# ADR 0004 — Ktor client with Jackson deserialization for the remote version-check endpoint

## Context

The wrapper needs to ask a remote HTTP endpoint
(`https://serva.click/prove/thermo.php`) for the latest available jar
versions/URLs and parse the JSON response, from Kotlin coroutine-based
code (`suspend fun searchNewVersion()`).

## Capability statement

`VersionManager` uses the Ktor HTTP client (CIO engine) to call the
remote endpoint asynchronously, and deserializes the JSON response
into a typed `RemoteVersionResponse` data class using Jackson
(`jackson-module-kotlin`).

## User stories / scenarios

- As the wrapper, I want a coroutine-friendly HTTP client, so the
  periodic version check doesn't block a thread.
- As the maintainer, I want the response parsed into a typed model
  with unknown fields ignored
  (`@JsonIgnoreProperties(ignoreUnknown = true)`), so the remote
  endpoint can add fields without breaking the client.

## Acceptance criteria

1. `VersionManager.searchNewVersion()` performs a GET against the
   configured URL using Ktor's `HttpClient(CIO)` and returns a
   `HashMap<String, Info?>` built from the parsed response.
2. The HTTP client is injectable via constructor parameter, enabling
   `ktor-client-mock` in tests (`VersionManagerTest`) without a real
   network call.
3. JSON deserialization uses Jackson
   (`ObjectMapper().findAndRegisterModules()` /
   `jackson-module-kotlin`), not `org.json`.
4. A malformed or unreachable response is caught and logged
   (`log.error`), returning an empty result map rather than
   propagating the exception into the scheduled task.

## Out of scope

- Retry/backoff policy on failure — none; the next scheduled run
  (every 12h, per [ADR 0001](0001-auto-update-and-relaunch-bundled-jars.md))
  is the retry.
- Authentication against the remote endpoint — none in use.

## Open questions

- None.

## References / cross-links

- `src/main/kotlin/it/srv/thermoWrapperK/VersionManager.kt`
- `src/main/kotlin/it/srv/thermoWrapperK/model/RemoteVersionResponse.kt`
- [ADR 0001](0001-auto-update-and-relaunch-bundled-jars.md) — the capability this client serves

## Revision History

| Date | Revision | Author | Change |
|------|----------|--------|--------|
| 2020-12-05 | r1 | human | Initial: Ktor client (CIO) + `org.json.JSONObject` for manual JSON parsing (`ac0405a`). |
| 2022-05-01 | r2 | human | Backend endpoint moved from `http://serva.altervista.org/...` to the current host (`c581e9f`). |
| 2023-02-24 | r3 | human | Ktor bumped to 2.2.3 alongside an `org.json` version bump (`d328549`). |
| 2026-08-04 | r4 | human+agent | Replaced manual `org.json` parsing with typed Jackson deserialization (`RemoteVersionResponse`), fixing a version-check regression in the process (`d505cc8`). |

## Approvals

| Role | Name | Date | Signature |
|------|------|------|-----------|

_No formal approval process existed before docflow adoption
(2026-08-04); shipping to `master` was the de-facto approval for every
revision listed above._
