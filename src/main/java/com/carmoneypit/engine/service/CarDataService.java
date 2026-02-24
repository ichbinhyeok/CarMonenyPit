package com.carmoneypit.engine.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service responsible for loading and providing access to car data.
 * Follows Single Responsibility Principle (SRP) - only handles data access.
 */
@Service
public class CarDataService {

    private static final Logger logger = LoggerFactory.getLogger(CarDataService.class);

    private final List<CarModel> carModels;
    private final Map<String, CarModel> carModelsMap; // Key: "brand|model" (normalized)

    private final Map<String, ModelReliability> reliabilityMap;
    private final Map<String, ModelMarket> marketMap;
    private final Map<String, MajorFaults> faultsMap;

    public CarDataService(ObjectMapper objectMapper) {
        this.carModels = loadJson(objectMapper, "/data/car_models.json", new TypeReference<>() {
        });
        var reliabilityData = loadJson(objectMapper, "/data/model_reliability.json",
                new TypeReference<List<ModelReliability>>() {
                });
        var marketData = loadJson(objectMapper, "/data/model_market.json", new TypeReference<List<ModelMarket>>() {
        });
        var faultsData = loadJson(objectMapper, "/data/major_faults.json", new TypeReference<List<MajorFaults>>() {
        });

        // Initialize Lookup Maps
        this.carModelsMap = carModels.stream()
                .collect(Collectors.toMap(
                        c -> makeKey(c.brand(), c.model()),
                        Function.identity(),
                        (existing, replacement) -> existing));

        this.reliabilityMap = reliabilityData.stream()
                .collect(Collectors.toMap(ModelReliability::modelId, Function.identity(), (a, b) -> a));

        this.marketMap = marketData.stream()
                .collect(Collectors.toMap(ModelMarket::modelId, Function.identity(), (a, b) -> a));

        this.faultsMap = faultsData.stream()
                .collect(Collectors.toMap(MajorFaults::modelIdRef, Function.identity(), (a, b) -> a));

        logger.info("Loaded {} car models", carModels.size());
    }

    private <T> List<T> loadJson(ObjectMapper mapper, String path, TypeReference<List<T>> typeRef) {
        try {
            InputStream stream = getClass().getResourceAsStream(path);
            if (stream != null) {
                return mapper.readValue(stream, typeRef);
            }
        } catch (Exception e) {
            logger.error("Failed to load {}", path, e);
        }
        return List.of();
    }

    // --- Query Methods ---

    public Optional<CarModel> findCarBySlug(String brandSlug, String modelSlug) {
        return Optional.ofNullable(carModelsMap.get(makeKey(brandSlug, modelSlug)));
    }

    public Optional<MajorFaults> findFaultsByModelId(String modelId) {
        return Optional.ofNullable(faultsMap.get(modelId));
    }

    public Optional<ModelReliability> findReliabilityByModelId(String modelId) {
        return Optional.ofNullable(reliabilityMap.get(modelId));
    }

    public Optional<ModelMarket> findMarketByModelId(String modelId) {
        return Optional.ofNullable(marketMap.get(modelId));
    }

    public List<String> getAllBrands() {
        return carModels.stream()
                .map(CarModel::brand)
                .distinct()
                .map(this::toTitleCase)
                .sorted()
                .toList();
    }

    private String toTitleCase(String input) {
        if (input == null || input.isEmpty())
            return input;

        // Handle special cases
        if (input.equalsIgnoreCase("BMW") || input.equalsIgnoreCase("GMC"))
            return input.toUpperCase();
        if (input.equalsIgnoreCase("VW") || input.equalsIgnoreCase("Volkswagen"))
            return "Volkswagen";

        String[] words = input.toLowerCase().split("[\\s-_]");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                if (sb.length() > 0)
                    sb.append(" ");
                sb.append(Character.toUpperCase(word.charAt(0)));
                sb.append(word.substring(1));
            }
        }
        return sb.toString();
    }

    public List<CarModel> getModelsByBrand(String brandSlug) {
        String normalizedBrand = normalize(brandSlug);
        return carModels.stream()
                .filter(c -> normalize(c.brand()).equals(normalizedBrand))
                .sorted((c1, c2) -> String.CASE_INSENSITIVE_ORDER.compare(c1.model(), c2.model()))
                .toList();
    }

    public List<CarModel> getAllModels() {
        return carModels;
    }

    private String makeKey(String brand, String model) {
        return normalize(brand) + "|" + normalize(model);
    }

    private String normalize(String input) {
        return input == null ? "" : input.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    // --- Data Records (Updated) ---

    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    public record CarModel(
            String id,
            String brand,
            String model,
            String generation,
            @com.fasterxml.jackson.annotation.JsonProperty("start_year") int startYear,
            @com.fasterxml.jackson.annotation.JsonProperty("end_year") int endYear) {
    }

    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    public record ModelReliability(
            @com.fasterxml.jackson.annotation.JsonProperty("model_id") String modelId,
            int score,
            @com.fasterxml.jackson.annotation.JsonProperty("lifespan_miles") int lifespanMiles,
            @com.fasterxml.jackson.annotation.JsonProperty("best_years") List<Integer> bestYears,
            @com.fasterxml.jackson.annotation.JsonProperty("worst_years") List<Integer> worstYears,
            @com.fasterxml.jackson.annotation.JsonProperty("common_trouble_spots") List<String> commonTroubleSpots,
            @com.fasterxml.jackson.annotation.JsonProperty("critical_milestones") List<Milestone> criticalMilestones,
            @com.fasterxml.jackson.annotation.JsonProperty("mileage_logic_text") Map<String, String> mileageLogicText) {

        // Constructor for Jackson (handles missing field)
        public ModelReliability(String modelId, int score, int lifespanMiles, List<Integer> bestYears,
                List<Integer> worstYears, List<String> commonTroubleSpots, List<Milestone> criticalMilestones) {
            this(modelId, score, lifespanMiles, bestYears, worstYears, commonTroubleSpots, criticalMilestones, null);
        }
    }

    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    public record Milestone(
            int mileage,
            String description,
            @com.fasterxml.jackson.annotation.JsonProperty("est_cost") int estCost) {
    }

    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    public record ModelMarket(
            @com.fasterxml.jackson.annotation.JsonProperty("model_id") String modelId,
            @com.fasterxml.jackson.annotation.JsonProperty("jan_2026_avg_price") int jan2026AvgPrice,
            @com.fasterxml.jackson.annotation.JsonProperty("depreciation_rate") double depreciationRate,
            @com.fasterxml.jackson.annotation.JsonProperty("avg_annual_repair_cost") int avgAnnualRepairCost,
            @com.fasterxml.jackson.annotation.JsonProperty("depreciation_outlook") String depreciationOutlook,
            @com.fasterxml.jackson.annotation.JsonProperty("common_junk_value") Integer commonJunkValue) {

        public ModelMarket(String modelId, int jan2026AvgPrice, double depreciationRate, int avgAnnualRepairCost,
                String depreciationOutlook) {
            this(modelId, jan2026AvgPrice, depreciationRate, avgAnnualRepairCost, depreciationOutlook, 500); // Default
                                                                                                             // $500
                                                                                                             // junk
                                                                                                             // value
        }
    }

    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    public record MajorFaults(
            @com.fasterxml.jackson.annotation.JsonProperty("model_id_ref") String modelIdRef,
            List<Fault> faults) {
    }

    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    public record Fault(
            String component,
            String symptoms,
            @com.fasterxml.jackson.annotation.JsonProperty("repairCost") double repairCost,
            @com.fasterxml.jackson.annotation.JsonProperty("verdictImplication") String verdictImplication,
            @com.fasterxml.jackson.annotation.JsonProperty("occurrence_rate") double occurrenceRate,
            @com.fasterxml.jackson.annotation.JsonProperty("avg_failure_mileage") int avgFailureMileage) {

        public Fault(String component, String symptoms, double repairCost, String verdictImplication) {
            this(component, symptoms, repairCost, verdictImplication, 0.0, 0);
        }
    }
}
