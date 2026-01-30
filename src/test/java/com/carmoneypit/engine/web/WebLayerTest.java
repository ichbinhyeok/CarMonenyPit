package com.carmoneypit.engine.web;

import com.carmoneypit.engine.api.InputModels.EngineInput;
import com.carmoneypit.engine.api.InputModels.SimulationControls;
import com.carmoneypit.engine.api.OutputModels.VerdictResult;
import com.carmoneypit.engine.api.OutputModels.VerdictState;
import com.carmoneypit.engine.api.OutputModels.VisualizationHint;
import com.carmoneypit.engine.api.OutputModels.PeerData;
import com.carmoneypit.engine.api.OutputModels.EconomicContext;
import com.carmoneypit.engine.core.DecisionEngine;
import com.carmoneypit.engine.core.ValuationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

@WebMvcTest(CarDecisionController.class)
public class WebLayerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private DecisionEngine decisionEngine;

        @MockBean
        private VerdictPresenter verdictPresenter;

        @MockBean
        private ValuationService valuationService;

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
                                new VisualizationHint(100, 200, "SURFACE"),
                                Collections.emptyList(),
                                0L, 0.0, 0.0, 90, null, null);
                given(decisionEngine.evaluate(any(EngineInput.class))).willReturn(mockResult);

                // Mock Presenter Response
                given(verdictPresenter.getVerdictTitle(any())).willReturn("SUSTAIN");
                given(verdictPresenter.getLawyerExplanation(any(), any())).willReturn("Explanation");
                given(verdictPresenter.getActionPlan(any())).willReturn("Action");
                given(verdictPresenter.getCssClass(any())).willReturn("class-sustain");
                given(verdictPresenter.getLeadLabel(any(), any(), any())).willReturn("Lead Label");
                given(verdictPresenter.getLeadDescription(any(), any(), any())).willReturn("Lead Desc");
                given(verdictPresenter.getLeadUrl(any(), any(), any())).willReturn("http://example.com");

                mockMvc.perform(post("/analyze-final")
                                .param("brand", "TOYOTA")
                                .param("year", "2018")
                                .param("vehicleType", "SEDAN")
                                .param("mileage", "50000")
                                .param("repairQuoteUsd", "500")
                                .param("currentValueUsd", "10000"))
                                .andExpect(status().isOk())
                                .andExpect(view().name(""))
                                .andExpect(header().string("HX-Location", containsString("/report?token=")));
        }

        @Test
        public void testSimulate() throws Exception {
                // Mock Engine Response
                VerdictResult mockResult = new VerdictResult(
                                VerdictState.TIME_BOMB,
                                "Time Bomb Narrative",
                                new VisualizationHint(300, 100, "DEEP_PIT"),
                                Collections.emptyList(),
                                0L, 0.0, 0.0, 90, null, null);
                given(decisionEngine.simulate(any(EngineInput.class), any(SimulationControls.class)))
                                .willReturn(mockResult);

                // Mock Presenter Response
                given(verdictPresenter.getVerdictTitle(any())).willReturn("TERMINATE");
                given(verdictPresenter.getLawyerExplanation(any(), any())).willReturn("Term Explanation");
                given(verdictPresenter.getActionPlan(any())).willReturn("Term Action");
                given(verdictPresenter.getCssClass(any())).willReturn("class-terminate");
                given(verdictPresenter.getLeadLabel(any(), any(), any())).willReturn("Term Label");
                given(verdictPresenter.getLeadDescription(any(), any(), any())).willReturn("Term Desc");
                given(verdictPresenter.getLeadUrl(any(), any(), any())).willReturn("http://term-url.com");

                mockMvc.perform(post("/simulate")
                                .param("brand", "TOYOTA")
                                .param("year", "2018")
                                .param("vehicleType", "SEDAN")
                                .param("mileage", "50000")
                                .param("repairQuoteUsd", "500")
                                .param("currentValueUsd", "10000")
                                .param("failureSeverity", "ENGINE_TRANSMISSION")
                                .param("mobilityStatus", "DRIVABLE")
                                .param("hassleTolerance", "NEUTRAL"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("simulation_response"))
                                .andExpect(view().name("simulation_response"));
        }

        @Test
        public void testAnalyze_NoValue_ShouldEstimate() throws Exception {
                // Mock Engine Response
                VerdictResult mockResult = new VerdictResult(
                                VerdictState.STABLE,
                                "Stable Narrative",
                                new VisualizationHint(100, 200, "SURFACE"),
                                Collections.emptyList(),
                                0L, 0.0, 0.0, 90, null, null);

                // Mock Oracle
                given(valuationService.estimateValue(any(), any(), any(), anyInt(), anyLong())).willReturn(12500L);
                given(decisionEngine.evaluate(any(EngineInput.class))).willReturn(mockResult);

                // Mock Presenter
                given(verdictPresenter.getVerdictTitle(any())).willReturn("SUSTAIN");
                given(verdictPresenter.getLawyerExplanation(any(), any())).willReturn("Explanation");
                given(verdictPresenter.getActionPlan(any())).willReturn("Action");
                given(verdictPresenter.getCssClass(any())).willReturn("class-sustain");
                given(verdictPresenter.getLeadLabel(any(), any(), any())).willReturn("Lead Label");
                given(verdictPresenter.getLeadDescription(any(), any(), any())).willReturn("Lead Desc");
                given(verdictPresenter.getLeadUrl(any(), any(), any())).willReturn("http://example.com");

                mockMvc.perform(post("/analyze-final")
                                .param("brand", "TOYOTA")
                                .param("year", "2018")
                                .param("vehicleType", "SEDAN")
                                .param("mileage", "50000")
                                .param("repairQuoteUsd", "500"))
                                .andDo(print())
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(view().name(""))
                                .andExpect(header().string("HX-Location", containsString("/report?token=")));
        }
}
