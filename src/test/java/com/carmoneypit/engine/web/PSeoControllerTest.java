package com.carmoneypit.engine.web;

import com.carmoneypit.engine.config.PartnerRoutingConfig;
import com.carmoneypit.engine.core.DecisionEngine;
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
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(PSeoController.class)
public class PSeoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarDataService dataService;

    @MockBean
    private DecisionEngine decisionEngine;

    @MockBean
    private PartnerRoutingConfig routingConfig;

    @Test
    void modelPageShouldRenderDecisionHubWithRepresentativeYearLink() throws Exception {
        CarDataService.CarModel car = new CarDataService.CarModel(
                "toyota_camry_xv50", "TOYOTA", "Camry", "XV50", 2012, 2017);
        CarDataService.Fault fault = new CarDataService.Fault(
                "Torque Converter", "Shudder under load", 2500, "SELL", 0.0, 0);
        CarDataService.MajorFaults majorFaults = new CarDataService.MajorFaults(
                "toyota_camry_xv50", List.of(fault));
        CarDataService.ModelReliability reliability = new CarDataService.ModelReliability(
                "toyota_camry_xv50", 82, 220000, List.of(2015, 2016, 2017), List.of(2012, 2014),
                List.of("Transmission", "Oil leaks"), List.of(), Map.of());
        CarDataService.ModelMarket market = new CarDataService.ModelMarket(
                "toyota_camry_xv50", 13500, 0.08, 388, "stable", 1200);

        given(dataService.findCarBySlug("toyota", "camry")).willReturn(Optional.of(car));
        given(dataService.findFaultsByModelId("toyota_camry_xv50")).willReturn(Optional.of(majorFaults));
        given(dataService.findReliabilityByModelId("toyota_camry_xv50")).willReturn(Optional.of(reliability));
        given(dataService.findMarketByModelId("toyota_camry_xv50")).willReturn(Optional.of(market));

        mockMvc.perform(get("/models/toyota/camry"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/model_hub"))
                .andExpect(model().attribute("representativeYear", 2017))
                .andExpect(model().attribute("shouldFixUrl", "/should-i-fix/2017-toyota-camry"))
                .andExpect(model().attribute("decisionPageLinks", hasSize(3)));
    }

    @Test
    void brandDirectoryShouldExposePriorityDecisionLinks() throws Exception {
        given(dataService.getModelsByBrand("honda")).willReturn(List.of(
                new CarDataService.CarModel("honda_accord_9g", "HONDA", "Accord", "9G", 2013, 2017),
                new CarDataService.CarModel("honda_crv_rm", "HONDA", "CR-V", "RM", 2012, 2016)));

        mockMvc.perform(get("/models/honda"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/directory_list"))
                .andExpect(model().attribute("featuredItems", hasSize(2)))
                .andExpect(model().attribute("metaDescription", containsString("HONDA")));
    }
}
