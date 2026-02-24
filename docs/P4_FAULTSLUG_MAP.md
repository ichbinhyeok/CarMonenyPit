# P4 Fault Slug Map

## Slug Rules (Immutable)
- Lowercase
- Hyphen only separator
- `[a-z0-9-]` character set only
- Once created, slugs are **permanent** and must never change

## Active Slug Map

| Component (Dataset) | Final Slug | Priority Rule | Models |
|---|---|---|---|
| CVT Transmission / CVT Failure / CVT Shudder / CVT Whine / CVT Judder / CVT Transmission Failure | `cvt-transmission` | Normalized component (all CVT variants) | 7 |
| Timing Chain / Timing Chain Failure / Timing Chain Tensioner / Timing Chain Guides / Timing Chain Tensioner Failure | `timing-chain` | Normalized component | 5 |
| Oil Consumption / Oil Dilution / Excessive Oil Consumption / High Oil Consumption (Piston Rings) | `oil-consumption` | Normalized component | 7 |
| Torque Converter / Torque Converter Shudder | `torque-converter` | Normalized component | 4 |
| Air Suspension / Air Suspension Compressor / Quadra-Lift Air Suspension Failure | `air-suspension` | Normalized component | 4 |

## Collision Report

| Potential Collision | Resolution |
|---|---|
| "CVT Whine" vs "CVT Transmission" vs "CVT Failure" | All map to `cvt-transmission` — they describe the same system |
| "Timing Chain Tensioner" vs "Timing Chain Guides" | All map to `timing-chain` — all are timing chain system components |
| "Oil Dilution" vs "Oil Consumption" vs "Excessive Oil Consumption" | All map to `oil-consumption` — same root cause category |
| "Transmission Shudder" (GMC) vs "Torque Converter" | `Transmission Shudder` does NOT contain "torque converter" → maps to generic `transmission-shudder`, NOT an allowed hub. Only explicit "Torque Converter" components map to `torque-converter`. No collision. |
| "Air Suspension Compressor" (Range Rover) vs "Air Suspension" (Ram/Jeep) | Both map to `air-suspension` — sub-component of same system |

## Normalization Function
Single Source of Truth: `FaultHubService.normalizeToSlug(String component)`

The function uses ordered substring matching:
1. Contains "cvt" → `cvt-transmission`
2. Contains "torque converter" → `torque-converter`
3. Contains "timing chain" → `timing-chain`
4. Contains "oil consumption" or "oil dilution" or "excessive oil" → `oil-consumption`
5. Contains "air suspension" → `air-suspension`
6. Else → generic slug (not an allowed hub)
