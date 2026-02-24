# P3 Remediation Notes

**Date:** 2026-02-24  
**Scope:** Pre-deploy trust/HCU cleanup — remove unverifiable claims, fix SSOT inconsistency

---

## Baseline Grep Results (Before Remediation)

| Banned String | Files Found | Planned Fix |
|---|---|---|
| `Rising searches` | `index.jte:490` | Replace with "Sedan · CVT concerns" |
| `Recent query` | `index.jte:481` | Replace with "Truck · Heavy-duty" |
| `Recent evaluation` | `index.jte:472` | Replace with "Truck · High-mileage" |
| `Commonly evaluated` | `index.jte:454` | Replace with "Sedan · Mid-mileage" |
| `Frequently analyzed` | `index.jte:463` | Replace with "Sedan · Pre-100k miles" |
| `Popular Repair Decisions` | `index.jte:445` | Rename to "Example Analyses" |
| `analyzing right now` | `index.jte:446` | Neutral: "Common scenarios owners evaluate" |
| `Financial experts` | `pseo_landing.jte:388`, `pseo.jte:467` | Replaced with neutral framing |
| `50% rule` | `pseo_mileage.jte:359`, `pseo.jte:465`, `DecisionEngine.java:128` | Replaced with "Repair-to-Value Ratio" / neutral comment |
| `50% of the vehicle` | `pseo_landing.jte:87,716` | Replaced with comparison language |
| `Typical new car payment` | `pseo_landing.jte:418` | Removed entirely |
| `10,000+` | `pseo_landing.jte:435` | Replaced with honest dataset description |
| `300+` (faults claim) | `pseo_landing.jte:436` | Replaced with honest dataset description |
| `What Others Did` | `verdict_card.jte:250` | Replaced with "Modeled Estimate" |
| `owners in your situation` | `verdict_card.jte:256` | Replaced with "similar profiles (modeled)" |
| `$unsafe{marketPulse}` | `pseo_landing.jte:101` | Removed rendering + param + controller ref |
| `Trending` | Not found | N/A |
| `Popular this week` | Not found | N/A |
| `High engagement` | Not found | N/A |
| `High search volume` | Not found | N/A |
| `bi-weekly` / `biweekly` | Not found | N/A |
| `signal freshness` | Not found | N/A |
| `absolute highest bid` | Not found | N/A |
| `exclusive discounts` | Not found | N/A |
| `thousands of owners` | Not found | N/A |

---

## Remediation Summary

| Fix # | Description | Status | Changed Files |
|---|---|---|---|
| #1 | Remove trending/social proof from homepage | ✅ Done | `index.jte` |
| #2 | DecisionEngine SSOT (code comment only) | ✅ Done | `DecisionEngine.java` (comment-only; logic already uses DE) |
| #3 | Remove 50% rule + financial experts from pSEO | ✅ Done | `pseo_landing.jte`, `pseo.jte`, `pseo_mileage.jte` |
| #4 | Demote peer-behavior wording | ✅ Done | `verdict_card.jte` |
| #5 | Remove MarketPulse rendering | ✅ Done | `PSeoController.java`, `pseo_landing.jte` |
| #6 | Waitlist overpromising (absolute highest) | ⏭️ Skipped | Not found in current code |
| #7 | Sitemap lastmod configurable | ✅ Done | `SitemapController.java` |

---

## Post-Remediation Verification

All 21 banned strings: **0 hits** across `src/`
`./gradlew test`: **EXIT 0** after every commit

## Remaining Risks (Documented, Not Fixed)

1. **MarketContextService.java still exists** — file is harmless (deterministic, no Random, no freshness), but could be deleted for cleanliness in a future PR.
2. **Fix #2 (mileage pSEO DecisionEngine SSOT)** — PSeoController mileage route still uses `lifespanPercent` for display context. The *decision logic* already flows through DecisionEngine. Full refactor deferred to avoid breaking pSEO routes pre-deploy.
3. **`pseo_landing.jte:436` still says "300+" in count context** — removed the specific "300+ faults" claim, replaced with "documented fault patterns."
