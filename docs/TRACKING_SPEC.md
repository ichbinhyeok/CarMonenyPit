# Tracking Spec

## 1. Purpose
This document is the single-source tracking spec for AutoMoneyPit.

Use it to answer 4 questions:
- what events exist
- what properties they carry
- what the CSV log stores
- how current funnel performance should be interpreted

## 2. Tracking Stack

### A. Client-side GA4
- loaded in `layout.jte`
- blocked for automation and tagged test traffic
- used for behavior and funnel instrumentation

### B. Server-side CSV log
- logger: `CSV_LEAD_LOGGER`
- file: `automoneypit-leads.csv`
- used as the attribution source of truth for lead routing and waitlist submits

## 3. Canonical Dimensions
These dimensions should be treated as the canonical reporting fields.

- `event_type`
- `page_type`
- `intent`
- `verdict`
- `brand`
- `model`
- `detail`
- `referrer_path`
- `placement`

## 4. Canonical Intent Taxonomy
Only these values are valid.

- `SELL`
- `REPAIR`
- `WARRANTY`
- `VALUE`
- `WAITLIST`

Normalization rules:
- `FIX` -> `REPAIR`
- lowercase or mixed-case values are normalized to uppercase
- new CTA surfaces should emit canonical uppercase values directly

## 5. Event Inventory

### A. `calculator_submit`
- source: `index.jte`
- meaning: user submitted the homepage calculator
- key payload:
  - `page_type = home_calculator`
  - `situation`

### B. `verdict_shown`
- source: `result.jte`
- meaning: owner verdict page rendered
- key payload:
  - `page_type = calculator_result`
  - `verdict_state`
  - `brand`
  - `model`
  - `mileage`

### C. `cta_click`
- source: `layout.jte`
- meaning: user clicked a `/lead` CTA
- key payload:
  - `page_type`
  - `intent`
  - `verdict_state`
  - `placement`

### D. `lead_capture_view`
- source: `pages/lead_capture.jte`
- meaning: waitlist page rendered
- key payload:
  - `page_type`
  - `verdict_state`
  - `brand`
  - `model`
  - `detail`
  - `placement`
  - `intent`
  - `status`

### E. `lead_submit`
- source: `pages/lead_capture.jte` and `LeadController`
- meaning: waitlist form submitted
- client event:
  - lightweight GA4 conversion signal
- server event:
  - full attribution row written to CSV

### F. `share_open`
- source: `result.jte`
- meaning: user triggered receipt sharing

## 6. CSV Schema
The lead CSV currently writes:

```text
timestamp,event_type,page_type,intent,verdict,brand,model,detail,referrer_path,placement
```

Examples:
- `/lead` click row
- `/waitlist/submit` -> `lead_submit`

Important:
- CSV is the reliable source for attribution analysis
- GA4 is the reliable source for client-side behavior trends

## 7. Waitlist Tracking Rules

### A. Redirect Rules
- `/lead` stores context in session before redirecting to `/lead-capture`
- `/lead-capture?...` legacy query variants are folded back to clean `/lead-capture`
- session IDs must not appear in the URL

### B. Context Preservation
The following fields must survive the redirect into the waitlist form:
- `verdict`
- `brand`
- `model`
- `pageType`
- `detail`
- `placement`
- `intent`
- `referrerPath`

### C. SEO Safety
- `/lead-capture` must stay `noindex, nofollow`
- `/waitlist/submit` must not become crawlable

## 8. Page Type Guide
Common `page_type` values in current implementation:

- `home_calculator`
- `calculator_result`
- `pseo_fault`
- `pseo_mileage`
- `fault_hub`
- `lead_capture`

New page types should be stable, descriptive, and lowercase with underscores.

## 9. How To Read The Funnel
Primary funnel:

1. `calculator_submit`
2. `verdict_shown`
3. `cta_click`
4. `lead_capture_view`
5. `lead_submit`
6. partner `approved_action` after partner launch

Interpretation:
- low `verdict_shown -> cta_click`
  - CTA offer or placement problem
- low `cta_click -> lead_capture_view`
  - redirect or routing problem
- low `lead_capture_view -> lead_submit`
  - waitlist friction problem
- low `lead_submit -> approved_action`
  - partner quality or downstream conversion problem

## 10. Current State Evaluation (2026-03-20)

### A. SEO Read
- indexing is mostly no longer the bottleneck
- impressions are growing
- CTR is now the weak point

### B. Tracking Read
- attribution is now structurally preserved across the waitlist flow
- clean URL handling is fixed
- taxonomy is much healthier than before

### C. Business Read
- if `approvalPending=true`, this is still a validation funnel
- `lead_submit` is not revenue
- treat `lead_submit` as the current best internal conversion proxy

## 11. Operating Recommendation
Monitor these slices first:

- `page_type`
- `intent`
- `placement`
- `detail`

The main question to answer each week is:

`Which indexed pages generate the most qualified lead_submit volume per click?`

## 12. Known Limitations
- historical CSV rows from before taxonomy cleanup are noisy
- GA4 can undercount if scripts are blocked
- partner-side approval data is still outside this internal tracking spec

## 13. Audit Summary (2026-03-20)
This section records what was wrong during the March 20, 2026 tracking audit, what was fixed, and what must be checked next.

### A. What Was Wrong
- waitlist attribution broke across the redirect
  - `/lead` knew the source page context
  - `/lead-capture` and `/waitlist/submit` did not reliably preserve all of it
- legacy waitlist URLs were still query-based
  - example: `/lead-capture?verdict=TIME_BOMB&brand=toyota`
  - this created unnecessary crawlable variants and made tracking/debugging noisier
- session IDs could leak into URLs as `;jsessionid=...`
  - this is bad for clean URL hygiene and bad for SEO
- intent taxonomy was inconsistent
  - values like `FIX`, `repair`, `sell`, and `analyze` were mixed across surfaces
- event naming and CSV interpretation were inconsistent in docs
  - `lead_submit` vs `submit_lead`
  - old notes still described an outdated CSV meaning
- mileage CTA detail values were not clean enough for analysis
  - examples like `100000k` were less useful than canonical detail values

### B. What Was Fixed
- waitlist routing now normalizes to clean `/lead-capture`
- attribution context is preserved in session across:
  - `/lead`
  - `/lead-capture`
  - `/waitlist/submit`
- legacy query-style waitlist URLs are folded back to clean `/lead-capture`
- session tracking is forced to cookies so `;jsessionid=...` does not pollute URLs
- canonical intent taxonomy is now:
  - `SELL`
  - `REPAIR`
  - `WARRANTY`
  - `VALUE`
  - `WAITLIST`
- mileage detail tracking now uses canonical detail values like `150000-miles`
- tracking docs were rewritten to match implementation

### C. What This Means
- source attribution is now readable again
- future `lead_submit` rows are more trustworthy than historical rows
- tracking quality is no longer the main blocker
- the main blocker has moved to:
  - CTR
  - page-level click quality
  - eventual partner-side monetization

## 14. What To Check In The Next Review
At the next review, do not start by asking whether tracking exists. Start by validating whether tracking is still clean.

### A. Routing Integrity
Check that these are still true:
- `/lead` returns a `302`
- `/lead` routes to clean `/lead-capture` when `approvalPending=true`
- `/lead-capture?...` variants redirect to clean `/lead-capture`
- no `;jsessionid=...` appears in public URLs

### B. Attribution Integrity
Check recent CSV rows and confirm these fields are populated:
- `page_type`
- `intent`
- `verdict`
- `brand`
- `model`
- `detail`
- `placement`

If these are blank or noisy again, attribution regressed.

### C. Funnel Integrity
Compare:
- `cta_click`
- `lead_capture_view`
- `lead_submit`

Questions to answer:
- are users reaching the waitlist page after the CTA click?
- are waitlist submits attributable to a real page type and intent?
- which page types convert best?

### D. SEO / Tracking Boundary
Because the main SEO issue has shifted from indexation to CTR, the next review should check:
- which tracked pages are getting impressions but not clicks
- which tracked pages are getting clicks but not `lead_submit`
- whether the best traffic is coming from:
  - `/verdict/`
  - `/should-i-fix/`
  - `/models/`
  - `/guides/`

### E. Revenue Readiness
If partners are still not live:
- do not treat `lead_submit` as revenue
- treat it as a proxy conversion only

If partners are live:
- add partner-side `approved_action` measurement to the review

## 15. Next-Review Checklist
Use this exact checklist on the next checkpoint.

1. Confirm current date range being reviewed.
2. Confirm whether `approvalPending` is still `true` or `false`.
3. Confirm `/lead` and `/lead-capture` clean URL behavior.
4. Inspect the last 20 CSV lead rows for attribution completeness.
5. Review `cta_click -> lead_capture_view -> lead_submit` ratios.
6. Review top pages by impressions and identify zero-click pages.
7. Review top pages by clicks and identify zero-submit pages.
8. Decide whether the bottleneck is:
   - snippet / CTR
   - waitlist friction
   - weak traffic quality
   - partner-side monetization

## 16. Current Conclusion For This Audit
As of March 20, 2026:

- tracking structure: fixed
- tracking docs: updated
- attribution continuity: fixed
- waitlist URL hygiene: fixed
- remaining growth problem: not tracking, but CTR and monetization readiness
