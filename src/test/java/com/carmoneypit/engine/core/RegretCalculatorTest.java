package com.carmoneypit.engine.core;

import com.carmoneypit.engine.api.InputModels.EngineInput;
import com.carmoneypit.engine.api.InputModels.VehicleType;
import com.carmoneypit.engine.service.CarDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RegretCalculatorTest {

    private CarDataService carDataService;
    private RegretCalculator regretCalculator;

    @BeforeEach
    void setUp() {
        carDataService = mock(CarDataService.class);
        regretCalculator = new RegretCalculator(carDataService, mock(ValuationService.class));
    }

    @Test
    void zeroCostFaultRowsShouldNotCollapseFutureFailureRiskToZero() {
        CarDataService.CarModel rx = new CarDataService.CarModel("lexus_rx_al20", "LEXUS", "RX", "AL20", 2016, 2022);
        CarDataService.Fault zeroCostFault = new CarDataService.Fault(
                "Fuel Pump",
                "Hard starts",
                0.0,
                "Recall check first",
                0.2,
                110000);
        CarDataService.MajorFaults faults = new CarDataService.MajorFaults("lexus_rx_al20", List.of(zeroCostFault));

        when(carDataService.findCarBySlug(null, "RX")).thenReturn(Optional.of(rx));
        when(carDataService.findFaultsByModelId("lexus_rx_al20")).thenReturn(Optional.of(faults));

        EngineInput input = new EngineInput("RX", VehicleType.SUV, "LEXUS", 2020, 120000, 1800, 25000, false, false);
        var result = regretCalculator.calculateRF(input, null);

        double riskAmount = result.items().stream()
                .filter(item -> item.label().contains("Risk of Next Breakdown"))
                .mapToDouble(item -> item.amount())
                .findFirst()
                .orElse(0.0);

        assertTrue(riskAmount > 0.0,
                "Future failure risk should fall back to a positive baseline even when source data has a zero-cost fault row.");
    }
}
