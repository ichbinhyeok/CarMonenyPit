package com.carmoneypit.engine.web;

import com.carmoneypit.engine.service.CarDataService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SeoDecisionSupportTest {

    @Test
    void rogueCvtSupportIncludesDiagnosticCodesAndWarrantyCheck() {
        var car = new CarDataService.CarModel(
                "nissan_rogue_t32", "NISSAN", "Rogue", "T32", 2014, 2020);
        var fault = new CarDataService.Fault(
                "CVT Transmission", "Judder", 4500, "Verify diagnosis",
                0.45, 147610, 3500, 6000, "high", "2026-07-16", List.of());
        var market = new CarDataService.ModelMarket(
                "nissan_rogue_t32", 9000, 0.1, 700, "declining", 700);

        var support = SeoDecisionSupport.forFault(car, fault, market);

        assertThat(support.priority()).isTrue();
        assertThat(support.diagnosisChecks())
                .anyMatch(item -> item.contains("P17F0") && item.contains("P17F1"))
                .anyMatch(item -> item.contains("VIN-specific eligibility"));
        assertThat(support.decisionSignals())
                .anyMatch(item -> item.contains("50%"));
    }

    @Test
    void mileageSupportAvoidsTreatingOdometerAsAutomaticSellSignal() {
        var car = new CarDataService.CarModel(
                "honda_odyssey_rl6", "HONDA", "Odyssey", "RL6", 2018, 2023);
        var reliability = new CarDataService.ModelReliability(
                "honda_odyssey_rl6", 75, 250000, List.of(2020), List.of(2018),
                List.of("Sliding doors"), List.of(), Map.of());
        var market = new CarDataService.ModelMarket(
                "honda_odyssey_rl6", 20000, 0.1, 800, "stable", 1200);

        var support = SeoDecisionSupport.forMileage(car, 200000, reliability, market);

        assertThat(support.searchIntentAnswer()).contains("odometer alone is not a sell signal");
        assertThat(support.inspectionChecks()).hasSizeGreaterThanOrEqualTo(4);
        assertThat(support.decisionSignals())
                .anyMatch(item -> item.contains("75%"));
    }
}
