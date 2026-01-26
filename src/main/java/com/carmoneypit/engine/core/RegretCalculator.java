package com.carmoneypit.engine.core;

import com.carmoneypit.engine.api.InputModels.EngineInput;
import com.carmoneypit.engine.api.InputModels.HassleTolerance;
import com.carmoneypit.engine.api.InputModels.SimulationControls;
import com.carmoneypit.engine.api.InputModels.VehicleType;
import com.carmoneypit.engine.data.EngineDataProvider;
import com.carmoneypit.engine.model.EngineDataModels.EngineData;
import com.carmoneypit.engine.model.EngineDataModels.RiskProbability;
import com.carmoneypit.engine.model.EngineDataModels.CostRange;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RegretCalculator {

    private final EngineDataProvider dataProvider;
    private final PointConverter pointConverter;

    public RegretCalculator(EngineDataProvider dataProvider, PointConverter pointConverter) {
        this.dataProvider = dataProvider;
        this.pointConverter = pointConverter;
    }

    /**
     * RF = Repair Regret (수리했을 때의 후회)
     * Components:
     * 1. Current Repair Cost (Points)
     * 2. Future Failure Probability Cost (Prob * Avg Major Cost * Cascade
     * Multiplier)
     * 3. Pain Index (Downtime * Stress)
     */
    public double calculateRF(EngineInput input, SimulationControls controls) {
        EngineData data = dataProvider.getData();

        // 1. Current Repair Cost
        int repairPoints = pointConverter.convertUsdToPoints(input.repairQuoteUsd());

        // 2. Future Failure Risk
        double futureRiskPoints = calculateFutureRisk(input.mileage(), data);

        // 3. Pain Index
        double painPoints = calculatePainPoints(controls, data);

        // Simulation Modifiers (Phase 2)
        if (controls != null) {
            // If severity is high (Engine/Trans), multipliers might apply to future risk
            // perception
            if (controls
                    .failureSeverity() == com.carmoneypit.engine.api.InputModels.FailureSeverity.ENGINE_TRANSMISSION) {
                futureRiskPoints *= 1.5; // Perceived risk increase
            }
        }

        return repairPoints + futureRiskPoints + painPoints;
    }

    /**
     * RM = Replacement Regret (차를 정리했을 때의 후회)
     * Components:
     * 1. Switching Friction (Psychological cost of finding new car)
     * 2. Temporary Inconvenience
     * 3. Valid Exit Options (Sellability / Trade-in value proxy)
     */
    public double calculateRM(EngineInput input, SimulationControls controls) {
        EngineData data = dataProvider.getData();

        // 1. Switching Friction Base
        double frictionPoints = 100.0; // Base "annoyance" points

        if (controls != null) {
            if (controls.hassleTolerance() == HassleTolerance.HATE_SWITCHING) {
                frictionPoints *= 2.0;
            } else if (controls.hassleTolerance() == HassleTolerance.WANT_NEW_CAR) {
                frictionPoints *= 0.5;
            }
        }

        // 2. Sellability Factor (If hard to sell, RM increases because exit is painful)
        // However, in "Regret" logic:
        // If it's easy to sell (High liquidity), RM is LOW (Low regret to switch).
        // If it's hard to sell (Low liquidity), RM is HIGH (High regret to
        // switch/scrap).
        double sellabilityScore = getSellabilityScore(input.mileage(), data);
        // Invert sellability: Score 1.0 (Good) -> Low Penalty. Score 0.1 (Bad) -> High
        // Penalty.
        double liquidityPenalty = (1.0 - sellabilityScore) * 200;

        // 3. Opportunity Cost Floor
        // "Scrap Value" logic - if value is basically defined as scrap, RM is lower
        // because you have nothing to lose?
        // Actually, if you are at floor, you might as well switch.

        return frictionPoints + liquidityPenalty;
    }

    private double calculateFutureRisk(long mileage, EngineData data) {
        String mileageKey = getMileageKey(mileage);
        Map<String, RiskProbability> intervalProbs = data.failure_cascade_probability().mileage_intervals()
                .get(mileageKey);

        if (intervalProbs == null)
            return 0;

        double totalExpectedCost = 0;
        double cascadeMult = data.failure_cascade_probability().cascade_multiplier();

        // Iterate over major systems (engine, transmission, etc.)
        for (Map.Entry<String, RiskProbability> entry : intervalProbs.entrySet()) {
            String systemName = entry.getKey();
            RiskProbability risk = entry.getValue();

            // Get avg cost for this system
            CostRange costRange = data.major_repair_cost_range().get(systemName);
            if (costRange != null) {
                double avgCost = (costRange.min_points() + costRange.max_points()) / 2.0;
                // E[Cost] = Prob * AvgCost * CascadeMultiplier
                totalExpectedCost += (risk.base_prob() * avgCost * cascadeMult);
            }
        }

        return totalExpectedCost;
    }

    private double calculatePainPoints(SimulationControls controls, EngineData data) {
        // Base pain
        double pain = 0;

        // If simulation controls act active
        if (controls != null) {
            if (controls.mobilityStatus() == com.carmoneypit.engine.api.InputModels.MobilityStatus.NEEDS_TOW) {
                pain += 50; // Immediate pain of towing
            }

            // Add system specific downtime pain if known
            if (controls.failureSeverity() != com.carmoneypit.engine.api.InputModels.FailureSeverity.GENERAL_UNKNOWN) {
                // Simplified lookup: Assuming generic "major repair" pain if specific system
                // not passed
                // In v2, we can pass specific system. For v1, add generic buffer.
                pain += 30;
            }
        }

        return pain;
    }

    private double getSellabilityScore(long mileage, EngineData data) {
        // Simple mapping based on mileage for v1
        if (mileage < 80000)
            return data.sellability_score().get("condition_good");
        if (mileage < 150000)
            return data.sellability_score().get("condition_fair");
        return data.sellability_score().get("condition_poor");
    }

    private String getMileageKey(long mileage) {
        if (mileage < 50000)
            return "0_50k";
        if (mileage < 100000)
            return "50k_100k";
        if (mileage < 150000)
            return "100k_150k";
        return "150k_plus";
    }
}
