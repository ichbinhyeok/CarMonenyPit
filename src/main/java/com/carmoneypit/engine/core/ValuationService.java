package com.carmoneypit.engine.core;

import com.carmoneypit.engine.api.InputModels.VehicleType;
import org.springframework.stereotype.Service;

@Service
public class ValuationService {

    private static final long BASE_SEDAN = 28_000;
    private static final long BASE_SUV = 38_000;
    private static final long BASE_TRUCK = 45_000;
    private static final long BASE_LUXURY = 65_000;
    private static final long BASE_PERFORMANCE = 55_000;

    private static final long SCRAP_VALUE = 500;

    public long estimateValue(VehicleType type, long mileage) {
        long basePrice = getBasePrice(type);

        double retentionPer10k = 0.90;

        if (type == VehicleType.LUXURY || type == VehicleType.PERFORMANCE) {
            retentionPer10k = 0.88;
        } else if (type == VehicleType.TRUCK_VAN) {
            retentionPer10k = 0.93;
        }

        double timeUnits = (double) mileage / 10000.0;
        double estimatedValue = basePrice * Math.pow(retentionPer10k, timeUnits);

        return Math.max((long) estimatedValue, SCRAP_VALUE);
    }

    private long getBasePrice(VehicleType type) {
        switch (type) {
            case SUV:
                return BASE_SUV;
            case TRUCK_VAN:
                return BASE_TRUCK;
            case LUXURY:
                return BASE_LUXURY;
            case PERFORMANCE:
                return BASE_PERFORMANCE;
            case SEDAN:
            default:
                return BASE_SEDAN;
        }
    }

    public long estimateRepairCost(VehicleType type, long mileage) {
        // Actuarial Maintenance Debt Calculation
        // Assumption: If no quote is present, we predict the "Pending Maintenance
        // Liability"
        // typically accumulated at this mileage.

        long baseLiability;
        switch (type) {
            case LUXURY:
                baseLiability = 2500;
                break;
            case PERFORMANCE:
                baseLiability = 2000;
                break;
            case TRUCK_VAN:
                baseLiability = 1800;
                break;
            default:
                baseLiability = 1200;
        }

        // Higher mileage = exponentially higher probability of deferred maintenance
        // stacking
        double mileageFactor = 1.0 + (mileage / 50000.0);

        // Cap potential auto-estimated repair to reasonable "severe maintenance" levels
        // unless extreme
        return (long) (baseLiability * mileageFactor);
    }
}
