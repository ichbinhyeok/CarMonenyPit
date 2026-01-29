package com.carmoneypit.engine.core;

import com.carmoneypit.engine.api.InputModels.VehicleType;
import org.springframework.stereotype.Component;

@Component
public class CostOfInactionCalculator {

    public long calculateAssetBleed(VehicleType vehicleType, long mileage, long repairQuoteUsd, long currentValueUsd,
            Double depRateOverride) {
        // 公式: Asset Bleed (6mo) = (CurrentValue × 6moDepreciationRate) +
        // (BaseRepairCost × 0.5 [Risk Probability])

        // 1. 6moDepreciationRate
        double depRate = 0.10; // Default

        if (depRateOverride != null) {
            // JSON rate is annual (e.g., 0.15), convert to 6-mo
            depRate = depRateOverride / 2.0;
        } else {
            // Fallback to legacy heuristic
            switch (vehicleType) {
                case LUXURY:
                case PERFORMANCE:
                    depRate = 0.15;
                    break;
                case SUV:
                case TRUCK_VAN:
                    depRate = 0.08;
                    break;
                case SEDAN:
                default:
                    depRate = 0.10;
                    break;
            }
        }

        // 2. Risk Probability
        double riskProb = 0.10;
        if (mileage > 150000)
            riskProb = 0.60;
        else if (mileage > 100000)
            riskProb = 0.35;

        double bleed = (currentValueUsd * depRate) + (repairQuoteUsd * riskProb);

        // Rounding Rule (100s)
        return Math.round(bleed / 100.0) * 100;
    }
}
