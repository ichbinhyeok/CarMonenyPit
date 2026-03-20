# Revenue Model & KPI Tracking

Primary tracking reference: see `docs/TRACKING_SPEC.md`.

## 1. Business Logic
AutoMoneyPit monetizes by routing users after a repair-vs-sell decision.

There are 2 operating phases:

### A. Waitlist Validation Phase
- Partner approval is not live yet.
- `/lead` sends users to the internal waitlist at `/lead-capture`.
- Revenue is not realized yet.
- The main goal is to validate:
  - search demand
  - CTA engagement
  - lead capture rate
  - attribution quality by page and intent

### B. Partner Routing Phase
- Partner approval is live.
- `/lead` sends users to a live external partner.
- Revenue is generated only when the downstream partner records a valid action.

## 2. Intent-to-Revenue Map
- `SELL`
  - typical destination: instant cash-offer / wholesale buyer network
  - used for `TIME_BOMB` scenarios
- `REPAIR`
  - typical destination: repair marketplace / estimate partner
  - used for normal stable repair flows
- `WARRANTY`
  - typical destination: vehicle service contract / extended warranty partner
  - used for high-mileage keep-the-car flows
- `VALUE`
  - typical destination: valuation / comparison flow
  - used for borderline or information-heavy decision flows

Note:
- `FIX` is not a reporting category anymore. Use `REPAIR`.

## 3. What Counts As Revenue
- `cta_click`
  - interest signal only
- `lead_capture_view`
  - waitlist exposure only
- `lead_submit`
  - internal conversion proxy only
- partner `approved_action`
  - real monetization event

Important:
- A `lead_submit` is not revenue.
- A `lead_submit` is only a useful proxy while partners are not live.

## 4. Event Inventory

### A. GA4 Events
- `calculator_submit`
  - user submitted the homepage calculator
- `verdict_shown`
  - owner-facing verdict page rendered
- `cta_click`
  - user clicked a `/lead` CTA
- `lead_capture_view`
  - waitlist page rendered
- `lead_submit`
  - waitlist form submitted
- `share_open`
  - receipt share action triggered

### B. CSV Lead Log
- file: `automoneypit-leads.csv`
- schema:
```text
timestamp,event_type,page_type,intent,verdict,brand,model,detail,referrer_path,placement
```

This CSV is required for operational analysis because it preserves attribution fields across the waitlist redirect.

## 5. KPI Hierarchy
Track KPIs in this order.

### A. Search / Acquisition KPIs
- impressions
- clicks
- CTR
- average position
- page-group performance by prefix:
  - `/verdict/`
  - `/should-i-fix/`
  - `/models/`
  - `/guides/`

### B. Funnel KPIs
- `verdict_shown -> cta_click`
  - measures CTA strength
- `cta_click -> lead_capture_view`
  - measures redirect integrity
- `lead_capture_view -> lead_submit`
  - measures waitlist friction

### C. Monetization KPIs
- `lead_submit -> approved_action`
  - only relevant after partner launch

## 6. How To Evaluate The Current State (2026-03-20)
Use these dates and numbers when interpreting the current situation.

### A. Search Console Read
- Window `2026-02-21` to `2026-03-19`
  - `9 clicks`
  - `600 impressions`
  - `1.50% CTR`
  - `12.33 avg position`
- Previous window `2026-01-24` to `2026-02-20`
  - `15 clicks`
  - `216 impressions`
  - `6.94% CTR`
  - `15.25 avg position`

Interpretation:
- impressions are up sharply
- average position improved
- CTR fell hard

Conclusion:
- indexation is no longer the main problem
- snippet-to-intent match and page-level CTR are now the main acquisition problem

### B. Page-Type Read
- `/verdict/` is the strongest traffic cluster
- `/should-i-fix/` is improving
- `/guides/` is indexed but still weak in demand capture

Conclusion:
- pSEO decision pages are carrying search demand
- guides should be treated as support content, not the main acquisition engine yet

### C. Monetization Read
- If `approvalPending=true`, the site is still in validation mode
- That means search growth does not equal revenue growth yet

Conclusion:
- the correct success metric right now is not affiliate revenue
- the correct success metric is:
  - higher qualified impressions
  - better CTR on pages already ranking
  - reliable `lead_submit` attribution by page type and intent

## 7. Practical Operating Scorecard
This is the simplest way to score current health.

### Healthy
- more pages indexed
- impressions growing
- clean waitlist routing
- `lead_submit` attributed to page type / intent / detail

### Unhealthy
- impressions grow but CTR falls
- waitlist submit volume cannot be tied back to source page
- protected pages such as `/lead-capture` leak crawlable variants
- partner revenue is discussed before partner routing is live

## 8. Current Verdict
As of `2026-03-20`, the project should be evaluated like this:

- SEO status: improving
- indexing status: mostly solved
- acquisition status: bottleneck has moved to CTR
- tracking status: structurally healthy after the waitlist cleanup
- monetization status: still pre-revenue if `approvalPending=true`

The right reading is:

`This is no longer an indexing problem. It is now a CTR optimization and waitlist-to-revenue readiness problem.`
