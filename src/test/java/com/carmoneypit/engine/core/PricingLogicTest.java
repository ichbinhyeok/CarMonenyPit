package com.carmoneypit.engine.core;

import com.carmoneypit.engine.api.InputModels.VehicleType;
import com.carmoneypit.engine.service.CarDataService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class PricingLogicTest {

    private ValuationService valuationService;

    @BeforeEach
    void setUp() {
        // Mock CarDataService as we want to test algorithmic logic, not DB lookup
        CarDataService carDataService = Mockito.mock(CarDataService.class);
        valuationService = new ValuationService(new ObjectMapper(), carDataService);
        valuationService.init(); // Load brand JSON
    }

    @Test
    void testGarageQueen_Toyota() {
        // 2010 Toyota Camry (16 years old) with only 20,000 miles.
        // Old logic would treat this as a nearly new car (20k miles ~ 1.6 years).
        // New logic should heavily depreciate it based on 16 years of age.

        long value = valuationService.estimateValue(
                "TOYOTA", "Camry", VehicleType.SEDAN, 2010, 20000);

        System.out.println("Garage Queen Value: $" + value);

        // Base Sedan ~32,000
        // Age 16. Retention 0.94^16 = ~0.37
        // Base Value ~ 11,800
        // Mileage Bonus: 20k actual / (16*12k expected = 192k) = 0.1 ratio
        // Adjuster: 1 + (0.9 * 0.15) = 1.135
        // Expected ~ 13,000 - 14,000 range

        assertTrue(value < 18000, "16 year old car should not be worth new car prices");
        assertTrue(value > 8000, "Toyota should hold some value");
    }

    @Test
    void testRoadWarrior_BMW() {
        // 2023 BMW (3 years old) with 100,000 miles.
        // Heavy mileage penalty + Cliff depreciation.

        long value = valuationService.estimateValue(
                "BMW", "3 Series", VehicleType.SEDAN, 2023, 100000);

        System.out.println("Road Warrior Value: $" + value);

        // Base Sedan ~32,000
        // Age 3. Retention 0.80^3 = 0.512
        // Base Value ~ 16,384
        // Mileage Penalty: 100k / (3*12k = 36k) = 2.77 ratio
        // Adjuster: 1 + (1 - 2.77)*0.15 = 1 - 0.26 = 0.74 (capped at 0.5?)
        // Final ~ 12,000 range

        assertTrue(value < 15000, "High mileage BMW should depreciate hard");
    }

    @Test
    void testBrandComparison() {
        // Comparing Toyota vs BMW for same Age (5 years) and Miles (60k)
        int year = 2021;
        long miles = 60000;

        long toyotaValue = valuationService.estimateValue("TOYOTA", "Camry", VehicleType.SEDAN, year, miles);
        long bmwValue = valuationService.estimateValue("BMW", "3 Series", VehicleType.SEDAN, year, miles);

        System.out.println("Toyota Value (5y/60k): $" + toyotaValue);
        System.out.println("BMW Value (5y/60k): $" + bmwValue);

        assertTrue(toyotaValue > bmwValue, "Toyota should retain value better than BMW");
    }
}
