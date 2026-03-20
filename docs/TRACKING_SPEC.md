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
