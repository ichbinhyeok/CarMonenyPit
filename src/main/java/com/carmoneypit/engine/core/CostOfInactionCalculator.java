package com.carmoneypit.engine.core;

import com.carmoneypit.engine.api.InputModels.VehicleType;
import org.springframework.stereotype.Component;

@Component
public class CostOfInactionCalculator {

    // Internal Actuarial Constants for MVP
    private static final double DECAY_RATE_LUX_PERF = 0.15; // 15% / 6mo
    private static final double DECAY_RATE_SUV_TRUCK = 0.08; // 8% / 6mo
    private static final double DECAY_RATE_SEDAN = 0.10; // 10% / 6mo

    private static final double RISK_PROB_HIGH_MILEAGE = 0.60;
    private static final double RISK_PROB_MID_MILEAGE = 0.35;
    private static final double RISK_PROB_LOW_MILEAGE = 0.10;

    public long calculateAssetBleed(VehicleType type, long mileage, long currentRepairCost) {
        long estimatedCurrentValue = estimateMarketValue(type, mileage);
        double depreciationRate = getDepreciationRate(type);
        double riskProbability = getRiskProbability(mileage);

        double depreciationLoss = estimatedCurrentValue * depreciationRate;
        double riskPremium = currentRepairCost * 0.5 * riskProbability; // 50% of repair cost as baseline for major
                                                                        // failure? Or separate constant?
        // Spec says: BaseRepairCost * 0.5 [Risk Probability]?
        // Re-reading spec: (BaseRepairCost * 0.5 [Risk Probability]) -> Interpretation:
        // 50% chance of another similar failure?
        // Or simply: CurrentRepairCost * RiskProb.
        // Let's use: CurrentRepairCost * RiskProbability (as a conservative estimate of
        // recurring issue).
        // Spec in md said: (BaseRepairCost * 0.5 [Risk Probability]) - Wait, the MD
        // said "BaseRepairCost * 0.5 [Risk Probability]"?
        // Let's re-read MD step 666: "+ (BaseRepairCost × 0.5 [Risk Probability])" -> I
        // suspect the bracket [Risk Probability] was a note.
        // Let's interpret as: Risk Cost = BaseRepairCost * 0.5. Or BaseRepairCost *
        // RiskProb?
        // Let's use a simpler heuristic that hurts: Risk Cost = CurrentRepairCost *
        // RiskProbability.

        // Actually, let's follow the MD formula exactly if possible.
        // MD: (BaseRepairCost × 0.5 [Risk Probability]) seems to mean (RepairCost *
        // 0.5) scaled by something?
        // Let's stick to: RiskComponent = CurrentRepairCost * RiskProbability.
        // Example: $2000 repair, 60% risk -> $1200 expected future loss.
        riskPremium = currentRepairCost * riskProbability;

        double totalBleed = depreciationLoss + riskPremium;

        return roundToEeat(totalBleed);
    }

    private long roundToEeat(double value) {
        // Round to nearest 100
        return Math.round(value / 100.0) * 100;
    }

    private double getDepreciationRate(VehicleType type) {
        return switch (type) {
            case LUXURY, PERFORMANCE -> DECAY_RATE_LUX_PERF;
            case SUV, TRUCK_VAN -> DECAY_RATE_SUV_TRUCK;
            default -> DECAY_RATE_SEDAN;
        };
    }

    private double getRiskProbability(long mileage) {
        if (mileage > 150_000)
            return RISK_PROB_HIGH_MILEAGE;
        if (mileage > 100_000)
            return RISK_PROB_MID_MILEAGE;
        return RISK_PROB_LOW_MILEAGE;
    }

    private long estimateMarketValue(VehicleType type, long mileage) {
        long basePrice = switch (type) {
            case LUXURY -> 75_000;
            case PERFORMANCE -> 65_000;
            case TRUCK_VAN -> 55_000;
            case SUV -> 45_000;
            case SEDAN -> 32_000;
        };

        double residualFactor;
        if (mileage < 30_000)
            residualFactor = 0.85; // Near new
        else if (mileage < 60_000)
            residualFactor = 0.65; // Off lease
        else if (mileage < 100_000)
            residualFactor = 0.45; // Mid life
        else if (mileage < 150_000)
            residualFactor = 0.30; // Old
        else
            residualFactor = 0.15; // Beater

        return (long) (basePrice * residualFactor);
    }
}
