package com.carmoneypit.engine.core;

import com.carmoneypit.engine.api.InputModels.VehicleType;
import org.springframework.stereotype.Component;

@Component
public class CostOfInactionCalculator {

    public long calculateAssetBleed(VehicleType vehicleType, long mileage, long repairQuoteUsd) {
        // Placeholder implementation
        return repairQuoteUsd + 500;
    }
}
