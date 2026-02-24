# Architecture Hardening Changelog
**Status**: Ready for Affiliate Approval
**Focus**: Security, SEO compliance, Revenue tracking

### 1. Unified Lead Flow Security
- Refactored `LeadController.java` to apply `URLEncoder.encode` before injecting unvalidated parameters into waitlist redirect strings.
- Upgraded the `sanitize()` method to strip null bytes and prepend an apostrophe to inputs starting with special CSV operator characters, effectively blocking CSV Injection.
- Confirmed Open Redirect validation via config-bounded properties (`app.partner.sellPartnerUrl` / `app.partner.repairPartnerUrl`).
- Created `LEAD_FLOW_ARCHITECTURE.md` to document the router's behavior pre/post affiliate approval.

### 2. De-Risking Market Pulse
- Renamed `MarketPulseService` to `MarketContextService`.
- Replaced the SEO-risky, `Random`-derived biweekly updates with deterministic, educational ranges of pricing.
- Added localized logic stating that independent shop vs. dealer differences dictate exact numbers.
- Integrated `MarketContextService` strictly within `PSeoController.java`.

### 3. Verdict Consistency Enforcement
- Established the `DecisionEngine` as the unquestioned Single Source of Truth for *both* pSEO and the live calculator.
- Implemented `VerdictConsistencyTest.java` holding invariant checks:
  - "civic 200k miles @ $4k bill should be TIME BOMB"
  - "camry 50k miles @ $800 bill should be STABLE"
- Wrote `VERDICT_POLICY.md` clarifying that UI-layer limit assumptions are forbidden.

### 4. Added Trust Disclosures (YMYL)
- Updated `verdict_card.jte` to check `result.confidence() < 75`. If the confidence drops, the user sees a specific warning downgrading the certainty ("Lower-confidence estimate — real-world costs may vary significantly"), establishing credibility under YMYL scrutiny.
- Documented these boundaries in `METHODOLOGY_TRUST.md`.

### 5. Configured Revenue Events
- Tracked main flow submissions (`calculator_submit`) inside `index.jte` `onsubmit` logic.
- Tracked organic virality via (`share_open`) applied inside `result.jte`'s clipboard copy function.
- Detailed tracking schemas in `REVENUE_MODEL.md`.

### Summary
All five phases of the pre-deployment architecture hardening roadmap are completed. The project compiles 100% cleanly and passes all test suites. Affiliate risks and Google HCU risks have been minimized.
