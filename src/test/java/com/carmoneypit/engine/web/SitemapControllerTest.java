package com.carmoneypit.engine.web;

import com.carmoneypit.engine.service.CarDataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SitemapController.class)
class SitemapControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarDataService dataService;

    @Test
    void sitemapShouldPromoteRepresentativeShouldFixPageOverStartYear() throws Exception {
        CarDataService.CarModel car = new CarDataService.CarModel(
                "toyota_camry_xv50", "TOYOTA", "Camry", "XV50", 2012, 2017);
        CarDataService.MajorFaults faults = new CarDataService.MajorFaults(
                "toyota_camry_xv50",
                List.of(new CarDataService.Fault("Torque Converter", "Shudder", 2500, "SELL", 0.0, 0)));
        CarDataService.ModelReliability reliability = new CarDataService.ModelReliability(
                "toyota_camry_xv50", 82, 220000, List.of(2015, 2016, 2017), List.of(2012, 2014),
                List.of("Transmission"), List.of(), Map.of());

        given(dataService.getAllBrands()).willReturn(List.of("TOYOTA"));
        given(dataService.getAllModels()).willReturn(List.of(car));
        given(dataService.findReliabilityByModelId("toyota_camry_xv50")).willReturn(Optional.of(reliability));
        given(dataService.findFaultsByModelId("toyota_camry_xv50")).willReturn(Optional.of(faults));

        mockMvc.perform(get("/sitemap.xml"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "https://automoneypit.com/should-i-fix/2017-toyota-camry")))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString(
                        "https://automoneypit.com/should-i-fix/2012-toyota-camry"))))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(
                        "<loc>https://automoneypit.com/verdict/toyota/camry/50000-miles</loc>")));
    }
}
