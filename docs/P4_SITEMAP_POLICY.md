# P4 Sitemap Policy

## New URLs Added (Exactly 6)

| URL | Type | Priority |
|---|---|---|
| `/faults` | Directory | 0.9 |
| `/fault/air-suspension` | Hub | 0.8 |
| `/fault/cvt-transmission` | Hub | 0.8 |
| `/fault/oil-consumption` | Hub | 0.8 |
| `/fault/timing-chain` | Hub | 0.8 |
| `/fault/torque-converter` | Hub | 0.8 |

## `lastmod` Strategy

### What We Do
- Use `fault_references.json` → `last_verified` date when available
- Fall back to fixed `DATASET_DATE` config (currently `2026-02-24`)
- Currently hardcoded as the fixed `lastModDate` in `SitemapController`

### What We NEVER Do
- Use `LocalDate.now()` — this creates false freshness signals
- Use "today" for all entries — Google sees this as manipulation
- Change lastmod without actual content changes

## Changefreq
- `/faults` directory: `weekly` (may add new hubs in future phases)
- `/fault/{slug}` hubs: `monthly` (content is database-driven, not frequently updated)

## Future Phases
If P5 adds model×fault leaf pages, they will follow the same policy:
- `lastmod` = date of last data update
- Never dynamic `now()`
- Must be added to this document before implementation
