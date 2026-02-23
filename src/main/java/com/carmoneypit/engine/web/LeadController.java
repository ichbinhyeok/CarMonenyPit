package com.carmoneypit.engine.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
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

        // 3. Log securely to CSV format: timestamp is added by Logback -> event_type,page_type,verdict_type,brand,model,detail,referrer_path,placement
        // Sanitize inputs to prevent CSV injection / commas breaking the parser
        String safeEventType = sanitize(event_type);
        String safePageType = sanitize(page_type);
        String safeVerdict = sanitize(verdict_type);
        String safeBrand = sanitize(brand);
        String safeModel = sanitize(model);
        String safeDetail = sanitize(detail);
        String safeReferrer = sanitize(referrerPath);
        String safePlacement = sanitize(placement);

        csvLogger.info("{},{},{},{},{},{},{},{}", 
            safeEventType, safePageType, safeVerdict, safeBrand, safeModel, safeDetail, safeReferrer, safePlacement);

        // 4. Determine partner redirect URL based on intent (SELL vs FIX)
        String partnerUrl;
        if ("SELL".equalsIgnoreCase(verdict_type)) {
            // Future integration: Peddle, Copart, CarMax, etc.
            partnerUrl = "https://example-partner-sell.com/offer?make=" + safeBrand + "&model=" + safeModel;
        } else {
            // Future integration: RepairPal, YourMechanic, Autozone, etc.
            partnerUrl = "https://example-partner-repair.com/quote?make=" + safeBrand + "&model=" + safeModel + "&issue=" + safeDetail;
        }

        // 5. Redirect (302 Found or 307 Temporary Redirect)
        RedirectView redirectView = new RedirectView(partnerUrl);
        redirectView.setStatusCode(HttpStatus.FOUND); // 302
        return redirectView;
    }

    private String sanitize(String input) {
        if (input == null) return "null";
        // Remove commas and newlines to prevent CSV structural breaks
        return input.replace(",", " ").replace("\n", " ").replace("\r", " ").trim();
    }
}
