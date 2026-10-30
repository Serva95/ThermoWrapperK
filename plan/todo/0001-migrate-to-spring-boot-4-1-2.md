# Plan Item: Migrate ThermoWrapperK to Spring Boot 4.1.2

**Owning ADR:** [adr/0005-migrate-to-spring-boot-4-1-2.md](../adr/0005-migrate-to-spring-boot-4-1-2.md)

**Status:** queued — **work is NOT started** until explicitly authorised.

## Scope

Upgrade `ThermoWrapperK` from Spring Boot 3.5.16 to **Spring Boot 4.1.2** in `build.gradle.kts`:

1. Bump `org.springframework.boot` to `4.1.2`; adjust
   `io.spring.dependency-management` to the required version (or
   remove it if the SB 4 Gradle plugin no longer needs it).
2. **`version` string token sync in the same commit** (repo hard
   rule): `1.3.0-3.5.16_2.3.12_42.7.4` → `1.3.0-4.1.2_<ktor>_<postgres>`,
   updating the Ktor and Postgres tokens only if a bump is required for
   Spring Boot 4 compatibility.
3. Resolve any `javax.*` → `jakarta.*` requirements introduced by the
   framework migration (JPA `Info` entity and repository layer must
   keep working — ADR 0003).
4. Keep the Ktor client + Jackson version-check flow behaviour-neutral
   (ADR 0004); use the mock-client tests, no real network calls.
5. Run the full test suite including
   `ThermoWrapperKApplicationTests.contextLoads()` (local Postgres
   required — see repo AGENTS verify-gate note).

**Run order:** this item is the **first** of the two remaining
Spring Boot 4.1.2 migrations (it is the lower-risk, single-major
upgrade and the wrapper is the component that relaunches the other two
jars). The `ThermoTools` migration
(`ThermoSmartSpring/plan/todo/0002-migrate-thermotools-spring-boot-4-1-2.md`,
separate repository) is deliberately sequenced **after** this one.

## Exit criteria (mapped to ADR 0005 acceptance criteria)

| # | Criterion | AC mapping |
|---|-----------|-----------|
| 1 | `build.gradle.kts` declares Spring Boot `4.1.2`; dependency-management plugin at a SB 4-compatible version. | AC-1 |
| 2 | `version` string token updated in the same commit (hard rule). | AC-2 |
| 3 | Ktor/Jackson version-check flow unmodified or minimally bumped; `VersionManagerTest` passes. | AC-3 |
| 4 | `javax.*` → `jakarta.*` migration complete where required; `Info` entity + repository intact. | AC-4 |
| 5 | `./gradlew build` passes (with local Postgres available for `contextLoads()`). | AC-5 |
| 6 | No behavioural change to scheduling, download, launch/relaunch, or error logging. | AC-6 |

## Completion event (per CONVENTIONS.md)

- `./gradlew build` passes (exit criterion 5).
- ADR 0005 status advances `Proposed` → `Accepted` on go-ahead of
  work, then `Accepted` → `Implemented` on ship (Revision History row
  appended, `INDEX.md` regenerated).
- `git mv plan/todo/0001-migrate-to-spring-boot-4-1-2.md plan/done/<YYYY-MM-DD>-0001-migrate-to-spring-boot-4-1-2.md`
  with a footer naming the HEAD SHA.

## Dependencies

- **ADR 0005** (above) — the spec.
- Local Postgres reachable for the `contextLoads()` test (pre-existing
  requirement, per AGENTS note).
- **After this item:** the `ThermoTools` migration
  (`ThermoSmartSpring/plan/todo/0002-migrate-thermotools-spring-boot-4-1-2.md`,
  separate repository) is unblocked and becomes the only remaining
  Spring Boot 4.1.2 item in the fleet.

## Out of scope

- Migration of `ThermoSmartSpring` (done — `ThermoSmartSpring` ADR 0001)
  or `ThermoTools` (item 0002 in the `ThermoSmartSpring` queue).
- Any change to the remote version-check endpoint contract (ADR 0004).
- Database schema changes or data migration.
- Renaming/restructuring the codebase beyond what the framework migration
  mechanically requires.

## Push authorisation

Per the repo's standing practice, a push to `origin` requires an
explicit go-ahead from the owner for each push.
