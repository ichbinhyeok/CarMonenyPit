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

import com.carmoneypit.engine.api.FinancialLineItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RegretCalculator {

    private final EngineDataProvider dataProvider;
    private final PointConverter pointConverter;

    public record RegretDetail(double score, List<FinancialLineItem> items) {
    }

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
    public RegretDetail calculateRF(EngineInput input, SimulationControls controls) {
        EngineData data = dataProvider.getData();
        List<FinancialLineItem> items = new ArrayList<>();
        int divisor = data.conversion_rules().standard_divisor();

        // 1. Current Repair Cost
        int repairPoints = pointConverter.convertUsdToPoints(input.repairQuoteUsd());
        items.add(new FinancialLineItem(
                "Immediate Repair Quote",
                String.format("$%d", input.repairQuoteUsd()),
                "The amount you were quoted by the mechanic today.",
                true));

        // 2. Future Failure Risk
        double futureRiskPoints = calculateFutureRisk(input.mileage(), data);
        double perceivedRiskPoints = futureRiskPoints;

        // Simulation Modifiers (Phase 2)
        if (controls != null) {
            if (controls
                    .failureSeverity() == com.carmoneypit.engine.api.InputModels.FailureSeverity.ENGINE_TRANSMISSION) {
                perceivedRiskPoints *= 1.5;
            }
        }

        if (perceivedRiskPoints > 0) {
            String riskDesc = "Estimated cost of imminent failures based on current mileage trends.";
            if (perceivedRiskPoints > futureRiskPoints) {
                riskDesc += " (Adjusted for high severity repair)";
            }
            items.add(new FinancialLineItem(
                    "Future Failure Risk (Est.)",
                    String.format("~$%.0f", perceivedRiskPoints * divisor),
                    riskDesc,
                    true));
        }

        // 3. Pain Index
        double painPoints = calculatePainPoints(controls, data);
        if (painPoints > 0) {
            items.add(new FinancialLineItem(
                    "Stress & Downtime Factor",
                    String.format("+$%.0f", painPoints * divisor),
                    "Hidden cost of being without a vehicle and the hassle of towing/rental.",
                    true));
        }

        double totalScore = repairPoints + perceivedRiskPoints + painPoints;
        return new RegretDetail(totalScore, items);
    }

    /**
     * RM = Replacement Regret (차를 정리했을 때의 후회)
     * Components:
     * 1. Switching Friction (Psychological cost of finding new car)
     * 2. Temporary Inconvenience
     * 3. Valid Exit Options (Sellability / Trade-in value proxy)
     */
    public RegretDetail calculateRM(EngineInput input, SimulationControls controls) {
        EngineData data = dataProvider.getData();
        List<FinancialLineItem> items = new ArrayList<>();
        int divisor = data.conversion_rules().standard_divisor();

        // 1. Switching Friction Base
        double frictionPoints = 100.0; // Base "annoyance" points
        String frictionLabel = "Replacement Hassle";

        if (controls != null) {
            if (controls.hassleTolerance() == HassleTolerance.HATE_SWITCHING) {
                frictionPoints *= 2.0;
                frictionLabel += " (High)";
            } else if (controls.hassleTolerance() == HassleTolerance.WANT_NEW_CAR) {
                frictionPoints *= 0.5;
                frictionLabel += " (Low)";
            }
        }

        items.add(new FinancialLineItem(
                frictionLabel,
                String.format("~$%.0f", frictionPoints * divisor),
                "Psychological and logistical cost of acquiring a different vehicle.",
                false));

        // 2. Sellability Factor
        double sellabilityScore = getSellabilityScore(input.mileage(), data);
        double liquidityPenalty = (1.0 - sellabilityScore) * 200;

        if (liquidityPenalty > 0) {
            items.add(new FinancialLineItem(
                    "Exit Liquidity Penalty",
                    String.format("+$%.0f", liquidityPenalty * divisor),
                    "Loss incurred due to current vehicle's low market demand/scrap condition.",
                    false));
        }

        double totalScore = frictionPoints + liquidityPenalty;
        return new RegretDetail(totalScore, items);
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
