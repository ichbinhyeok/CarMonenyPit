# SEO measurement plan: 100 organic clicks per day

## Target and operating model

- North-star target: 100 non-brand organic clicks per day, measured as a 28-day rolling average.
- Planning equivalent: about 3,000 clicks per month.
- Required impression range: roughly 60,000-100,000 monthly impressions at a blended 3%-5% CTR.
- Current baseline from the supplied GSC export: about 0.14 clicks per day. This is a growth target, not a forecast or guarantee.

## Weekly scorecard

Track these in GSC for the last 28 days and compare with the preceding 28 days:

| Layer | Metric | Early target |
| --- | --- | --- |
| Coverage | Valid indexed sitemap URLs | 95%+ of intentional indexable URLs |
| Discovery | Pages receiving impressions | +15% per four weeks until the portfolio matures |
| Ranking | Queries in positions 4-20 | Grow every week; these are the main optimization pool |
| CTR | Clicks from positions 1-10 | 4%+ blended, reviewed by page/query pair |
| Growth | Non-brand organic clicks | 25/day, then 50/day, then 100/day |
| Engagement | Calculator completions | 10%+ of calculator entrances |
| Assistance | Second-opinion checklist opens | 5%+ of priority fault-page sessions |

## Page cohorts

Do not evaluate every URL as one pool:

1. Priority fault pages: Rogue CVT, Fusion/Escape coolant intrusion, Tesla Model 3 control arms, Ram exhaust-manifold bolts.
2. High-mileage pages: Odyssey 200k, Pilot 200k, Corolla 200k, GLC 150k, XC90 150k.
3. Vehicle decision pages: the representative-year `/should-i-fix/` set.
4. Linkable assets: repair-or-sell calculator and repair-estimate second-opinion checklist.
5. Directories and hubs: models, brands, model hubs, and fault hubs.

## Decision rules

- High impressions, position 1-10, low CTR: rewrite title and description around the exact query; do not add another URL.
- Position 4-20 and stable impressions: add query-specific evidence, answer blocks, and contextual internal links.
- Indexed with no impressions after 8-12 weeks: merge, noindex, or remove from the sitemap unless it supports a meaningful hub.
- Crawled/discovered but not indexed: check duplication, thin content, canonical consistency, orphaning, and server rendering.
- Tool or guide earns links but low search clicks: preserve it as an authority asset and strengthen links into commercial decision pages.

## Events

- `repair_sell_tool_open`
- `decision_tool_calculated`
- `second_opinion_guide_open`
- `second_opinion_request_copied`
- Existing `cta_click`

Review events by landing-page cohort. The SEO target is clicks, but these events show whether new traffic reaches a useful decision surface.

## Release checks

- Run `./scripts/seo-canary.ps1` against production after deployment.
- Confirm new URLs in the sitemap and submit the sitemap in GSC.
- Inspect the two new URLs and the five priority fault URLs in GSC.
- Record the release date as an annotation in the weekly scorecard.
- Avoid judging ranking impact for at least two full crawl/index cycles; monitor technical regressions immediately.
