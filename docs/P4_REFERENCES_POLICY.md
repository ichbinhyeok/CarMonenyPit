# P4 References Policy

## Core Rule
**No numeric data imports from external sources.**

The `fault_references.json` file contains **links only** — URLs to external resources with a one-line paraphrase of what each link covers. We never:
- Copy repair cost numbers from external sites into our dataset
- Import failure rate statistics from external databases
- Scrape or replicate external data tables

## Why This Policy Exists
1. **HCU Compliance**: Google's Helpful Content Update penalizes sites that aggregate external data without adding value. Our value comes from the **DecisionEngine's analysis**, not copied numbers.
2. **YMYL Safety**: Making specific numeric claims sourced from third parties creates liability. Our numbers come exclusively from our own curated dataset.
3. **Freshness Honesty**: External data changes. By linking rather than copying, we avoid stale data while providing users access to current external sources.

## Reference Schema
```json
{
  "slug": {
    "last_verified": "YYYY-MM-DD",
    "sources": [
      {
        "name": "Human-readable source name",
        "url": "https://...",
        "retrieved_at": "YYYY-MM-DD",
        "note": "One-line paraphrase of what this source covers"
      }
    ]
  }
}
```

## Rules
- Each hub must have **3-6 reference sources**
- `last_verified` is the date when sources were manually checked
- `note` must be a **paraphrase**, not copied text
- URLs must point to informational resources, not affiliate links
- `retrieved_at` is the date the URL was accessed

## Maintenance
When updating references:
1. Visit each URL and verify it still exists
2. Update `last_verified` and `retrieved_at` dates
3. Do **NOT** import any numbers from the source into our dataset
