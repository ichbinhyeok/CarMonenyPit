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

### C. Server-side partner events log
- logger: `CSV_PARTNER_LOGGER`
- file: `automoneypit-partner-events.csv`
- used as the revenue source of truth after a lead becomes a downstream partner action

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
- `lead_id`

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
- logging:
  - client GA4 event on render, including validation/success status
  - server CSV row from `LeadController` only on the first clean waitlist landing
- key payload:
  - `page_type`
  - `verdict_state`
  - `brand`
  - `model`
  - `detail`
  - `placement`
  - `intent`
  - `lead_id`
  - `status`

### E. `lead_submit`
- source: `pages/lead_capture.jte` and `LeadController`
- meaning: waitlist form submitted
- client event:
  - lightweight GA4 conversion signal
  - should preserve original source context and `lead_id`
- server event:
  - full attribution row written to CSV

### F. `share_open`
- source: `result.jte`
- meaning: user triggered receipt sharing

### G. `approved_action`
- source: `/partner/approved-action`
- meaning: a submitted lead reached a downstream partner outcome worth tracking
- authentication:
  - requires `app.partner.callbackToken`
  - accepts `X-Partner-Token` header or `token` parameter
- key payload:
  - `leadId`
  - `partner`
  - `approvedAction`
  - `revenueUsd`
  - `currency`
  - `status`
  - `note`

## 6. CSV Schema
The lead CSV currently writes:

```text
timestamp,event_type,page_type,intent,verdict,brand,model,detail,referrer_path,placement,lead_id
```

Examples:
- `/lead` click row
- `/waitlist/submit` -> `lead_submit`

Important:
- CSV is the reliable source for attribution analysis
- GA4 is the reliable source for client-side behavior trends

## 6A. Partner Event CSV Schema
The partner event CSV currently writes:

```text
timestamp,event_type,lead_id,partner,approved_action,revenue_usd,currency,status,note
```

Important:
- this log is keyed by `lead_id`
- join this log back to `automoneypit-leads.csv` on `lead_id` to recover the original source page and intent
- this is the correct place to track approved outcomes and revenue, not the waitlist CSV

### Partner callback example

```bash
curl -X POST "https://automoneypit.com/partner/approved-action" \
  -H "X-Partner-Token: $APP_PARTNER_CALLBACK_TOKEN" \
  -d "leadId=abc123xyz" \
  -d "partner=peddle" \
  -d "approvedAction=sold_to_partner" \
  -d "revenueUsd=125.50" \
  -d "currency=USD" \
  -d "status=paid" \
  -d "note=manual close"
```

## 7. Waitlist Tracking Rules

### A. Redirect Rules
- `/lead` stores context in session before redirecting to `/lead-capture`
- `/lead-capture?...` legacy query variants are folded back to clean `/lead-capture`
- session IDs must not appear in the URL

### B. Context Preservation
The following fields must survive the redirect into the waitlist form:
- `leadId`
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
- `/partner/approved-action` must not be exposed without a callback token

## 8. Page Type Guide
Common `page_type` values in current implementation:

- `home_calculator`
- `calculator_result`
- `pseo_fault`
- `pseo_mileage`
- `fault_hub`
- `lead_capture`

New page types should be stable, descriptive, and lowercase with underscores.

Important:
- `/should-i-fix/*` is currently an organic entry surface, not a lead CSV page type by itself
- `/models/*` is currently an organic directory / model-hub surface, not a lead CSV page type by itself
- these surfaces are evaluated mainly through GSC, click-through into calculator/report flows, and downstream lead attribution after a CTA is clicked

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
- once partner callbacks are live, treat `approved_action` and `revenue_usd` as the monetization truth

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
- partner-side attribution depends on valid `lead_id` handoff and callback token setup

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
- downstream partner outcomes can now be logged into a separate revenue CSV keyed by `lead_id`

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
- if partner flows are live, which `lead_id` values actually become approved actions?

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
- confirm callback token is configured and partner events are landing in `automoneypit-partner-events.csv`

## 15. Next-Review Checklist
Use this exact checklist on the next checkpoint.

1. Confirm current date range being reviewed.
2. Confirm whether `approvalPending` is still `true` or `false`.
3. Confirm `/lead` and `/lead-capture` clean URL behavior.
4. Inspect the last 20 CSV lead rows for attribution completeness.
5. Review `cta_click -> lead_capture_view -> lead_submit` ratios.
6. Review partner `approved_action` rows and join them back to lead CSV by `lead_id`.
7. Review top pages by impressions and identify zero-click pages.
8. Review top pages by clicks and identify zero-submit pages.
9. Decide whether the bottleneck is:
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

## 17. Update Note (2026-04-01)
This section records tracking-relevant changes shipped after the March 20 audit.

### A. What Changed
- pSEO fault and mileage pages now render waitlist-aware CTA copy when `approvalPending=true`
- `/should-i-fix/*` entry pages now surface stronger decision-threshold copy higher on the page
- `/models/{brand}/{model}` pages are being upgraded from plain directories toward model decision hubs
- top opportunity `/should-i-fix/*` pages now receive more model-specific intro and FAQ copy for US-market repair decisions

### B. What Did Not Change
- no canonical event names changed
- no CSV schemas changed
- no canonical intent values changed
- the main attribution path is still:
  - `cta_click`
  - `lead_capture_view`
  - `lead_submit`
  - partner `approved_action`

### C. How To Interpret This
- these changes are expected to affect CTR and click quality before they affect downstream submit volume
- if pSEO landing pages become more honest about waitlist mode, short-term CTA click-through may dip while submit quality improves
- `/should-i-fix/*` and `/models/*` should be judged first by:
  - GSC impressions
  - GSC CTR
  - click-through into calculator / report / lead flows
  - not by raw lead CSV volume alone

### D. What To Check Next
- whether top `/should-i-fix/*` pages with positions 1-10 gain clicks after the new copy
- whether pSEO fault and mileage pages still preserve clean waitlist routing after CTA copy changes
- whether the new model hubs improve internal handoff into year-specific decision pages

### E. Practical Reading Guide
- if CTR rises first on `/should-i-fix/*` without an immediate lead spike, that is still a good sign
- page-level copy changes should be judged in this order:
  - impressions
  - CTR
  - calculator / report handoff
  - `lead_submit`
- do not expect all model pages to move at once; watch the highest-opportunity models first:
  - Camry
  - Altima
  - Accord
  - CR-V
  - CX-5
