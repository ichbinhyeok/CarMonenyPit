package com.carmoneypit.engine.core;

import com.carmoneypit.engine.api.InputModels.EngineInput;
import com.carmoneypit.engine.api.InputModels.SimulationControls;
import com.carmoneypit.engine.api.FinancialLineItem;
import com.carmoneypit.engine.api.OutputModels.VerdictResult;
import com.carmoneypit.engine.api.OutputModels.VerdictState;
import com.carmoneypit.engine.api.OutputModels.VisualizationHint;
import com.carmoneypit.engine.core.RegretCalculator.RegretDetail;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DecisionEngine {

    private final RegretCalculator regretCalculator;
    private final CostOfInactionCalculator costOfInactionCalculator;
    private final ValuationService valuationService;

    // --- Calculation Constants & Thresholds ---
    private static final double SIGNIFICANCE_MARGIN = 500.0;
    private static final int BASE_CONFIDENCE_GENERIC = 60;
    private static final int BOOST_MODEL_DATA = 25;
    private static final int BOOST_VEHICLE_SEGMENT = 10;
    private static final int BOOST_REAL_QUOTE = 13;
    private static final int BOOST_MARKETING_MATCH = 5;

    private static final int DEFAULT_SELL_PCT = 40;
    private static final int SELL_PCT_SHIFT_BOMB = 15;
    private static final int SELL_PCT_SHIFT_STABLE = 15;

    private static final long DEFAULT_SWITCHING_FRICTION = 3000L;
    private static final int DEFAULT_NEW_MONTHLY_PAYMENT = 748;

    public DecisionEngine(RegretCalculator regretCalculator, CostOfInactionCalculator costOfInactionCalculator,
            ValuationService valuationService) {
        this.regretCalculator = regretCalculator;
        this.costOfInactionCalculator = costOfInactionCalculator;
        this.valuationService = valuationService;
    }

    /**
     * Phase 1: Fast Verdict (Input only)
     */
    public VerdictResult evaluate(EngineInput input) {
        return processVerdict(input, null);
    }

    /**
     * Phase 2: Simulation Lab (Input + Controls)
     */
    public VerdictResult simulate(EngineInput input, SimulationControls controls) {
        return processVerdict(input, controls);
    }

    private VerdictResult processVerdict(EngineInput input, SimulationControls controls) {
        RegretDetail rfDetail = regretCalculator.calculateRF(input, controls);
        RegretDetail rmDetail = regretCalculator.calculateRM(input, controls);

        VerdictState state = determineState(rfDetail.score(), rmDetail.score());
        String narrative = generateNarrative(state, rfDetail.score(), rmDetail.score());

        String moneyPitState = (state == VerdictState.TIME_BOMB) ? "DEEP_PIT" : "SURFACE";

        // Aggregate breakdowns for the "Smart Receipt"
        List<FinancialLineItem> breakdown = new ArrayList<>();
        breakdown.addAll(rfDetail.items());
        breakdown.addAll(rmDetail.items());

        // Granular Model Context if available
        var marketDataOpt = valuationService.getMarketData(input.model());

        // Calculate Cost of Inaction (Asset Bleed)
        Double depRate = marketDataOpt.map(m -> m.depreciationRate()).orElse(null);
        long assetBleed = costOfInactionCalculator.calculateAssetBleed(input.vehicleType(), input.mileage(),
                input.repairQuoteUsd(), input.currentValueUsd(), depRate);

        // Rounding Rule (10s) for "Professional Estimate" feel
        List<FinancialLineItem> roundedBreakdown = new ArrayList<>();
        for (FinancialLineItem item : breakdown) {
            double roundedAmount = Math.round(item.amount() / 10.0) * 10.0;
            roundedBreakdown.add(new FinancialLineItem(item.label(), roundedAmount, item.description(),
                    item.isNegative(), item.category()));
        }

        // Rounding Rule (100s) for display scores
        double roundedRF = Math.round(rfDetail.score() / 100.0) * 100.0;
        double roundedRM = Math.round(rmDetail.score() / 100.0) * 100.0;

        // --- NEW STRATEGIC METRICS ---
        var brandDataOpt = valuationService.getBrandData(input.brand());

        // 1. Data Integrity & Confidence
        int confidence = BASE_CONFIDENCE_GENERIC;
        if (input.model() != null && !input.model().isBlank() && !"other".equalsIgnoreCase(input.model())) {
            confidence += BOOST_MODEL_DATA;
        } else if (input.vehicleType() != null) {
            confidence += BOOST_VEHICLE_SEGMENT;
        }
        if (!input.isQuoteEstimated())
            confidence += BOOST_REAL_QUOTE;
        if (marketDataOpt.isPresent())
            confidence += BOOST_MARKETING_MATCH;
        confidence = Math.min(confidence, 98);

        // 2. Peer Data (Owner Behavior)
        int sellPct = brandDataOpt.map(d -> d.sellStatPct).orElse(DEFAULT_SELL_PCT);
        if (state == VerdictState.TIME_BOMB)
            sellPct += SELL_PCT_SHIFT_BOMB;
        if (state == VerdictState.STABLE)
            sellPct -= SELL_PCT_SHIFT_STABLE;
        sellPct = Math.min(Math.max(sellPct, 5), 95);

        var peerData = new com.carmoneypit.engine.api.OutputModels.PeerData(
                sellPct,
                100 - sellPct,
                state == VerdictState.STABLE ? 8.2 : 4.1);

        // 3. Economic Context (Switching Reality)
        long friction = brandDataOpt.map(d -> d.avgSwitchingFriction).orElse(DEFAULT_SWITCHING_FRICTION);
        int monthly = brandDataOpt.map(d -> d.avgNewMonthly).orElse(DEFAULT_NEW_MONTHLY_PAYMENT);

        var econContext = new com.carmoneypit.engine.api.OutputModels.EconomicContext(
                friction,
                monthly,
                input.currentValueUsd() / 2 // 50% Rule threshold
        );

        VisualizationHint hint = new VisualizationHint(roundedRF, roundedRM, moneyPitState);
        return new VerdictResult(state, narrative, hint, roundedBreakdown, assetBleed, rfDetail.score(),
                rmDetail.score(),
                confidence, peerData, econContext);
    }

    private VerdictState determineState(double rf, double rm) {
        if (rf > (rm + SIGNIFICANCE_MARGIN)) {
            return VerdictState.TIME_BOMB;
        } else if (rf <= (rm - SIGNIFICANCE_MARGIN)) {
            return VerdictState.STABLE;
        } else {
            return VerdictState.BORDERLINE;
        }
    }

    private String generateNarrative(VerdictState state, double rf, double rm) {
        return switch (state) {
            case TIME_BOMB ->
                "This repair is likely a sunk cost. The regret of fixing (RF) significantly outweighs the hassle of switching.";
            case STABLE ->
                "Proceed with caution, but fixing once makes sense. The cost of switching is currently higher than the repair regret.";
            case BORDERLINE ->
                "It's a toss-up. You are arguably in the 'Zone of Indecision'. Consider your personal stress tolerance.";
            default -> "Engine analysis complete. Review specialized metrics for guidance.";
        };
    }
}
