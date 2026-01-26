package com.carmoneypit.engine;

import com.carmoneypit.engine.api.InputModels.EngineInput;
import com.carmoneypit.engine.api.InputModels.VehicleType;
import com.carmoneypit.engine.api.OutputModels.VerdictResult;
import com.carmoneypit.engine.api.OutputModels.VerdictState;
import com.carmoneypit.engine.core.CostOfInactionCalculator;
import com.carmoneypit.engine.core.DecisionEngine;
import com.carmoneypit.engine.core.RegretCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EngineTest {

    private DecisionEngine engine;

    @BeforeEach
    public void setup() {
        RegretCalculator calculator = new RegretCalculator();
        CostOfInactionCalculator costCalc = new CostOfInactionCalculator();
        engine = new DecisionEngine(calculator, costCalc);
    }

    @Test
    public void testCheapRepairLowMileage_ShouldBeStable() {
        // 40k miles, $500 repair -> Should be STABLE
        EngineInput input = new EngineInput(
                VehicleType.SEDAN,
                40000,
                500, // $500
                15000 // currentValue
        );

        VerdictResult result = engine.evaluate(input);

        System.out.println("Result: " + result);
        assertEquals(VerdictState.STABLE, result.verdictState());
    }

    @Test
    public void testExpensiveRepairHighMileage_ShouldBeTimeBomb() {
        // 160k miles (High Risk), $3000 repair -> Should be TIME_BOMB
        // High future risk + high cost
        EngineInput input = new EngineInput(
                VehicleType.SEDAN,
                160000,
                3000, // $3000
                5000 // currentValue
        );

        VerdictResult result = engine.evaluate(input);

        System.out.println("Result: " + result);
        assertEquals(VerdictState.TIME_BOMB, result.verdictState());
    }
}
