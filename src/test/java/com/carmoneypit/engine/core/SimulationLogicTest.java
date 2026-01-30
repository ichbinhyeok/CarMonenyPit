package com.carmoneypit.engine.core;

import com.carmoneypit.engine.api.InputModels.CarBrand;
import com.carmoneypit.engine.api.InputModels.EngineInput;
import com.carmoneypit.engine.api.InputModels.FailureSeverity;
import com.carmoneypit.engine.api.InputModels.HassleTolerance;
import com.carmoneypit.engine.api.InputModels.MobilityStatus;
import com.carmoneypit.engine.api.InputModels.SimulationControls;
import com.carmoneypit.engine.api.InputModels.VehicleType;
import com.carmoneypit.engine.api.OutputModels.VerdictResult;
import com.carmoneypit.engine.api.OutputModels.VerdictState;
import com.carmoneypit.engine.service.CarDataService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

public class SimulationLogicTest {

    private DecisionEngine decisionEngine;
    private RegretCalculator regretCalculator;
    private CostOfInactionCalculator coiCalculator;
    private ValuationService valuationService;
    private CarDataService carDataService;

    @BeforeEach
    void setUp() {
        carDataService = Mockito.mock(CarDataService.class);
        valuationService = new ValuationService(new ObjectMapper(), carDataService);
        valuationService.init(); // Load static brand data if possible, or mock fully

        regretCalculator = new RegretCalculator(carDataService, valuationService);
        coiCalculator = new CostOfInactionCalculator();
        decisionEngine = new DecisionEngine(regretCalculator, coiCalculator, valuationService);

        // Mock Market Data to avoid NPEs
        given(carDataService.findMarketByModelId(anyString())).willReturn(Optional.empty());
    }

    private EngineInput createInput(long quote, long mileage) {
        return new EngineInput(
                "TestModel",
                VehicleType.SEDAN,
                CarBrand.TOYOTA,
                2018,
                mileage,
                quote,
                15000, // Current Value
                false,
                false);
    }

    private SimulationControls createControls(HassleTolerance tolerance) {
        return new SimulationControls(
                FailureSeverity.GENERAL_UNKNOWN,
                MobilityStatus.DRIVABLE,
                tolerance,
                null);
    }

    @Test
    void testRepairQuoteSensitivity() {
        // Scenario 1: Formatting Fix ($500)
        EngineInput cheapFix = createInput(500, 50_000);
        VerdictResult resultCheap = decisionEngine.evaluate(cheapFix);

        // Scenario 2: Major Engine Work ($4000)
        EngineInput expensiveFix = createInput(4000, 50_000);
        VerdictResult resultExpensive = decisionEngine.evaluate(expensiveFix);

        System.out.println("Cheap Fix RF (Regret of Fixing): " + resultCheap.stayTotal());
        System.out.println("Expensive Fix RF (Regret of Fixing): " + resultExpensive.stayTotal());

        // Verification: Higher quote MUST lead to higher 'Stay' regret
        assertTrue(resultExpensive.stayTotal() > resultCheap.stayTotal(),
                "Higher repair quote should increase the Regret of Staying");

        // Verification: Expensive quote should push towards TIME_BOMB or BORDERLINE
        // (Assuming 4000 is high for a 15k car? Not necessarily TIME_BOMB yet, but
        // definitely higher risk)
        // Let's check the delta (RF - RM).
        double cheapDelta = resultCheap.stayTotal() - resultCheap.moveTotal();
        double expensiveDelta = resultExpensive.stayTotal() - resultExpensive.moveTotal();

        assertTrue(expensiveDelta > cheapDelta, "Expensive fix should make Staying less attractive (higher delta)");
    }

    @Test
    void testMileageSensitivity() {
        // Scenario 1: Low Mileage (50k)
        EngineInput youngCar = createInput(1000, 50_000);
        VerdictResult resultYoung = decisionEngine.evaluate(youngCar);

        // Scenario 2: High Mileage (160k) - Triggers "Risk Multipliers"
        EngineInput oldCar = createInput(1000, 160_000);
        VerdictResult resultOld = decisionEngine.evaluate(oldCar);

        System.out.println("Young Car Risk Item: " + getRiskItemAmount(resultYoung));
        System.out.println("Old Car Risk Item: " + getRiskItemAmount(resultOld));

        // Verification: High mileage should trigger higher "Risk of Next Breakdown"
        assertTrue(getRiskItemAmount(resultOld) > getRiskItemAmount(resultYoung),
                "High mileage should significantly increase future breakdown risk cost");

        // Verification: High mileage should calculate higher Asset Bleed (COI)
        assertTrue(resultOld.assetBleedAmount() > 0, "Asset bleed should be calculated");
    }

    @Test
    void testHassleToleranceImpact() {
        // Baseline Input
        EngineInput input = createInput(2000, 80_000);

        // Scenario 1: Hates Switching (High Friction)
        SimulationControls haterControls = createControls(HassleTolerance.HATE_SWITCHING);
        VerdictResult resultHater = decisionEngine.simulate(input, haterControls);

        // Scenario 2: Wants New Car (Low Friction)
        SimulationControls loverControls = createControls(HassleTolerance.WANT_NEW_CAR);
        VerdictResult resultLover = decisionEngine.simulate(input, loverControls);

        System.out.println("Hater Friction (Move Regret): " + resultHater.moveTotal());
        System.out.println("Lover Friction (Move Regret): " + resultLover.moveTotal());

        // Verification: Hating switching adds "Psychological Costs" to Moving, making
        // it HARDER to switch.
        // So Move Regret (RM) should be HIGHER for the Hater.
        assertTrue(resultHater.moveTotal() > resultLover.moveTotal(),
                "Hating switching should increase the Cost of Moving (RM)");
    }

    private double getRiskItemAmount(VerdictResult result) {
        return result.costBreakdown().stream()
                .filter(i -> i.label().contains("Risk of Next"))
                .mapToDouble(i -> i.amount())
                .findFirst()
                .orElse(0.0);
    }
}
