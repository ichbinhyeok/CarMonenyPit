package com.carmoneypit.engine.web;

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
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.view.RedirectView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Controller
public class CarDecisionController {

        private static final Logger log = LoggerFactory.getLogger(CarDecisionController.class);

        private final DecisionEngine decisionEngine;
        private final VerdictPresenter presenter;
        private final ValuationService valuationService;
        private final CarDataService carDataService;

        @Value("${app.baseUrl:https://automoneypit.com}")
        private String baseUrl;

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

                // Prefill form if brand parameter is provided (no longer requires pSEO=true)
                if (brandParam != null) {
                        model.addAttribute("prefillBrand", brandParam);
                        model.addAttribute("prefillModel", modelParam);
                        model.addAttribute("prefillQuote", repairQuoteParam);
                }
                model.addAttribute("baseUrl", baseUrl);
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
        public Object pSeoLanding(@PathVariable("slug") String slug, Model model) {
                // Parse slug: "2018-toyota-camry" -> year=2018, brand=TOYOTA, model=Camry
                String[] parts = slug.split("-", 3);
                if (parts.length < 2) {
                        return "redirect:/";
                }

                try {
                        int year = Integer.parseInt(parts[0]);
                        String brandSlug = parts[1].toUpperCase().replace(" ", "_");
                        String modelSlug = parts.length > 2 ? formatModelName(parts[2]) : "";
                        String canonicalBrandSlug = normalizeSlugSegment(brandSlug);
                        String canonicalModelSlug = normalizeSlugSegment(modelSlug);
                        String canonicalSlug = null;
                        String quickSignal = "Run the numbers";
                        String quickAnswer = "The right answer depends on your actual quote, current value, and mileage.";
                        Integer marketValue = null;
                        Integer lifespanMiles = null;
                        String primaryFaultName = null;
                        Integer primaryFaultCost = null;
                        Integer primaryFaultMileage = null;
                        String primaryFaultUrl = null;
                        String mileageVerdictUrl = null;
                        String modelDirectoryUrl = null;
                        String brandDirectoryUrl = null;

                        // Validate brand exists in loaded data
                        if (!valuationService.isValidBrand(brandSlug)) {
                                // Try alternate formats
                                if (!valuationService.isValidBrand(brandSlug.replace("_", ""))) {
                                        log.warn("Unknown brand in pSEO route: {}", brandSlug);
                                        return "redirect:/";
                                }
                        }

                        // Find matching CarModel to get ID
                        // We iterate to find a model that matches the slug using fuzzy matching
                        // (normalization)
                        String normalizedSlugModel = modelSlug.toLowerCase().replaceAll("[^a-z0-9]", "");
                        final String lookupBrand = brandSlug;

                        var carModelOpt = carDataService.getAllModels().stream()
                                        .filter(m -> m.brand().equalsIgnoreCase(lookupBrand))
                                        .filter(m -> m.model().toLowerCase().replaceAll("[^a-z0-9]", "")
                                                        .equals(normalizedSlugModel))
                                        .findFirst();

                        if (carModelOpt.isPresent()) {
                                var carModel = carModelOpt.get();
                                // If we found a model, use its official display name instead of the slug
                                brandSlug = carModel.brand();
                                modelSlug = carModel.model();
                                canonicalBrandSlug = normalizeSlugSegment(carModel.brand());
                                canonicalModelSlug = normalizeSlugSegment(carModel.model());
                                canonicalSlug = year + "-" + canonicalBrandSlug + "-"
                                                + canonicalModelSlug;
                                brandDirectoryUrl = baseUrl + "/models/" + canonicalBrandSlug;
                                modelDirectoryUrl = baseUrl + "/models/" + canonicalBrandSlug + "/" + canonicalModelSlug;
                                mileageVerdictUrl = baseUrl + "/verdict/" + canonicalBrandSlug + "/" + canonicalModelSlug
                                                + "/100000-miles";

                                if (!slug.equals(canonicalSlug)) {
                                        RedirectView rv = new RedirectView(baseUrl + "/should-i-fix/" + canonicalSlug);
                                        rv.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
                                        return rv;
                                }

                                // Fetch specific faults
                                var faultsOpt = carDataService.findFaultsByModelId(carModel.id());
                                if (faultsOpt.isPresent()) {
                                        model.addAttribute("majorFaults", faultsOpt.get());
                                        var topFault = faultsOpt.get().faults().stream()
                                                        .max(java.util.Comparator.comparingDouble(f -> f.repairCost()));
                                        if (topFault.isPresent()) {
                                                primaryFaultName = topFault.get().component();
                                                primaryFaultCost = (int) Math.round(topFault.get().repairCost());
                                                primaryFaultMileage = topFault.get().avgFailureMileage() > 0
                                                                ? topFault.get().avgFailureMileage()
                                                                : null;
                                                primaryFaultUrl = baseUrl + "/verdict/" + canonicalBrandSlug + "/"
                                                                + canonicalModelSlug + "/"
                                                                + normalizeSlugSegment(topFault.get().component());
                                        }
                                }

                                var reliabilityOpt = carDataService.findReliabilityByModelId(carModel.id());
                                if (reliabilityOpt.isPresent()) {
                                        lifespanMiles = reliabilityOpt.get().lifespanMiles();
                                }

                                var marketOpt = carDataService.findMarketByModelId(carModel.id());
                                if (marketOpt.isPresent()) {
                                        marketValue = marketOpt.get().jan2026AvgPrice();
                                }

                                quickSignal = buildQuickSignal(marketValue, primaryFaultCost, lifespanMiles, year);
                                quickAnswer = buildQuickAnswer(carModel.brand(), carModel.model(), marketValue,
                                                primaryFaultName, primaryFaultCost, primaryFaultMileage, lifespanMiles,
                                                year);
                        }
                        // SEO Meta - Optimized for CTR and direct decision intent
                        String seoTitle = String.format("Should I Fix or Sell My %d %s %s?",
                                        year, brandSlug, modelSlug);
                        String seoDescription;
                        if (marketValue != null && primaryFaultName != null && primaryFaultCost != null) {
                                seoDescription = String.format(
                                                "See typical value ($%,d), expected lifespan, and %s repair risk (~$%,d) before you approve a big repair on your %d %s %s.",
                                                marketValue, primaryFaultName, primaryFaultCost, year, brandSlug, modelSlug);
                        } else {
                                seoDescription = String.format(
                                                "See whether your next repair is worth it. Compare value, lifespan, and repair risk before you fix or sell your %d %s %s.",
                                                year, brandSlug, modelSlug);
                        }

                        model.addAttribute("seoTitle", seoTitle);
                        model.addAttribute("seoDescription", seoDescription);
                        model.addAttribute("prefillYear", year);
                        model.addAttribute("prefillBrand", brandSlug);
                        model.addAttribute("prefillModel", modelSlug);
                        model.addAttribute("prefillBrandSlug", canonicalBrandSlug);
                        model.addAttribute("prefillModelSlug", canonicalModelSlug);
                        model.addAttribute("isPseoPage", true);
                        model.addAttribute("pseoSlug", canonicalSlug != null ? canonicalSlug : slug);
                        model.addAttribute("canonicalUrl", baseUrl + "/should-i-fix/" + (canonicalSlug != null ? canonicalSlug : slug));
                        model.addAttribute("quickSignal", quickSignal);
                        model.addAttribute("quickAnswer", quickAnswer);
                        model.addAttribute("marketValue", marketValue);
                        model.addAttribute("lifespanMiles", lifespanMiles);
                        model.addAttribute("primaryFaultName", primaryFaultName);
                        model.addAttribute("primaryFaultCost", primaryFaultCost);
                        model.addAttribute("primaryFaultMileage", primaryFaultMileage);
                        model.addAttribute("primaryFaultUrl", primaryFaultUrl);
                        model.addAttribute("mileageVerdictUrl", mileageVerdictUrl);
                        model.addAttribute("modelDirectoryUrl", modelDirectoryUrl);
                        model.addAttribute("brandDirectoryUrl", brandDirectoryUrl);

                        return "pseo";
                } catch (NumberFormatException e) {
                        return "redirect:/";
                }
        }

        private String buildQuickSignal(Integer marketValue, Integer primaryFaultCost, Integer lifespanMiles, int year) {
                if (marketValue != null && primaryFaultCost != null && marketValue > 0) {
                        double repairToValue = (double) primaryFaultCost / marketValue;
                        if (repairToValue >= 0.35) {
                                return "Likely sell zone";
                        }
                        if (repairToValue >= 0.2) {
                                return "Borderline decision";
                        }
                        return "Usually worth fixing";
                }

                if (lifespanMiles != null && year <= 2018) {
                        return "Mileage-sensitive call";
                }

                return "Quote decides";
        }

        private String buildQuickAnswer(String brand, String model, Integer marketValue, String primaryFaultName,
                        Integer primaryFaultCost, Integer primaryFaultMileage, Integer lifespanMiles, int year) {
                String vehicle = year + " " + brand + " " + model;
                if (marketValue != null && primaryFaultCost != null && primaryFaultName != null && marketValue > 0) {
                        double repairToValue = (double) primaryFaultCost / marketValue;
                        String ratioText = String.format("%.0f", repairToValue * 100);
                        String failureMileageText = primaryFaultMileage != null
                                        ? " The priciest known issue in our data is " + primaryFaultName
                                                        + ", which tends to show up around "
                                                        + String.format("%,d", primaryFaultMileage) + " miles."
                                        : " The priciest known issue in our data is " + primaryFaultName + ".";

                        if (repairToValue >= 0.35) {
                                return vehicle + " enters real fix-or-sell territory when a major quote approaches $"
                                                + String.format("%,d", primaryFaultCost) + ", or about " + ratioText
                                                + "% of a typical $" + String.format("%,d", marketValue)
                                                + " market value." + failureMileageText
                                                + " If your quote is anywhere near that level, selling deserves a serious look.";
                        }

                        if (repairToValue >= 0.2) {
                                return vehicle + " often becomes a borderline decision once major repairs climb toward $"
                                                + String.format("%,d", primaryFaultCost) + ", roughly " + ratioText
                                                + "% of current value." + failureMileageText
                                                + " This is the range where mileage and repeat-failure risk usually decide the answer.";
                        }

                        return vehicle + " is usually still worth fixing when the repair is modest relative to its typical $"
                                        + String.format("%,d", marketValue) + " market value." + failureMileageText
                                        + " The main question is whether your actual quote stays well below that known failure ceiling.";
                }

                if (lifespanMiles != null) {
                        return vehicle + " looks like a mileage-sensitive call. Our dataset puts expected lifespan around "
                                        + String.format("%,d", lifespanMiles)
                                        + " miles, so your actual quote and where the car sits on that curve matter more than generic advice.";
                }

                return vehicle
                                + " should be judged by quote-versus-value, not instinct. Use the prefilled calculator below to see whether the next repair still makes financial sense.";
        }

        private String formatModelName(String slug) {
                // "camry" -> "Camry", "cr-v" -> "CR-V", "f-150" -> "F-150"
                if (slug == null || slug.isEmpty())
                        return "";
                return slug.substring(0, 1).toUpperCase() + slug.substring(1).replace("-", " ");
        }

        private String normalizeSlugSegment(String input) {
                if (input == null) {
                        return "";
                }
                return input.toLowerCase()
                                .replaceAll("[^a-z0-9]+", "-")
                                .replaceAll("^-|-$", "");
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
