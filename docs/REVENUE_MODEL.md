# Revenue Model & KPI Tracking

## Business Logic
The revenue generation of AutoMoneyPit relies on targeted affiliate routing upon `DecisionEngine` evaluation.

1. **Pre-Approval Lead Generation** (Pending Partner Approval):
   Users are placed on an internal waitlist. This provides traffic validation without burning affiliate quotas.
2. **Post-Approval Action Flow** (Approved Partners):
   - **`SELL` / TIME BOMB verdicts**: Routing to an instant cash-offer network (e.g. Peddle) via `app.partner.sellPartnerUrl`.
   - **`FIX` / STABLE verdicts**: Routing to a nationwide repair certification network (e.g. RepairPal) via `app.partner.repairPartnerUrl`.

## Revenue Examples
Based on current average partner payouts in the automotive sector:
- **Repair Booking Lead:** ~$10 per qualified appointment.
- **Selling Offer Completion:** ~$25 per accepted bid/truck scheduling.

Because all out-bound pathways pass through `LeadController.java`, we can easily track:
- Total hits to `/lead`
- Conversions to `sellPartnerUrl` vs `repairPartnerUrl`

## Defined Events for Google Analytics 4 (GA4)
- **`calculator_submit`**: Triggered upon standard interact tool submission on `index.jte`.
- **`share_open`**: Triggered when the user shares their resulting "Diagnostic Receipt" URL.
- **`cta_click`**: Tracked via `app.js` or `layout.jte` when users follow external links (specifically through the unified router).

## Definition of Success
- **Clicks**: Number of times users interact with the main CTA.
- **Approved Actions (Conversions)**: Validated leads on the affiliate side.
- Note: A `submit_lead` is internally tracked via `lead.csv`, but does not equal an `approved_action` on the affiliate's end until the user books the diagnostic or hands over the keys.
