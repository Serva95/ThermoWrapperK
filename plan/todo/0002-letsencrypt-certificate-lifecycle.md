# Plan Item: Own the Let's Encrypt certificate lifecycle (wrapper side)

**Owning ADR:** [ThermoSmartSpring/adr/0003-trusted-letsencrypt-tls.md](../../ThermoSmartSpring/adr/0003-trusted-letsencrypt-tls.md) (cross-component catalogue in the conventions home; `status: Accepted`)

**Status:** queued — **work is NOT started** until explicitly authorised.

**Cross-repo scope note.** ADR 0003 spans **ThermoSmartSpring** (the Spring
app — the trust-anchor side) and **this repository** (deployment/lifecycle
owner). The app-side portion is queued at
`ThermoSmartSpring/plan/todo/0001-letsencrypt-app-side.md`. This item covers
the **wrapper-side** work only, per ADR 0003 AC-3 / AC-4 / AC-6: certbot
install, initial issue, scheduled renewal, renew→restart hook, expiry
monitor, and the failure alert channel.

**Preconditions (owner / network — not work of this repository):**

- The Fritz!Box forwards **port 80** (ACME HTTP-01) and **port 8443** (app
  TLS) to the Pi's LAN IP. AC-6 requires the HTTP-01 challenge to be
  reachable from the public internet at issuance and at every renewal; a
  failed HTTP-01 path must be *detectable, not silent*.
- The `2bquxcwzlpdfb6cz.myfritz.net` DDNS record stays current (AVM service;
  out of scope per ADR 0003).

## Scope

1. **certbot install** on the host (AC-3a) — one-time, idempotent
   provisioning by the wrapper (the wrapper currently supervises JVMs only;
   the provisioning mechanism is an open question below).
2. **Initial issue** (AC-3b) — `certonly` for
   `2bquxcwzlpdfb6cz.myfritz.net` via HTTP-01; the failure path must be
   surfaced, never swallowed.
3. **Scheduled renewal** (AC-3c) — certbot renewal hook and/or systemd timer,
   managed by the wrapper; renewal must succeed **with no manual step**.
4. **Renew→restart hook** (AC-3d) — after a successful renewal, relaunch the
   supervised **ThermoSmartSpring** child JVM so it loads the refreshed
   bundle (reuses the existing supervised-child process model, ADR 0001); a
   ThermoTools relaunch is required only if it terminates external TLS —
   confirm during work.
5. **Expiry monitor** (AC-4) — periodic `certbot certificates`-equivalent
   check reporting *valid until*, days-to-expiry, and a **PASS/FAIL verdict
   against the ≥ 30-days threshold**; candidate: record state in the
   wrapper's Postgres persistence (ADR 0003) alongside the version state.
6. **Alert channel** (AC-4) — a deliberately failed renewal must raise an
   operator alert; the mechanism is an open question below and needs an
   owner decision **before** work starts.

## Open questions (owner decision required before work starts)

- **Alert channel** — AC-4 requires an alert on renewal failure; the ADR
  does not fix the mechanism. Candidates: wrapper log line + Postgres state
  only (pull); reuse of the remote version-check endpoint (ADR 0004) as a
  pull-based status surface; external notification (e-mail/push).
- **certbot provisioning** — package install (apt) vs static/tarball
  download; depends on the Pi image. The wrapper has no package-manager
  integration today; this is a new capability surface.
- **Cert format for the app** — PEM-direct (the app-side item's preferred
  option; then the hook only restarts, no bundle to maintain) vs a
  file-path PKCS12 bundle the renew hook must additionally regenerate.
  Coordinate with the app-side item so both sides agree on one shape.

## Exit criteria (mapped to ADR 0003 acceptance criteria)

| # | Criterion | AC mapping |
|---|-----------|------------|
| 1 | certbot installed and functional on the host (`certbot --version`), provisioned idempotently by the wrapper. | AC-3a |
| 2 | Initial issue for `2bquxcwzlpdfb6cz.myfritz.net` succeeds via HTTP-01; the failure path is detectable, not silent. | AC-3b, AC-6 |
| 3 | A scheduled renewal is present and observable (timer/hooks/wrapper log); a forced renewal (`certbot renew --dry-run`, then a real run) succeeds with **no manual step**. | AC-3c |
| 4 | The renew→restart hook relaunches ThermoSmartSpring after renewal and the running service then presents the refreshed chain. | AC-3d |
| 5 | Expiry monitor reports *valid until* + days-to-expiry + **PASS/FAIL (≥ 30 days = PASS)**. | AC-4 |
| 6 | A deliberately failed renewal raises an operator alert (mechanism per the resolved open question). | AC-4 |
| 7 | `:80` serves **only** the ACME challenge window — no application traffic on port 80; `:8443` stays the sole app TLS surface. | AC-7 |
| 8 | `./gradlew build` GREEN, including `contextLoads()` against the live dev Postgres. | repo verify gate |

**Not satisfiable by this repository alone** (require the app-side item +
router forwards + a running deployment — the live-system go-live gates; they
do **not** block this item's completion):

- AC-1 — fresh iOS and Android devices, Wi-Fi and cellular, complete a TLS
  handshake to `https://2bquxcwzlpdfb6cz.myfritz.net:8443/` with no
  untrusted-certificate warning and no manual cert install
  (`openssl s_client … -verify_return_error` → `Verify return code: 0 (ok)`).
- AC-2 — served leaf issued by a Let's Encrypt CA, chain validates against
  the default store, SAN includes `2bquxcwzlpdfb6cz.myfritz.net`.
- AC-5 — the self-signed `thermo.p12` anchor no longer presented on `:8443`
  (supplied by the app-side item; the wrapper's switchover must not
  reintroduce it as a fallback).

## Completion event (per CONVENTIONS.md)

- Exit criteria 1–8 green.
- `git mv plan/todo/0002-letsencrypt-certificate-lifecycle.md plan/done/<YYYY-MM-DD>-0002-letsencrypt-certificate-lifecycle.md`.
- **ADR 0003 status is shared across two repositories.** ADR 0003 advances
  `Accepted → Implemented` (Revision History row + `INDEX.md` regeneration
  in the conventions home) **only when both** plan items — this one and
  `ThermoSmartSpring/plan/todo/0001-letsencrypt-app-side.md` — have shipped
  **and** the live-system ACs (AC-1, AC-2, AC-5) have been verified on the
  running deployment. The repository that closes last performs the flip.

## Dependencies

- **ADR 0003** (cross-component catalogue) — the spec.
- **`ThermoSmartSpring/plan/todo/0001-letsencrypt-app-side.md`** (separate
  repository) — the sibling item; the live switchover requires both sides.
- **ADR 0001** (this repository) — the supervised-child relaunch model the
  renew→restart hook reuses.
- **ADR 0004** (this repository) — candidate pull surface for status/
  alerting, if the owner selects it.
- **Router (Fritz!Box)** — port 80 + 8443 forwards (owner: network).

## Out of scope (per ADR 0003)

- Reverse-proxy / web-server front — certbot + the Spring server directly.
- Multi-host / wildcard certificates; client-side pinning.
- Domain / DDNS provisioning.
- Rate limiting, MFA, WAF/DoS hardening (separate ADR).
- The pre-existing `permitAll` list re-review — a recommended go-live
  precondition, tracked outside this ADR (flagged in ADR 0003 AC-7 / Open
  questions).
