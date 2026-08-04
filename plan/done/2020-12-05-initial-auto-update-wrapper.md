# Initial implementation of the auto-update wrapper service

Built ThermoWrapperK from scratch: a Spring Boot + Kotlin background
service that periodically polls a remote endpoint, downloads new
versions of the `ThermoSmartSpring` and `ThermoTools` jars, launches
them as supervised child processes, and persists version/state
information in Postgres via Spring Data JPA.

**Owning ADRs:** [0001](../../adr/0001-auto-update-and-relaunch-bundled-jars.md),
[0002](../../adr/0002-spring-boot-kotlin-platform.md),
[0003](../../adr/0003-persist-version-state-in-postgres-jpa.md),
[0004](../../adr/0004-ktor-jackson-remote-version-check-client.md)

---

Shipped at HEAD `ac0405a` (backfilled entry — this predates docflow
adoption; no plan/todo file ever existed for it).
