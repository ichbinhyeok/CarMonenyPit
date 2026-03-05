package com.carmoneypit.engine.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RootController {

    @Value("${app.baseUrl:https://automoneypit.com}")
    private String baseUrl;

    @GetMapping("/about")
    public String about() {
        return "pages/about";
    }

    @GetMapping("/methodology")
    public String methodology() {
        return "pages/methodology";
    }

    @GetMapping("/privacy")
    public String privacy() {
        return "pages/privacy";
    }

    @GetMapping("/terms")
    public String terms() {
        return "pages/terms";
    }

    @GetMapping("/contact")
    public String contact() {
        return "pages/contact";
    }

    @GetMapping("/guides")
    public String guides() {
        return "pages/guides/index";
    }

    @GetMapping("/guides/when-to-stop-repairing-your-car")
    public String guideWhenToStopRepairing() {
        return "pages/guides/when_to_stop_repairing";
    }

    @GetMapping("/guides/sunk-cost-fallacy-car-repairs")
    public String guideSunkCost() {
        return "pages/guides/sunk_cost_fallacy";
    }

    @GetMapping("/guides/car-repair-cost-vs-value")
    public String guideRepairCostVsValue() {
        return "pages/guides/repair_cost_vs_value";
    }

    @GetMapping(value = "/robots.txt", produces = "text/plain")
    @ResponseBody
    public String robots() {
        return "User-agent: *\n" +
                "Allow: /\n" +
                "Disallow: /lead\n" +
                "Disallow: /lead-capture\n" +
                "Disallow: /waitlist/submit\n" +
                "Disallow: /report\n" +
                "Disallow: /verdict?\n" +
                "Disallow: /report?\n" +
                "Sitemap: " + baseUrl + "/sitemap.xml\n";
    }

}
