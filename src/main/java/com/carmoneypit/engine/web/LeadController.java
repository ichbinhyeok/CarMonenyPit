package com.carmoneypit.engine.web;

import com.carmoneypit.engine.config.PartnerRoutingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

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
            @RequestParam(required = false, defaultValue = "unknown") String verdict_type,
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
        String safeVerdict = sanitize(verdict_type);
        String safeBrand = sanitize(brand);
        String safeModel = sanitize(model);
        String safeDetail = sanitize(detail);
        String safeReferrer = sanitize(referrerPath);
        String safePlacement = sanitize(placement);

        csvLogger.info("{},{},{},{},{},{},{},{}",
                safeEventType, safePageType, safeVerdict, safeBrand, safeModel, safeDetail, safeReferrer,
                safePlacement);

        // 4. If approval is pending, redirect to waitlist instead of external partner
        if (routingConfig.isApprovalPending()) {
            RedirectView waitlistView = new RedirectView(
                    routingConfig.getWaitlistUrl() + "?verdict=" + safeVerdict + "&brand=" + safeBrand);
            waitlistView.setStatusCode(HttpStatus.FOUND);
            return waitlistView;
        }

        // 5. Determine partner redirect URL based on intent (SELL vs FIX)
        String partnerUrl;
        if ("SELL".equalsIgnoreCase(verdict_type)) {
            partnerUrl = routingConfig.getSellPartnerUrl();
        } else {
            partnerUrl = routingConfig.getRepairPartnerUrl();
        }

        // 6. Redirect (302 Found)
        RedirectView redirectView = new RedirectView(partnerUrl);
        redirectView.setStatusCode(HttpStatus.FOUND);
        return redirectView;
    }

    @GetMapping("/lead-capture")
    public String showWaitlistForm(
            @RequestParam(name = "verdict", required = false) String verdict,
            @RequestParam(name = "brand", required = false) String brand,
            Model model) {

        model.addAttribute("verdict", verdict != null ? verdict : "UNKNOWN");
        model.addAttribute("brand", brand != null ? brand.replace("+", " ") : "your vehicle");

        return "pages/lead_capture";
    }

    private String sanitize(String input) {
        if (input == null)
            return "null";
        return input.replace(",", " ").replace("\n", " ").replace("\r", " ").trim();
    }
}
