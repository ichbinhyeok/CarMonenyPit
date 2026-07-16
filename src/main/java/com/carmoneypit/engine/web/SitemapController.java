package com.carmoneypit.engine.web;

import com.carmoneypit.engine.service.CarDataService;
import com.carmoneypit.engine.service.CarDataService.CarModel;
import com.carmoneypit.engine.service.CarDataService.MajorFaults;
import com.carmoneypit.engine.service.CarDataService.ModelReliability;
import com.carmoneypit.engine.service.FaultHubService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;

@RestController
public class SitemapController {

    private final CarDataService dataService;

    private final String baseUrl;
    private final String lastModDate;

    public SitemapController(CarDataService dataService,
            @Value("${app.baseUrl:https://automoneypit.com}") String baseUrl,
            @Value("${app.contentLastmod:2026-07-16}") String lastModDate) {
        this.dataService = dataService;
        this.baseUrl = baseUrl;
        this.lastModDate = lastModDate;
    }

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public String generateSitemap() {
        Set<String> urls = new LinkedHashSet<>();

        // 1. Static Pages
        urls.add(baseUrl + "/");
        urls.add(baseUrl + "/models");
        urls.add(baseUrl + "/guides");
        urls.add(baseUrl + "/guides/when-to-stop-repairing-your-car");
        urls.add(baseUrl + "/guides/sunk-cost-fallacy-car-repairs");
        urls.add(baseUrl + "/guides/car-repair-cost-vs-value");
        urls.add(baseUrl + "/guides/car-repair-estimate-second-opinion");
        urls.add(baseUrl + "/tools/repair-or-sell-calculator");

        // 2. Fault Hub Pages (directory + 5 hubs)
        urls.add(baseUrl + "/faults");
        for (String slug : FaultHubService.ALLOWED_SLUGS.stream().sorted().toList()) {
            urls.add(baseUrl + "/fault/" + slug);
        }

        // 3. Directory Pages (Brands)
        List<String> brands = dataService.getAllBrands();
        for (String brand : brands) {
            String brandSlug = SeoIndexPolicy.normalize(brand);
            urls.add(baseUrl + "/models/" + brandSlug);
        }

        // 4. Model Directory & pSEO Pages
        List<CarModel> allModels = dataService.getAllModels();
        Set<String> processedModelRoutes = new LinkedHashSet<>();
        for (CarModel car : allModels) {
            String brandSlug = SeoIndexPolicy.normalize(car.brand());
            String modelSlug = SeoIndexPolicy.normalize(car.model());
            if (!processedModelRoutes.add(brandSlug + "|" + modelSlug)) {
                continue;
            }
            Optional<ModelReliability> reliabilityOpt = dataService.findReliabilityByModelId(car.id());

            // Model Directory Page
            urls.add(baseUrl + "/models/" + brandSlug + "/" + modelSlug);

            // Primary decision surface
            if (SeoIndexPolicy.isIndexableDecision(car)) {
                urls.add(baseUrl + SeoIndexPolicy.decisionPath(car, reliabilityOpt.orElse(null)));
            }

            // Verdict Fault Pages
            Optional<MajorFaults> faultsOpt = dataService.findFaultsByModelId(car.id());
            if (faultsOpt.isPresent()) {
                for (CarDataService.Fault fault : faultsOpt.get().faults()) {
                    if (SeoIndexPolicy.isIndexableFault(car, fault)) {
                        urls.add(baseUrl + "/verdict/" + brandSlug + "/" + modelSlug + "/"
                                + SeoIndexPolicy.faultSlug(fault.component()));
                    }
                }
            }

            // Verdict Mileage Pages (Generate predictable buckets)
            int[] mileageBuckets = { 50000, 75000, 100000, 125000, 150000, 175000, 200000 };
            for (int miles : mileageBuckets) {
                if (SeoIndexPolicy.isIndexableMileage(car, miles)) {
                    urls.add(baseUrl + "/verdict/" + brandSlug + "/" + modelSlug + "/" + miles + "-miles");
                }
            }
        }

        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xmlBuilder.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");
        for (String url : urls) {
            addUrl(xmlBuilder, url, lastModDate);
        }
        xmlBuilder.append("</urlset>");
        return xmlBuilder.toString();
    }

    private void addUrl(StringBuilder builder, String loc, String lastmod) {
        builder.append("  <url>\n");
        builder.append("    <loc>").append(loc).append("</loc>\n");
        builder.append("    <lastmod>").append(lastmod).append("</lastmod>\n");
        builder.append("  </url>\n");
    }
}
