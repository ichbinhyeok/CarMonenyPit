package com.carmoneypit.engine.core;

import com.carmoneypit.engine.api.InputModels.EngineInput;
import com.carmoneypit.engine.api.InputModels.VehicleType;
import com.carmoneypit.engine.data.CarBrandData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DecisionEngineTest {

    private RegretCalculator regretCalculator;
    private CostOfInactionCalculator costOfInactionCalculator;
    private ValuationService valuationService;
    private DecisionEngine decisionEngine;

    @BeforeEach
    void setUp() {
        regretCalculator = mock(RegretCalculator.class);
        costOfInactionCalculator = mock(CostOfInactionCalculator.class);
        valuationService = mock(ValuationService.class);
        decisionEngine = new DecisionEngine(regretCalculator, costOfInactionCalculator, valuationService);

        when(regretCalculator.calculateRF(any(), any())).thenReturn(
                new RegretCalculator.RegretDetail(3200.0, List.of()));
        when(regretCalculator.calculateRM(any(), any())).thenReturn(
                new RegretCalculator.RegretDetail(2400.0, List.of()));
        when(costOfInactionCalculator.calculateAssetBleed(any(), anyLong(), anyLong(), anyLong(), any()))
                .thenReturn(0L);
        when(valuationService.getMarketData(anyString())).thenReturn(Optional.empty());
    }

    @Test
    void shouldFallBackToDefaultsWhenBrandEconomicsAreMissing() {
        CarBrandData incompleteBrand = new CarBrandData();
        incompleteBrand.sellStatPct = null;
        incompleteBrand.avgSwitchingFriction = null;
        incompleteBrand.avgNewMonthly = null;
        when(valuationService.getBrandData("AUDI")).thenReturn(Optional.of(incompleteBrand));

        EngineInput input = new EngineInput("A4", VehicleType.LUXURY, "AUDI", 2018, 90000, 2500, 18000, false, false);
        var result = decisionEngine.evaluate(input);

        assertEquals(55, result.peerData().sellPercentage());
        assertEquals(45, result.peerData().repairPercentage());
        assertEquals(3000L, result.economicContext().totalSwitchingFriction());
        assertEquals(748L, result.economicContext().avgMonthlyPayment());
    }

    @Test
    void shouldUseBrandEconomicsWhenTheyExist() {
        CarBrandData completeBrand = new CarBrandData();
        completeBrand.sellStatPct = 62;
        completeBrand.avgSwitchingFriction = 4100L;
        completeBrand.avgNewMonthly = 920;
        when(valuationService.getBrandData("BMW")).thenReturn(Optional.of(completeBrand));

        EngineInput input = new EngineInput("X5", VehicleType.LUXURY, "BMW", 2019, 80000, 3000, 32000, false, false);
        var result = decisionEngine.evaluate(input);

        assertEquals(77, result.peerData().sellPercentage());
        assertEquals(23, result.peerData().repairPercentage());
        assertEquals(4100L, result.economicContext().totalSwitchingFriction());
        assertEquals(920L, result.economicContext().avgMonthlyPayment());
    }
}
