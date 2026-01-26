package com.carmoneypit.engine.web;

import com.carmoneypit.engine.api.InputModels.EngineInput;
import com.carmoneypit.engine.api.InputModels.SimulationControls;
import com.carmoneypit.engine.api.OutputModels.VerdictResult;
import com.carmoneypit.engine.api.OutputModels.VerdictState;
import com.carmoneypit.engine.api.OutputModels.VisualizationHint;
import com.carmoneypit.engine.core.DecisionEngine;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarDecisionController.class)
public class WebLayerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DecisionEngine decisionEngine;

    @Test
    public void testIndexPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    public void testAnalyze() throws Exception {
        // Mock Engine Response
        VerdictResult mockResult = new VerdictResult(
                VerdictState.STABLE,
                "Stable Narrative",
                new VisualizationHint(100, 200, "SURFACE"));
        given(decisionEngine.evaluate(any(EngineInput.class))).willReturn(mockResult);

        mockMvc.perform(post("/analyze")
                .param("vehicleType", "SEDAN")
                .param("mileage", "50000")
                .param("repairQuoteUsd", "500"))
                .andExpect(status().isOk())
                .andExpect(view().name("result"))
                .andExpect(model().attributeExists("result", "input", "controls"));
    }

    @Test
    public void testSimulate() throws Exception {
        // Mock Engine Response
        VerdictResult mockResult = new VerdictResult(
                VerdictState.TIME_BOMB,
                "Time Bomb Narrative",
                new VisualizationHint(300, 100, "DEEP_PIT"));
        given(decisionEngine.simulate(any(EngineInput.class), any(SimulationControls.class))).willReturn(mockResult);

        mockMvc.perform(post("/simulate")
                .param("vehicleType", "SEDAN")
                .param("mileage", "50000")
                .param("repairQuoteUsd", "500")
                .param("failureSeverity", "ENGINE_TRANSMISSION")
                .param("mobilityStatus", "DRIVABLE")
                .param("hassleTolerance", "NEUTRAL"))
                .andExpect(status().isOk())
                // Should return fragment view name
                .andExpect(view().name("fragments/verdict_card"))
                .andExpect(model().attributeExists("result"));
    }
}
