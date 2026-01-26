package com.carmoneypit.engine.core;

import com.carmoneypit.engine.api.InputModels.EngineInput;
import com.carmoneypit.engine.api.InputModels.SimulationControls;
import com.carmoneypit.engine.api.OutputModels.VerdictResult;
import com.carmoneypit.engine.api.OutputModels.VerdictState;
import com.carmoneypit.engine.api.OutputModels.VisualizationHint;
import org.springframework.stereotype.Service;

@Service
public class DecisionEngine {

    private final RegretCalculator regretCalculator;

    // Margin to prevent flickering verdicts (hysteresis-like thought process)
    private static final double SIGNIFICANCE_MARGIN = 50.0;

    public DecisionEngine(RegretCalculator regretCalculator) {
        this.regretCalculator = regretCalculator;
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
        double rf = regretCalculator.calculateRF(input, controls);
        double rm = regretCalculator.calculateRM(input, controls);

        VerdictState state = determineState(rf, rm);
        String narrative = generateNarrative(state, rf, rm);

        String moneyPitState = (state == VerdictState.TIME_BOMB) ? "DEEP_PIT" : "SURFACE";

        VisualizationHint hint = new VisualizationHint(rf, rm, moneyPitState);
        return new VerdictResult(state, narrative, hint);
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
