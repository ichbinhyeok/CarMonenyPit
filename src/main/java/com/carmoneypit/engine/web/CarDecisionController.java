package com.carmoneypit.engine.web;

<<<<<<< HEAD
import com.carmoneypit.engine.service.CarDataService;
import com.carmoneypit.engine.api.InputModels.CarBrand;
=======
>>>>>>> 3f08322 (Enhance pSEO content, Refactor to JSON-based data, and Fix UI glitches)
import com.carmoneypit.engine.api.InputModels.EngineInput;
import com.carmoneypit.engine.api.InputModels.SimulationControls;
import com.carmoneypit.engine.api.InputModels.VehicleType;
import com.carmoneypit.engine.api.InputModels.FailureSeverity;
import com.carmoneypit.engine.api.InputModels.MobilityStatus;
import com.carmoneypit.engine.api.InputModels.HassleTolerance;
import com.carmoneypit.engine.api.OutputModels.VerdictResult;
import com.carmoneypit.engine.core.DecisionEngine;
import com.carmoneypit.engine.core.ValuationService;
import com.carmoneypit.engine.service.CarDataService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Controller
public class CarDecisionController {

        private static final Logger log = LoggerFactory.getLogger(CarDecisionController.class);

        private final DecisionEngine decisionEngine;
        private final VerdictPresenter presenter;
<<<<<<< HEAD
        private final CarDataService carDataService; // Add dependency
=======
        private final ValuationService valuationService;
        private final CarDataService carDataService;
>>>>>>> 3f08322 (Enhance pSEO content, Refactor to JSON-based data, and Fix UI glitches)

        public CarDecisionController(DecisionEngine decisionEngine, VerdictPresenter presenter,
                        ValuationService valuationService, CarDataService carDataService) {
                this.decisionEngine = decisionEngine;
                this.presenter = presenter;
                this.valuationService = valuationService;
                this.carDataService = carDataService;
        }

        @GetMapping("/")
        public String index(
                        @RequestParam(value = "brand", required = false) String brandParam,
                        @RequestParam(value = "model", required = false) String modelParam,
                        @RequestParam(value = "year", required = false) Integer yearParam,
                        @RequestParam(value = "repairQuoteUsd", required = false) Long repairQuoteParam,
                        @RequestParam(value = "pSEO", required = false) Boolean fromPSEO,
                        Model model) {

                // Provide all brands from JSON data to the template
                List<String> allBrands = carDataService.getAllBrands();
                model.addAttribute("allBrands", allBrands);

                if (brandParam != null && fromPSEO != null && fromPSEO) {
                        model.addAttribute("prefillBrand", brandParam);
                        model.addAttribute("prefillModel", modelParam);
                        model.addAttribute("prefillYear", yearParam);
                        model.addAttribute("prefillQuote", repairQuoteParam);
                }
                return "index"; // Renders src/main/jte/index.jte
        }

        @GetMapping("/api/models")
        public String getModelsByBrand(@RequestParam("brand") String brand, Model model) {
                var models = carDataService.getModelsByBrand(brand); // Use carDataService directly
                model.addAttribute("models", models);
                return "fragments/model_options";
        }

        // ========== pSEO ROUTES ==========
        // Format: /should-i-fix/{year}-{brand}-{model}
        // Example: /should-i-fix/2018-toyota-camry
        @GetMapping("/should-i-fix/{slug}")
        public String pSeoLanding(@PathVariable("slug") String slug, Model model) {
                // Parse slug: "2018-toyota-camry" -> year=2018, brand=TOYOTA, model=Camry
                String[] parts = slug.split("-", 3);
                if (parts.length < 2) {
                        return "redirect:/";
                }

                try {
                        int year = Integer.parseInt(parts[0]);
<<<<<<< HEAD
                        String brandSlug = parts[1].toUpperCase();
                        String modelSlug = parts.length > 2 ? parts[2] : "";
=======
                        String brandSlug = parts[1].toUpperCase().replace(" ", "_");
                        String modelSlug = parts.length > 2 ? formatModelName(parts[2]) : "";
>>>>>>> 3f08322 (Enhance pSEO content, Refactor to JSON-based data, and Fix UI glitches)

                        // Validate brand exists in loaded data
                        if (!valuationService.isValidBrand(brandSlug)) {
                                // Try alternate formats
                                if (!valuationService.isValidBrand(brandSlug.replace("_", ""))) {
                                        log.warn("Unknown brand in pSEO route: {}", brandSlug);
                                        return "redirect:/";
                                }
                        }

<<<<<<< HEAD
                        if (brand == null) {
                                return "redirect:/";
                        }

                        // Find matching CarModel to get ID
                        // We iterate to find a model that matches the slug using fuzzy matching
                        // (normalization)
                        String normalizedSlugModel = modelSlug.toLowerCase().replaceAll("[^a-z0-9]", "");

                        var carModelOpt = carDataService.getAllModels().stream()
                                        .filter(m -> m.brand().equalsIgnoreCase(brand.name()))
                                        .filter(m -> m.model().toLowerCase().replaceAll("[^a-z0-9]", "")
                                                        .equals(normalizedSlugModel))
                                        .findFirst();

                        if (carModelOpt.isPresent()) {
                                var carModel = carModelOpt.get();
                                // If we found a model, use its official display name instead of the slug
                                modelSlug = carModel.model();

                                // Fetch specific faults
                                var faultsOpt = carDataService.findFaultsByModelId(carModel.id());
                                if (faultsOpt.isPresent()) {
                                        model.addAttribute("majorFaults", faultsOpt.get());
                                }
                        }

=======
>>>>>>> 3f08322 (Enhance pSEO content, Refactor to JSON-based data, and Fix UI glitches)
                        // SEO Meta - Optimized for CTR
                        String seoTitle = String.format("%d %s %s: Fix or Sell? [Free 2026 Calculator]",
                                        year, brandSlug, modelSlug);
                        String seoDescription = String.format(
                                        "Got a repair quote on your %d %s %s? Our free calculator tells you if fixing is worth it—based on NADA/KBB data. Takes 30 seconds.",
                                        year, brandSlug, modelSlug);

                        model.addAttribute("seoTitle", seoTitle);
                        model.addAttribute("seoDescription", seoDescription);
                        model.addAttribute("prefillYear", year);
                        model.addAttribute("prefillBrand", brandSlug);
                        model.addAttribute("prefillModel", modelSlug);
                        model.addAttribute("isPseoPage", true);
                        model.addAttribute("pseoSlug", slug);

                        return "pseo";
                } catch (NumberFormatException e) {
                        return "redirect:/";
                }
        }

        private String formatModelName(String slug) {
                // "camry" -> "Camry", "cr-v" -> "CR-V", "f-150" -> "F-150"
                if (slug == null || slug.isEmpty())
                        return "";
                return slug.substring(0, 1).toUpperCase() + slug.substring(1).replace("-", " ");
        }

        @GetMapping(value = "/favicon.ico", produces = "image/x-icon")
        @ResponseBody
        public Resource getFaviconIco() {
                return new ClassPathResource("static/favicon.ico");
        }

        @GetMapping(value = "/favicon.png", produces = MediaType.IMAGE_PNG_VALUE)
        @ResponseBody
        public Resource getFaviconPng() {
                return new ClassPathResource("static/favicon.png");
        }

        @PostMapping("/analyze")
        public String analyzeLoading(
                        @RequestParam("brand") String brand, // Changed from CarBrand to String
                        @RequestParam(value = "model", required = false) String carModel,
                        @RequestParam(value = "year", defaultValue = "2018") int year,
                        @RequestParam(value = "vehicleType", required = false) VehicleType vehicleType,
                        @RequestParam("mileage") long mileage,
                        @RequestParam(value = "repairQuoteUsd", required = false) Long repairQuoteUsd,
                        @RequestParam(value = "isQuoteMissing", defaultValue = "false") boolean isQuoteMissing,
                        @RequestParam(value = "currentValueUsd", required = false) Long currentValueUsd,
                        Model model) {

                VehicleType effectiveType = (vehicleType != null) ? vehicleType : VehicleType.SEDAN;

                long effectiveValue = (currentValueUsd != null && currentValueUsd > 0)
                                ? currentValueUsd
                                : valuationService.estimateValue(brand, carModel, effectiveType, year, mileage);
                boolean isEstimated = (currentValueUsd == null || currentValueUsd <= 0);

                long effectiveRepairQuote = (repairQuoteUsd != null && repairQuoteUsd > 0)
                                ? repairQuoteUsd
                                : valuationService.estimateRepairCost(brand, effectiveType, mileage);

                model.addAttribute("brand", brand);
                model.addAttribute("modelName", carModel != null ? carModel : "Other");
                model.addAttribute("year", year);
                model.addAttribute("vehicleType", effectiveType);
                model.addAttribute("mileage", mileage);
                model.addAttribute("repairQuoteUsd", effectiveRepairQuote);
                model.addAttribute("currentValueUsd", effectiveValue);
                model.addAttribute("isValueEstimated", isEstimated);
                model.addAttribute("isQuoteEstimated", isQuoteMissing);
                return "fragments/loading";
        }

        @GetMapping("/verdict")
        public String shareVerdict(
                        @RequestParam("token") String token,
                        Model model,
                        jakarta.servlet.http.HttpServletResponse response) {

                response.setHeader("X-Robots-Tag", "noindex, nofollow");

                try {
                        EngineInput input = presenter.decodeToken(token);
                        VerdictResult result = decisionEngine.evaluate(input);

                        SimulationControls sharedControls = new SimulationControls(
                                        FailureSeverity.GENERAL_UNKNOWN,
                                        MobilityStatus.DRIVABLE,
                                        HassleTolerance.NEUTRAL,
                                        null);

                        presenter.populateModel(model, input, result, sharedControls, "RECEIPT", token);
                        return "result";
                } catch (Exception e) {
                        log.error("Invalid token provided: {}", token);
                        return "redirect:/";
                }
        }

        @RequestMapping(value = "/analyze-final", method = { RequestMethod.GET, RequestMethod.POST })
        public String analyzeFinal(
                        @RequestParam("brand") String brand, // Changed from CarBrand to String
                        @RequestParam(value = "model", required = false) String carModel,
                        @RequestParam(value = "year", defaultValue = "2018") int year,
                        @RequestParam(value = "vehicleType", required = false) VehicleType vehicleType,
                        @RequestParam("mileage") long mileage,
                        @RequestParam(value = "repairQuoteUsd", required = false) Long repairQuoteUsd,
                        @RequestParam(value = "isQuoteMissing", defaultValue = "false") boolean isQuoteMissing,
                        @RequestParam(value = "currentValueUsd", required = false) Long currentValueUsd,
                        @RequestParam(value = "isValueEstimated", defaultValue = "false") boolean isValueEstimated,
                        @RequestParam(value = "isQuoteEstimated", defaultValue = "false") boolean isQuoteEstimated,
                        Model model,
                        jakarta.servlet.http.HttpServletResponse response) {

                VehicleType effectiveType = (vehicleType != null) ? vehicleType : VehicleType.SEDAN;

                long effectiveValue = (currentValueUsd != null && currentValueUsd > 0)
                                ? currentValueUsd
                                : valuationService.estimateValue(brand, carModel, effectiveType, year, mileage);
                boolean finalIsEstimated = isValueEstimated || (currentValueUsd == null || currentValueUsd <= 0);

                long effectiveRepairQuote = (repairQuoteUsd != null && repairQuoteUsd > 0)
                                ? repairQuoteUsd
                                : valuationService.estimateRepairCost(brand, effectiveType, mileage);
                boolean finalIsQuoteEstimated = isQuoteEstimated || isQuoteMissing
                                || (repairQuoteUsd == null || repairQuoteUsd <= 0);

                EngineInput input = new EngineInput(carModel != null ? carModel : "Other", effectiveType, brand,
                                year,
                                mileage,
                                effectiveRepairQuote, effectiveValue,
                                finalIsQuoteEstimated, finalIsEstimated);

                String shareToken = presenter.encodeToken(input);
                response.setHeader("HX-Location", "/report?token=" + shareToken);
                return "";
        }

        @GetMapping("/report")
        public String getReport(
                        @RequestParam("token") String token,
                        Model model,
                        jakarta.servlet.http.HttpServletResponse response) {

                response.setHeader("X-Robots-Tag", "noindex, nofollow");

                try {
                        EngineInput input = presenter.decodeToken(token);
                        VerdictResult result = decisionEngine.evaluate(input);

                        SimulationControls defaultControls = new SimulationControls(
                                        FailureSeverity.GENERAL_UNKNOWN,
                                        MobilityStatus.DRIVABLE,
                                        HassleTolerance.NEUTRAL,
                                        null);

                        presenter.populateModel(model, input, result, defaultControls, "OWNER", token);
                        return "result";
                } catch (Exception e) {
                        log.error("Invalid report token: {}", token);
                        return "redirect:/";
                }
        }

        @PostMapping("/simulate")
        public String simulate(
                        @RequestParam(value = "model", required = false) String carModel,
                        @RequestParam(value = "year", defaultValue = "2018") int year,
                        @RequestParam("vehicleType") VehicleType vehicleType,
                        @RequestParam("brand") String brand, // Changed from CarBrand to String
                        @RequestParam("mileage") long mileage,
                        @RequestParam("repairQuoteUsd") long repairQuoteUsd,
                        @RequestParam("currentValueUsd") long currentValueUsd,
                        @RequestParam("failureSeverity") FailureSeverity failureSeverity,
                        @RequestParam("mobilityStatus") MobilityStatus mobilityStatus,
                        @RequestParam("hassleTolerance") HassleTolerance hassleTolerance,
                        @RequestParam(value = "retentionHorizon", required = false) com.carmoneypit.engine.api.InputModels.RetentionHorizon retentionHorizon,
                        @RequestParam(value = "isValueEstimated", defaultValue = "false") boolean isValueEstimated,
                        Model model) {
                EngineInput input = new EngineInput(carModel != null ? carModel : "Other", vehicleType, brand, year,
                                mileage,
                                repairQuoteUsd, currentValueUsd,
                                false, isValueEstimated);
                SimulationControls controls = new SimulationControls(failureSeverity, mobilityStatus, hassleTolerance,
                                retentionHorizon);

                VerdictResult result = decisionEngine.simulate(input, controls);

                presenter.populateModel(model, input, result, controls, "OWNER", null);

                return "simulation_response";
        }
}
