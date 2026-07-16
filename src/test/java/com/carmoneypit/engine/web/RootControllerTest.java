package com.carmoneypit.engine.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(RootController.class)
class RootControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void repairOrSellCalculatorRouteRenders() throws Exception {
        mockMvc.perform(get("/tools/repair-or-sell-calculator"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/tools/repair_or_sell_calculator"));
    }

    @Test
    void secondOpinionGuideRouteRenders() throws Exception {
        mockMvc.perform(get("/guides/car-repair-estimate-second-opinion"))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/guides/repair_estimate_second_opinion"));
    }
}
