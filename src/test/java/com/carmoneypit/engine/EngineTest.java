package com.carmoneypit.engine;

import com.carmoneypit.engine.api.InputModels.EngineInput;
import com.carmoneypit.engine.api.InputModels.VehicleType;
import com.carmoneypit.engine.api.OutputModels.VerdictResult;
import com.carmoneypit.engine.api.OutputModels.VerdictState;
import com.carmoneypit.engine.core.DecisionEngine;
import com.carmoneypit.engine.core.PointConverter;
import com.carmoneypit.engine.core.RegretCalculator;
import com.carmoneypit.engine.data.EngineDataProvider;
import com.carmoneypit.engine.model.EngineDataModels.EngineData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class EngineTest {

    private DecisionEngine engine;

    @BeforeEach
    public void setup() {
        // Real Data Loading (Integration style test)
        ObjectMapper mapper = new ObjectMapper();
        EngineDataProvider provider = new EngineDataProvider(mapper);
        provider.loadData();

        PointConverter converter = new PointConverter(provider);
        RegretCalculator calculator = new RegretCalculator(provider, converter);
        engine = new DecisionEngine(calculator);
    }

    @Test
    public void testCheapRepairLowMileage_ShouldBeStable() {
        // 40k miles, $500 repair -> Should be STABLE
        EngineInput input = new EngineInput(
                VehicleType.SEDAN,
                40000,
                500 // $500 -> 50 points
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
                3000 // $3000 -> 300 points
        );

        VerdictResult result = engine.evaluate(input);

        System.out.println("Result: " + result);
        assertEquals(VerdictState.TIME_BOMB, result.verdictState());
    }
}
