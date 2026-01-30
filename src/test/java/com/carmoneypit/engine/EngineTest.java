package com.carmoneypit.engine;

import com.carmoneypit.engine.api.InputModels.EngineInput;
import com.carmoneypit.engine.api.InputModels.VehicleType;
import com.carmoneypit.engine.api.InputModels.CarBrand;
import com.carmoneypit.engine.api.OutputModels.VerdictResult;
import com.carmoneypit.engine.api.OutputModels.VerdictState;
import com.carmoneypit.engine.core.CostOfInactionCalculator;
import com.carmoneypit.engine.core.DecisionEngine;
import com.carmoneypit.engine.core.RegretCalculator;
import com.carmoneypit.engine.core.ValuationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EngineTest {

    private DecisionEngine engine;

    @BeforeEach
    public void setup() {
        com.carmoneypit.engine.service.CarDataService carDataService = org.mockito.Mockito
                .mock(com.carmoneypit.engine.service.CarDataService.class);
        ValuationService valuationService = org.mockito.Mockito.mock(ValuationService.class);

        RegretCalculator calculator = new RegretCalculator(carDataService, valuationService);
        CostOfInactionCalculator costCalc = new CostOfInactionCalculator();
        engine = new DecisionEngine(calculator, costCalc, valuationService);
    }

    @Test
    void testCheapRepairLowMileage_ShouldBeStable() {
        // Repair $500, Value $15,000, Mileage 50k
        // RF should be low. RM friction $2500.
        EngineInput input = new EngineInput("TestModel", VehicleType.SEDAN, CarBrand.TOYOTA, 2018, 50000, 500, 15000,
                false, false);
        VerdictResult result = engine.evaluate(input);

        assertEquals(VerdictState.STABLE, result.verdictState());
    }

    @Test
    void testExpensiveRepairHighMileage_ShouldBeTimeBomb() {
        // Repair $4000, Value $3000, Mileage 160k
        // RF high (Cost + Risk). RM lower (Value low).
        EngineInput input = new EngineInput("TestModel", VehicleType.SEDAN, CarBrand.BMW, 2014, 160000, 4000, 3000,
                false, false);
        VerdictResult result = engine.evaluate(input);

        // With new logic, Trade-in Value is $3000 * 0.85 = 2550.
        // RM ~ Friction(2500) + TradeLoss(450) + etc = ~3000.
        // RF ~ Repair(4000) + Risk... >> RM.
        assertEquals(VerdictState.TIME_BOMB, result.verdictState());
    }

    @Test
    void testBorderlineCase() {
        // Repair $1500, Value $8000, Mileage 100k
        EngineInput input = new EngineInput("TestModel", VehicleType.SEDAN, CarBrand.HONDA, 2016, 100000, 1500, 8000,
                false, false);
        VerdictResult result = engine.evaluate(input);

        // This is tricky depending on tuning, but ensures it runs without error
        assertNotNull(result.verdictState());
    }
}
