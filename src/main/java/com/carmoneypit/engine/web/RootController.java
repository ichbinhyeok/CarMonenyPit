package com.carmoneypit.engine.web;

import com.carmoneypit.engine.service.CarDataService;
import com.carmoneypit.engine.service.CarDataService.CarModel;
import com.carmoneypit.engine.service.CarDataService.MajorFaults;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Controller
public class RootController {

    private final CarDataService dataService;

    public RootController(CarDataService dataService) {
        this.dataService = dataService;
    }

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

    @GetMapping(value = "/robots.txt", produces = "text/plain")
    @ResponseBody
    public String robots() {
        return """
                User-agent: *
                Allow: /
                Disallow: /verdict/share # Prevent indexing of temporary personal results
                Sitemap: https://carmoneypit.com/sitemap.xml
                """;
    }

    @GetMapping(value = "/sitemap.xml", produces = "application/xml")
    @ResponseBody
    public String sitemap() {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        String baseUrl = "https://carmoneypit.com";

        // Static Pages
        addUrl(xml, baseUrl + "/", today, "1.0");
        addUrl(xml, baseUrl + "/about", today, "0.8");
        addUrl(xml, baseUrl + "/models", today, "0.9");
        addUrl(xml, baseUrl + "/methodology", today, "0.8");

        // Dynamic Pages
        for (CarModel car : dataService.getAllModels()) {
            String brandSlug = normalize(car.brand());
            String modelSlug = normalize(car.model());

            // Brand listing (if we supported it explicitly, but /models/{brand} works via
            // PSeoController)
            addUrl(xml, baseUrl + "/models/" + brandSlug, today, "0.8");

            // Model listing
            addUrl(xml, baseUrl + "/models/" + brandSlug + "/" + modelSlug, today, "0.9");

            // Fault Pages
            Optional<MajorFaults> faultsOpt = dataService.findFaultsByModelId(car.id());
            if (faultsOpt.isPresent()) {
                for (var fault : faultsOpt.get().faults()) {
                    String faultSlug = fault.component().toLowerCase()
                            .replace(" ", "-")
                            .replaceAll("[^a-z0-9-]", "");

                    String verdictUrl = baseUrl + "/verdict/" + brandSlug + "/" + modelSlug + "/" + faultSlug;
                    addUrl(xml, verdictUrl, today, "1.0");
                }
            }
        }

        xml.append("</urlset>");
        return xml.toString();
    }

    private void addUrl(StringBuilder xml, String loc, String lastmod, String priority) {
        xml.append("  <url>\n");
        xml.append("    <loc>").append(loc).append("</loc>\n");
        xml.append("    <lastmod>").append(lastmod).append("</lastmod>\n");
        xml.append("    <priority>").append(priority).append("</priority>\n");
        xml.append("  </url>\n");
    }

    private String normalize(String input) {
        return input.toLowerCase().replaceAll("[^a-z0-9]", "");
    }
}
