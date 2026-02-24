# Trust & Methodology Architecture

## Overview
This document outlines the safeguards and transparency mechanisms implemented to protect the user and comply with Your Money or Your Life (YMYL) content standards. The `DecisionEngine` relies solely on market data correlations, and we strive to represent these correlations accurately without presenting them as subjective or guaranteed financial advice.

## Data Sources
Our logic references generic profiles representing national averages.
1. **Model Baseline:** General configuration data (Year, Make, Model).
2. **Value Curves:** Estimations of historical retail pricing (`market_data.json`).
3. **Repair Incidents:** Aggregated common failure points and typical out-of-pocket costs (`faults_data.json`).
*When direct sources are missing, "national averages" or "general diagnostic buffer" defaults are applied gracefully.*

## Verdict Confidence
Because every combination of car, repair estimate, and geographic region is different:
- **`DecisionEngine.evaluate()`** returns a confidence score (0-100).
- If confidence dips **below 75**, the interface automatically downgrades its certainty.
- The UI triggers a *`"Lower-confidence estimate"`* warning instead of the standard data disclosure, advising the user that their specific reality might deviate significantly from typical values.

## Ethical Boundaries
- We do **NOT** act as financial advisors or legal appraisers.
- We do **NOT** guarantee a vehicle won't break down if the verdict is "STABLE".
- We do **NOT** tell users *who* to fix their car with, rather, we evaluate *if* it makes mathematical sense against the replacement friction.

*By adhering to these boundaries, we maximize user transparency and protect the brand from liability.*
