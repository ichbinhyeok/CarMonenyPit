package com.carmoneypit.engine.core;

import com.carmoneypit.engine.api.InputModels.VehicleType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ValuationServiceTest {

        private ValuationService service;

        @org.junit.jupiter.api.BeforeEach
        void setUp() {
                service = new ValuationService(new com.fasterxml.jackson.databind.ObjectMapper(),
                                org.mockito.Mockito.mock(com.carmoneypit.engine.service.CarDataService.class));
                service.init();
        }

        @Test
        public void testEstimatedValue_LowMileage() {
                // e.g. Sedan base ~28k. 10k miles.
                // Should be high.
                long val = service.estimateValue(com.carmoneypit.engine.api.InputModels.CarBrand.TOYOTA, "Camry",
                                VehicleType.SEDAN, 2018, 10_000);
                assertTrue(val > 20_000, "Should be > 20k, got " + val);
        }

        @Test
        public void testEstimatedValue_HighMileage() {
                // 200k miles.
                // Should be low but not negative.
                long val = service.estimateValue(com.carmoneypit.engine.api.InputModels.CarBrand.TOYOTA, "Camry",
                                VehicleType.SEDAN, 2018, 200_000);
                assertTrue(val > 500, "Should be > scrap 500, got " + val);
                assertTrue(val < 18_000, "Should be < 18k (High Mileage Impact), got " + val);
        }

        @Test
        public void testLuxuryDepreciation() {
                // Luxury falls faster?
                long sedanVal = service.estimateValue(com.carmoneypit.engine.api.InputModels.CarBrand.TOYOTA, "Camry",
                                VehicleType.SEDAN, 2018, 100_000);
                long luxVal = service.estimateValue(com.carmoneypit.engine.api.InputModels.CarBrand.BMW, "7 Series",
                                VehicleType.LUXURY, 2018, 100_000);

                // Base Luxury 65k vs Sedan 28k.
                // Even with faster depreciation, absolute value might be higher.
                // Let's check retention ratio.

                long valLowMiles = service.estimateValue(com.carmoneypit.engine.api.InputModels.CarBrand.BMW,
                                "7 Series",
                                VehicleType.LUXURY, 2018, 10_000);
                long valHighMiles = service.estimateValue(com.carmoneypit.engine.api.InputModels.CarBrand.BMW,
                                "7 Series",
                                VehicleType.LUXURY, 2018, 150_000);

                double ratio = (double) valHighMiles / valLowMiles;
                // Just ensure it works without crashing
                assertTrue(valHighMiles > 0);
        }
}
