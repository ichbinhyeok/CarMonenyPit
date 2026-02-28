# Risk Detox Changelog

Date Anchor: 2026-02-28

## Scope
- Removed policy-risk copy triggers in templates/controllers.
- Unified canonical/robots URL generation on `app.baseUrl`.
- Replaced hardcoded freshness/year signals with dataset version wiring.
- Added a regression test to block reintroduction of banned patterns.

## Key Corrections Applied
1. `No invented claims` guard
- Removed unverifiable source/expert claims:
  - `NADA/KBB data` claim in `CarDecisionController`
  - `actual repair databases`, `owner reports`, `team of data analysts`, `thousands of data points`

2. Doorway copy coverage expanded beyond templates
- Softened urgency/partner list text in:
  - `src/main/jte/fragments/verdict_card.jte`
  - `src/main/java/com/carmoneypit/engine/web/VerdictPresenter.java`

3. Domain hardcoding cleanup with safe templating
- `pseo.jte` canonical now injected via controller.
- `index.jte` now takes `baseUrl` and builds schema JSON from `resolvedBaseUrl`.
- `robots.txt` sitemap now uses `app.baseUrl`.

4. Freshness-language cleanup in comments
- Removed "freshness" wording from `MarketContextService` comments only.
- Kept safe user recommendation to compare external listings (no claim of internal data usage).

5. Hardcoded year/spin/AI label cleanup
- Removed headline spin block in `pseo_landing.jte`.
- Replaced `Updated: 2026` with `Dataset: ${datasetVersion}`.
- Replaced `AI Diagnostic Summary` with `Vehicle Analysis Summary`.
- Replaced strict schema assertions in `pseo.jte`.

6. Inline affiliate micro-disclosure near CTAs
- Added concise disclosure link near CTA blocks in:
  - `pseo_landing.jte`
  - `fragments/verdict_card.jte`

## Files Updated
- `src/main/java/com/carmoneypit/engine/web/CarDecisionController.java`
- `src/main/java/com/carmoneypit/engine/web/RootController.java`
- `src/main/java/com/carmoneypit/engine/web/PSeoController.java`
- `src/main/java/com/carmoneypit/engine/web/VerdictPresenter.java`
- `src/main/java/com/carmoneypit/engine/service/MarketContextService.java`
- `src/main/jte/index.jte`
- `src/main/jte/pseo.jte`
- `src/main/jte/pseo_landing.jte`
- `src/main/jte/pseo_mileage.jte`
- `src/main/jte/fragments/verdict_card.jte`
- `src/main/jte/pages/about.jte`
- `src/main/jte/pages/contact.jte`
- `src/test/java/com/carmoneypit/engine/PolicyComplianceTest.java`

## Validation
- Added `PolicyComplianceTest` to fail builds when banned copy patterns reappear in `.java` and `.jte` under `src/main`.
