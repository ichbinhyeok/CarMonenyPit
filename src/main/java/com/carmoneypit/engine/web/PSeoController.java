package com.carmoneypit.engine.web;

import com.carmoneypit.engine.api.OutputModels.VerdictResult;
import com.carmoneypit.engine.api.OutputModels.VerdictState;
import com.carmoneypit.engine.api.InputModels.EngineInput;
import com.carmoneypit.engine.api.InputModels.VehicleType;
import com.carmoneypit.engine.core.DecisionEngine;
import com.carmoneypit.engine.service.CarDataService;
import com.carmoneypit.engine.service.CarDataService.*;
import com.carmoneypit.engine.service.MarketContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import jakarta.servlet.http.HttpServletResponse;

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
  private final MarketContextService marketContextService;
  private final DecisionEngine decisionEngine;

  @Value("${app.baseUrl:https://automoneypit.com}")
  private String baseUrl;

  public PSeoController(CarDataService dataService, MarketContextService marketContextService,
      DecisionEngine decisionEngine) {
    this.dataService = dataService;
    this.marketContextService = marketContextService;
    this.decisionEngine = decisionEngine;
  }

  @GetMapping("/verdict/{brand}/{model}/{faultSlug}")
  public String showVerdict(
      @PathVariable("brand") String brand,
      @PathVariable("model") String model,
      @PathVariable("faultSlug") String faultSlug,
      Model modelMap,
      HttpServletResponse response) {

    response.setHeader("Cache-Control", "public, max-age=86400");
    logger.info("Request for {} / {} / {}", brand, model, faultSlug);

    // 1. Find Car Model
    Optional<CarModel> carOpt = dataService.findCarBySlug(brand, model);
    if (carOpt.isEmpty()) {
      logger.warn("Car not found: {} / {}", brand, model);
      throw new ResourceNotFoundException("Car model not found");
    }
    CarModel car = carOpt.get();

    // 2. Find Faults for this Model
    Optional<MajorFaults> faultsOpt = dataService.findFaultsByModelId(car.id());
    if (faultsOpt.isEmpty()) {
      logger.warn("No faults data for model: {}", car.id());
      throw new ResourceNotFoundException("No faults found for model");
    }

    // 3. Find the Specific Fault by Slug
    Optional<Fault> faultOpt = faultsOpt.get().faults().stream()
        .filter(f -> {
          // Generate the exact slug for this fault, same as in listFaults
          String slug = f.component().toLowerCase()
              .replace(" ", "-")
              .replaceAll("[^a-z0-9-]", "");
          return slug.equals(faultSlug);
        })
        .findFirst();

    if (faultOpt.isEmpty()) {
      logger.warn("Fault not found: {}", faultSlug);
      throw new ResourceNotFoundException("Fault not found");
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

    String metaDescription = "Experiencing " + fault.symptoms().toLowerCase() + "? Is a $"
        + String.format("%,d", Math.round(fault.repairCost())) + " " + fault.component() +
        " repair worth it on your " + car.brand() + " " + car.model()
        + "? We analyzed market data and depreciation curves to give you a clear financial verdict.";

    String canonicalUrl = baseUrl + "/verdict/" + brand + "/" + model + "/" + faultSlug;

    // 6. Generate Social Assets using Central Logic (SSOT)
    long marketValue = profile != null ? profile.market().jan2026AvgPrice() : 0;
    double repairCost = fault.repairCost();

    // Call unified SSOT Decision Engine
    EngineInput input = new EngineInput(car.model(), VehicleType.SEDAN, car.brand(), car.startYear(),
        profile != null ? profile.reliability().lifespanMiles() / 2 : 100000,
        (long) repairCost, marketValue, true, true);

    VerdictResult result = decisionEngine.evaluate(input);
    boolean isSell = result.verdictState() == VerdictState.TIME_BOMB;
    String verdictType = isSell ? "SELL" : "FIX";

    // Build tracking URLs
    String leadUrlInline = "/lead?page_type=pseo_fault&intent=" + verdictType + "&verdict_state="
        + result.verdictState().name() + "&brand=" + normalize(car.brand())
        + "&model=" + normalize(car.model()) + "&detail=" + faultSlug + "&placement=inline";
    String leadUrlSticky = "/lead?page_type=pseo_fault&intent=" + verdictType + "&verdict_state="
        + result.verdictState().name() + "&brand=" + normalize(car.brand())
        + "&model=" + normalize(car.model()) + "&detail=" + faultSlug + "&placement=sticky";

    // Related faults for internal linking
    List<Fault> relatedFaults = faultsOpt.get().faults().stream()
        .filter(f -> !f.component().equals(fault.component()))
        .limit(3)
        .toList();

    String ogImage = baseUrl + "/og-image.png";

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
    modelMap.addAttribute("leadUrlInline", leadUrlInline);
    modelMap.addAttribute("leadUrlSticky", leadUrlSticky);
    modelMap.addAttribute("relatedFaults", relatedFaults);
    modelMap.addAttribute("marketPulse", marketContextService.generateMarketContext(car, fault));
    modelMap.addAttribute("ogImage", ogImage);
    modelMap.addAttribute("breadcrumbs", breadcrumbs);

    return "pseo_landing";
  }

  // --- Mileage-Based pSEO Pages (NEW) ---

  @GetMapping("/verdict/{brand}/{model}/{mileage}-miles")
  public Object showMileageVerdict(
      @PathVariable("brand") String brand,
      @PathVariable("model") String model,
      @PathVariable("mileage") int mileage,
      Model modelMap,
      HttpServletResponse response) {

    // 1. Mileage Bucketing 301 Redirect Logic (Crawl Optimization)
    List<Integer> allowedBuckets = List.of(50000, 75000, 100000, 125000, 150000, 175000, 200000);
    if (!allowedBuckets.contains(mileage)) {
      int closestBucket = allowedBuckets.get(0);
      int minDiff = Math.abs(mileage - closestBucket);
      for (int bucket : allowedBuckets) {
        int diff = Math.abs(mileage - bucket);
        if (diff < minDiff || (diff == minDiff && bucket > closestBucket)) {
          minDiff = diff;
          closestBucket = bucket;
        }
      }
      String redirectUrl = baseUrl + "/verdict/" + normalize(brand) + "/" + normalize(model) + "/" + closestBucket
          + "-miles";
      org.springframework.web.servlet.view.RedirectView rv = new org.springframework.web.servlet.view.RedirectView(
          redirectUrl);
      rv.setStatusCode(org.springframework.http.HttpStatus.MOVED_PERMANENTLY);
      return rv;
    }

    response.setHeader("Cache-Control", "public, max-age=86400");
    logger.info("Mileage request for {} / {} at {} miles", brand, model, mileage);

    // 1. Find Car Model
    Optional<CarModel> carOpt = dataService.findCarBySlug(brand, model);
    if (carOpt.isEmpty()) {
      logger.warn("Car not found: {} / {}", brand, model);
      throw new ResourceNotFoundException("Car model not found");
    }
    CarModel car = carOpt.get();

    // 2. Load Reliability & Market Data
    Optional<ModelReliability> reliabilityOpt = dataService.findReliabilityByModelId(car.id());
    Optional<ModelMarket> marketOpt = dataService.findMarketByModelId(car.id());

    if (reliabilityOpt.isEmpty() || marketOpt.isEmpty()) {
      logger.warn("Missing data for model: {}", car.id());
      throw new ResourceNotFoundException("Reliability or Market data not found for model");
    }

    ModelReliability reliability = reliabilityOpt.get();
    ModelMarket market = marketOpt.get();

    // 3. Build breadcrumbs
    List<Breadcrumb> breadcrumbs = List.of(
        new Breadcrumb("Home", "/"),
        new Breadcrumb(car.brand(), "/models/" + normalize(car.brand())),
        new Breadcrumb(car.model(), "/models/" + normalize(car.brand()) + "/" + normalize(car.model())),
        new Breadcrumb(String.format("%,d Miles", mileage), "#"));

    // 4. Build canonical URL
    String canonicalUrl = baseUrl + "/verdict/" + normalize(brand) + "/" + normalize(model) + "/" + mileage + "-miles";

    // 5. Meta description
    String metaDescription = String.format(
        "Is your %s %s worth keeping at %,d miles? Expert analysis, expected repairs, and data-driven recommendations for %d-%d owners.",
        car.brand(), car.model(), mileage, car.startYear(), car.endYear());

    // 6. Schema JSON (FAQPage)
    String schemaJson = String.format("""
        {
          "@context": "https://schema.org",
          "@type": "FAQPage",
          "mainEntity": [
            {
              "@type": "Question",
              "name": "Is a %s %s with %,d miles reliable?",
              "acceptedAnswer": {
                "@type": "Answer",
                "text": "At %,d miles, a %s %s is at %d%% of its expected lifespan. %s"
              }
            },
            {
              "@type": "Question",
              "name": "What is the junk value of a %s %s?",
              "acceptedAnswer": {
                "@type": "Answer",
                "text": "The minimum scrap/junk value for a %s %s is approximately $%,d."
              }
            }
          ]
        }
        """,
        car.brand(), car.model(), mileage,
        mileage, car.brand(), car.model(),
        (int) ((1.0 - (double) mileage / reliability.lifespanMiles()) * 100),
        reliability.mileageLogicText() != null && !reliability.mileageLogicText().isEmpty()
            ? reliability.mileageLogicText().values().iterator().next()
            : "Regular maintenance is key.",
        car.brand(), car.model(),
        car.brand(), car.model(), market.commonJunkValue() != null ? market.commonJunkValue() : 500);

    // Build Tracking URLs
    double lifespanPercent = Math.min(100.0, (double) mileage / reliability.lifespanMiles() * 100);
    String verdictType = lifespanPercent >= 50.0 ? "SELL" : "FIX";
    String leadUrlInline = "/lead?page_type=pseo_mileage&verdict_type=" + verdictType + "&brand="
        + normalize(car.brand()) + "&model=" + normalize(car.model()) + "&detail=" + mileage + "k&placement=inline";
    String leadUrlSticky = "/lead?page_type=pseo_mileage&verdict_type=" + verdictType + "&brand="
        + normalize(car.brand()) + "&model=" + normalize(car.model()) + "&detail=" + mileage + "k&placement=sticky";

    // Load faults data
    Optional<MajorFaults> faultsOpt = dataService.findFaultsByModelId(car.id());

    modelMap.addAttribute("car", car);
    modelMap.addAttribute("reliability", reliability);
    modelMap.addAttribute("market", market);
    modelMap.addAttribute("majorFaults", faultsOpt.orElse(null)); // Pass null if not found
    modelMap.addAttribute("targetMileage", mileage);
    modelMap.addAttribute("breadcrumbs", breadcrumbs);
    modelMap.addAttribute("canonicalUrl", canonicalUrl);
    modelMap.addAttribute("metaDescription", metaDescription);
    modelMap.addAttribute("schemaJson", schemaJson);
    modelMap.addAttribute("leadUrlInline", leadUrlInline);
    modelMap.addAttribute("leadUrlSticky", leadUrlSticky);

    return "pseo_mileage";
  }

  // --- Decision Page (CTA Target) ---

  @GetMapping("/decision")
  public String showDecisionPage() {
    // Redirect to home page where the main calculator is
    return "redirect:/";
  }

  // --- Directory Navigation (Silo Structure) ---

  @GetMapping("/models")
  public String listBrands(Model modelMap, HttpServletResponse response) {
    response.setHeader("Cache-Control", "public, max-age=86400");
    List<java.util.Map.Entry<String, String>> brands = dataService.getAllBrands().stream()
        .map(brand -> java.util.Map.entry(brand, "/models/" + normalize(brand)))
        .toList();

    modelMap.addAttribute("title", "Car Brands - AutoMoneyPit");
    modelMap.addAttribute("metaDescription",
        "Browse common repair costs and market value insights by car brand. See data-driven verdicts on whether to fix or sell.");
    modelMap.addAttribute("canonicalUrl", baseUrl + "/models");
    modelMap.addAttribute("breadcrumbs", List.of("Models")); // Keep simple for directory for now
    modelMap.addAttribute("items", brands);
    return "pages/directory_list";
  }

  // --- View Helpers ---

  public record Breadcrumb(String label, String url) {
  }

  @GetMapping("/models/{brandSlug}")
  public String listModels(@PathVariable("brandSlug") String brandSlug, Model modelMap, HttpServletResponse response) {
    response.setHeader("Cache-Control", "public, max-age=86400");
    List<CarModel> models = dataService.getModelsByBrand(brandSlug);

    if (models.isEmpty()) {
      throw new ResourceNotFoundException("Brand not found");
    }

    List<java.util.Map.Entry<String, String>> modelLinks = models.stream()
        .map(c -> java.util.Map.entry(
            c.model(),
            "/models/" + brandSlug + "/" + normalize(c.model())))
        .distinct()
        .toList();

    modelMap.addAttribute("title", "Models for " + brandSlug.toUpperCase() + " - AutoMoneyPit");
    modelMap.addAttribute("metaDescription", "Explore specific models from " + brandSlug.toUpperCase()
        + ". Find the most common repairs, costs, and value preservation data.");
    modelMap.addAttribute("canonicalUrl", baseUrl + "/models/" + brandSlug);
    modelMap.addAttribute("breadcrumbs", List.of("Models", brandSlug));
    modelMap.addAttribute("items", modelLinks);
    return "pages/directory_list";
  }

  @GetMapping("/models/{brandSlug}/{modelSlug}")
  public String listFaults(@PathVariable("brandSlug") String brandSlug, @PathVariable("modelSlug") String modelSlug,
      Model modelMap, HttpServletResponse response) {
    response.setHeader("Cache-Control", "public, max-age=86400");
    Optional<CarModel> carOpt = dataService.findCarBySlug(brandSlug, modelSlug);
    if (carOpt.isEmpty()) {
      throw new ResourceNotFoundException("Model not found");
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

    modelMap.addAttribute("title", "Common Problems: " + car.brand() + " " + car.model() + " - AutoMoneyPit");
    modelMap.addAttribute("metaDescription", "See the most expensive common repairs for the " + car.brand() + " "
        + car.model() + " and decide whether to fix them or sell your vehicle.");
    modelMap.addAttribute("canonicalUrl", baseUrl + "/models/" + brandSlug + "/" + modelSlug);
    modelMap.addAttribute("breadcrumbs", List.of("Models", brandSlug, modelSlug));
    modelMap.addAttribute("items", faultLinks);
    return "pages/directory_list";
  }

  // --- Helper Methods ---

  private String normalize(String input) {
    if (input == null)
      return "";
    return input.toLowerCase()
        .replaceAll("[^a-z0-9]+", "-")
        .replaceAll("^-|-$", "");
  }

  private String generateSchema(CarModel car, Fault fault, ProfileViewModel profile,
      String brandSlug, String modelSlug, String faultSlug) {
    int switchingCost = 2500 + (Math.abs(car.id().hashCode()) % 1000);

    // Note: Using WebApplication schema instead of Product to avoid Google Shopping
    // indexing
    // This site is a vehicle repair analysis tool, NOT a shopping site
    return "{" +
        "\"@context\": \"https://schema.org\"," +
        "\"@graph\": [" +
        "{" +
        "\"@type\": \"WebApplication\"," +
        "\"name\": \"AutoMoneyPit - " + car.brand() + " " + car.model() + " Analysis\"," +
        "\"description\": \"Data-driven repair cost vs market value analysis tool for " + car.brand() + " "
        + car.model() + " (" + car.generation() + ") owners.\"," +
        "\"url\": \"" + baseUrl + "/verdict/" + brandSlug + "/" + modelSlug + "/" + faultSlug + "\"," +
        "\"image\": \"" + baseUrl + "/og-image.png\"," +
        "\"applicationCategory\": \"FinanceApplication\"," +
        "\"operatingSystem\": \"Web Browser\"," +
        "\"offers\": {" +
        "\"@type\": \"Offer\"," +
        "\"price\": \"0\"," +
        "\"priceCurrency\": \"USD\"" +
        "}" +
        "}," +
        "{" +
        "\"@type\": \"FAQPage\"," +
        "\"mainEntity\": [{" +
        "\"@type\": \"Question\"," +
        "\"name\": \"How much does it cost to fix " + fault.component() + " on a " + car.brand() + " " + car.model()
        + "?\"," +
        "\"acceptedAnswer\": {" +
        "\"@type\": \"Answer\"," +
        "\"text\": \"The average repair cost for the " + fault.component() + " in a " + car.brand() + " " + car.model()
        + " is $" + String.format("%,d", Math.round(fault.repairCost())) + ".\"" +
        "}" +
        "}, {" +
        "\"@type\": \"Question\"," +
        "\"name\": \"Is it worth fixing the " + fault.component() + " on my " + car.brand() + " " + car.model() + "?\","
        +
        "\"acceptedAnswer\": {" +
        "\"@type\": \"Answer\"," +
        "\"text\": \"" + fault.verdictImplication().replace("\"", "\\\"") + "\"" +
        "}" +
        "}]" +
        "}," +
        "{" +
        "\"@type\": \"HowTo\"," +
        "\"name\": \"How to Decide if " + fault.component() + " Repair is Worth It on Your " + car.brand() + " "
        + car.model() + "\"," +
        "\"description\": \"Step-by-step guide to make the right decision about your vehicle repair.\"," +
        "\"step\": [" +
        "{" +
        "\"@type\": \"HowToStep\"," +
        "\"position\": 1," +
        "\"name\": \"Calculate Repair-to-Value Ratio\"," +
        "\"text\": \"Divide the $" + String.format("%,d", Math.round(fault.repairCost()))
        + " repair cost by your vehicle's current market value. If the ratio exceeds 50%, it's typically not worth repairing.\""
        +
        "}," +
        "{" +
        "\"@type\": \"HowToStep\"," +
        "\"position\": 2," +
        "\"name\": \"Check peer behavior data\"," +
        "\"text\": \"Our market analysis suggests that for repairs exceeding 50% of vehicle value, financially-optimized owners typically choose to sell.\""
        +
        "}," +
        "{" +
        "\"@type\": \"HowToStep\"," +
        "\"position\": 3," +
        "\"name\": \"Factor in replacement costs\"," +
        "\"text\": \"Replacing your vehicle will incur approximately $" + switchingCost
        + " in taxes, fees, registration, and other switching costs.\"" +
        "}," +
        "{" +
        "\"@type\": \"HowToStep\"," +
        "\"position\": 4," +
        "\"name\": \"Run a financial analysis\"," +
        "\"text\": \"Use our free calculator to input your specific mileage, vehicle value, and repair quote for a personalized verdict.\""
        +
        "}" +
        "]" +
        "}," +
        "{" +
        "\"@type\": \"BreadcrumbList\"," +
        "\"itemListElement\": [" +
        "{" +
        "\"@type\": \"ListItem\"," +
        "\"position\": 1," +
        "\"name\": \"Models\"," +
        "\"item\": \"" + baseUrl + "/models\"" +
        "}," +
        "{" +
        "\"@type\": \"ListItem\"," +
        "\"position\": 2," +
        "\"name\": \"" + car.brand() + "\"," +
        "\"item\": \"" + baseUrl + "/models/" + brandSlug + "\"" +
        "}," +
        "{" +
        "\"@type\": \"ListItem\"," +
        "\"position\": 3," +
        "\"name\": \"" + car.model() + "\"," +
        "\"item\": \"" + baseUrl + "/models/" + brandSlug + "/" + modelSlug + "\"" +
        "}," +
        "{" +
        "\"@type\": \"ListItem\"," +
        "\"position\": 4," +
        "\"name\": \"" + fault.component() + " Analysis\"," +
        "\"item\": \"" + baseUrl + "/verdict/" + brandSlug + "/" + modelSlug + "/" + faultSlug + "\"" +
        "}" +
        "]" +
        "}" +
        "]" +
        "}";
  }

  // --- View Model (for template) ---

  public record ProfileViewModel(
      CarModel car,
      ModelReliability reliability,
      ModelMarket market) {
  }
}
