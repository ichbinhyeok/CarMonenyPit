package com.carmoneypit.engine.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * Service responsible for loading and providing access to car data.
 * Follows Single Responsibility Principle (SRP) - only handles data access.
 */
@Service
public class CarDataService {

    private static final Logger logger = LoggerFactory.getLogger(CarDataService.class);

    private final List<CarModel> carModels;
    private final List<ModelReliability> reliabilityData;
    private final List<ModelMarket> marketData;
    private final List<MajorFaults> faultsData;

    public CarDataService(ObjectMapper objectMapper) {
        this.carModels = loadJson(objectMapper, "/data/car_models.json", new TypeReference<>() {
        });
        this.reliabilityData = loadJson(objectMapper, "/data/model_reliability.json", new TypeReference<>() {
        });
        this.marketData = loadJson(objectMapper, "/data/model_market.json", new TypeReference<>() {
        });
        this.faultsData = loadJson(objectMapper, "/data/major_faults.json", new TypeReference<>() {
        });

        logger.info("Loaded {} car models, {} reliability profiles, {} market profiles, {} fault profiles",
                carModels.size(), reliabilityData.size(), marketData.size(), faultsData.size());
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
        return carModels.stream()
                .filter(c -> normalize(c.brand()).equals(normalize(brandSlug)) &&
                        normalize(c.model()).equals(normalize(modelSlug)))
                .findFirst();
    }

    public Optional<MajorFaults> findFaultsByModelId(String modelId) {
        return faultsData.stream()
                .filter(f -> f.modelIdRef().equals(modelId))
                .findFirst();
    }

    public Optional<ModelReliability> findReliabilityByModelId(String modelId) {
        return reliabilityData.stream()
                .filter(r -> r.modelId().equals(modelId))
                .findFirst();
    }

    public Optional<ModelMarket> findMarketByModelId(String modelId) {
        return marketData.stream()
                .filter(m -> m.modelId().equals(modelId))
                .findFirst();
    }

    public List<String> getAllBrands() {
        return carModels.stream()
                .map(CarModel::brand)
                .distinct()
                .sorted()
                .toList();
    }

    public List<CarModel> getModelsByBrand(String brandSlug) {
        return carModels.stream()
                .filter(c -> normalize(c.brand()).equals(normalize(brandSlug)))
                .toList();
    }

    public List<CarModel> getAllModels() {
        return carModels;
    }

    private String normalize(String input) {
        return input.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    // --- Data Records ---

    public record CarModel(
            String id,
            String brand,
            String model,
            String generation,
            @com.fasterxml.jackson.annotation.JsonProperty("start_year") int startYear,
            @com.fasterxml.jackson.annotation.JsonProperty("end_year") int endYear) {
    }

    public record ModelReliability(
            @com.fasterxml.jackson.annotation.JsonProperty("model_id") String modelId,
            int score,
            @com.fasterxml.jackson.annotation.JsonProperty("lifespan_miles") int lifespanMiles,
            @com.fasterxml.jackson.annotation.JsonProperty("best_years") List<Integer> bestYears,
            @com.fasterxml.jackson.annotation.JsonProperty("worst_years") List<Integer> worstYears,
            @com.fasterxml.jackson.annotation.JsonProperty("common_trouble_spots") List<String> commonTroubleSpots,
            @com.fasterxml.jackson.annotation.JsonProperty("critical_milestones") List<Milestone> criticalMilestones) {
    }

    public record Milestone(
            int mileage,
            String description,
            @com.fasterxml.jackson.annotation.JsonProperty("est_cost") int estCost) {
    }

    public record ModelMarket(
            @com.fasterxml.jackson.annotation.JsonProperty("model_id") String modelId,
            @com.fasterxml.jackson.annotation.JsonProperty("jan_2026_avg_price") int jan2026AvgPrice,
            @com.fasterxml.jackson.annotation.JsonProperty("depreciation_rate") double depreciationRate,
            @com.fasterxml.jackson.annotation.JsonProperty("avg_annual_repair_cost") int avgAnnualRepairCost,
            @com.fasterxml.jackson.annotation.JsonProperty("depreciation_outlook") String depreciationOutlook) {
    }

    public record MajorFaults(
            @com.fasterxml.jackson.annotation.JsonProperty("model_id_ref") String modelIdRef,
            List<Fault> faults) {
    }

    public record Fault(
            String component,
            String symptoms,
            @com.fasterxml.jackson.annotation.JsonProperty("repairCost") double repairCost,
            @com.fasterxml.jackson.annotation.JsonProperty("verdictImplication") String verdictImplication) {
    }
}
