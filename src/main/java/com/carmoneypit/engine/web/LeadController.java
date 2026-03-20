package com.carmoneypit.engine.web;

import com.carmoneypit.engine.config.PartnerRoutingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

@Controller
public class LeadController {

    private static final Logger csvLogger = LoggerFactory.getLogger("CSV_LEAD_LOGGER");
    private static final String WAITLIST_CONTEXT_SESSION_KEY = "leadCaptureContext";
    private static final String WAITLIST_STATUS_SESSION_KEY = "leadCaptureStatus";
    private final PartnerRoutingConfig routingConfig;

    @Value("${app.baseUrl:https://automoneypit.com}")
    private String baseUrl;

    public LeadController(PartnerRoutingConfig routingConfig) {
        this.routingConfig = routingConfig;
    }

    @GetMapping("/lead")
    public RedirectView trackLead(
            @RequestParam(required = false, defaultValue = "cta_click") String event_type,
            @RequestParam(required = false, defaultValue = "unknown") String page_type,
            @RequestParam(required = false, defaultValue = "unknown") String intent,
            @RequestParam(required = false, defaultValue = "unknown") String verdict_state,
            @RequestParam(required = false, defaultValue = "") String brand,
            @RequestParam(required = false, defaultValue = "") String model,
            @RequestParam(required = false, defaultValue = "") String detail,
            @RequestParam(required = false, defaultValue = "") String placement,
            HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session) {

        applyNoindexHeaders(response);

        // 1. Extract referrer securely for attribution.
        String referrer = request.getHeader("referer");
        String referrerPath = "";
        if (referrer != null && !referrer.isEmpty()) {
            try {
                URI uri = new URI(referrer);
                referrerPath = uri.getPath();
            } catch (URISyntaxException e) {
                referrerPath = "invalid_uri";
            }
        }

        // 2. Sanitize incoming tracking context.
        String safeEventType = sanitize(event_type);
        String safePageType = sanitize(page_type);
        String safeIntent = normalizeIntent(intent);
        String safeVerdictState = sanitize(verdict_state);
        String safeBrand = sanitize(brand);
        String safeModel = sanitize(model);
        String safeDetail = sanitize(detail);
        String safeReferrer = sanitize(referrerPath);
        String safePlacement = sanitize(placement);
        LeadCaptureContext context = new LeadCaptureContext(
                safeVerdictState,
                safeBrand,
                safeModel,
                safePageType,
                safeDetail,
                safePlacement,
                safeIntent,
                safeReferrer);

        boolean filteredTestTraffic = isSyntheticTraffic(safePageType, request);
        response.setHeader("X-Analytics-Filtered", filteredTestTraffic ? "1" : "0");

        if (!filteredTestTraffic) {
            csvLogger.info("{},{},{},{},{},{},{},{},{}",
                    safeEventType,
                    context.pageType(),
                    context.intent(),
                    context.verdict(),
                    context.brand(),
                    context.model(),
                    context.detail(),
                    context.referrerPath(),
                    context.placement());
        }

        // 3. If approval is pending, store attribution context in the session and
        // redirect to a clean waitlist URL without query params.
        if (routingConfig.isApprovalPending()) {
            storeWaitlistState(session, "", context);
            return buildRedirect(routingConfig.getWaitlistUrl());
        }

        // 4. Determine partner redirect URL based on normalized intent.
        String partnerUrl;
        if ("SELL".equalsIgnoreCase(context.intent())) {
            partnerUrl = routingConfig.getSellPartnerUrl();
        } else if ("WARRANTY".equalsIgnoreCase(context.intent())) {
            partnerUrl = routingConfig.getWarrantyPartnerUrl();
        } else if ("VALUE".equalsIgnoreCase(context.intent())) {
            partnerUrl = routingConfig.getMarketValuePartnerUrl();
        } else {
            partnerUrl = routingConfig.getRepairPartnerUrl();
        }

        // 5. Redirect (302 Found)
        RedirectView redirectView = new RedirectView(partnerUrl);
        redirectView.setStatusCode(HttpStatus.FOUND);
        return redirectView;
    }

    @PostMapping("/waitlist/submit")
    public RedirectView submitWaitlist(
            @RequestParam(name = "email") String email,
            @RequestParam(name = "verdict", required = false, defaultValue = "UNKNOWN") String verdict,
            @RequestParam(name = "brand", required = false, defaultValue = "") String brand,
            @RequestParam(name = "model", required = false, defaultValue = "") String model,
            @RequestParam(name = "pageType", required = false, defaultValue = "lead_capture") String pageType,
            @RequestParam(name = "detail", required = false, defaultValue = "") String detail,
            @RequestParam(name = "placement", required = false, defaultValue = "waitlist") String placement,
            @RequestParam(name = "intent", required = false, defaultValue = "WAITLIST") String intent,
            @RequestParam(name = "referrerPath", required = false, defaultValue = "") String referrerPath,
            HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session) {

        applyNoindexHeaders(response);

        String safeVerdict = sanitize(verdict);
        String safeBrand = sanitize(brand);
        String safeModel = sanitize(model);
        String safePageType = sanitize(pageType);
        String safeDetail = sanitize(detail);
        String safePlacement = sanitize(placement);
        String safeIntent = normalizeIntent(intent);
        String safeReferrerPath = sanitize(referrerPath);
        String safeEmail = sanitizeEmail(email);
        LeadCaptureContext context = new LeadCaptureContext(
                safeVerdict,
                safeBrand,
                safeModel,
                safePageType,
                safeDetail,
                safePlacement,
                safeIntent,
                safeReferrerPath);

        boolean filteredTestTraffic = isSyntheticTraffic(safePageType, request);
        response.setHeader("X-Analytics-Filtered", filteredTestTraffic ? "1" : "0");

        if (safeEmail == null) {
            storeWaitlistState(session, "invalid_email", context);
            return buildRedirect(routingConfig.getWaitlistUrl());
        }

        if (!filteredTestTraffic) {
            csvLogger.info("{},{},{},{},{},{},{},{},{}",
                    "lead_submit",
                    context.pageType(),
                    context.intent(),
                    context.verdict(),
                    context.brand(),
                    context.model(),
                    context.detail(),
                    context.referrerPath(),
                    context.placement());
        }

        storeWaitlistState(session, "success", context);
        return buildRedirect(routingConfig.getWaitlistUrl());
    }

    @GetMapping("/lead-capture")
    public Object showWaitlistForm(
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "verdict", required = false) String verdict,
            @RequestParam(name = "brand", required = false) String brand,
            @RequestParam(name = "model", required = false) String modelValue,
            @RequestParam(name = "pageType", required = false) String pageType,
            @RequestParam(name = "detail", required = false) String detail,
            @RequestParam(name = "placement", required = false) String placement,
            @RequestParam(name = "intent", required = false) String intent,
            @RequestParam(name = "referrerPath", required = false) String referrerPath,
            Model model,
            HttpServletResponse response,
            HttpSession session) {

        applyNoindexHeaders(response);
        response.setHeader("Link", "<" + baseUrl + "/lead-capture>; rel=\"canonical\"");

        if (hasLegacyWaitlistParams(status, verdict, brand, modelValue, pageType, detail, placement, intent,
                referrerPath)) {
            LeadCaptureContext context = new LeadCaptureContext(
                    sanitize(defaultIfBlank(verdict, "UNKNOWN")),
                    sanitize(defaultIfBlank(brand, "")),
                    sanitize(defaultIfBlank(modelValue, "")),
                    sanitize(defaultIfBlank(pageType, "lead_capture")),
                    sanitize(defaultIfBlank(detail, "")),
                    sanitize(defaultIfBlank(placement, "waitlist")),
                    normalizeIntent(defaultIfBlank(intent, "WAITLIST")),
                    sanitize(defaultIfBlank(referrerPath, "")));
            storeWaitlistState(session, sanitizeStatus(status), context);
            return buildRedirect(routingConfig.getWaitlistUrl());
        }

        LeadCaptureContext context = readWaitlistContext(session);
        String safeStatus = sanitizeStatus(readWaitlistStatus(session));

        String displayBrand = "your vehicle";
        if (context.brand() != null && !context.brand().isEmpty()) {
            displayBrand = context.brand().replace("+", " ");
            if (displayBrand.length() > 0) {
                String[] words = displayBrand.split(" ");
                StringBuilder sb = new StringBuilder();
                for (String w : words) {
                    if (w.length() > 0) {
                        sb.append(w.substring(0, 1).toUpperCase()).append(w.substring(1).toLowerCase()).append(" ");
                    }
                }
                displayBrand = sb.toString().trim();
            }
        }

        model.addAttribute("status", safeStatus);
        model.addAttribute("verdict", context.verdict());
        model.addAttribute("brand", displayBrand);
        model.addAttribute("brandValue", context.brand());
        model.addAttribute("modelValue", context.model());
        model.addAttribute("pageType", context.pageType());
        model.addAttribute("detail", context.detail());
        model.addAttribute("placement", context.placement());
        model.addAttribute("intent", context.intent());
        model.addAttribute("referrerPath", context.referrerPath());
        model.addAttribute("canonicalUrl", baseUrl + "/lead-capture");

        return "pages/lead_capture";
    }

    private RedirectView buildRedirect(String location) {
        RedirectView redirectView = new RedirectView(location);
        redirectView.setStatusCode(HttpStatus.FOUND);
        return redirectView;
    }

    private String sanitizeEmail(String input) {
        if (input == null) {
            return null;
        }

        String clean = input.trim().toLowerCase(Locale.ROOT);
        if (clean.length() > 254) {
            return null;
        }

        if (!clean.matches("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$")) {
            return null;
        }

        return clean;
    }

    private String sanitize(String input) {
        if (input == null)
            return "null";
        // Remove null bytes and standard whitespace replacements
        String clean = input.replace("\u0000", "").replace(",", " ").replace("\n", " ").replace("\r", " ").trim();

        // CSV Injection defense
        if (!clean.isEmpty()) {
            char firstChar = clean.charAt(0);
            if (firstChar == '=' || firstChar == '+' || firstChar == '-' || firstChar == '@' || firstChar == '\t') {
                clean = "'" + clean;
            }
        }

        // Length limit
        if (clean.length() > 200) {
            clean = clean.substring(0, 200);
        }
        return clean;
    }

    private void applyNoindexHeaders(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        response.setHeader("X-Robots-Tag", "noindex, nofollow, noarchive");
    }

    private void storeWaitlistState(HttpSession session, String status, LeadCaptureContext context) {
        session.setAttribute(WAITLIST_STATUS_SESSION_KEY, sanitizeStatus(status));
        session.setAttribute(WAITLIST_CONTEXT_SESSION_KEY, context);
    }

    private String readWaitlistStatus(HttpSession session) {
        Object raw = session.getAttribute(WAITLIST_STATUS_SESSION_KEY);
        session.removeAttribute(WAITLIST_STATUS_SESSION_KEY);
        return raw instanceof String value ? value : "";
    }

    private LeadCaptureContext readWaitlistContext(HttpSession session) {
        Object raw = session.getAttribute(WAITLIST_CONTEXT_SESSION_KEY);
        session.removeAttribute(WAITLIST_CONTEXT_SESSION_KEY);
        if (raw instanceof LeadCaptureContext context) {
            return context;
        }
        return new LeadCaptureContext("UNKNOWN", "", "", "lead_capture", "", "waitlist", "WAITLIST", "");
    }

    private boolean hasLegacyWaitlistParams(String status, String verdict, String brand, String model, String pageType,
            String detail, String placement, String intent, String referrerPath) {
        return status != null
                || verdict != null
                || brand != null
                || model != null
                || pageType != null
                || detail != null
                || placement != null
                || intent != null
                || referrerPath != null;
    }

    private String sanitizeStatus(String status) {
        if ("success".equals(status) || "invalid_email".equals(status)) {
            return status;
        }
        return "";
    }

    private String normalizeIntent(String intent) {
        String clean = sanitize(defaultIfBlank(intent, "WAITLIST"));
        if ("FIX".equalsIgnoreCase(clean) || "REPAIR".equalsIgnoreCase(clean)) {
            return "REPAIR";
        }
        if ("SELL".equalsIgnoreCase(clean)) {
            return "SELL";
        }
        if ("VALUE".equalsIgnoreCase(clean)) {
            return "VALUE";
        }
        if ("WARRANTY".equalsIgnoreCase(clean)) {
            return "WARRANTY";
        }
        if ("WAITLIST".equalsIgnoreCase(clean)) {
            return "WAITLIST";
        }
        return clean.toUpperCase(Locale.ROOT);
    }

    private String defaultIfBlank(String input, String fallback) {
        return input == null || input.isBlank() ? fallback : input;
    }

    private boolean isSyntheticTraffic(String sourceOrPageType, HttpServletRequest request) {
        String source = sourceOrPageType == null ? "" : sourceOrPageType.toLowerCase(Locale.ROOT);
        if ("playwright".equals(source)
                || "smoke".equals(source)
                || "e2e".equals(source)
                || "test".equals(source)
                || source.startsWith("test_")
                || source.startsWith("qa_")) {
            return true;
        }

        String marker = request.getHeader("X-Test-Traffic");
        if ("1".equals(marker) || "true".equalsIgnoreCase(marker)) {
            return true;
        }

        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            return false;
        }

        String ua = userAgent.toLowerCase(Locale.ROOT);
        return ua.contains("playwright")
                || ua.contains("headlesschrome")
                || ua.contains("puppeteer")
                || ua.contains("lighthouse");
    }

    private record LeadCaptureContext(
            String verdict,
            String brand,
            String model,
            String pageType,
            String detail,
            String placement,
            String intent,
            String referrerPath) {
    }
}
