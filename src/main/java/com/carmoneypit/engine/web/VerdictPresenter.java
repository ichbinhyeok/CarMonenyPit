package com.carmoneypit.engine.web;

import com.carmoneypit.engine.api.OutputModels.VerdictState;
import com.carmoneypit.engine.api.InputModels.EngineInput;
import com.carmoneypit.engine.api.InputModels.SimulationControls;
import com.carmoneypit.engine.core.ValuationService;
import com.carmoneypit.engine.data.CarBrandData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
public class VerdictPresenter {

    private final ObjectMapper objectMapper;
    private final ValuationService valuationService;

    public VerdictPresenter(ObjectMapper objectMapper, ValuationService valuationService) {
        this.objectMapper = objectMapper;
        this.valuationService = valuationService;
    }

    /**
     * Centralized UI Model preparation for the verdict report/result page.
     */
    public void populateModel(Model model, EngineInput input,
            com.carmoneypit.engine.api.OutputModels.VerdictResult result,
            SimulationControls controls, String viewMode, String token) {
        model.addAttribute("input", input);
        model.addAttribute("result", result);
        model.addAttribute("verdictTitle", getVerdictTitle(result.verdictState()));
        model.addAttribute("verdictExplanation", getLawyerExplanation(result.verdictState(), input));
        model.addAttribute("verdictAction", getActionPlan(result.verdictState()));
        model.addAttribute("verdictCss", getCssClass(result.verdictState()));
        model.addAttribute("leadLabel", getLeadLabel(result.verdictState(), input, controls));
        model.addAttribute("leadDescription", getLeadDescription(result.verdictState(), input, controls));
        model.addAttribute("leadUrl", getLeadUrl(result.verdictState(), input, controls));

        model.addAttribute("isValueEstimated", input.isValueEstimated());
        model.addAttribute("isQuoteEstimated", input.isQuoteEstimated());

        model.addAttribute("viewMode", viewMode);
        model.addAttribute("shareToken", token);
        model.addAttribute("controls", controls);

        if ("RECEIPT".equals(viewMode)) {
            model.addAttribute("ogTitle", getViralOgTitle(result.verdictState()));
        }
    }

    public String encodeToken(EngineInput input) {
        try {
            String json = objectMapper.writeValueAsString(input);
            return compress(json);
        } catch (Exception e) {
            throw new RuntimeException("Token encoding failed", e);
        }
    }

    public EngineInput decodeToken(String token) {
        try {
            String json = decompress(token);
            return objectMapper.readValue(json, EngineInput.class);
        } catch (Exception e) {
            throw new RuntimeException("Token decoding failed", e);
        }
    }

    private String compress(String str) throws java.io.IOException {
        if (str == null || str.length() == 0) {
            return str;
        }
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        java.util.zip.GZIPOutputStream gzip = new java.util.zip.GZIPOutputStream(out);
        gzip.write(str.getBytes(StandardCharsets.UTF_8));
        gzip.close();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(out.toByteArray());
    }

    private String decompress(String str) throws java.io.IOException {
        if (str == null || str.length() == 0) {
            return str;
        }
        byte[] bytes = Base64.getUrlDecoder().decode(str);

        try (java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(bytes);
                java.util.zip.GZIPInputStream gzip = new java.util.zip.GZIPInputStream(in);
                java.io.InputStreamReader reader = new java.io.InputStreamReader(gzip, StandardCharsets.UTF_8);
                java.io.BufferedReader br = new java.io.BufferedReader(reader)) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (java.util.zip.ZipException e) {
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }

    public String getViralOgTitle(VerdictState state) {
        return switch (state) {
            case TIME_BOMB -> "Statistical analysis signals higher financial utility in liquidation than repair.";
            case STABLE -> "Maintenance profile suggests asset stability within normal operating parameters.";
            case BORDERLINE ->
                "Market data indicates an inflection point. Retention viability is sensitive to service pricing.";
            default -> "Estimated AutoMoneyPit Diagnostic Result";
        };
    }

    public String getVerdictTitle(VerdictState state) {
        return switch (state) {
            case TIME_BOMB -> "Sell It.";
            case STABLE -> "Fix It.";
            case BORDERLINE -> "It's Close.";
            default -> "Needs Review";
        };
    }

    private static final String NARRATIVE_TIME_BOMB = "Based on the numbers, this repair doesn't make financial sense.%s At this point, your car is likely to cost you more in repairs than it's worth. The smart move is to get a few offers and see what you can get for it.";
    private static final String NARRATIVE_STABLE = "Good news: fixing your %s looks like the right call. The repair cost is reasonable compared to your car's value, and you should get plenty more miles out of it. Go ahead and get it fixed.";
    private static final String NARRATIVE_BORDERLINE = "Honestly, this one's a coin flip.%s The numbers are close enough that it could go either way. My advice: get a second quote on the repair. If you can knock a few hundred off, it tips toward fixing. If not, selling might be smarter.";

    public String getLawyerExplanation(VerdictState state, EngineInput input) {
        Optional<CarBrandData> brandData = (input != null) ? valuationService.getBrandData(input.brand())
                : Optional.empty();
        String brandName = (input != null && input.brand() != null) ? input.brand().name() : "Vehicle";

        switch (state) {
            case TIME_BOMB:
                String brandRisk = "";
                if (brandData.isPresent() && !brandData.get().majorIssues.isEmpty()) {
                    brandRisk = String.format(
                            " Statistical trends for %s detected: %s issues frequently peak at this mileage.",
                            brandName, brandData.get().majorIssues.get(0).part);
                }
                return String.format(NARRATIVE_TIME_BOMB, brandRisk);

            case STABLE:
                return String.format(NARRATIVE_STABLE, brandName);

            case BORDERLINE:
            default:
                String issueWarning = "";
                if (brandData.isPresent() && !brandData.get().majorIssues.isEmpty()) {
                    issueWarning = String.format(" Potential %s lifecycle bottleneck: %s. ", brandName,
                            brandData.get().majorIssues.get(0).part);
                }
                return String.format(NARRATIVE_BORDERLINE, issueWarning);
        }
    }

    public String getActionPlan(VerdictState state) {
        return switch (state) {
            case TIME_BOMB -> "Get offers from Peddle, CarMax, or local dealers this week.";
            case STABLE -> "Approve the repair. Your car should serve you well for years to come.";
            case BORDERLINE -> "Get a second repair quote. If it's lower, fix it. If not, consider selling.";
            default ->
                "Try negotiating a lower repair price. If they won't budge, selling might be better.";
        };
    }

    public String getCssClass(VerdictState state) {
        return switch (state) {
            case TIME_BOMB -> "verdict-liquidate";
            case STABLE -> "verdict-sustain";
            case BORDERLINE -> "verdict-risk_alert";
            default -> "verdict-unknown";
        };
    }

    public String getLeadLabel(VerdictState state, EngineInput input, SimulationControls controls) {
        boolean isAccident = input.isQuoteEstimated() && (controls != null
                && controls.mobilityStatus() == com.carmoneypit.engine.api.InputModels.MobilityStatus.NEEDS_TOW);
        boolean hasQuote = !input.isQuoteEstimated();
        boolean isHighMileage = input.mileage() > 80000;

        switch (state) {
            case TIME_BOMB:
                return "Get Instant Cash Offer";
            case STABLE:
                if (isHighMileage)
                    return "Protect with Extended Warranty";
                if (hasQuote)
                    return "Verify Fair Repair Price";
                if (isAccident)
                    return "Find Certified Shop";
                return "Check RepairPal Estimate";
            case BORDERLINE:
            default:
                if (hasQuote)
                    return "Get 2nd Opinion";
                return "Check Market Value";
        }
    }

    public String getLeadDescription(VerdictState state, EngineInput input, SimulationControls controls) {
        boolean isAccident = input.isQuoteEstimated() && (controls != null
                && controls.mobilityStatus() == com.carmoneypit.engine.api.InputModels.MobilityStatus.NEEDS_TOW);
        boolean hasQuote = !input.isQuoteEstimated();
        boolean isHighMileage = input.mileage() > 80000;

        switch (state) {
            case TIME_BOMB:
                return "Stop the bleeding. See exactly what your vehicle is worth in its current condition (even if broken).";
            case STABLE:
                if (isHighMileage)
                    return "Your car is worth keeping, but high-mileage repairs can add up. Protect yourself from unexpected breakdowns.";
                if (hasQuote)
                    return "Ensure you aren't being overcharged. Compare your quote against the national average for this specific repair.";
                if (isAccident)
                    return "Don't guess on damage. Connect with a certified facility to get an accurate assessment.";
                return "Your car is worth keeping. Find a trusted mechanic to perform this repair at a fair price.";
            case BORDERLINE:
            default:
                if (hasQuote)
                    return "This repair is risky. Get a second opinion to confirm the diagnosis before committing.";
                return "The decision is close. Check the current private party value to see if repairing makes sense.";
        }
    }

    public String getLeadUrl(VerdictState state, EngineInput input, SimulationControls controls) {
        boolean isHighMileage = input.mileage() > 80000;

        switch (state) {
            case TIME_BOMB:
                return "https://www.peddle.com/instant-offer?utm_source=automoneypit&utm_medium=referral&utm_campaign=verdict_tool";
            case STABLE:
                // TODO: Replace with real affiliate link after approval
                if (isHighMileage)
                    return "https://www.endurancewarranty.com/get-quote/?ref=automoneypit";
                return "https://repairpal.com/estimator?utm_source=automoneypit";
            case BORDERLINE:
            default:
                return "https://www.kbb.com/?utm_source=automoneypit";
        }
    }
}
