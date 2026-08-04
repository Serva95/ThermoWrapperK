---
adr: 0002
title: Spring Boot + Kotlin as the application platform
status: Implemented
date: 2020-12-05
owner: human (backfilled by docflow bootstrap)
supersedes:
superseded-by:
depends-on: []
tags: [technology, platform]
---

# ADR 0002 — Spring Boot + Kotlin as the application platform

## Context

ThermoWrapperK needs a JVM-based runtime with dependency injection,
JPA auto-configuration, and background/scheduled bean wiring, for a
small always-on service with no inbound HTTP traffic. Kotlin was
chosen as the implementation language on the JVM; Spring Boot for its
starters (`spring-boot-starter-data-jpa`, `spring-boot-devtools`) and
autoconfiguration, avoiding hand-rolled infrastructure for a
single-purpose background service.

## Capability statement

The system runs as a Spring Boot application (`@SpringBootApplication`),
written in Kotlin, so that dependency injection, JPA
auto-configuration, and startup/background bean wiring are available
without hand-rolled infrastructure.

## User stories / scenarios

- As the maintainer, I want a single `bootJar` artifact I can run with
  `java -jar`, so deployment is a one-file copy.
- As the maintainer, I want Spring's DI/auto-configuration for the
  datasource and JPA repositories, so I don't hand-wire a connection
  pool or ORM.

## Acceptance criteria

1. The project builds a runnable Spring Boot fat jar via
   `./gradlew bootJar`.
2. Application context wiring for `InfoDAO`, `InfoRepository`, and the
   startup `ApplicationRunner` bean succeeds at boot.
3. The declared Kotlin and JVM toolchain versions in
   `build.gradle.kts` are mutually compatible with the declared Spring
   Boot version line.

## Out of scope

- Reactive/WebFlux stack.
- Spring MVC / a REST/HTTP API surface — this service exposes none.
- Spring Security — not needed; there is no inbound traffic to
  authenticate or authorise.

## Open questions

- None.

## References / cross-links

- `build.gradle.kts`
- [ADR 0001](0001-auto-update-and-relaunch-bundled-jars.md) — the capability this platform hosts
- [ADR 0003](0003-persist-version-state-in-postgres-jpa.md) — persistence built on this platform

## Revision History

| Date | Revision | Author | Change |
|------|----------|--------|--------|
| 2020-12-05 | r1 | human | Initial: Spring Boot 2.4.0, Kotlin 1.4.10, Java 11 (`ac0405a`). |
| 2020-12-14 | r2 | human | Spring Boot version bump (`d7bb894`). |
| 2021-01-20 | r3 | human | Spring/plugins version bump (`3b30013`). |
| 2021-04-07 | r4 | human | Spring/plugins version bump (`790a781`). |
| 2022-05-01 | r5 | human | Spring Boot 2.7 line + plugins bump (`c581e9f`). |
| 2023-02-24 | r6 | human | Spring Boot 2.7.9, Kotlin bumped toward the 1.8.x line (`d328549`). |
| 2026-08-04 | r7 | human+agent | Kotlin/toolchain bump fixing a version-check regression (`d505cc8`). |
| 2026-08-04 | r8 | human+agent | Major upgrade: Spring Boot 2.7.18 → 3.5.16, Kotlin 1.8.22 → 2.4.10, JVM target 11 → 17; migrated the Kotlin Gradle `kotlinOptions` DSL to `compilerOptions` (removed in Kotlin 2.4) (`7eb89ac`). |

## Approvals

| Role | Name | Date | Signature |
|------|------|------|-----------|

_No formal approval process existed before docflow adoption
(2026-08-04); shipping to `master` was the de-facto approval for every
revision listed above._
