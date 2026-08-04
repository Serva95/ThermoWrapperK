# Migrate to Spring Boot 3 (Spring Framework 6)

Upgraded Spring Boot 2.7.18 → 3.5.16, Kotlin 1.8.22 → 2.4.10, and the
JVM target 11 → 17. Migrated `javax.persistence` to
`jakarta.persistence` in the `Info` entity, moved the Gradle Kotlin
plugin config from the removed `kotlinOptions` DSL to
`compilerOptions`, dropped the unused Lombok dependency, and fixed a
startup `@Bean` method with an implicit-`Unit` (void) return type —
rejected by Spring Framework 6.2's stricter bean validation — by
wrapping it in an `ApplicationRunner`.

**Owning ADR:** [0002](../../adr/0002-spring-boot-kotlin-platform.md)
(also touches [0001](../../adr/0001-auto-update-and-relaunch-bundled-jars.md)
for the `ApplicationRunner` fix and
[0003](../../adr/0003-persist-version-state-in-postgres-jpa.md) for the
`jakarta.persistence` migration)

---

Shipped at HEAD `7eb89ac` (backfilled entry — this predates docflow
adoption; no plan/todo file ever existed for it).
