# P4 Query Evidence

## Selection Methodology
1. **Coverage scoring** (local dataset only): weighted combination of model count, occurrence rate presence, failure mileage presence, repair cost presence, and verdict impact presence
2. **Demand scoring** (web search): real query phrasings from SERP titles/snippets grouped by intent
3. **Final score** = coverage × 0.6 + demand × 0.4

## Ranking Table

| Rank | Slug | Models | Coverage | Demand | Final | Selected |
|------|------|--------|----------|--------|-------|----------|
| 1 | cvt-transmission | 7 | 1.000 | 0.95 | 0.980 | ✅ |
| 2 | oil-consumption | 7 | 1.000 | 0.85 | 0.940 | ✅ |
| 3 | timing-chain | 5 | 0.886 | 0.92 | 0.900 | ✅ |
| 4 | torque-converter | 4 | 0.829 | 0.90 | 0.857 | ✅ |
| 5 | air-suspension | 4 | 0.829 | 0.88 | 0.849 | ✅ |
| 6 | transmission-failure | 7 | 1.000 | 0.80 | 0.920 | ❌ Too generic, overlaps CVT |
| 7 | engine-failure | 7 | 1.000 | 0.78 | 0.912 | ❌ Too generic, overlaps DecisionEngine |
| 8 | water-pump | 4 | 0.829 | 0.70 | 0.777 | ❌ Lower demand |
| 9 | lifter-failure | 3 | 0.771 | 0.72 | 0.751 | ❌ Lower coverage |
| 10 | ac-system | 3 | 0.771 | 0.68 | 0.735 | ❌ Lower coverage+demand |

## Hub 1: `cvt-transmission` (Score: 0.980)

### Query Phrasings (SERP evidence)
| # | Query | Intent Group |
|---|-------|-------------|
| 1 | CVT transmission failure symptoms | symptoms |
| 2 | CVT transmission problems | symptoms |
| 3 | CVT transmission repair cost | cost |
| 4 | CVT replacement cost | cost |
| 5 | how much to replace CVT transmission | cost |
| 6 | is CVT transmission worth fixing | worth fixing |
| 7 | should I fix CVT or sell car | worth fixing |
| 8 | CVT transmission cost vs car value | worth fixing |
| 9 | can you drive with CVT problems | drivable risk |
| 10 | CVT slipping dangerous | drivable risk |
| 11 | Nissan CVT failure | symptoms |
| 12 | Subaru CVT shudder | symptoms |
| 13 | CVT vs automatic reliability | worth fixing |
| 14 | CVT whining noise | symptoms |
| 15 | CVT overheating | symptoms |
| 16 | CVT fluid change cost | cost |
| 17 | CVT warranty coverage | worth fixing |
| 18 | CVT judder fix | symptoms |
| 19 | CVT remanufactured vs new cost | cost |
| 20 | signs CVT is going bad | symptoms |

Intent groups: cost (5), symptoms (8), worth fixing (5), drivable risk (2) = **4 groups**

### Competitor URLs
1. [carparts.com/blog/cvt-transmission-problems](https://www.carparts.com) — Broad CVT symptom guide without fix-or-sell framing
2. [lemonlawexperts.com/cvt-transmission](https://www.lemonlawexperts.com) — Focus on legal remedies, not repair economics
3. [consumeraffairs.com/cvt-replacement-cost](https://www.consumeraffairs.com) — Cost-focused but no model-specific data

**Missing vs competitors**: None offer a model-specific comparison table with repair cost AND occurrence data

---

## Hub 2: `timing-chain` (Score: 0.900)

### Query Phrasings
| # | Query | Intent Group |
|---|-------|-------------|
| 1 | timing chain failure symptoms | symptoms |
| 2 | timing chain replacement cost | cost |
| 3 | is timing chain worth replacing | worth fixing |
| 4 | timing chain broken should I sell car | worth fixing |
| 5 | timing chain rattle on startup | symptoms |
| 6 | how long does timing chain last | worth fixing |
| 7 | can you drive with loose timing chain | drivable risk |
| 8 | timing chain tensioner failure | symptoms |
| 9 | timing chain vs timing belt | worth fixing |
| 10 | BMW timing chain cost | cost |
| 11 | VW timing chain problem | symptoms |
| 12 | timing chain guides broken | symptoms |
| 13 | timing chain metal shavings in oil | symptoms |
| 14 | timing chain engine damage | drivable risk |
| 15 | timing chain labor cost | cost |
| 16 | timing chain interference engine | drivable risk |
| 17 | timing chain repair vs new car | worth fixing |
| 18 | timing chain noise diagnosis | symptoms |
| 19 | timing chain stretched | symptoms |
| 20 | timing chain replacement labor hours | cost |

Intent groups: cost (4), symptoms (8), worth fixing (5), drivable risk (3) = **4 groups**

### Competitor URLs
1. [repairpal.com/estimator/timing-chain-replacement-cost](https://repairpal.com) — Cost estimator only
2. [consumeraffairs.com/timing-chain-replacement-cost](https://www.consumeraffairs.com) — Cost guide without model table
3. [pandahub.com/timing-chain-replacement-cost](https://pandahub.com) — Cost-focused, no fix-or-sell framework

---

## Hub 3: `oil-consumption` (Score: 0.940)

### Query Phrasings
| # | Query | Intent Group |
|---|-------|-------------|
| 1 | car burning oil repair cost | cost |
| 2 | engine burning oil symptoms | symptoms |
| 3 | excessive oil consumption causes | symptoms |
| 4 | is it worth fixing oil consumption | worth fixing |
| 5 | how much oil burning is normal | symptoms |
| 6 | blue smoke from exhaust | symptoms |
| 7 | piston ring replacement cost | cost |
| 8 | engine rebuild cost oil burning | cost |
| 9 | sell car burning oil | worth fixing |
| 10 | oil consumption vs oil leak | symptoms |
| 11 | PCV valve oil consumption | symptoms |
| 12 | Subaru oil consumption problem | symptoms |
| 13 | Honda oil consumption V6 | symptoms |
| 14 | should I sell a car that burns oil | worth fixing |
| 15 | engine oil consumption 1 quart per 1000 miles | symptoms |
| 16 | is oil burning dangerous to drive | drivable risk |
| 17 | low oil warning light causes | symptoms |
| 18 | valve seal replacement cost | cost |
| 19 | engine overhaul vs new car | worth fixing |
| 20 | oil additive stop burning | symptoms |

Intent groups: cost (4), symptoms (10), worth fixing (4), drivable risk (2) = **4 groups**

### Competitor URLs
1. [autozone.com/diy/engine/why-is-my-car-burning-oil](https://www.autozone.com) — Diagnostic guide, no model data
2. [consumerreports.org/excessive-oil-consumption](https://www.consumerreports.org) — Paywalled analysis
3. [cbac.com/car-burning-oil](https://www.cbac.com) — General FAQ format, no repair-vs-sell angle

---

## Hub 4: `torque-converter` (Score: 0.857)

### Query Phrasings
| # | Query | Intent Group |
|---|-------|-------------|
| 1 | torque converter shudder symptoms | symptoms |
| 2 | torque converter replacement cost | cost |
| 3 | torque converter shudder fix | symptoms |
| 4 | is torque converter worth fixing | worth fixing |
| 5 | torque converter vs transmission rebuild | cost |
| 6 | can fluid change fix torque converter | worth fixing |
| 7 | torque converter shudder 40-50 mph | symptoms |
| 8 | is torque converter shudder dangerous | drivable risk |
| 9 | torque converter labor cost | cost |
| 10 | torque converter vibration diagnosis | symptoms |
| 11 | Toyota torque converter shudder | symptoms |
| 12 | Chevy torque converter shudder | symptoms |
| 13 | remanufactured torque converter cost | cost |
| 14 | torque converter clutch failure | symptoms |
| 15 | torque converter fix or sell | worth fixing |
| 16 | torque converter stalling car | drivable risk |
| 17 | transmission fluid flush torque converter | worth fixing |
| 18 | how long can you drive with bad torque converter | drivable risk |
| 19 | torque converter overheating | symptoms |
| 20 | torque converter slipping | symptoms |

Intent groups: cost (4), symptoms (9), worth fixing (4), drivable risk (3) = **4 groups**

### Competitor URLs
1. [carparts.com/blog/torque-converter-problems](https://www.carparts.com) — Symptom guide
2. [gearstar.com/torque-converter-replacement-cost](https://www.gearstar.com) — Cost breakdown
3. [torque-converter.com/replacement-cost](https://torque-converter.com) — Parts pricing

---

## Hub 5: `air-suspension` (Score: 0.849)

### Query Phrasings
| # | Query | Intent Group |
|---|-------|-------------|
| 1 | air suspension failure symptoms | symptoms |
| 2 | air suspension repair cost | cost |
| 3 | is air suspension worth fixing | worth fixing |
| 4 | air suspension convert to coil springs | worth fixing |
| 5 | air suspension compressor failure | symptoms |
| 6 | air suspension sagging one side | symptoms |
| 7 | air suspension replacement cost | cost |
| 8 | air ride vs coil spring conversion cost | cost |
| 9 | can you drive with broken air suspension | drivable risk |
| 10 | Range Rover air suspension cost | cost |
| 11 | Jeep air suspension problems | symptoms |
| 12 | air suspension leaking | symptoms |
| 13 | air spring replacement cost per corner | cost |
| 14 | air suspension compressor running constantly | symptoms |
| 15 | sell car with broken air suspension | worth fixing |
| 16 | air suspension height sensor failure | symptoms |
| 17 | air suspension warning light | symptoms |
| 18 | aftermarket air suspension kit cost | cost |
| 19 | should I keep air suspension or convert | worth fixing |
| 20 | air suspension rebuild cost | cost |

Intent groups: cost (7), symptoms (8), worth fixing (4), drivable risk (1) = **4 groups**

### Competitor URLs
1. [strutmasters.com/air-suspension-repair](https://www.strutmasters.com) — Sells conversion kits (biased)
2. [consumeraffairs.com/air-suspension-repair-cost](https://www.consumeraffairs.com) — Cost guide only
3. [autoguide.com/air-suspension-vs-coil-springs](https://www.autoguide.com) — Comparison article, no model data
