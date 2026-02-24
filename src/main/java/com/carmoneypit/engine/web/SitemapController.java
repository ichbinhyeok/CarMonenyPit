package com.carmoneypit.engine.web;

import com.carmoneypit.engine.service.CarDataService;
import com.carmoneypit.engine.service.CarDataService.CarModel;
import com.carmoneypit.engine.service.CarDataService.MajorFaults;
import com.carmoneypit.engine.service.FaultHubService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;

@RestController
public class SitemapController {

    private final CarDataService dataService;

    private final String baseUrl;
    private final String lastModDate;

    public SitemapController(CarDataService dataService,
            @Value("${app.baseUrl:https://automoneypit.com}") String baseUrl,
            @Value("${app.datasetLastmod:2026-02-24}") String lastModDate) {
        this.dataService = dataService;
        this.baseUrl = baseUrl;
        this.lastModDate = lastModDate;
    }

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public String generateSitemap() {
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xmlBuilder.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

        String lastMod = lastModDate;

        // 1. Static Pages
        addUrl(xmlBuilder, baseUrl + "/", lastMod, "daily", "1.0");
        addUrl(xmlBuilder, baseUrl + "/models", lastMod, "weekly", "0.9");

        // 2. Fault Hub Pages (directory + 5 hubs)
        addUrl(xmlBuilder, baseUrl + "/faults", lastMod, "weekly", "0.9");
        for (String slug : FaultHubService.ALLOWED_SLUGS.stream().sorted().toList()) {
            addUrl(xmlBuilder, baseUrl + "/fault/" + slug, lastMod, "monthly", "0.8");
        }

        // 3. Directory Pages (Brands)
        List<String> brands = dataService.getAllBrands();
        for (String brand : brands) {
            String brandSlug = normalize(brand);
            addUrl(xmlBuilder, baseUrl + "/models/" + brandSlug, lastMod, "weekly", "0.8");
        }

        // 4. Model Directory & pSEO Pages
        List<CarModel> allModels = dataService.getAllModels();
        for (CarModel car : allModels) {
            String brandSlug = normalize(car.brand());
            String modelSlug = normalize(car.model());

            // Model Directory Page
            addUrl(xmlBuilder, baseUrl + "/models/" + brandSlug + "/" + modelSlug, lastMod, "weekly", "0.8");

            // Verdict Fault Pages
            Optional<MajorFaults> faultsOpt = dataService.findFaultsByModelId(car.id());
            if (faultsOpt.isPresent()) {
                for (CarDataService.Fault fault : faultsOpt.get().faults()) {
                    String faultSlug = fault.component().toLowerCase().replace(" ", "-").replaceAll("[^a-z0-9-]", "");
                    addUrl(xmlBuilder, baseUrl + "/verdict/" + brandSlug + "/" + modelSlug + "/" + faultSlug, lastMod,
                            "monthly", "0.7");
                }
            }

            // Verdict Mileage Pages (Generate predictable buckets)
            int[] mileageBuckets = { 75000, 100000, 150000, 200000 };
            for (int miles : mileageBuckets) {
                addUrl(xmlBuilder, baseUrl + "/verdict/" + brandSlug + "/" + modelSlug + "/" + miles + "-miles",
                        lastMod, "monthly", "0.7");
            }
        }

        xmlBuilder.append("</urlset>");
        return xmlBuilder.toString();
    }

    private void addUrl(StringBuilder builder, String loc, String lastmod, String changefreq, String priority) {
        builder.append("  <url>\n");
        builder.append("    <loc>").append(loc).append("</loc>\n");
        builder.append("    <lastmod>").append(lastmod).append("</lastmod>\n");
        builder.append("    <changefreq>").append(changefreq).append("</changefreq>\n");
        builder.append("    <priority>").append(priority).append("</priority>\n");
        builder.append("  </url>\n");
    }

    private String normalize(String input) {
        if (input == null)
            return "";
        return input.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }
}
