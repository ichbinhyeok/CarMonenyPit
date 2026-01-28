package com.carmoneypit.engine.web;

import com.carmoneypit.engine.api.OutputModels.VerdictState;
import com.carmoneypit.engine.api.InputModels.EngineInput;
import com.carmoneypit.engine.api.InputModels.SimulationControls;
import com.carmoneypit.engine.core.ValuationService;
import com.carmoneypit.engine.data.CarBrandData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
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
        // Fallback for legacy tokens (non-GZIP, plain Base64)
        // Check if it's likely GZIP (GZIP header is 0x1f8b, but Base64 makes it hard to
        // peek directly without decoding)
        // So we try strict GZIP decode, if fail, try legacy plain Base64 decode.
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
            // Not in GZIP format, assume legacy plain Base64 JSON
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }

    public String getViralOgTitle(VerdictState state) {
        switch (state) {
            case TIME_BOMB:
                return "My Car is a TIME BOMB | AutoMoneyPit Diagnostic";
            case STABLE:
                return "My Car is STABLE | AutoMoneyPit Diagnostic";
            case BORDERLINE:
                return "My Car is a MONEY PIT | AutoMoneyPit Diagnostic";
            default:
                return "AutoMoneyPit Diagnostic Result";
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

    public String getLawyerExplanation(VerdictState state, EngineInput input) {
        Optional<CarBrandData> brandData = (input != null) ? valuationService.getBrandData(input.brand())
                : Optional.empty();
        String brandName = (input != null && input.brand() != null) ? input.brand().name() : "Vehicle";

        switch (state) {
            case TIME_BOMB:
                String brandRisk = "";
                if (brandData.isPresent() && !brandData.get().majorIssues.isEmpty()) {
                    brandRisk = String.format(
                            " Statistical failure node for %s detected: %s issues peak at this mileage.",
                            brandName, brandData.get().majorIssues.get(0).part);
                }
                return String.format(
                        "DECISION_AUDIT: Our market data model suggests your asset is entering a rapid depreciation phase. While emotionally difficult, the statistical indicators are unfavorable.%s Projected maintenance overhead may exceed asset core equity. Strategic liquidation is strongly recommended by our algorithm.",
                        brandRisk);

            case STABLE:
                return String.format(
                        "VALIDATED: Maintenance appears to be a sound investment. The %s platform remains statistically efficient for your current usage window based on our data. Authorizing repair is likely to preserve capital better than entering the high-friction replacement market.",
                        brandName);

            case BORDERLINE:
            default:
                String issueWarning = "";
                if (brandData.isPresent() && !brandData.get().majorIssues.isEmpty()) {
                    issueWarning = String.format(" Known %s vulnerability: %s. ", brandName,
                            brandData.get().majorIssues.get(0).part);
                }
                return String.format(
                        "CAUTION: Correlation shift detected.%s Your asset is entering a high-variance risk window. Proceeding with repair may be speculative. We suggest securing a 15%% discount on services or considering an exit.",
                        issueWarning);
        }
    }

    // Overload for backward compatibility if needed, though we should update
    // callers
    public String getLawyerExplanation(VerdictState state) {
        return getLawyerExplanation(state, null);
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

    public String getLeadLabel(VerdictState state, EngineInput input, SimulationControls controls) {
        boolean isWorrier = input.isQuoteEstimated() && (controls == null
                || controls.mobilityStatus() == com.carmoneypit.engine.api.InputModels.MobilityStatus.DRIVABLE);
        boolean isAccident = input.isQuoteEstimated() && (controls != null
                && controls.mobilityStatus() == com.carmoneypit.engine.api.InputModels.MobilityStatus.NEEDS_TOW);
        boolean hasQuote = !input.isQuoteEstimated();

        switch (state) {
            case TIME_BOMB:
                return "[COMING SOON] Exit Plan";
            case STABLE:
                if (hasQuote)
                    return "[COMING SOON] Quote Verify";
                if (isAccident)
                    return "[COMING SOON] Appraisal";
                return "[COMING SOON] Protection";
            case BORDERLINE:
            default:
                if (hasQuote)
                    return "[COMING SOON] Audit Prep";
                return "[COMING SOON] Explore";
        }
    }

    public String getLeadDescription(VerdictState state, EngineInput input, SimulationControls controls) {
        boolean isWorrier = input.isQuoteEstimated() && (controls == null
                || controls.mobilityStatus() == com.carmoneypit.engine.api.InputModels.MobilityStatus.DRIVABLE);
        boolean isAccident = input.isQuoteEstimated() && (controls != null
                && controls.mobilityStatus() == com.carmoneypit.engine.api.InputModels.MobilityStatus.NEEDS_TOW);
        boolean hasQuote = !input.isQuoteEstimated();

        switch (state) {
            case TIME_BOMB:
                return "Your asset is hemorrhaging value. Convert it to cash within 48 hours before the next total failure.";
            case STABLE:
                if (hasQuote)
                    return "Verify your current estimate against market fair-price indices to ensure you aren't overpaying.";
                if (isAccident)
                    return "Asset remains viable. Connect with a mobile appraiser to get a professional damage summary.";
                return "Asset efficiency verified. Find a specialist to execute a multi-year reliability hold.";
            case BORDERLINE:
            default:
                if (hasQuote)
                    return "Compare immediate sell-out offers versus tiered financing plans for this specific repair.";
                return "Asset is entering a high-variance window. Check local liquidation bids versus certified refurbishers.";
        }
    }

    public String getLeadUrl(VerdictState state, EngineInput input, SimulationControls controls) {
        // Partnership links currently under negotiation (3-day hold)
        return "javascript:void(0)";
    }
}
