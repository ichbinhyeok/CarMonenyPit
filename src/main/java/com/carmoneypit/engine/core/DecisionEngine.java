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

    // Margin to prevent flickering verdicts (hysteresis-like thought process)
    private static final double SIGNIFICANCE_MARGIN = 50.0;

    public DecisionEngine(RegretCalculator regretCalculator, CostOfInactionCalculator costOfInactionCalculator) {
        this.regretCalculator = regretCalculator;
        this.costOfInactionCalculator = costOfInactionCalculator;
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
                input.repairQuoteUsd());

        VisualizationHint hint = new VisualizationHint(rfDetail.score(), rmDetail.score(), moneyPitState);
        return new VerdictResult(state, narrative, hint, breakdown, assetBleed);
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
