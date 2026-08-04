# Migrate remote version-check JSON parsing from org.json to Jackson

Replaced manual `org.json.JSONObject` parsing of the remote
version-check response with typed Jackson deserialization
(`RemoteVersionResponse`), fixing a version-check regression
introduced by the manual parsing approach, and upgraded the Gradle
toolchain alongside it.

**Owning ADR:** [0004](../../adr/0004-ktor-jackson-remote-version-check-client.md)

---

Shipped at HEAD `d505cc8` (backfilled entry — this predates docflow
adoption; no plan/todo file ever existed for it).
