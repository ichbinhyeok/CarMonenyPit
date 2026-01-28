package com.carmoneypit.engine.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class PSeoController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(PSeoController.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<CarModelStats> carModels;
    private List<MajorFaultsData> majorFaults;

    // Data Records
    public record CarModelStats(
            String id, String brand, String model, String generation, int startYear, int endYear,
            double avgAnnualRepairCost, int reliabilityScore, double depreciationRate) {
    }

    public record MajorFaultsData(String model_id_ref, List<Fault> faults) {
    }

    public record Fault(String component, String symptoms, double repairCost, String verdictImplication) {
    }

    @PostConstruct
    public void init() throws IOException {
        InputStream modelsStream = getClass().getResourceAsStream("/data/car_models_top30.json");
        InputStream faultsStream = getClass().getResourceAsStream("/data/major_faults.json");

        if (modelsStream != null) {
            carModels = objectMapper.readValue(modelsStream, new TypeReference<List<CarModelStats>>() {
            });
        }
        if (faultsStream != null) {
            majorFaults = objectMapper.readValue(faultsStream, new TypeReference<List<MajorFaultsData>>() {
            });
        }
    }

    @GetMapping("/verdict/{brand}/{model}/{faultSlug}")
    public String showVerdict(@PathVariable String brand, @PathVariable String model, @PathVariable String faultSlug,
            Model modelMap) {

        logger.info("Request for {} / {} / {}", brand, model, faultSlug);

        // 1. Find the Car Model (Robust Matching)
        // Normalize: "F-150" -> "f150", "f-150" -> "f150", "CR-V" -> "crv"
        Optional<CarModelStats> carModelOpt = carModels.stream()
                .filter(c -> normalize(c.brand()).equals(normalize(brand)) &&
                        normalize(c.model()).equals(normalize(model)))
                .findFirst();

        if (carModelOpt.isEmpty()) {
            logger.info("Car Found FAILED for {} / {}", normalize(brand), normalize(model));
            return "redirect:/"; // Soft fallback
        }
        CarModelStats carStats = carModelOpt.get();
        logger.info("Car Found ID: {}", carStats.id());

        // 2. Find the Fault Data
        Optional<MajorFaultsData> faultsDataOpt = majorFaults.stream()
                .filter(f -> f.model_id_ref().equals(carStats.id()))
                .findFirst();

        Fault specificFault = null;
        if (faultsDataOpt.isPresent()) {
            // Keyword matching: "cam-phasers" -> "cam phasers"
            String targetKeyword = faultSlug.replace("-", " ").toLowerCase();

            specificFault = faultsDataOpt.get().faults().stream()
                    .filter(f -> {
                        String comp = f.component().toLowerCase();
                        boolean match = comp.contains(targetKeyword) || targetKeyword.contains(comp);
                        return match;
                    })
                    .findFirst()
                    .orElse(null);
        }

        if (specificFault == null) {
            logger.info("Fault Match FAILED");
            return "redirect:/";
        }

        // 3. Populate Model
        modelMap.addAttribute("car", carStats);
        modelMap.addAttribute("fault", specificFault);

        // 4. Generate Schema Markup (JSON-LD)
        String schema = generateFaqSchema(carStats, specificFault);
        modelMap.addAttribute("schemaJson", schema);

        return "pseo_landing";
    }

    private String normalize(String input) {
        return input.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    private String generateFaqSchema(CarModelStats car, Fault fault) {
        return """
                {
                  "@context": "https://schema.org",
                  "@type": "FAQPage",
                  "mainEntity": [{
                    "@type": "Question",
                    "name": "How much does it cost to fix %s %s on a %s %s?",
                    "acceptedAnswer": {
                      "@type": "Answer",
                      "text": "The average repair cost for the %s in a %s %s is $%s. However, if the car has high mileage, the total liability may exceed the vehicle's value."
                    }
                  }, {
                    "@type": "Question",
                    "name": "Is it worth fixing the %s on my %s %s?",
                    "acceptedAnswer": {
                      "@type": "Answer",
                      "text": "%s Our actuarial analysis suggests evaluating the Regret Score before authorized repairs."
                    }
                  }]
                }
                """
                .formatted(
                        fault.component(), car.model(), car.brand(), car.model(), // Q1
                        fault.component(), car.brand(), car.model(), String.format("%,.0f", fault.repairCost()), // A1
                        fault.component(), car.brand(), car.model(), // Q2
                        fault.verdictImplication() // A2
                );
    }
}
