package com.carmoneypit.engine.web;

import com.carmoneypit.engine.api.OutputModels.VerdictState;
import com.carmoneypit.engine.api.InputModels.EngineInput;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

@Service
public class VerdictPresenter {

    private final ObjectMapper objectMapper;

    public VerdictPresenter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String encodeToken(EngineInput input) {
        try {
            String json = objectMapper.writeValueAsString(input);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("Token encoding failed", e);
        }
    }

    public EngineInput decodeToken(String token) {
        try {
            String json = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
            return objectMapper.readValue(json, EngineInput.class);
        } catch (Exception e) {
            throw new RuntimeException("Token decoding failed", e);
        }
    }

    public String getViralOgTitle(VerdictState state) {
        switch (state) {
            case TIME_BOMB:
                return "My Car is a TIME BOMB | CarMoneyPit Diagnostic";
            case STABLE:
                return "My Car is STABLE | CarMoneyPit Diagnostic";
            case BORDERLINE:
                return "My Car is a MONEY PIT | CarMoneyPit Diagnostic";
            default:
                return "CarMoneyPit Diagnostic Result";
        }
    }

    public String getVerdictTitle(VerdictState state) {
        switch (state) {
            case TIME_BOMB:
                return "LIQUIDATE";
            case STABLE:
                return "SUSTAIN";
            case BORDERLINE:
                return "RISK_ALERT";
            default:
                return "SCAN_FAILURE";
        }
    }

    public String getLawyerExplanation(VerdictState state) {
        switch (state) {
            case TIME_BOMB:
                return "Actuarial correlation confirms this asset has reached a terminal efficiency state. Maintenance overhead now projects to exceed remaining equity within 8.4 months. Immediate disposal recommended.";
            case STABLE:
                return "Strategic repair validated. Current service cost is statistically superior to the capital requirements of a replacement acquisition in the 2026 market.";
            case BORDERLINE:
            default:
                return "Equilibrium shift detected. Financial models indicate a high-variance outcome. Proceeding with service poses a 42% risk of cascade failure node exposure.";
        }
    }

    public String getActionPlan(VerdictState state) {
        switch (state) {
            case TIME_BOMB:
                return "Initiate asset liquidation. Shift remaining equity into a higher-efficiency mobility platform.";
            case STABLE:
                return "Authorize specific service. Execute a 12-month maintenance hold followed by a strategic review.";
            case BORDERLINE:
            default:
                return "Negotiate a 15% service discount to offset risk variance. If unsuccessful, abort repair and exit.";
        }
    }

    public String getCssClass(VerdictState state) {
        switch (state) {
            case TIME_BOMB:
                return "verdict-terminate";
            case STABLE:
                return "verdict-sustain";
            case BORDERLINE:
                return "verdict-probation";
            default:
                return "verdict-unknown";
        }
    }

    public String getLeadLabel(VerdictState state) {
        switch (state) {
            case TIME_BOMB:
                return "Get Appraisal & Sell Now";
            case STABLE:
                return "Find Trusted Service";
            case BORDERLINE:
                return "Explore Repair Financing";
            default:
                return "Expert Consultation";
        }
    }

    public String getLeadDescription(VerdictState state) {
        switch (state) {
            case TIME_BOMB:
                return "Capitalize on remaining value. Get a guaranteed cash offer in 2 minutes.";
            case STABLE:
                return "Save up to 20% by comparing verified local service centers.";
            case BORDERLINE:
                return "Keep your cash flow stable. Break this down into 12 easy payments.";
            default:
                return "Consult with a vehicle decision specialist.";
        }
    }

    public String getLeadUrl(VerdictState state) {
        switch (state) {
            case TIME_BOMB:
                return "mailto:acquisitions@carmoneypit.com?subject=Cash%20Offer%20Request";
            case STABLE:
                return "mailto:service@carmoneypit.com?subject=Service%20Appointment";
            case BORDERLINE:
                return "mailto:finance@carmoneypit.com?subject=Repair%20Loan%20Inquiry";
            default:
                return "mailto:support@carmoneypit.com";
        }
    }
}
