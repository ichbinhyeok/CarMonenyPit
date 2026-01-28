package com.carmoneypit.engine.web;

import com.carmoneypit.engine.service.CarDataService;
import com.carmoneypit.engine.service.CarDataService.*;
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

  public PSeoController(CarDataService dataService) {
    this.dataService = dataService;
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
    String schemaJson = generateSchema(car, fault, profile);
    String metaDescription = String.format(
        "Actuarial verdict for %s %s owners facing %s failure. Repair cost: $%,.0f vs Market Value: $%,d. Don't fix it until you read this.",
        car.brand(), car.model(), fault.component(), fault.repairCost(),
        profile != null ? profile.market().jan2026AvgPrice() : 0);
    String canonicalUrl = String.format("https://carmoneypit.com/verdict/%s/%s/%s",
        brand, model, faultSlug);

    modelMap.addAttribute("car", car);
    modelMap.addAttribute("fault", fault);
    modelMap.addAttribute("details", profile);
    modelMap.addAttribute("schemaJson", schemaJson);
    modelMap.addAttribute("metaDescription", metaDescription);
    modelMap.addAttribute("canonicalUrl", canonicalUrl);

    return "pseo_landing";
  }

  // --- Directory Navigation (Silo Structure) ---

  @GetMapping("/models")
  public String listBrands(Model modelMap) {
    List<java.util.Map.Entry<String, String>> brands = dataService.getAllBrands().stream()
        .map(brand -> java.util.Map.entry(brand, "/models/" + normalize(brand)))
        .toList();

    modelMap.addAttribute("title", "Car Brands");
    modelMap.addAttribute("breadcrumbs", List.of("Models"));
    modelMap.addAttribute("items", brands);
    return "pages/directory_list";
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

  private String generateSchema(CarModel car, Fault fault, ProfileViewModel profile) {
    long marketValue = profile != null ? profile.market().jan2026AvgPrice() : 0;

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
            fault.verdictImplication().replace("\"", "\\\""));
  }

  // --- View Model (for template) ---

  public record ProfileViewModel(
      CarModel car,
      ModelReliability reliability,
      ModelMarket market) {
  }
}
