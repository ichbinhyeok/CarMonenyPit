package com.carmoneypit.engine.web;

import com.carmoneypit.engine.config.PartnerRoutingConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(LeadController.class)
class LeadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PartnerRoutingConfig routingConfig;

    @Test
    void leadRedirectShouldUseCleanWaitlistUrlAndPreserveContext() throws Exception {
        given(routingConfig.isApprovalPending()).willReturn(true);
        given(routingConfig.getWaitlistUrl()).willReturn("/lead-capture");

        MvcResult redirectResult = mockMvc.perform(get("/lead")
                        .session(new MockHttpSession())
                        .param("page_type", "pseo_fault")
                        .param("intent", "SELL")
                        .param("verdict_state", "TIME_BOMB")
                        .param("brand", "toyota")
                        .param("model", "camry")
                        .param("detail", "torque-converter")
                        .param("placement", "inline"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/lead-capture"))
                .andReturn();

        MockHttpSession session = (MockHttpSession) redirectResult.getRequest().getSession(false);

        mockMvc.perform(get("/lead-capture").session(session))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Robots-Tag", containsString("noindex")))
                .andExpect(header().string("Link", "<https://automoneypit.com/lead-capture>; rel=\"canonical\""))
                .andExpect(view().name("pages/lead_capture"))
                .andExpect(model().attribute("brand", "Toyota"))
                .andExpect(model().attribute("brandValue", "toyota"))
                .andExpect(model().attribute("modelValue", "camry"))
                .andExpect(model().attribute("pageType", "pseo_fault"))
                .andExpect(model().attribute("detail", "torque-converter"))
                .andExpect(model().attribute("placement", "inline"))
                .andExpect(model().attribute("intent", "SELL"))
                .andExpect(model().attributeExists("leadId"));
    }

    @Test
    void legacyWaitlistQueryUrlShouldRedirectToCleanCanonicalPath() throws Exception {
        given(routingConfig.getWaitlistUrl()).willReturn("/lead-capture");

        MvcResult redirectResult = mockMvc.perform(get("/lead-capture")
                        .session(new MockHttpSession())
                        .param("verdict", "TIME_BOMB")
                        .param("brand", "toyota")
                        .param("model", "camry")
                        .param("pageType", "pseo_fault")
                        .param("detail", "torque-converter")
                        .param("placement", "inline")
                        .param("intent", "SELL"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/lead-capture"))
                .andReturn();

        MockHttpSession session = (MockHttpSession) redirectResult.getRequest().getSession(false);

        mockMvc.perform(get("/lead-capture").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/lead_capture"))
                .andExpect(model().attribute("brand", "Toyota"))
                .andExpect(model().attribute("brandValue", "toyota"))
                .andExpect(model().attribute("pageType", "pseo_fault"))
                .andExpect(model().attributeExists("leadId"));
    }

    @Test
    void invalidWaitlistSubmissionShouldKeepCleanUrlAndStatusMessage() throws Exception {
        given(routingConfig.getWaitlistUrl()).willReturn("/lead-capture");

        MvcResult redirectResult = mockMvc.perform(post("/waitlist/submit")
                        .session(new MockHttpSession())
                        .param("email", "invalid-email")
                        .param("verdict", "TIME_BOMB")
                        .param("brand", "toyota")
                        .param("model", "camry")
                        .param("pageType", "pseo_fault")
                        .param("detail", "torque-converter")
                        .param("placement", "inline")
                        .param("intent", "SELL")
                        .param("leadId", "leadtest1234"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/lead-capture"))
                .andReturn();

        MockHttpSession session = (MockHttpSession) redirectResult.getRequest().getSession(false);

        mockMvc.perform(get("/lead-capture").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("pages/lead_capture"))
                .andExpect(model().attribute("status", "invalid_email"))
                .andExpect(model().attribute("brandValue", "toyota"))
                .andExpect(model().attribute("modelValue", "camry"))
                .andExpect(model().attribute("detail", "torque-converter"))
                .andExpect(model().attribute("leadId", "leadtest1234"));
    }

    @Test
    void partnerApprovedActionShouldRejectMissingOrInvalidToken() throws Exception {
        given(routingConfig.getCallbackToken()).willReturn("super-secret");

        mockMvc.perform(post("/partner/approved-action")
                        .param("leadId", "leadtest1234")
                        .param("approvedAction", "sold_to_partner"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/partner/approved-action")
                        .header("X-Partner-Token", "wrong-token")
                        .param("leadId", "leadtest1234")
                        .param("approvedAction", "sold_to_partner"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void partnerApprovedActionShouldAcceptValidTokenAndCleanPayload() throws Exception {
        given(routingConfig.getCallbackToken()).willReturn("super-secret");

        mockMvc.perform(post("/partner/approved-action")
                        .header("X-Partner-Token", "super-secret")
                        .param("leadId", "leadtest1234")
                        .param("approvedAction", "Sold To Partner")
                        .param("partner", "Peddle")
                        .param("revenueUsd", "125.50")
                        .param("currency", "usd")
                        .param("status", "Paid")
                        .param("note", "manual close"))
                .andExpect(status().isNoContent())
                .andExpect(header().string("X-Robots-Tag", containsString("noindex")));
    }
}
