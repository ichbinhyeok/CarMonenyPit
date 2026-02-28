# Playwright Testing Baseline (2026-02-28)

## Why this exists
This suite is designed to test more than "page loads":
- User journey credibility (can a real user navigate and act?)
- Ecosystem credibility (canonical, sitemap, robots, content depth, disclosure)
- Policy-risk regression (high-risk marketing phrases)

## Run commands
- Install dependencies: `npm.cmd install`
- Install browser: `npm.cmd run pw:install`
- Run against existing server (default `http://127.0.0.1:8080`): `npm.cmd run pw:test`
- Run with managed server startup (default `8091`): `npm.cmd run pw:test:managed`
- Open report: `npm.cmd run pw:report`

## Environment note
- Managed mode assumes Java 21 is available.
- Current script pins Java path to `C:\Program Files\Microsoft\jdk-21.0.10.7-hotspot` for stable CI/dev shell behavior.

## Test groups
- `01-smoke-routes-and-headers.spec.ts`
  - Static route health
  - `/lead` redirect+cache headers
  - `/report` noindex behavior
  - `robots.txt` + `sitemap.xml` sanity

- `02-content-trust-and-quality.spec.ts`
  - Homepage trust/disclosure checks
  - `should-i-fix` content depth + banned phrase checks
  - Verdict page evidence/method/disclaimer checks
  - Canonical + JSON-LD parse checks

- `03-navigation-and-ecosystem-fit.spec.ts`
  - End-to-end user journey (`/models` -> verdict -> `/lead-capture`)
  - Sitemapped family sampling with depth/uniqueness checks

- `04-credibility-sampling.spec.ts`
  - Multi-page sitemap sampling (`should-i-fix`, fault verdict, mileage verdict)
  - Trust framing, numeric context, and banned-phrase regression checks

## Scope note
This suite validates rendered behavior and published content integrity.
It does not replace deep business-logic unit tests in `src/test/java`.
