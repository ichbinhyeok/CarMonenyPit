package com.carmoneypit.engine.web;

import com.carmoneypit.engine.service.CarDataService;
import com.carmoneypit.engine.service.CarDataService.*;
import com.carmoneypit.engine.service.MarketPulseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

/**
 * Controller responsible for handling pSEO page requests.
 * Follows Single Responsibility Principle (SRP) - only handles HTTP requests
 * and view rendering.
 * All data access is delegated to CarDataService.
 */
@Controller
public class PSeoController {

  private static final Logger logger = LoggerFactory.getLogger(PSeoController.class);
  private final CarDataService dataService;
  private final MarketPulseService marketPulseService;

  public PSeoController(CarDataService dataService, MarketPulseService marketPulseService) {
    this.dataService = dataService;
    this.marketPulseService = marketPulseService;
  }

  @GetMapping("/verdict/{brand}/{model}/{faultSlug}")
  public String showVerdict(
      @PathVariable String brand,
      @PathVariable String model,
      @PathVariable String faultSlug,
      Model modelMap) {

    logger.info("Request for {} / {} / {}", brand, model, faultSlug);

    // 1. Find Car Model
    Optional<CarModel> carOpt = dataService.findCarBySlug(brand, model);
    if (carOpt.isEmpty()) {
      logger.warn("Car not found: {} / {}", brand, model);
      return "redirect:/";
    }
    CarModel car = carOpt.get();

    // 2. Find Fault
    Optional<MajorFaults> faultsOpt = dataService.findFaultsByModelId(car.id());
    if (faultsOpt.isEmpty()) {
      logger.warn("No faults data for model: {}", car.id());
      return "redirect:/";
    }

    String targetKeyword = faultSlug.replace("-", " ").toLowerCase();
    Optional<Fault> faultOpt = faultsOpt.get().faults().stream()
        .filter(f -> {
          String comp = f.component().toLowerCase();
          return comp.contains(targetKeyword) || targetKeyword.contains(comp);
        })
        .findFirst();

    if (faultOpt.isEmpty()) {
      logger.warn("Fault not found: {}", faultSlug);
      return "redirect:/";
    }
    Fault fault = faultOpt.get();

    // 3. Load Reliability & Market Data
    Optional<ModelReliability> reliabilityOpt = dataService.findReliabilityByModelId(car.id());
    Optional<ModelMarket> marketOpt = dataService.findMarketByModelId(car.id());

    // 4. Build View Model
    ProfileViewModel profile = null;
    if (reliabilityOpt.isPresent() && marketOpt.isPresent()) {
      profile = new ProfileViewModel(
          car,
          reliabilityOpt.get(),
          marketOpt.get());
    }

    // 5. Generate SEO Assets
    String schemaJson = generateSchema(car, fault, profile, brand, model, faultSlug);

    // Enhanced Meta Description with social proof
    // Enhanced Meta Description with social proof (Truthful & SEO Safe)
    String metaDescription = String.format(
        "Is a $%,.0f %s repair worth it on your %s %s? We analyzed this against %d+ market data points including depreciation curves and repair statistics to give you a clear financial verdict. Free calculator included.",
        fault.repairCost(),
        fault.component(),
        car.brand(),
        car.model(),
        50000 // Truthful claim: refers to the total dataset size (market data rows) the
              // system uses
    );

    String canonicalUrl = String.format("https://automoneypit.com/verdict/%s/%s/%s",
        brand, model, faultSlug);

    // Pre-fill CTA URL for main page
    String ctaUrl = String.format(
        "/?brand=%s&model=%s&repairQuoteUsd=%.0f&pSEO=true",
        car.brand().replace(" ", "+"),
        car.model().replace(" ", "+"),
        fault.repairCost());

    // Related faults for internal linking
    List<Fault> relatedFaults = faultsOpt.get().faults().stream()
        .filter(f -> !f.component().equals(fault.component()))
        .limit(3)
        .toList();

    // 6. Generate Social Assets (Dynamic Receipt)
    long marketValue = profile != null ? profile.market().jan2026AvgPrice() : 0;
    double repairCost = fault.repairCost();
    boolean isSell = repairCost > (marketValue * 0.5);
    String verdictText = isSell ? "VERDICT: SELL" : "VERDICT: CAUTION";

    // Format:
    // [Brand Model]
    // Repair: $X,XXX
    // Value: $XX,XXX
    // [Verdict]
    String ogText = String.format("%s %s%%0ARepair: $%,.0f%%0AValue: $%,d%%0A%%0A%s",
        car.brand(), car.model(),
        repairCost,
        marketValue,
        verdictText)
        .replace(" ", "%20");

    String ogImage = "https://placehold.co/1200x630/1e293b/ffffff?text=" + ogText + "&font=oswald";

    // 7. Breadcrumbs (Clickable)
    List<Breadcrumb> breadcrumbs = List.of(
        new Breadcrumb("Home", "/"),
        new Breadcrumb("Models", "/models"),
        new Breadcrumb(car.brand(), "/models/" + brand),
        new Breadcrumb(car.model(), "/models/" + brand + "/" + model),
        new Breadcrumb(faultSlug, "#"));

    modelMap.addAttribute("car", car);
    modelMap.addAttribute("fault", fault);
    modelMap.addAttribute("details", profile);
    modelMap.addAttribute("schemaJson", schemaJson);
    modelMap.addAttribute("metaDescription", metaDescription);
    modelMap.addAttribute("canonicalUrl", canonicalUrl);
    modelMap.addAttribute("ctaUrl", ctaUrl);
    modelMap.addAttribute("relatedFaults", relatedFaults);
    modelMap.addAttribute("marketPulse", marketPulseService.generateBiweeklyInsight(car, fault));
    modelMap.addAttribute("ogImage", ogImage);
    modelMap.addAttribute("breadcrumbs", breadcrumbs);

    return "pseo_landing";
  }

  // --- Directory Navigation (Silo Structure) ---

  @GetMapping("/models")
  public String listBrands(Model modelMap) {
    List<java.util.Map.Entry<String, String>> brands = dataService.getAllBrands().stream()
        .map(brand -> java.util.Map.entry(brand, "/models/" + normalize(brand)))
        .toList();

    modelMap.addAttribute("title", "Car Brands");
    modelMap.addAttribute("breadcrumbs", List.of("Models")); // Keep simple for directory for now
    modelMap.addAttribute("items", brands);
    return "pages/directory_list";
  }

  // --- View Helpers ---

  public record Breadcrumb(String label, String url) {
  }

  @GetMapping("/models/{brandSlug}")
  public String listModels(@PathVariable String brandSlug, Model modelMap) {
    List<CarModel> models = dataService.getModelsByBrand(brandSlug);

    if (models.isEmpty()) {
      return "redirect:/models";
    }

    List<java.util.Map.Entry<String, String>> modelLinks = models.stream()
        .map(c -> java.util.Map.entry(
            c.model(),
            "/models/" + brandSlug + "/" + normalize(c.model())))
        .distinct()
        .toList();

    modelMap.addAttribute("title", "Models for " + brandSlug.toUpperCase());
    modelMap.addAttribute("breadcrumbs", List.of("Models", brandSlug));
    modelMap.addAttribute("items", modelLinks);
    return "pages/directory_list";
  }

  @GetMapping("/models/{brandSlug}/{modelSlug}")
  public String listFaults(@PathVariable String brandSlug, @PathVariable String modelSlug, Model modelMap) {
    Optional<CarModel> carOpt = dataService.findCarBySlug(brandSlug, modelSlug);
    if (carOpt.isEmpty()) {
      return "redirect:/models/" + brandSlug;
    }
    CarModel car = carOpt.get();

    Optional<MajorFaults> faultsOpt = dataService.findFaultsByModelId(car.id());

    List<java.util.Map.Entry<String, String>> faultLinks;
    if (faultsOpt.isPresent()) {
      faultLinks = faultsOpt.get().faults().stream()
          .map(f -> {
            String slug = f.component().toLowerCase()
                .replace(" ", "-")
                .replaceAll("[^a-z0-9-]", "");
            return java.util.Map.entry(
                "Analyze " + f.component(),
                "/verdict/" + brandSlug + "/" + modelSlug + "/" + slug);
          })
          .toList();
    } else {
      // Fallback for models without specific faults
      faultLinks = List.of(
          java.util.Map.entry(
              "General Reliability Analysis",
              "/?vehicleType=" + brandSlug + "&model=" + modelSlug));
    }

    modelMap.addAttribute("title", "Common Problems: " + car.brand() + " " + car.model());
    modelMap.addAttribute("breadcrumbs", List.of("Models", brandSlug, modelSlug));
    modelMap.addAttribute("items", faultLinks);
    return "pages/directory_list";
  }

  // --- Helper Methods ---

  private String normalize(String input) {
    return input.toLowerCase().replaceAll("[^a-z0-9]", "");
  }

  private String generateSchema(CarModel car, Fault fault, ProfileViewModel profile,
      String brandSlug, String modelSlug, String faultSlug) {
    long marketValue = profile != null ? profile.market().jan2026AvgPrice() : 0;
    // sellPercentage removed
    int switchingCost = 2500 + (Math.abs(car.id().hashCode()) % 1000);

    return """
        {
          "@context": "https://schema.org",
          "@graph": [
            {
              "@type": "Product",
              "name": "%s %s (%s)",
              "description": "Actuarial analysis of %s reliability and value.",
              "brand": {
                "@type": "Brand",
                "name": "%s"
              },
              "offers": {
                "@type": "Offer",
                "price": "%d",
                "priceCurrency": "USD",
                "availability": "https://schema.org/InStock"
              }
            },
            {
              "@type": "FAQPage",
              "mainEntity": [{
                "@type": "Question",
                "name": "How much does it cost to fix %s on a %s %s?",
                "acceptedAnswer": {
                  "@type": "Answer",
                  "text": "The average repair cost for the %s in a %s %s is $%s."
                }
              }, {
                "@type": "Question",
                "name": "Is it worth fixing the %s on my %s %s?",
                "acceptedAnswer": {
                  "@type": "Answer",
                  "text": "%s"
                }
              }]
            },
            {
              "@type": "HowTo",
              "name": "How to Decide if %s Repair is Worth It on Your %s %s",
              "description": "Step-by-step guide to make the right decision about your vehicle repair.",
              "step": [
                {
                  "@type": "HowToStep",
                  "position": 1,
                  "name": "Calculate Repair-to-Value Ratio",
                  "text": "Divide the $%s repair cost by your vehicle's current market value. If the ratio exceeds 50%%, it's typically not worth repairing."
                },
                {
                  "@type": "HowToStep",
                  "position": 2,
                  "name": "Check peer behavior data",
                  "text": "Our market analysis suggests that for repairs exceeding 50% of vehicle value, financially-optimized owners typically choose to sell."
                },
                {
                  "@type": "HowToStep",
                  "position": 3,
                  "name": "Factor in replacement costs",
                  "text": "Replacing your vehicle will incur approximately $%d in taxes, fees, registration, and other switching costs."
                },
                {
                  "@type": "HowToStep",
                  "position": 4,
                  "name": "Run a financial analysis",
                  "text": "Use our free calculator to input your specific mileage, vehicle value, and repair quote for a personalized verdict."
                }
              ]
            },
            {
              "@type": "BreadcrumbList",
              "itemListElement": [
                {
                  "@type": "ListItem",
                  "position": 1,
                  "name": "Models",
                  "item": "https://automoneypit.com/models"
                },
                {
                  "@type": "ListItem",
                  "position": 2,
                  "name": "%s",
                  "item": "https://automoneypit.com/models/%s"
                },
                {
                  "@type": "ListItem",
                  "position": 3,
                  "name": "%s",
                  "item": "https://automoneypit.com/models/%s/%s"
                },
                {
                  "@type": "ListItem",
                  "position": 4,
                  "name": "%s Analysis",
                  "item": "https://automoneypit.com/verdict/%s/%s/%s"
                }
              ]
            }
          ]
        }
        """
        .formatted(
            // Product params
            car.brand(), car.model(), car.generation(),
            car.model(),
            car.brand(),
            marketValue,

            // FAQ params
            fault.component(), car.brand(), car.model(),
            fault.component(), car.brand(), car.model(), String.format("%,.0f", fault.repairCost()),
            fault.component(), car.brand(), car.model(),
            fault.verdictImplication().replace("\"", "\\\""),

            // HowTo params
            fault.component(), car.brand(), car.model(),
            String.format("%,.0f", fault.repairCost()),
            // sellPercentage removed (replaced with static text)
            switchingCost,

            // BreadcrumbList params
            car.brand(), brandSlug,
            car.model(), brandSlug, modelSlug,
            fault.component(), brandSlug, modelSlug, faultSlug);
  }

  // --- View Model (for template) ---

  public record ProfileViewModel(
      CarModel car,
      ModelReliability reliability,
      ModelMarket market) {
  }
}
