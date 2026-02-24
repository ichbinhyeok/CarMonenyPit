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

        @Test
        public void testMarginBoundaryBorderlineWhenJustUnderTimeBomb() {
                RegretCalculator mockRegret = mock(RegretCalculator.class);
                CostOfInactionCalculator mockCoi = mock(CostOfInactionCalculator.class);

                when(mockRegret.calculateRF(any(), any())).thenReturn(
                                new RegretCalculator.RegretDetail(10500.0, java.util.Collections.emptyList()));
                when(mockRegret.calculateRM(any(), any())).thenReturn(
                                new RegretCalculator.RegretDetail(10000.0, java.util.Collections.emptyList()));

                DecisionEngine testEngine = new DecisionEngine(mockRegret, mockCoi, valuationService);

                EngineInput input = new EngineInput("Generic", VehicleType.SEDAN, "Brand", 2015, 100000, 1000L, 10000L,
                                true, true);
                VerdictResult result = testEngine.evaluate(input);

                assertEquals(VerdictState.BORDERLINE, result.verdictState(),
                                "RM=10000, RF=10500 should be exactly BORDERLINE (not >)");
        }

        @Test
        public void testMarginBoundaryTimeBombWhenJustOver() {
                RegretCalculator mockRegret = mock(RegretCalculator.class);
                CostOfInactionCalculator mockCoi = mock(CostOfInactionCalculator.class);

                when(mockRegret.calculateRF(any(), any())).thenReturn(
                                new RegretCalculator.RegretDetail(10501.0, java.util.Collections.emptyList()));
                when(mockRegret.calculateRM(any(), any())).thenReturn(
                                new RegretCalculator.RegretDetail(10000.0, java.util.Collections.emptyList()));

                DecisionEngine testEngine = new DecisionEngine(mockRegret, mockCoi, valuationService);

                EngineInput input = new EngineInput("Generic", VehicleType.SEDAN, "Brand", 2015, 100000, 1000L, 10000L,
                                true, true);
                VerdictResult result = testEngine.evaluate(input);

                assertEquals(VerdictState.TIME_BOMB, result.verdictState(), "RM=10000, RF=10501 should be TIME_BOMB");
        }

        @Test
        public void testMarginBoundaryStableWhenJustUnder() {
                RegretCalculator mockRegret = mock(RegretCalculator.class);
                CostOfInactionCalculator mockCoi = mock(CostOfInactionCalculator.class);

                when(mockRegret.calculateRF(any(), any())).thenReturn(
                                new RegretCalculator.RegretDetail(9500.0, java.util.Collections.emptyList()));
                when(mockRegret.calculateRM(any(), any())).thenReturn(
                                new RegretCalculator.RegretDetail(10000.0, java.util.Collections.emptyList()));

                DecisionEngine testEngine = new DecisionEngine(mockRegret, mockCoi, valuationService);

                EngineInput input = new EngineInput("Generic", VehicleType.SEDAN, "Brand", 2015, 100000, 1000L, 10000L,
                                true, true);
                VerdictResult result = testEngine.evaluate(input);

                assertEquals(VerdictState.STABLE, result.verdictState(),
                                "RM=10000, RF=9500 should be STABLE (<= condition)");
        }

        @Test
        public void testExtremeMileageMaintainsMarginBoundary() {
                RegretCalculator mockRegret = mock(RegretCalculator.class);
                CostOfInactionCalculator mockCoi = mock(CostOfInactionCalculator.class);

                // Even with extreme mileage, if RegretCalculator says it's borderline, Engine
                // should enforce borderline.
                when(mockRegret.calculateRF(any(), any())).thenReturn(
                                new RegretCalculator.RegretDetail(10499.0, java.util.Collections.emptyList()));
                when(mockRegret.calculateRM(any(), any())).thenReturn(
                                new RegretCalculator.RegretDetail(10000.0, java.util.Collections.emptyList()));

                DecisionEngine testEngine = new DecisionEngine(mockRegret, mockCoi, valuationService);

                // Extreme mileage 200k
                EngineInput input = new EngineInput("Generic", VehicleType.SEDAN, "Brand", 2015, 200000, 1000L, 10000L,
                                true, true);
                VerdictResult result = testEngine.evaluate(input);

                assertEquals(VerdictState.BORDERLINE, result.verdictState(),
                                "Extreme mileage should not break margin invariant");
        }
}
