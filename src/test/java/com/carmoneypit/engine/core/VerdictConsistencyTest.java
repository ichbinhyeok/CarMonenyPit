package com.carmoneypit.engine.core;

import com.carmoneypit.engine.api.InputModels.EngineInput;
import com.carmoneypit.engine.api.OutputModels.VerdictResult;
import com.carmoneypit.engine.api.OutputModels.VerdictState;
import com.carmoneypit.engine.api.InputModels.VehicleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.Optional;

public class VerdictConsistencyTest {

    private DecisionEngine decisionEngine;
    private ValuationService valuationService;

    @BeforeEach
    public void setUp() {
        com.carmoneypit.engine.service.CarDataService carDataService = mock(
                com.carmoneypit.engine.service.CarDataService.class);
        valuationService = mock(ValuationService.class);
        RegretCalculator regretCalculator = new RegretCalculator(carDataService, valuationService);
        CostOfInactionCalculator coiCalculator = new CostOfInactionCalculator();

        when(valuationService.getMarketData(any())).thenReturn(Optional.empty());
        when(valuationService.getBrandData(any())).thenReturn(Optional.empty());

        decisionEngine = new DecisionEngine(regretCalculator, coiCalculator, valuationService);
    }

    @Test
    public void testHighMileageHighRepairCostShouldBeTimeBomb() {
        // High mileage, high repair cost vs low market value -> Time Bomb
        EngineInput input = new EngineInput(
                "Civic", VehicleType.SEDAN, "Honda", 2010,
                200000, 4500L, 3000L, true, true);

        VerdictResult result = decisionEngine.evaluate(input);
        assertEquals(VerdictState.TIME_BOMB, result.verdictState(),
                "High repair cost on a worn out car should be a TIME_BOMB");
    }

    @Test
    public void testLowMileageLowRepairCostShouldBeStable() {
        // Low mileage, low repair cost vs high market value -> Stable
        EngineInput input = new EngineInput(
                "Camry", VehicleType.SEDAN, "Toyota", 2020,
                50000, 800L, 20000L, true, true);

        VerdictResult result = decisionEngine.evaluate(input);
        assertEquals(VerdictState.STABLE, result.verdictState(),
                "Low repair cost on a fresh car should be STABLE");
    }
}
