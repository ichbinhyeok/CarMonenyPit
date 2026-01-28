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

    // Margin to prevent flickering verdicts (hysteresis-like thought process)
    private static final double SIGNIFICANCE_MARGIN = 50.0;

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

        // Calculate Cost of Inaction (Asset Bleed)
        long assetBleed = costOfInactionCalculator.calculateAssetBleed(input.vehicleType(), input.mileage(),
                input.repairQuoteUsd(), input.currentValueUsd());

        // Rounding Rule (100s) for display scores
        double roundedRF = Math.round(rfDetail.score() / 100.0) * 100.0;
        double roundedRM = Math.round(rmDetail.score() / 100.0) * 100.0;

        // --- NEW STRATEGIC METRICS ---
        var brandDataOpt = valuationService.getBrandData(input.brand());

        // 3. Confidence Score (Data Source Integrity)
        int confidence = 60; // Base for generic brand data
        if (input.model() != null && !input.model().isBlank() && !"other".equalsIgnoreCase(input.model())) {
            confidence += 25; // Significant boost for specific model data
        } else if (input.vehicleType() != null) {
            confidence += 10; // Better if we have segments
        }

        if (!input.isQuoteEstimated())
            confidence += 13; // Better if we have real quote

        // 1. Peer Data (Owner Behavior)
        int sellPct = brandDataOpt.map(d -> d.sellStatPct).orElse(40);
        if (state == VerdictState.TIME_BOMB)
            sellPct += 15; // Shift if it's a bomb
        if (state == VerdictState.STABLE)
            sellPct -= 15;
        sellPct = Math.min(Math.max(sellPct, 5), 95);

        var peerData = new com.carmoneypit.engine.api.OutputModels.PeerData(
                sellPct,
                100 - sellPct,
                state == VerdictState.STABLE ? 8.2 : 4.1);

        // 2. Economic Context (Switching Reality)
        long friction = brandDataOpt.map(d -> d.avgSwitchingFriction).orElse(3000L);
        int monthly = brandDataOpt.map(d -> d.avgNewMonthly).orElse(748);

        // Granular Model Context if available
        var marketDataOpt = valuationService.getMarketData(input.model());
        if (marketDataOpt.isPresent()) {
            // Adjust confidence if we have exact market matchups
            confidence += 5;
        }
        confidence = Math.min(confidence, 98);

        var econContext = new com.carmoneypit.engine.api.OutputModels.EconomicContext(
                friction,
                (int) monthly,
                input.currentValueUsd() / 2 // 50% Rule threshold
        );

        VisualizationHint hint = new VisualizationHint(roundedRF, roundedRM, moneyPitState);
        return new VerdictResult(state, narrative, hint, breakdown, assetBleed, rfDetail.score(), rmDetail.score(),
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
        // v1 Simple Narrative Generation
        switch (state) {
            case TIME_BOMB:
                return "This repair is likely a sunk cost. The regret of fixing (RF) significantly outweighs the hassle of switching.";
            case STABLE:
                return "Proceed with caution, but fixing once makes sense. The cost of switching is currently higher than the repair regret.";
            case BORDERLINE:
            default:
                return "It's a toss-up. You are arguably in the 'Zone of Indecision'. Consider your personal stress tolerance.";
        }
    }
}
