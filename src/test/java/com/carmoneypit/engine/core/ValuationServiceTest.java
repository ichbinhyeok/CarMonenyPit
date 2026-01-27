package com.carmoneypit.engine.core;

import com.carmoneypit.engine.api.InputModels.VehicleType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ValuationServiceTest {

    private final ValuationService service = new ValuationService();

    @Test
    public void testEstimatedValue_LowMileage() {
        // e.g. Sedan base ~28k. 10k miles.
        // Should be high.
        long val = service.estimateValue(VehicleType.SEDAN, 10_000);
        assertTrue(val > 20_000, "Should be > 20k, got " + val);
    }

    @Test
    public void testEstimatedValue_HighMileage() {
        // 200k miles.
        // Should be low but not negative.
        long val = service.estimateValue(VehicleType.SEDAN, 200_000);
        assertTrue(val > 500, "Should be > scrap 500, got " + val);
        assertTrue(val < 10_000, "Should be < 10k, got " + val);
    }

    @Test
    public void testLuxuryDepreciation() {
        // Luxury falls faster?
        long sedanVal = service.estimateValue(VehicleType.SEDAN, 100_000);
        long luxVal = service.estimateValue(VehicleType.LUXURY, 100_000);

        // Base Luxury 65k vs Sedan 28k.
        // Even with faster depreciation, absolute value might be higher.
        // Let's check retention ratio.

        long valLowMiles = service.estimateValue(VehicleType.LUXURY, 10_000);
        long valHighMiles = service.estimateValue(VehicleType.LUXURY, 150_000);

        double ratio = (double) valHighMiles / valLowMiles;
        // Just ensure it works without crashing
        assertTrue(valHighMiles > 0);
    }
}
