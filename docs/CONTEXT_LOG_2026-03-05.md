# Context Log - 2026-03-05 (KST)

## Snapshot
- Project: AutoMoneyPit
- Branch: `main`
- Purpose: Preserve today's technical and SEO context for clean handoff in the next session.

## What Changed Today
1. Commit `db318e7` (already pushed)
- Hardened SEO canonicalization and analytics test filtering.
- Removed hardcoded Java path from Playwright scripts.
- Added/updated noindex and canonical handling on lead/report-related pages.
- Added synthetic test-traffic filtering in lead endpoints.

2. Commit `6981091` (already pushed)
- Added non-brand guide hub and pages:
  - `/guides`
  - `/guides/when-to-stop-repairing-your-car`
  - `/guides/sunk-cost-fallacy-car-repairs`
  - `/guides/car-repair-cost-vs-value`
- Added internal links to guides from header/footer/home.
- Added guides to sitemap generation.
- Implemented funnel event instrumentation:
  - `calculator_submit`
  - `verdict_shown`
  - `cta_click` (parsed from `/lead` params)
  - `lead_capture_view`
  - `lead_submit`

## MCP Search Console Findings (2026-03-05)
1. Property access
- Google: `sc-domain:automoneypit.com` accessible (`siteFullUser`).
- Bing: no connected account in MCP output.

2. Sitemap
- `https://automoneypit.com/sitemap.xml` is registered.
- Last submitted: `2026-03-05T10:10:05Z` (UTC).
- Last downloaded: `2026-03-05T10:10:07Z` (UTC).
- Errors: `0`, Warnings: `0`.
- Contents currently show `submitted: 999`, `indexed: 0`.
- MCP `sitemaps_submit` currently fails with `Permission denied` (read works, submit blocked).

3. URL inspection sample
- PASS (submitted and indexed):
  - `/`
  - `/models`
  - `/should-i-fix/2018-toyota-camry`
- Discovered but not indexed (new content):
  - `/guides`
  - `/guides/when-to-stop-repairing-your-car`

4. Recent performance summary (GSC)
- 7-day window `2026-02-23` to `2026-03-02`:
  - Clicks: `3`
  - Impressions: `95`
  - CTR: `3.16%`
  - Avg position: `12.58`

## Operating Decision Agreed Today
1. Do not freeze completely.
- Run in "observe mode" for `7-14 days` after deploy.

2. No major architecture changes during this window.
- Focus on indexing movement and query-level signal.

3. Use dated checkpoints
- Checkpoint A: `2026-03-12`
- Checkpoint B (go/no-go): `2026-03-19`

## Checkpoint Playbook
1. At Checkpoint A (`2026-03-12`)
- Re-check inspection status for all `/guides/*` URLs.
- Record whether any moved from `Discovered` to `Indexed`.
- Keep content and links stable unless critical bug.

2. At Checkpoint B (`2026-03-19`)
- If at least some guide URLs are indexed: continue current strategy.
- If still mostly `Discovered`: execute next wave:
  - stronger internal link density from high-crawl pages
  - one external seeding batch (community/social)
  - targeted refresh of guide intros and entity coverage

## Known Domain Note
- Main domain in production: `automoneypit.com`.
- `carmoneypit.com` observed as parked (Afternic/GoDaddy nameservers), not active launch domain.

## Validation Commands Used
1. Backend tests
```bash
./gradlew.bat test
```

2. Playwright smoke
```bash
npm run -s pw:test:managed -- tests/playwright/01-smoke-routes-and-headers.spec.ts
```

Both passed on 2026-03-05.
