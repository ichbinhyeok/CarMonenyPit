package com.carmoneypit.engine.web;

import com.carmoneypit.engine.config.PartnerRoutingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Controller
public class LeadController {

    private static final Logger csvLogger = LoggerFactory.getLogger("CSV_LEAD_LOGGER");
    private final PartnerRoutingConfig routingConfig;

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
            HttpServletResponse response) {

        // 1. Set NO-STORE for prevent caching redirects
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        // 2. Extract Referrer securely
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

        // 3. Log securely to CSV format
        String safeEventType = sanitize(event_type);
        String safePageType = sanitize(page_type);
        String safeIntent = sanitize(intent);
        String safeVerdictState = sanitize(verdict_state);
        String safeBrand = sanitize(brand);
        String safeModel = sanitize(model);
        String safeDetail = sanitize(detail);
        String safeReferrer = sanitize(referrerPath);
        String safePlacement = sanitize(placement);

        csvLogger.info("{},{},{},{},{},{},{},{},{}",
                safeEventType, safePageType, safeIntent, safeVerdictState, safeBrand, safeModel, safeDetail,
                safeReferrer,
                safePlacement);

        // 4. If approval is pending, redirect to waitlist instead of external partner
        // Target host is guaranteed to be from config, not user input.
        if (routingConfig.isApprovalPending()) {
            String encodedVerdict = URLEncoder.encode(safeVerdictState, StandardCharsets.UTF_8);
            String encodedBrand = URLEncoder.encode(safeBrand, StandardCharsets.UTF_8);
            RedirectView waitlistView = new RedirectView(
                    routingConfig.getWaitlistUrl() + "?verdict=" + encodedVerdict + "&brand=" + encodedBrand);
            waitlistView.setStatusCode(HttpStatus.FOUND);
            return waitlistView;
        }

        // 5. Determine partner redirect URL based on intent
        String partnerUrl;
        if ("SELL".equalsIgnoreCase(safeIntent)) {
            partnerUrl = routingConfig.getSellPartnerUrl();
        } else if ("WARRANTY".equalsIgnoreCase(safeIntent)) {
            partnerUrl = routingConfig.getWarrantyPartnerUrl();
        } else if ("VALUE".equalsIgnoreCase(safeIntent)) {
            partnerUrl = routingConfig.getMarketValuePartnerUrl();
        } else {
            partnerUrl = routingConfig.getRepairPartnerUrl();
        }

        // 6. Redirect (302 Found)
        RedirectView redirectView = new RedirectView(partnerUrl);
        redirectView.setStatusCode(HttpStatus.FOUND);
        return redirectView;
    }

    @PostMapping("/waitlist/submit")
    public RedirectView submitWaitlist(
            @RequestParam(name = "email") String email,
            @RequestParam(name = "verdict", required = false, defaultValue = "UNKNOWN") String verdict,
            @RequestParam(name = "brand", required = false, defaultValue = "") String brand,
            @RequestParam(name = "source", required = false, defaultValue = "lead_capture") String source,
            HttpServletResponse response) {

        // Prevent form result caching in browser history/proxy.
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        String safeVerdict = sanitize(verdict);
        String safeBrand = sanitize(brand);
        String safeSource = sanitize(source);
        String safeEmail = sanitizeEmail(email);

        if (safeEmail == null) {
            RedirectView invalidView = new RedirectView(buildWaitlistRedirectUrl("invalid_email", safeVerdict, safeBrand));
            invalidView.setStatusCode(HttpStatus.FOUND);
            return invalidView;
        }

        csvLogger.info("{},{},{},{},{},{},{},{},{}",
                "submit_lead", safeSource, "waitlist", safeVerdict, safeBrand, "", maskEmail(safeEmail),
                "/lead-capture", "form");

        RedirectView successView = new RedirectView(buildWaitlistRedirectUrl("success", safeVerdict, safeBrand));
        successView.setStatusCode(HttpStatus.FOUND);
        return successView;
    }

    @GetMapping("/lead-capture")
    public String showWaitlistForm(
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "verdict", required = false) String verdict,
            @RequestParam(name = "brand", required = false) String brand,
            Model model) {

        model.addAttribute("status", status != null ? status : "");
        model.addAttribute("verdict", verdict != null ? verdict : "UNKNOWN");
        model.addAttribute("brand", brand != null ? brand.replace("+", " ") : "your vehicle");

        return "pages/lead_capture";
    }

    private String buildWaitlistRedirectUrl(String status, String verdict, String brand) {
        return "/lead-capture?status=" + URLEncoder.encode(status, StandardCharsets.UTF_8)
                + "&verdict=" + URLEncoder.encode(verdict, StandardCharsets.UTF_8)
                + "&brand=" + URLEncoder.encode(brand, StandardCharsets.UTF_8);
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

    private String maskEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex <= 0 || atIndex == email.length() - 1) {
            return "hidden";
        }

        String localPart = email.substring(0, atIndex);
        String domainPart = email.substring(atIndex + 1);
        String maskedLocal = localPart.length() <= 2
                ? localPart.charAt(0) + "*"
                : localPart.substring(0, 2) + "***";
        return maskedLocal + "@" + domainPart;
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
}
