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

        private record ShouldFixCopy(
                        String heroSubtitle,
                        String introParagraph,
                        String valueFaqAnswer,
                        String repairsFaqAnswer,
                        String decisionFaqAnswer,
                        String highMileageFaqAnswer) {
        }

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
                        ShouldFixCopy shouldFixCopy = buildShouldFixCopy(year, brandSlug, modelSlug,
                                        canonicalBrandSlug, canonicalModelSlug, marketValue, primaryFaultName,
                                        primaryFaultCost, primaryFaultMileage, lifespanMiles);
                        // SEO Meta - Optimized for CTR and direct decision intent
                        String seoTitle;
                        if (primaryFaultCost != null && primaryFaultName != null) {
                                seoTitle = String.format("%d %s %s: Fix or Sell if Repair Hits $%,d?",
                                                year, brandSlug, modelSlug, primaryFaultCost);
                        } else {
                                seoTitle = String.format("Should I Fix or Sell My %d %s %s?",
                                                year, brandSlug, modelSlug);
                        }
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
                        model.addAttribute("heroSubtitle", shouldFixCopy.heroSubtitle());
                        model.addAttribute("introParagraph", shouldFixCopy.introParagraph());
                        model.addAttribute("valueFaqAnswer", shouldFixCopy.valueFaqAnswer());
                        model.addAttribute("repairsFaqAnswer", shouldFixCopy.repairsFaqAnswer());
                        model.addAttribute("decisionFaqAnswer", shouldFixCopy.decisionFaqAnswer());
                        model.addAttribute("highMileageFaqAnswer", shouldFixCopy.highMileageFaqAnswer());
                        model.addAttribute("faqSchemaJson", buildFaqSchemaJson(year, brandSlug, modelSlug,
                                        shouldFixCopy.valueFaqAnswer(), shouldFixCopy.decisionFaqAnswer(),
                                        shouldFixCopy.repairsFaqAnswer()));

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

        private ShouldFixCopy buildShouldFixCopy(int year, String brand, String model, String canonicalBrandSlug,
                        String canonicalModelSlug, Integer marketValue, String primaryFaultName,
                        Integer primaryFaultCost, Integer primaryFaultMileage, Integer lifespanMiles) {
                String vehicle = year + " " + brand + " " + model;
                String heroSubtitle = "If you already have a painful repair quote, start here. See the likely answer, typical market value, expected lifespan, and the biggest known failure cost for this "
                                + model + " before you approve anything.";
                Integer cautionThresholdUsd = marketValue != null ? Math.max(500, (int) Math.round(marketValue * 0.25))
                                : null;
                Integer sellThresholdUsd = marketValue != null ? Math.max(750, (int) Math.round(marketValue * 0.35))
                                : null;
                String valueText = marketValue != null ? "$" + String.format("%,d", marketValue) : "its real market value";
                String topRepairText = primaryFaultCost != null ? "$" + String.format("%,d", primaryFaultCost)
                                : "a four-figure repair bill";
                String faultText = primaryFaultName != null ? primaryFaultName : "the next major repair";
                String faultMileageText = primaryFaultMileage != null
                                ? " around " + String.format("%,d", primaryFaultMileage) + " miles"
                                : "";
                String lifespanText = lifespanMiles != null ? String.format("%,d", lifespanMiles) + " miles"
                                : "the rest of the car's usable life";
                String cautionText = cautionThresholdUsd != null ? "$" + String.format("%,d", cautionThresholdUsd)
                                : "a moderate repair bill";
                String sellText = sellThresholdUsd != null ? "$" + String.format("%,d", sellThresholdUsd)
                                : "a major repair quote";

                String introParagraph = vehicle
                                + " should not be judged by age alone. The real question is whether your current quote is isolated maintenance or the kind of repair that starts a more expensive chain of ownership.";
                String valueFaqAnswer = vehicle + " is worth comparing against a real private-party value, not just trade-in guesses. If the car is clean and close to "
                                + valueText
                                + ", you usually want a higher bar before replacing it. If condition is rough or multiple repairs are stacking, the usable value can be much lower than that headline number.";
                String repairsFaqAnswer = "The repair that changes the decision fastest on this platform is usually "
                                + faultText + ", which our dataset prices around " + topRepairText + faultMileageText
                                + ". The point is not just one invoice. It is whether the current quote looks like a normal one-off repair or the first sign that larger bills are lining up.";
                String decisionFaqAnswer = "For many " + vehicle + " owners, quotes below " + cautionText
                                + " are easier to justify. Once the bill pushes toward " + sellText
                                + ", the right move usually depends on mileage, condition, and whether the current problem is tied to a known expensive failure pattern.";
                String highMileageFaqAnswer = "A high-mileage " + model + " can still be worth fixing, but only if the current problem is contained. Once the car is deep into the "
                                + lifespanText + " range and the quote looks like " + faultText
                                + " territory, you should compare a repair against selling instead of defaulting to either choice.";

                String key = canonicalBrandSlug + "/" + canonicalModelSlug;
                switch (key) {
                        case "toyota/camry" -> {
                                heroSubtitle = "Camry owners usually have more repair runway than average. Use this page to see when a big quote is still normal ownership and when it starts to look transmission-scale.";
                                introParagraph = vehicle
                                                + " usually stays in repair territory longer than average, so the real danger is panic-selling after a normal wear-item quote. The decision gets much harder when the bill starts to look transmission-scale or multiple deferred repairs are stacking together.";
                                valueFaqAnswer = vehicle
                                                + " often holds value better than many midsize sedans, so the right comparison is not \"old car vs new car\" but \"this quote vs a still-sellable Camry.\" Trim, accident history, and cosmetic condition can swing the real price more than mileage alone.";
                                repairsFaqAnswer = "Camry owners usually need to worry most when the quote starts to look like "
                                                + faultText + " at about " + topRepairText + faultMileageText
                                                + ". Normal brakes, tires, struts, and smaller leaks are a different category from a bill that feels transmission-scale.";
                                decisionFaqAnswer = "Many Camry owners can justify repairs longer than average. Quotes below "
                                                + cautionText
                                                + " are often still fix territory, while quotes near " + sellText
                                                + " deserve a real sell comparison, especially if the issue touches the transmission or another major driveline component.";
                                highMileageFaqAnswer = "A high-mileage Camry can still be worth fixing if the engine and transmission feel stable and the current quote is isolated. The decision flips faster when the car is already showing repeated drivability or oil-consumption symptoms.";
                        }
                        case "nissan/altima" -> {
                                heroSubtitle = "Altima decisions get much harsher when a quote smells like CVT risk. Use this page before you approve a transmission-scale repair or assume the car is still safely in fix territory.";
                                introParagraph = vehicle
                                                + " flips into sell territory earlier than average when the quote is transmission-related. Altima owners usually regret approving a marginal CVT-scale repair more than they regret exiting a tired car a little early.";
                                valueFaqAnswer = "Altima values can look acceptable on paper while collapsing fast once a buyer suspects transmission risk. That means your real comparison is not just against the headline "
                                                + valueText + ", but against what the car is worth if the next buyer also worries about drivability.";
                                repairsFaqAnswer = "The repair that changes Altima math fastest is usually " + faultText
                                                + " at about " + topRepairText + faultMileageText
                                                + ". If your quote is transmission-, CVT-, or repeat-drivability-related, treat it as a very different decision from normal maintenance.";
                                decisionFaqAnswer = "For an Altima, quotes below " + cautionText
                                                + " can still be worth doing when the transmission is healthy. Once the quote pushes toward "
                                                + sellText
                                                + " or looks CVT-related, selling deserves a hard comparison much sooner than on a Camry or Accord.";
                                highMileageFaqAnswer = "A high-mileage Altima is only a comfortable repair candidate when the current problem is isolated and the transmission is not already a question mark. If the car is deep into "
                                                + lifespanText
                                                + " and the quote is drivability-related, the downside of keeping it rises fast.";
                        }
                        case "mazda/cx-5" -> {
                                heroSubtitle = "CX-5 owners can usually justify repairs longer than average, but engine-side or cooling-related quotes deserve a more careful fix-versus-sell check than routine maintenance.";
                                introParagraph = vehicle
                                                + " usually earns more repair tolerance than average if the engine is healthy and the body is clean. The decision gets harder when the current quote hints at oil, cooling, or multiple stacked repairs instead of a single clean fix.";
                                valueFaqAnswer = vehicle
                                                + " tends to keep value reasonably well, so replacing it too early can be expensive. The right comparison is whether this quote protects a still-healthy SUV or just delays a series of larger bills.";
                                repairsFaqAnswer = "For a CX-5, the biggest question is whether the current quote looks like "
                                                + faultText + " at about " + topRepairText + faultMileageText
                                                + " or just routine ownership cost. One clean repair is very different from the start of a recurring oil or cooling problem.";
                                decisionFaqAnswer = "Many CX-5 owners can justify repairs below " + cautionText
                                                + " if the rest of the vehicle is clean. Once the quote climbs toward " + sellText
                                                + " and the issue points to an engine-side problem, a sell comparison becomes much more reasonable.";
                                highMileageFaqAnswer = "A high-mileage CX-5 can still be worth fixing when the engine is dry, cooling is stable, and the current issue is isolated. The case weakens when the quote is large and you also see signs of oil consumption or repeat cooling repairs.";
                        }
                        case "honda/cr-v" -> {
                                heroSubtitle = "A clean CR-V often stays in keep territory for a long time. The job here is making sure a large quote is truly isolated before you approve it on instinct.";
                                introParagraph = vehicle
                                                + " is often a keep-it vehicle when the repair is straightforward. The main mistake is approving a big invoice before checking whether it is truly one repair or the start of A/C, suspension, and engine work stacking together.";
                                valueFaqAnswer = vehicle
                                                + " usually has a strong owner market, so it often deserves a fair repair comparison before you move on. Condition still matters: a clean CR-V with service history is not priced the same as a tired one with deferred work.";
                                repairsFaqAnswer = "On a CR-V, the decision usually changes when the current quote starts to look like "
                                                + faultText + " at about " + topRepairText + faultMileageText
                                                + " rather than normal maintenance. That is the point where future repeat-failure risk matters more than brand reputation.";
                                decisionFaqAnswer = "Many CR-V owners can justify repairs below " + cautionText
                                                + " fairly comfortably. Once the quote approaches " + sellText
                                                + ", especially for engine or repeated accessory failures, you want a more serious fix-versus-sell comparison.";
                                highMileageFaqAnswer = "A high-mileage CR-V can still make sense to fix because these vehicles often run long. The answer changes when the quote is large and the car is already deep into the "
                                                + lifespanText
                                                + " range, especially if several worn systems are coming due at once.";
                        }
                        case "honda/accord" -> {
                                heroSubtitle = "Accord owners usually have good repair economics until the bill starts to look drivetrain-scale. This page helps you tell the difference between a healthy save and an expensive delay.";
                                introParagraph = vehicle
                                                + " usually gives owners more repair runway than average, but it stops being an automatic fix once the quote starts looking like major transmission, engine, or repeated timing-related work.";
                                valueFaqAnswer = vehicle
                                                + " often keeps enough owner demand that replacing it too early can be expensive. The smarter comparison is whether the current bill is protecting a fundamentally healthy Accord or just buying time on a car entering a more expensive phase.";
                                repairsFaqAnswer = "Accord owners should pay close attention when the current quote resembles "
                                                + faultText + " at about " + topRepairText + faultMileageText
                                                + ". That kind of repair changes the math more than ordinary wear items.";
                                decisionFaqAnswer = "Many Accord owners can justify repairs below " + cautionText
                                                + ". Once the quote gets close to " + sellText
                                                + ", especially if the issue is drivetrain-related, selling deserves a serious comparison instead of an automatic repair approval.";
                                highMileageFaqAnswer = "A high-mileage Accord can still be worth fixing if the car has been maintained and the current issue is cleanly scoped. If the car is already deep into the "
                                                + lifespanText
                                                + " range and the quote is tied to a major driveline problem, the sell case gets stronger fast.";
                        }
                        case "toyota/corolla" -> {
                                heroSubtitle = "Corolla owners should usually start from fix, not fear. The exception is when a quote stops looking like normal wear and starts looking like a major driveline or stacked-neglect bill.";
                                introParagraph = vehicle
                                                + " usually belongs in fix territory longer than most compact cars, so the goal is not to overreact to age alone. The decision changes when the current quote is large enough that it no longer looks like ordinary Corolla ownership.";
                                valueFaqAnswer = vehicle
                                                + " often keeps enough resale demand that a clean example should be compared against real private-party value, not just a low trade number. The right question is whether this repair protects a still-liquid Corolla or just delays a more expensive phase.";
                                repairsFaqAnswer = "On a Corolla, routine wear items rarely justify selling by themselves. The decision changes faster when the current quote looks like "
                                                + faultText + " at about " + topRepairText + faultMileageText
                                                + " or when several deferred repairs are landing together.";
                                decisionFaqAnswer = "Many Corolla owners can justify repairs below " + cautionText
                                                + " without much drama. Once the quote moves toward " + sellText
                                                + ", especially if it is tied to transmission, engine, or multiple stacked issues, a sell comparison becomes more reasonable.";
                                highMileageFaqAnswer = "A high-mileage Corolla can still be worth fixing if the car is otherwise stable and the current issue is isolated. The answer gets harder when the bill is large and the car is already deep into the "
                                                + lifespanText + " range.";
                        }
                        case "ford/escape" -> {
                                heroSubtitle = "Escape decisions get riskier when the quote points to coolant, turbo, or transmission trouble. Use this page before you approve a repair that could turn into a repeat-failure story.";
                                introParagraph = vehicle
                                                + " can feel worth saving right up until the quote starts hinting at a deeper engine, cooling, or transmission pattern. The key is separating one contained repair from the kind of bill that often repeats.";
                                valueFaqAnswer = vehicle
                                                + " can still look decent on paper, but the practical resale number drops fast when buyers expect EcoBoost, coolant, or transmission questions. That makes quote quality and failure type matter more than headline book value alone.";
                                repairsFaqAnswer = "Escape owners should slow down when the current quote resembles "
                                                + faultText + " at about " + topRepairText + faultMileageText
                                                + ". Bills tied to coolant intrusion, turbo hardware, or transmission behavior are very different from normal wear-item repairs.";
                                decisionFaqAnswer = "For an Escape, quotes below " + cautionText
                                                + " can still be reasonable if the engine and transmission story is clean. Once the bill starts pushing toward " + sellText
                                                + " and the issue is engine- or transmission-adjacent, selling deserves a much harder look.";
                                highMileageFaqAnswer = "A high-mileage Escape can still be worth fixing if the current problem is genuinely isolated. The downside rises fast when the car is deep into the "
                                                + lifespanText
                                                + " range and the quote hints at turbo, coolant, or transmission trouble.";
                        }
                        default -> {
                        }
                }

                return new ShouldFixCopy(heroSubtitle, introParagraph, valueFaqAnswer, repairsFaqAnswer, decisionFaqAnswer,
                                highMileageFaqAnswer);
        }

        private String buildFaqSchemaJson(int year, String brand, String model, String valueFaqAnswer,
                        String decisionFaqAnswer, String repairsFaqAnswer) {
                return """
                                {
                                  "@context": "https://schema.org",
                                  "@type": "FAQPage",
                                  "mainEntity": [
                                    {
                                      "@type": "Question",
                                      "name": "How much is my %d %s %s worth?",
                                      "acceptedAnswer": {
                                        "@type": "Answer",
                                        "text": "%s"
                                      }
                                    },
                                    {
                                      "@type": "Question",
                                      "name": "Should I fix my %d %s %s or sell it?",
                                      "acceptedAnswer": {
                                        "@type": "Answer",
                                        "text": "%s"
                                      }
                                    },
                                    {
                                      "@type": "Question",
                                      "name": "What are common repairs for the %s %s?",
                                      "acceptedAnswer": {
                                        "@type": "Answer",
                                        "text": "%s"
                                      }
                                    }
                                  ]
                                }
                                """.formatted(
                                        year, brand, model, escapeJson(valueFaqAnswer),
                                        year, brand, model, escapeJson(decisionFaqAnswer),
                                        brand, model, escapeJson(repairsFaqAnswer));
        }

        private String escapeJson(String input) {
                if (input == null) {
                        return "";
                }
                return input
                                .replace("\\", "\\\\")
                                .replace("\"", "\\\"")
                                .replace("\r", " ")
                                .replace("\n", " ");
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
