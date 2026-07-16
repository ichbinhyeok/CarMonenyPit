package com.carmoneypit.engine.web;

import com.carmoneypit.engine.api.OutputModels.VerdictResult;
import com.carmoneypit.engine.api.OutputModels.VerdictState;
import com.carmoneypit.engine.api.InputModels.EngineInput;
import com.carmoneypit.engine.api.InputModels.VehicleType;
import com.carmoneypit.engine.config.PartnerRoutingConfig;
import com.carmoneypit.engine.core.DecisionEngine;
import com.carmoneypit.engine.service.CarDataService;
import com.carmoneypit.engine.service.CarDataService.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.view.RedirectView;
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
  private final DecisionEngine decisionEngine;
  private final PartnerRoutingConfig routingConfig;

  @Value("${app.baseUrl:https://automoneypit.com}")
  private String baseUrl;

  @Value("${app.datasetVersion:2026-04-01}")
  private String datasetVersion;

  public PSeoController(CarDataService dataService,
      DecisionEngine decisionEngine,
      PartnerRoutingConfig routingConfig) {
    this.dataService = dataService;
    this.decisionEngine = decisionEngine;
    this.routingConfig = routingConfig;
  }

  @GetMapping("/verdict/{brand}/{model}/{faultSlug}")
  public Object showVerdict(
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

    String requestedFaultSlug = normalize(faultSlug);

    // 3. Find the Specific Fault by Slug
    Optional<Fault> faultOpt = faultsOpt.get().faults().stream()
        .filter(f -> {
          String slug = toFaultSlug(f.component());
          // Accept non-canonical separators in the incoming URL, then redirect.
          return normalize(slug).equals(requestedFaultSlug);
        })
        .findFirst();

    if (faultOpt.isEmpty()) {
      logger.warn("Fault not found: {}", faultSlug);
      throw new ResourceNotFoundException("Fault not found");
    }
    Fault fault = faultOpt.get();
    String canonicalBrandSlug = normalize(car.brand());
    String canonicalModelSlug = normalize(car.model());
    String canonicalFaultSlug = toFaultSlug(fault.component());

    if (!brand.equals(canonicalBrandSlug)
        || !model.equals(canonicalModelSlug)
        || !faultSlug.equals(canonicalFaultSlug)) {
      return permanentRedirect(
          baseUrl + "/verdict/" + canonicalBrandSlug + "/" + canonicalModelSlug + "/" + canonicalFaultSlug);
    }

    // 3. Load Reliability & Market Data
    Optional<ModelReliability> reliabilityOpt = dataService.findReliabilityByModelId(car.id());
    Optional<ModelMarket> marketOpt = dataService.findMarketByModelId(car.id());

    String shouldFixUrl = SeoIndexPolicy.decisionPath(car, reliabilityOpt.orElse(null));

    // 4. Build View Model
    ProfileViewModel profile = null;
    if (reliabilityOpt.isPresent() && marketOpt.isPresent()) {
      profile = new ProfileViewModel(
          car,
          reliabilityOpt.get(),
          marketOpt.get());
    }

    // 5. Generate SEO Assets
    String schemaJson = generateSchema(car, fault, profile, canonicalBrandSlug, canonicalModelSlug, canonicalFaultSlug);

    String metaDescription;
    if (profile != null) {
      metaDescription = "See " + car.brand() + " " + car.model() + " " + fault.component()
          + " repair cost (~$" + String.format("%,d", Math.round(fault.repairCost()))
          + "), typical market value (~$" + String.format("%,d", profile.market().jan2026AvgPrice())
          + "), and whether it makes more sense to fix or sell.";
    } else {
      metaDescription = "See " + car.brand() + " " + car.model() + " " + fault.component()
          + " repair cost, common failure mileage, and whether it makes more sense to fix or sell before approving a $"
          + String.format("%,d", Math.round(fault.repairCost())) + " repair.";
    }

    String canonicalUrl = baseUrl + "/verdict/" + canonicalBrandSlug + "/" + canonicalModelSlug + "/" + canonicalFaultSlug;

    // 6. Generate Social Assets using Central Logic (SSOT)
    long marketValue = profile != null ? profile.market().jan2026AvgPrice() : 0;
    double repairCost = fault.repairCost();

    // Call unified SSOT Decision Engine
    EngineInput input = new EngineInput(car.model(), VehicleType.SEDAN, car.brand(), car.startYear(),
        profile != null ? profile.reliability().lifespanMiles() / 2 : 100000,
        (long) repairCost, marketValue, true, true);

    VerdictResult result = decisionEngine.evaluate(input);
    boolean isSell = result.verdictState() == VerdictState.TIME_BOMB;
    String verdictType = isSell ? "SELL" : "REPAIR";

    // Build tracking URLs
    String leadUrlInline = "/lead?page_type=pseo_fault&intent=" + verdictType + "&verdict_state="
        + result.verdictState().name() + "&brand=" + normalize(car.brand())
        + "&model=" + normalize(car.model()) + "&detail=" + canonicalFaultSlug + "&placement=inline";
    String leadUrlSticky = "/lead?page_type=pseo_fault&intent=" + verdictType + "&verdict_state="
        + result.verdictState().name() + "&brand=" + normalize(car.brand())
        + "&model=" + normalize(car.model()) + "&detail=" + canonicalFaultSlug + "&placement=sticky";

    // Related faults for internal linking
    List<RelatedFaultLink> relatedFaultLinks = faultsOpt.get().faults().stream()
        .filter(f -> !f.component().equals(fault.component()))
        .map(f -> new RelatedFaultLink(
            f.component(),
            "/verdict/" + canonicalBrandSlug + "/" + canonicalModelSlug + "/" + toFaultSlug(f.component()),
            f.repairCost()))
        .limit(3)
        .toList();

    String ogImage = baseUrl + "/og-image.png";

    // 7. Breadcrumbs (Clickable)
    List<Breadcrumb> breadcrumbs = List.of(
        new Breadcrumb("Home", "/"),
        new Breadcrumb("Models", "/models"),
        new Breadcrumb(car.brand(), "/models/" + canonicalBrandSlug),
        new Breadcrumb(car.model(), "/models/" + canonicalBrandSlug + "/" + canonicalModelSlug),
        new Breadcrumb(canonicalFaultSlug, "#"));

    modelMap.addAttribute("car", car);
    modelMap.addAttribute("fault", fault);
    modelMap.addAttribute("details", profile);
    modelMap.addAttribute("schemaJson", schemaJson);
    modelMap.addAttribute("metaDescription", metaDescription);
    modelMap.addAttribute("canonicalUrl", canonicalUrl);
    modelMap.addAttribute("leadUrlInline", leadUrlInline);
    modelMap.addAttribute("leadUrlSticky", leadUrlSticky);
    modelMap.addAttribute("relatedFaultLinks", relatedFaultLinks);
    modelMap.addAttribute("canonicalBrandSlug", canonicalBrandSlug);
    modelMap.addAttribute("canonicalModelSlug", canonicalModelSlug);
    modelMap.addAttribute("brandDirectoryUrl", "/models/" + canonicalBrandSlug);
    modelMap.addAttribute("modelDirectoryUrl", "/models/" + canonicalBrandSlug + "/" + canonicalModelSlug);
    modelMap.addAttribute("mileageBaseUrl", "/verdict/" + canonicalBrandSlug + "/" + canonicalModelSlug);
    modelMap.addAttribute("shouldFixUrl", shouldFixUrl);
    modelMap.addAttribute("ogImage", ogImage);
    modelMap.addAttribute("breadcrumbs", breadcrumbs);
    modelMap.addAttribute("datasetVersion", datasetVersion);
    modelMap.addAttribute("waitlistMode", routingConfig.isApprovalPending());
    modelMap.addAttribute("noindex", !SeoIndexPolicy.isIndexableFault(car, fault));
    modelMap.addAttribute("decisionSupport", SeoDecisionSupport.forFault(car, fault,
        marketOpt.orElse(null)));

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

    // 1. Find Car Model (supports loose slug matching), then normalize to canonical path.
    Optional<CarModel> carOpt = dataService.findCarBySlug(brand, model);
    if (carOpt.isEmpty()) {
      logger.warn("Car not found: {} / {}", brand, model);
      throw new ResourceNotFoundException("Car model not found");
    }
    CarModel car = carOpt.get();
    String canonicalBrandSlug = normalize(car.brand());
    String canonicalModelSlug = normalize(car.model());

    // 2. Mileage Bucketing + Canonical Slug Redirect (crawl budget + dedup)
    List<Integer> allowedBuckets = List.of(50000, 75000, 100000, 125000, 150000, 175000, 200000);
    int canonicalMileage = allowedBuckets.contains(mileage) ? mileage : findClosestBucket(mileage, allowedBuckets);
    if (!brand.equals(canonicalBrandSlug) || !model.equals(canonicalModelSlug) || mileage != canonicalMileage) {
      return permanentRedirect(baseUrl + "/verdict/" + canonicalBrandSlug + "/" + canonicalModelSlug + "/"
          + canonicalMileage + "-miles");
    }

    response.setHeader("Cache-Control", "public, max-age=86400");
    logger.info("Mileage request for {} / {} at {} miles", brand, model, mileage);

    // 2. Load Reliability & Market Data
    Optional<ModelReliability> reliabilityOpt = dataService.findReliabilityByModelId(car.id());
    Optional<ModelMarket> marketOpt = dataService.findMarketByModelId(car.id());
    String shouldFixUrl = SeoIndexPolicy.decisionPath(car, reliabilityOpt.orElse(null));

    if (reliabilityOpt.isEmpty() || marketOpt.isEmpty()) {
      logger.warn("Missing data for model: {}", car.id());
      throw new ResourceNotFoundException("Reliability or Market data not found for model");
    }

    ModelReliability reliability = reliabilityOpt.get();
    ModelMarket market = marketOpt.get();

    // 3. Build breadcrumbs
    List<Breadcrumb> breadcrumbs = List.of(
        new Breadcrumb("Home", "/"),
        new Breadcrumb(car.brand(), "/models/" + canonicalBrandSlug),
        new Breadcrumb(car.model(), "/models/" + canonicalBrandSlug + "/" + canonicalModelSlug),
        new Breadcrumb(String.format("%,d Miles", mileage), "#"));

    // 4. Build canonical URL
    String canonicalUrl = baseUrl + "/verdict/" + canonicalBrandSlug + "/" + canonicalModelSlug + "/" + mileage
        + "-miles";

    // 5. Meta description
    int estimatedValue = (int) Math.max(market.commonJunkValue(),
        market.jan2026AvgPrice() * Math.max(0.15, 1.0 - (mileage * 0.000003)));
    String metaDescription = String.format(
        "See whether a %s %s is still worth keeping at %,d miles. Compare expected lifespan, estimated value (~$%,d), and major repair risk before you fix or sell.",
        car.brand(), car.model(), mileage, estimatedValue);

    // FAQ rich results are no longer a useful search feature for this site type.
    String schemaJson = null;

    // Build Tracking URLs
    double lifespanPercent = Math.min(100.0, (double) mileage / reliability.lifespanMiles() * 100);
    String intent = lifespanPercent >= 50.0 ? "SELL" : "REPAIR";
    String verdictState = lifespanPercent >= 50.0 ? "TIME_BOMB" : "STABLE";
    String detailSlug = canonicalMileage + "-miles";
    String leadUrlInline = "/lead?page_type=pseo_mileage&intent=" + intent + "&verdict_state=" + verdictState
        + "&brand=" + canonicalBrandSlug + "&model=" + canonicalModelSlug + "&detail=" + detailSlug
        + "&placement=inline";
    String leadUrlSticky = "/lead?page_type=pseo_mileage&intent=" + intent + "&verdict_state=" + verdictState
        + "&brand=" + canonicalBrandSlug + "&model=" + canonicalModelSlug + "&detail=" + detailSlug
        + "&placement=sticky";

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
    modelMap.addAttribute("canonicalBrandSlug", canonicalBrandSlug);
    modelMap.addAttribute("canonicalModelSlug", canonicalModelSlug);
    modelMap.addAttribute("brandDirectoryUrl", "/models/" + canonicalBrandSlug);
    modelMap.addAttribute("modelDirectoryUrl", "/models/" + canonicalBrandSlug + "/" + canonicalModelSlug);
    modelMap.addAttribute("mileageBaseUrl", "/verdict/" + canonicalBrandSlug + "/" + canonicalModelSlug);
    modelMap.addAttribute("shouldFixUrl", shouldFixUrl);
    modelMap.addAttribute("datasetVersion", datasetVersion);
    modelMap.addAttribute("waitlistMode", routingConfig.isApprovalPending());
    modelMap.addAttribute("noindex", !SeoIndexPolicy.isIndexableMileage(car, mileage));
    modelMap.addAttribute("decisionSupport",
        SeoDecisionSupport.forMileage(car, mileage, reliability, market));

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

    modelMap.addAttribute("title", "Car Problems by Brand: Repair Costs and Fix-or-Sell Guides | AutoMoneyPit");
    modelMap.addAttribute("metaDescription",
        "Browse car brands to find common problems, repair costs, and fix-or-sell guides for specific models.");
    modelMap.addAttribute("canonicalUrl", baseUrl + "/models");
    modelMap.addAttribute("breadcrumbs", List.of("Models")); // Keep simple for directory for now
    modelMap.addAttribute("featuredItems", getGlobalPriorityDecisionLinks());
    modelMap.addAttribute("items", brands);
    return "pages/directory_list";
  }

  // --- View Helpers ---

  public record Breadcrumb(String label, String url) {
  }

  public record RelatedFaultLink(String component, String url, double repairCost) {
  }

  public record ModelHubLink(String label, String url) {
  }

  @GetMapping("/models/{brandSlug}")
  public Object listModels(@PathVariable("brandSlug") String brandSlug, Model modelMap, HttpServletResponse response) {
    response.setHeader("Cache-Control", "public, max-age=86400");
    List<CarModel> models = dataService.getModelsByBrand(brandSlug);

    if (models.isEmpty()) {
      throw new ResourceNotFoundException("Brand not found");
    }
    String canonicalBrandSlug = normalize(models.get(0).brand());
    if (!brandSlug.equals(canonicalBrandSlug)) {
      return permanentRedirect(baseUrl + "/models/" + canonicalBrandSlug);
    }
    String displayBrand = models.get(0).brand();

    List<java.util.Map.Entry<String, String>> modelLinks = models.stream()
        .map(c -> java.util.Map.entry(
            c.model(),
            "/models/" + canonicalBrandSlug + "/" + normalize(c.model())))
        .distinct()
        .toList();

    modelMap.addAttribute("title", displayBrand + " Problems by Model: Repair Costs and Ownership Risk");
    modelMap.addAttribute("metaDescription", "See " + displayBrand
        + " models with common problems, repair costs, and fix-or-sell guidance before you approve a big repair.");
    modelMap.addAttribute("canonicalUrl", baseUrl + "/models/" + canonicalBrandSlug);
    modelMap.addAttribute("breadcrumbs", List.of("Models", displayBrand));
    modelMap.addAttribute("featuredItems", getBrandPriorityDecisionLinks(canonicalBrandSlug));
    modelMap.addAttribute("items", modelLinks);
    return "pages/directory_list";
  }

  @GetMapping("/models/{brandSlug}/{modelSlug}")
  public Object listFaults(@PathVariable("brandSlug") String brandSlug, @PathVariable("modelSlug") String modelSlug,
      Model modelMap, HttpServletResponse response) {
    response.setHeader("Cache-Control", "public, max-age=86400");
    Optional<CarModel> carOpt = dataService.findCarBySlug(brandSlug, modelSlug);
    if (carOpt.isEmpty()) {
      throw new ResourceNotFoundException("Model not found");
    }
    CarModel car = carOpt.get();
    String canonicalBrandSlug = normalize(car.brand());
    String canonicalModelSlug = normalize(car.model());
    if (!brandSlug.equals(canonicalBrandSlug) || !modelSlug.equals(canonicalModelSlug)) {
      return permanentRedirect(baseUrl + "/models/" + canonicalBrandSlug + "/" + canonicalModelSlug);
    }

    Optional<MajorFaults> faultsOpt = dataService.findFaultsByModelId(car.id());
    Optional<ModelReliability> reliabilityOpt = dataService.findReliabilityByModelId(car.id());
    Optional<ModelMarket> marketOpt = dataService.findMarketByModelId(car.id());

    List<ModelHubLink> faultLinks;
    if (faultsOpt.isPresent()) {
      faultLinks = faultsOpt.get().faults().stream()
          .map(f -> {
            String slug = toFaultSlug(f.component());
            return new ModelHubLink(
                f.component() + " repair cost and fix-or-sell guide",
                "/verdict/" + canonicalBrandSlug + "/" + canonicalModelSlug + "/" + slug);
          })
          .toList();
    } else {
      // Fallback for models without specific faults
      faultLinks = List.of(
          new ModelHubLink(
              "Open The Fix-or-Sell Calculator",
              "/"));
    }

    int representativeYear = SeoIndexPolicy.representativeYear(car, reliabilityOpt.orElse(null));
    String shouldFixUrl = SeoIndexPolicy.decisionPath(car, reliabilityOpt.orElse(null));
    List<ModelHubLink> decisionPageLinks = buildDecisionPageLinks(car, reliabilityOpt.orElse(null),
        canonicalBrandSlug, canonicalModelSlug, representativeYear);
    String title = car.brand() + " " + car.model() + " Problems: Repair Costs and Should-You-Fix-It Guidance";
    String metaDescription;
    if (marketOpt.isPresent() && faultsOpt.isPresent() && !faultsOpt.get().faults().isEmpty()) {
      Fault topFault = faultsOpt.get().faults().stream()
          .max(java.util.Comparator.comparingDouble(Fault::repairCost))
          .orElse(faultsOpt.get().faults().get(0));
      metaDescription = "See common " + car.brand() + " " + car.model()
          + " problems, top repair costs like " + topFault.component() + " (~$"
          + String.format("%,.0f", topFault.repairCost()) + "), and whether owners should fix or sell before approving a major repair.";
    } else {
      metaDescription = "See common " + car.brand() + " " + car.model()
          + " problems, repair costs, and whether owners should fix or sell before approving a major repair.";
    }

    modelMap.addAttribute("title", title);
    modelMap.addAttribute("metaDescription", metaDescription);
    modelMap.addAttribute("canonicalUrl", baseUrl + "/models/" + canonicalBrandSlug + "/" + canonicalModelSlug);
    modelMap.addAttribute("breadcrumbs", List.of("Models", car.brand(), car.model()));
    modelMap.addAttribute("car", car);
    modelMap.addAttribute("faultLinks", faultLinks);
    modelMap.addAttribute("majorFaults", faultsOpt.orElse(null));
    modelMap.addAttribute("reliability", reliabilityOpt.orElse(null));
    modelMap.addAttribute("market", marketOpt.orElse(null));
    modelMap.addAttribute("representativeYear", representativeYear);
    modelMap.addAttribute("shouldFixUrl", shouldFixUrl);
    modelMap.addAttribute("decisionIndexable", SeoIndexPolicy.isIndexableDecision(car));
    modelMap.addAttribute("decisionPageLinks", decisionPageLinks);
    modelMap.addAttribute("datasetVersion", datasetVersion);
    return "pages/model_hub";
  }

  // --- Helper Methods ---

  private RedirectView permanentRedirect(String absoluteUrl) {
    RedirectView rv = new RedirectView(absoluteUrl);
    rv.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
    return rv;
  }

  private List<ModelHubLink> buildDecisionPageLinks(CarModel car, ModelReliability reliability,
      String canonicalBrandSlug, String canonicalModelSlug, int representativeYear) {
    if (!SeoIndexPolicy.isIndexableDecision(car)) {
      return List.of();
    }
    return List.of(new ModelHubLink(
        "Start with " + representativeYear + " " + car.brand() + " " + car.model() + " fix-or-sell page",
        SeoIndexPolicy.decisionPath(car, reliability)));
  }

  private int findClosestBucket(int mileage, List<Integer> allowedBuckets) {
    int closestBucket = allowedBuckets.get(0);
    int minDiff = Math.abs(mileage - closestBucket);
    for (int bucket : allowedBuckets) {
      int diff = Math.abs(mileage - bucket);
      if (diff < minDiff || (diff == minDiff && bucket > closestBucket)) {
        minDiff = diff;
        closestBucket = bucket;
      }
    }
    return closestBucket;
  }

  private List<java.util.Map.Entry<String, String>> getGlobalPriorityDecisionLinks() {
    return List.of(
        java.util.Map.entry("2014 Toyota Camry fix-or-sell page", "/should-i-fix/2014-toyota-camry"),
        java.util.Map.entry("2014 Nissan Altima fix-or-sell page", "/should-i-fix/2014-nissan-altima"),
        java.util.Map.entry("2015 Honda Accord fix-or-sell page", "/should-i-fix/2015-honda-accord"),
        java.util.Map.entry("2013 Honda CR-V fix-or-sell page", "/should-i-fix/2013-honda-cr-v"),
        java.util.Map.entry("2015 Mazda CX-5 fix-or-sell page", "/should-i-fix/2015-mazda-cx-5"));
  }

  private List<java.util.Map.Entry<String, String>> getBrandPriorityDecisionLinks(String canonicalBrandSlug) {
    return switch (canonicalBrandSlug) {
      case "toyota" -> List.of(
          java.util.Map.entry("Start with 2014 Toyota Camry", "/should-i-fix/2014-toyota-camry"),
          java.util.Map.entry("Check 2014 Toyota Corolla", "/should-i-fix/2014-toyota-corolla"));
      case "nissan" -> List.of(
          java.util.Map.entry("Start with 2014 Nissan Altima", "/should-i-fix/2014-nissan-altima"));
      case "honda" -> List.of(
          java.util.Map.entry("Start with 2015 Honda Accord", "/should-i-fix/2015-honda-accord"),
          java.util.Map.entry("Check 2013 Honda CR-V", "/should-i-fix/2013-honda-cr-v"));
      case "mazda" -> List.of(
          java.util.Map.entry("Start with 2015 Mazda CX-5", "/should-i-fix/2015-mazda-cx-5"));
      case "ford" -> List.of(
          java.util.Map.entry("Start with 2014 Ford Escape", "/should-i-fix/2014-ford-escape"));
      default -> List.of();
    };
  }

  private String toFaultSlug(String component) {
    return SeoIndexPolicy.faultSlug(component);
  }

  private String normalize(String input) {
    if (input == null)
      return "";
    return input.toLowerCase()
        .replaceAll("[^a-z0-9]+", "-")
        .replaceAll("^-|-$", "");
  }

  private String generateSchema(CarModel car, Fault fault, ProfileViewModel profile,
      String brandSlug, String modelSlug, String faultSlug) {
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
