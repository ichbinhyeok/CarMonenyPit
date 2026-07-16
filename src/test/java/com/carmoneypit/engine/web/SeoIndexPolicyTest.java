package com.carmoneypit.engine.web;

import com.carmoneypit.engine.service.CarDataService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SeoIndexPolicyTest {

    @Test
    void representativeYearAndMileageEligibilityStayCentralized() {
        var camry = new CarDataService.CarModel(
                "toyota_camry_xv50", "TOYOTA", "Camry", "XV50", 2012, 2017);
        var reliability = new CarDataService.ModelReliability(
                "toyota_camry_xv50", 82, 220000, List.of(2017), List.of(2012),
                List.of(), List.of(), Map.of());

        assertEquals(2014, SeoIndexPolicy.representativeYear(camry, reliability));
        assertTrue(SeoIndexPolicy.isIndexableDecision(camry));
        assertTrue(SeoIndexPolicy.isIndexableMileage(camry, 200000));
        assertFalse(SeoIndexPolicy.isIndexableMileage(camry, 50000));
    }

    @Test
    void faultSlugMatchesRouterRulesForPunctuationHeavyComponents() {
        assertEquals("15l20l-coolant-intrusion",
                SeoIndexPolicy.faultSlug("1.5L/2.0L Coolant Intrusion"));
        assertEquals("water-pump-thermostat-failure",
                SeoIndexPolicy.faultSlug("Water Pump & Thermostat Failure"));
        assertEquals("ac-condenser", SeoIndexPolicy.faultSlug("A/C Condenser"));
    }
}
