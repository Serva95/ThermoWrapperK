# ThermoWrapperK

A small always-on Spring Boot / Kotlin service that keeps two
companion applications — **ThermoSmartSpring** (web app) and
**ThermoTools** — up to date and running, without anyone having to log
into the host and redeploy them by hand.

## What it does

- Every **12 hours**, polls a remote endpoint for the latest published
  version of both jars.
- When a newer version is available, **downloads** the jar and
  **relaunches** it as a supervised child JVM process, replacing the
  previous one.
- Persists version/state information (last check, last update,
  installed versions) in **Postgres**, so a restart of the wrapper
  itself doesn't trigger a spurious re-download.

The wrapper exposes no HTTP API of its own — it has no controllers, no
security layer, nothing to browse to. It's infrastructure, not a
product surface.

## How it works

```
Runner (scheduler, every 12h)
  └─ VersionManager  ── Ktor client ──▶  remote version-check endpoint
        │                                (JSON, deserialized via Jackson)
        ▼
     InfoDAO / InfoRepository  ──▶  Postgres (`info` table)
        │
        ▼
     JarExecutor  ──▶  ThermoSmartSpring.jar  /  ThermoTools.jar
                        (child processes, `java -jar`)
```

See [`INDEX.md`](INDEX.md) and the [`adr/`](adr) folder for the
decisions behind this design, and [`AGENTS.md`](AGENTS.md) /
[`CONVENTIONS.md`](CONVENTIONS.md) if you're a coding agent working in
this repo.

## Requirements

- JDK **17+** (the Gradle wrapper is included, no local Gradle install
  needed)
- A reachable **Postgres** instance

## Configuration

`src/main/resources/application.properties` is **not tracked in git**
(it holds local datasource credentials) — create it yourself before
running:

```properties
spring.jpa.hibernate.ddl-auto=none
spring.datasource.url=jdbc:postgresql://localhost:5432/thermo
spring.datasource.username=<your-user>
spring.datasource.password=<your-password>
```

The `info` table schema is managed manually (no Flyway/Liquibase),
matching the `Info` entity in
`src/main/kotlin/it/srv/thermoWrapperK/model/Info.kt`.

## Running

```bash
./gradlew bootRun
```

or build a runnable jar and run it directly:

```bash
./gradlew bootJar
java -jar build/libs/ThermoWrapperK-*.jar
```

## Building & testing

```bash
./gradlew build        # compile + assemble the jar
./gradlew test          # full test suite
```

`ThermoWrapperKApplicationTests.contextLoads()` boots the full Spring
context and therefore needs a real Postgres instance reachable at the
configured `spring.datasource.url`; it fails without one. The other
tests (`VersionManagerTest`) are self-contained and use Ktor's
`MockEngine`, no network or database required.

## Tech stack

| Layer | Choice |
|---|---|
| Language / platform | Kotlin 2.4, Spring Boot 3.5, JVM 17 |
| Persistence | Spring Data JPA + Postgres |
| HTTP client | Ktor (CIO engine) |
| JSON | Jackson (`jackson-module-kotlin`) |
| Tests | JUnit 5 |

## License

[GNU AGPLv3](LICENSE.md).
