package com.carmoneypit.engine.service;

import com.carmoneypit.engine.service.CarDataService.CarModel;
import com.carmoneypit.engine.service.CarDataService.Fault;
import com.carmoneypit.engine.service.CarDataService.MajorFaults;
import com.carmoneypit.engine.web.FaultHubViewModel;
import com.carmoneypit.engine.web.FaultHubViewModel.AffectedModel;
import com.carmoneypit.engine.web.FaultHubViewModel.ReferenceSource;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FaultHubService {

        private static final Logger log = LoggerFactory.getLogger(FaultHubService.class);

        // Immutable allowed slugs — the ONLY hubs that will ever render
        public static final Set<String> ALLOWED_SLUGS = Set.of(
                        "cvt-transmission",
                        "timing-chain",
                        "oil-consumption",
                        "torque-converter",
                        "air-suspension");

        public static final Map<String, String> SLUG_DISPLAY_NAMES = Map.of(
                        "cvt-transmission", "CVT Transmission Failure",
                        "timing-chain", "Timing Chain Failure",
                        "oil-consumption", "Excessive Oil Consumption",
                        "torque-converter", "Torque Converter Shudder",
                        "air-suspension", "Air Suspension Failure");

        private final CarDataService carDataService;
        private final Map<String, List<ReferenceSource>> referencesMap;

        public FaultHubService(CarDataService carDataService, ObjectMapper objectMapper) {
                this.carDataService = carDataService;
                this.referencesMap = loadReferences(objectMapper);
        }

        /**
         * Returns a fully populated hub view model, or empty if slug is invalid.
         */
        public Optional<FaultHubViewModel> getHub(String slug) {
                if (!ALLOWED_SLUGS.contains(slug)) {
                        return Optional.empty();
                }

                String displayName = SLUG_DISPLAY_NAMES.getOrDefault(slug, slug);
                List<AffectedModel> affectedModels = aggregateModels(slug);

                // Sort: occurrence_rate desc, then avg_failure_mileage asc
                affectedModels.sort(Comparator
                                .comparingDouble(AffectedModel::occurrenceRate).reversed()
                                .thenComparing(AffectedModel::avgFailureMileage));

                List<ReferenceSource> references = referencesMap.getOrDefault(slug, List.of());

                return Optional.of(new FaultHubViewModel(
                                slug,
                                displayName,
                                generateQuickAnswer(slug, affectedModels),
                                affectedModels,
                                references,
                                generateFaqItems(slug)));
        }

        /**
         * Returns summary data for all 5 hubs (for the directory page).
         */
        public List<FaultHubViewModel> getAllHubSummaries() {
                return ALLOWED_SLUGS.stream()
                                .sorted()
                                .map(this::getHub)
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .collect(Collectors.toList());
        }

        /**
         * Returns the top N fault hub slugs relevant to a given model ID.
         */
        public List<String> getRelevantHubSlugs(String modelId, int maxLinks) {
                Optional<MajorFaults> faultsOpt = carDataService.findFaultsByModelId(modelId);
                if (faultsOpt.isEmpty())
                        return List.of();

                return faultsOpt.get().faults().stream()
                                .map(f -> normalizeToSlug(f.component()))
                                .filter(ALLOWED_SLUGS::contains)
                                .distinct()
                                .limit(maxLinks)
                                .collect(Collectors.toList());
        }

        // --- Aggregation Logic ---

        private List<AffectedModel> aggregateModels(String targetSlug) {
                List<AffectedModel> result = new ArrayList<>();
                List<CarModel> allModels = carDataService.getAllModels();

                for (CarModel car : allModels) {
                        Optional<MajorFaults> faultsOpt = carDataService.findFaultsByModelId(car.id());
                        if (faultsOpt.isEmpty())
                                continue;

                        for (Fault fault : faultsOpt.get().faults()) {
                                String faultSlug = normalizeToSlug(fault.component());
                                if (targetSlug.equals(faultSlug)) {
                                        result.add(new AffectedModel(
                                                        car.brand(),
                                                        car.model(),
                                                        car.generation(),
                                                        fault.repairCost(),
                                                        fault.occurrenceRate(),
                                                        fault.avgFailureMileage(),
                                                        fault.symptoms(),
                                                        fault.verdictImplication()));
                                }
                        }
                }
                return result;
        }

        /**
         * Normalizes a component name to a hub slug.
         * This is the single source of truth for component -> slug mapping.
         */
        public static String normalizeToSlug(String component) {
                if (component == null)
                        return "";
                String c = component.toLowerCase();

                if (c.contains("cvt"))
                        return "cvt-transmission";
                if (c.contains("torque converter"))
                        return "torque-converter";
                if (c.contains("timing chain"))
                        return "timing-chain";
                if (c.contains("oil consumption") || c.contains("oil dilution")
                                || c.contains("excessive oil"))
                        return "oil-consumption";
                if (c.contains("air suspension"))
                        return "air-suspension";

                // Not a hub-worthy fault
                return c.replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        }

        // --- Content Generation (deterministic, dataset-only) ---

        private String generateQuickAnswer(String slug, List<AffectedModel> models) {
                int modelCount = models.size();
                if (modelCount == 0)
                        return "Data not yet available for this fault.";

                double avgCost = models.stream()
                                .mapToDouble(AffectedModel::repairCost)
                                .filter(c -> c > 0)
                                .average().orElse(0);

                String costRange = avgCost > 0
                                ? String.format("$%,.0f", avgCost * 0.7) + "–" + String.format("$%,.0f", avgCost * 1.3)
                                : "varies";

                return String.format(
                                "This issue affects at least %d models in our dataset. "
                                                + "Typical repair costs range from %s, though actual prices vary by trim, condition, and location. "
                                                + "Use our calculator to compare your specific repair vs. replacement costs.",
                                modelCount, costRange);
        }

        private List<FaultHubViewModel.FaqItem> generateFaqItems(String slug) {
                return switch (slug) {
                        case "cvt-transmission" -> List.of(
                                        new FaultHubViewModel.FaqItem("What are common signs of CVT failure?",
                                                        "Common symptoms include whining noises, loss of power, RPM fluctuations, and shuddering during acceleration. Severity varies by vehicle."),
                                        new FaultHubViewModel.FaqItem("How much does a CVT replacement cost?",
                                                        "Costs typically range from $3,000 to $8,000 depending on the vehicle. Remanufactured units may be less expensive."),
                                        new FaultHubViewModel.FaqItem("Can I drive with a failing CVT?",
                                                        "It depends on the severity. Minor shuddering may be manageable short-term, but complete loss of power requires immediate attention. Consult a mechanic."),
                                        new FaultHubViewModel.FaqItem("Is it worth fixing a CVT or should I sell?",
                                                        "Compare the repair cost to your vehicle's current market value. If the repair exceeds 50% of the car's value, selling may be more practical."));
                        case "timing-chain" -> List.of(
                                        new FaultHubViewModel.FaqItem("What happens when a timing chain fails?",
                                                        "In interference engines, a broken chain can cause pistons to strike valves, potentially destroying the engine. In non-interference engines, the engine simply stops."),
                                        new FaultHubViewModel.FaqItem("How much does timing chain replacement cost?",
                                                        "Timing chain replacement typically costs $1,500 to $4,000, primarily due to labor. Some complex engines may cost more."),
                                        new FaultHubViewModel.FaqItem("How long does a timing chain last?",
                                                        "Timing chains are designed to last the life of the engine, but tensioner and guide failures can occur as early as 60,000 miles on some models."),
                                        new FaultHubViewModel.FaqItem("Can I drive with a noisy timing chain?",
                                                        "A rattling timing chain indicates imminent failure risk. Have it inspected immediately to avoid catastrophic engine damage."));
                        case "oil-consumption" -> List.of(
                                        new FaultHubViewModel.FaqItem("How much oil consumption is normal?",
                                                        "Most manufacturers consider up to 1 quart per 1,000-2,000 miles acceptable. Consumption beyond this rate may indicate a mechanical issue."),
                                        new FaultHubViewModel.FaqItem("What causes excessive oil burning?",
                                                        "Common causes include worn piston rings, valve seal deterioration, PCV valve failure, or design-specific issues in certain engine families."),
                                        new FaultHubViewModel.FaqItem("Is engine oil consumption expensive to fix?",
                                                        "Minor fixes like PCV valve replacement are inexpensive. Major repairs involving piston rings or an engine rebuild can cost $3,000–$8,000."),
                                        new FaultHubViewModel.FaqItem("Should I sell a car that burns oil?",
                                                        "If the repair cost approaches the vehicle's value, selling may be more practical. Use our calculator to compare repair vs. replacement costs."));
                        case "torque-converter" -> List.of(
                                        new FaultHubViewModel.FaqItem("What does torque converter shudder feel like?",
                                                        "It typically feels like driving over a rumble strip or small bumps, usually between 40-50 mph when the converter clutch engages."),
                                        new FaultHubViewModel.FaqItem(
                                                        "Can a fluid change fix torque converter shudder?",
                                                        "In some cases, replacing transmission fluid with the correct friction-modifier specification can resolve mild shudder. Severe cases require converter replacement."),
                                        new FaultHubViewModel.FaqItem(
                                                        "How much does torque converter replacement cost?",
                                                        "Replacement typically costs $600 to $2,500 including labor, since the transmission must be removed to access the converter."),
                                        new FaultHubViewModel.FaqItem("Is torque converter shudder dangerous?",
                                                        "Shudder itself is not immediately dangerous, but ignoring it can lead to full transmission failure over time, which is far more expensive to repair."));
                        case "air-suspension" -> List.of(
                                        new FaultHubViewModel.FaqItem("How much does air suspension repair cost?",
                                                        "Individual air spring replacement ranges from $500 to $1,500 per corner. Full system repairs can cost $2,000 to $7,000 depending on the vehicle."),
                                        new FaultHubViewModel.FaqItem("Can I convert air suspension to coil springs?",
                                                        "Yes, coil spring conversion kits are available for many vehicles at $500–$1,500, eliminating the complex air system. However, ride quality may change."),
                                        new FaultHubViewModel.FaqItem("What causes air suspension failure?",
                                                        "Common causes include rubber air spring deterioration, air line leaks, compressor burnout from overwork, and faulty height sensors."),
                                        new FaultHubViewModel.FaqItem("Is air suspension worth repairing?",
                                                        "Consider the vehicle's value, your comfort preferences, and whether you plan to keep the car long-term. Conversion to coils is often a cost-effective alternative."));
                        default -> List.of();
                };
        }

        // --- Reference Loading ---

        private Map<String, List<ReferenceSource>> loadReferences(ObjectMapper mapper) {
                Map<String, List<ReferenceSource>> result = new HashMap<>();
                try {
                        InputStream is = getClass().getResourceAsStream("/data/fault_references.json");
                        if (is == null) {
                                log.warn("fault_references.json not found, references will be empty");
                                return result;
                        }
                        JsonNode root = mapper.readTree(is);
                        root.fieldNames().forEachRemaining(slug -> {
                                JsonNode entry = root.get(slug);
                                JsonNode sources = entry.get("sources");
                                if (sources != null && sources.isArray()) {
                                        List<ReferenceSource> refs = new ArrayList<>();
                                        for (JsonNode s : sources) {
                                                refs.add(new ReferenceSource(
                                                                s.has("name") ? s.get("name").asText() : "",
                                                                s.has("url") ? s.get("url").asText() : "",
                                                                s.has("retrieved_at") ? s.get("retrieved_at").asText()
                                                                                : "",
                                                                s.has("note") ? s.get("note").asText() : ""));
                                        }
                                        result.put(slug, refs);
                                }
                        });
                } catch (Exception e) {
                        log.error("Failed to load fault_references.json", e);
                }
                return result;
        }
}
