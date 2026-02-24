# Verdict Policy (SSOT)

## 1. Single Source of Truth
The `DecisionEngine` class serves as the **Single Source of Truth (SSOT)** for all verdict decisions across the application. Waitlist redirects, pSEO pages, and the main interactive calculator all pull their final `VerdictState` (e.g., `TIME_BOMB`, `STABLE`, `BORDERLINE`) directly from `DecisionEngine.evaluate()`.

**NO local fallback limits are permitted in controllers.** (e.g., the old `price > 50% market value` rule found previously in `PSeoController` is safely retired).

## 2. Core Logic Workflow
Instead of arbitrary percentages, verdicts are driven by a purely **Regret-Based** formula:
1. **Regret of Fixing (RF):** Current repair cost + estimated future major failures + inconvenience and stress.
2. **Regret of Moving On (RM):** Loss of current asset value + friction of replacing the vehicle + assumed new monthly car payment.

## 3. Threshold Adjustments
`DecisionEngine.determineState()`:
- **`TIME_BOMB`**: `RF > RM + SIGNIFICANCE_MARGIN` ($500)
- **`STABLE`**: `RF <= RM - SIGNIFICANCE_MARGIN`
- **`BORDERLINE`**: Anything inside the $500 window on either side.

## 4. Policy Revisions
Any tweaks to how verdicts are generated MUST be made through adjustments in `RegretCalculator.java` or `DecisionEngine.java`. pSEO generating templates and frontend JS should NEVER attempt to re-estimate or guess the vehicle's verdict status.
