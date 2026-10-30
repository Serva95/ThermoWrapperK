# ADR Index

Generated from ADR metadata blocks. Treat as derived, not hand-edited
— regenerate after any ADR change.

| ADR | Title | Status | Supersedes | Superseded by | Depends on |
|-----|-------|--------|------------|----------------|------------|
| [0001](adr/0001-auto-update-and-relaunch-bundled-jars.md) | Automatic update and relaunch of bundled ThermoSmartSpring and ThermoTools jars | Implemented | — | — | 0002, 0003, 0004 |
| [0002](adr/0002-spring-boot-kotlin-platform.md) | Spring Boot + Kotlin as the application platform | Implemented | — | — | — |
| [0003](adr/0003-persist-version-state-in-postgres-jpa.md) | Persist update and version state in Postgres via Spring Data JPA | Implemented | — | — | 0002 |
| [0004](adr/0004-ktor-jackson-remote-version-check-client.md) | Ktor client with Jackson deserialization for the remote version-check endpoint | Implemented | — | — | — |
| [0005](adr/0005-migrate-to-spring-boot-4-1-2.md) | Migrate ThermoWrapperK to Spring Boot 4.1.2 | Proposed | — | — | ThermoSmartSpring/0001 |
