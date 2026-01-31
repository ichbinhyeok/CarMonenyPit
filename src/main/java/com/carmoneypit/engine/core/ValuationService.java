package com.carmoneypit.engine.core;

import com.carmoneypit.engine.api.InputModels.VehicleType;
import com.carmoneypit.engine.data.CarBrandData;
import com.carmoneypit.engine.service.CarDataService;
import com.carmoneypit.engine.service.CarDataService.CarModel;
import com.carmoneypit.engine.service.CarDataService.ModelMarket;
import com.carmoneypit.engine.service.CarDataService.ModelReliability;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * ValuationService - Data-Driven Vehicle Valuation Engine
 * 
 * This service provides vehicle value estimation and repair cost calculation
 * using a multi-layer data model:
 * 
 * 1. Model-Specific Data (model_market.json, model_reliability.json)
 * - Most accurate: Uses actual depreciation rates, market prices, lifespan
 * 
 * 2. Brand-Level Data (car_brands.json)
 * - Fallback: Uses brand segment characteristics
 * 
 * 3. Default Heuristics
 * - Last resort: Generic vehicle type assumptions
 */
@Service
public class ValuationService {

    private final ObjectMapper objectMapper;
    private final CarDataService carDataService;
    private Map<String, CarBrandData> brandDataMap = new HashMap<>();

    // --- Default MSRP by Vehicle Type (2026 Base) ---
    private static final long BASE_SEDAN = 32_000;
    private static final long BASE_SUV = 38_000;
    private static final long BASE_TRUCK = 55_000;
    private static final long BASE_LUXURY = 65_000;
    private static final long BASE_PERFORMANCE = 85_000;
    private static final long SCRAP_VALUE = 500;

    // --- Depreciation Curve Rates (Annual Retention) ---
    private static final double RETENTION_FLAT = 0.94; // Toyota, Lexus
    private static final double RETENTION_NORMAL = 0.88; // Ford, Chevy
    private static final double RETENTION_STEEP = 0.85; // Nissan, Hyundai
    private static final double RETENTION_CLIFF = 0.80; // BMW, Land Rover

    // --- Risk Multipliers (per mile over 100k) ---
    private static final double RISK_DEFAULT = 0.015;
    private static final double RISK_KEEPER = 0.008;
    private static final double RISK_WORKHORSE = 0.012;
    private static final double RISK_PERFORMANCE = 0.025;
    private static final double RISK_TECH = 0.020;

    // --- Helper Constants ---
    private static final double UNCERTAINTY_PENALTY_RATE = 0.60;
    private static final double DEFERRED_MAINTENANCE_RATE = 0.40;
    private static final int CURRENT_YEAR = 2026;

    public ValuationService(ObjectMapper objectMapper, CarDataService carDataService) {
        this.objectMapper = objectMapper;
        this.carDataService = carDataService;
    }

    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource("data/car_brands.json");
            try (InputStream inputStream = resource.getInputStream()) {
                Map<String, CarBrandData> data = objectMapper.readValue(inputStream, new TypeReference<>() {
                });
                brandDataMap = data;
                System.out.println("[ValuationService] Loaded " + brandDataMap.size() + " brands from car_brands.json");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load car_brands.json", e);
        }
    }

    /**
     * Estimate vehicle value using tiered data sources:
     * 1. Model-specific market data (if available)
     * 2. Brand-level depreciation curves (fallback)
     * 3. Vehicle type defaults (last resort)
     */
    public long estimateValue(String brand, String modelName, VehicleType type, int year, long mileage) {
        String normalizedBrand = normalizeBrandKey(brand);
        CarBrandData brandData = brandDataMap.get(normalizedBrand);

        // Try to find model-specific data
        Optional<CarModel> carModel = findCarModel(normalizedBrand, modelName);
        Optional<ModelMarket> marketData = carModel.flatMap(m -> carDataService.findMarketByModelId(m.id()));
        Optional<ModelReliability> reliabilityData = carModel
                .flatMap(m -> carDataService.findReliabilityByModelId(m.id()));

        // === TIER 1: Model-Specific Calculation ===
        if (marketData.isPresent()) {
            return calculateValueWithMarketData(marketData.get(), reliabilityData.orElse(null), year, mileage);
        }

        // === TIER 2: Brand-Level Calculation ===
        if (brandData != null) {
            return calculateValueWithBrandData(brandData, type, year, mileage);
        }

        // === TIER 3: Default Calculation ===
        return calculateDefaultValue(type, year, mileage);
    }

    /**
     * Calculate value using model-specific market data.
     * Uses actual depreciation rate and market price from JSON.
     */
    private long calculateValueWithMarketData(ModelMarket market, ModelReliability reliability, int year,
            long mileage) {
        int age = Math.max(1, CURRENT_YEAR - year);

        // Use actual depreciation rate from market data
        double depreciationRate = market.depreciationRate();
        double retentionRate = 1.0 - depreciationRate;

        // Base value from market data
        int basePrice = market.jan2026AvgPrice();

        // Age-adjusted value (reverse compound from current market price)
        // Current price already factors in some depreciation, adjust for specific age
        double ageAdjustedValue = basePrice * Math.pow(retentionRate, Math.max(0, age - 5)); // Assume market price is
                                                                                             // for ~5yr old average

        // Mileage adjustment
        int expectedLifespan = (reliability != null) ? reliability.lifespanMiles() : 200000;
        double mileageRatio = (double) mileage / expectedLifespan;
        double mileageFactor = Math.max(0.3, 1.0 - (mileageRatio * 0.5)); // Max 50% penalty for high mileage

        double finalValue = ageAdjustedValue * mileageFactor;

        // Floor at junk value
        long junkValue = (market.commonJunkValue() != null) ? market.commonJunkValue() : SCRAP_VALUE;

        return Math.max((long) finalValue, junkValue);
    }

    /**
     * Calculate value using brand-level data.
     * Uses segment characteristics and depreciation curve.
     */
    private long calculateValueWithBrandData(CarBrandData data, VehicleType type, int year, long mileage) {
        long msrp = getBasePrice(type);
        int age = Math.max(1, CURRENT_YEAR - year);

        // Get retention rate from depreciation curve
        double retentionRate = switch (data.depreciationCurve) {
            case "FLAT" -> RETENTION_FLAT;
            case "STEEP" -> RETENTION_STEEP;
            case "CLIFF" -> RETENTION_CLIFF;
            default -> RETENTION_NORMAL;
        };

        // Age-based depreciation
        double baseValue = msrp * Math.pow(retentionRate, age);

        // Mileage adjuster
        double expectedMileage = age * 12_000.0;
        double usageRatio = (double) mileage / Math.max(1, expectedMileage);
        double mileageAdjuster = 1.0 + (1.0 - usageRatio) * 0.15;
        mileageAdjuster = Math.max(0.5, Math.min(1.25, mileageAdjuster));

        // Segment factor
        double brandFactor = switch (data.segment) {
            case "KEEPER" -> 1.10;
            case "CULT" -> 1.15;
            case "LEASER" -> 0.95;
            default -> 1.0;
        };

        double finalValue = baseValue * mileageAdjuster * brandFactor;

        // Subtract known major issue costs
        if (data.majorIssues != null) {
            for (CarBrandData.MajorIssue issue : data.majorIssues) {
                if (mileage > issue.mileage) {
                    finalValue -= (issue.cost * UNCERTAINTY_PENALTY_RATE);
                }
            }
        }

        return Math.max((long) finalValue, SCRAP_VALUE);
    }

    /**
     * Default calculation when no brand data available.
     */
    private long calculateDefaultValue(VehicleType type, int year, long mileage) {
        long msrp = getBasePrice(type);
        int age = Math.max(1, CURRENT_YEAR - year);

        double baseValue = msrp * Math.pow(RETENTION_NORMAL, age);

        // Simple mileage penalty
        double mileagePenalty = Math.min(0.5, mileage / 300000.0);
        double finalValue = baseValue * (1.0 - mileagePenalty);

        return Math.max((long) finalValue, SCRAP_VALUE);
    }

    /**
     * Estimate repair cost using tiered data:
     * 1. Model-specific avg_annual_repair_cost
     * 2. Brand maintenance_cost_yearly
     * 3. Default estimates
     */
    public long estimateRepairCost(String brand, VehicleType type, long mileage) {
        return estimateRepairCost(brand, null, type, mileage);
    }

    public long estimateRepairCost(String brand, String modelName, VehicleType type, long mileage) {
        String normalizedBrand = normalizeBrandKey(brand);
        CarBrandData brandData = brandDataMap.get(normalizedBrand);

        // Try model-specific data
        if (modelName != null) {
            Optional<CarModel> carModel = findCarModel(normalizedBrand, modelName);
            Optional<ModelMarket> marketData = carModel.flatMap(m -> carDataService.findMarketByModelId(m.id()));

            if (marketData.isPresent()) {
                return calculateRepairCostWithMarketData(marketData.get(), mileage);
            }
        }

        // Brand-level fallback
        if (brandData != null) {
            return calculateRepairCostWithBrandData(brandData, mileage);
        }

        // Default fallback
        return calculateDefaultRepairCost(type, mileage);
    }

    private long calculateRepairCostWithMarketData(ModelMarket market, long mileage) {
        long baseCost = market.avgAnnualRepairCost();

        // High-mileage risk premium
        if (mileage > 100000) {
            baseCost += (long) ((mileage - 100000) * RISK_DEFAULT);
        }

        return Math.max(baseCost, 250);
    }

    private long calculateRepairCostWithBrandData(CarBrandData data, long mileage) {
        long estimatedCost = 0;

        // Deferred maintenance base
        long yearlyBase = data.maintenanceCostYearly;
        estimatedCost += (long) (yearlyBase * DEFERRED_MAINTENANCE_RATE);

        // Check for failure point milestones
        boolean hasMajorIssue = false;
        if (data.majorIssues != null) {
            for (CarBrandData.MajorIssue issue : data.majorIssues) {
                if (mileage >= issue.mileage - 5000 && mileage <= issue.mileage + 20000) {
                    estimatedCost += issue.cost;
                    hasMajorIssue = true;
                }
            }
        }

        // Segment-aware high mileage risk
        if (!hasMajorIssue && mileage > 100000) {
            double riskMultiplier = switch (data.segment) {
                case "KEEPER" -> RISK_KEEPER;
                case "WORKHORSE" -> RISK_WORKHORSE;
                case "LEASER", "PERFORMANCE" -> RISK_PERFORMANCE;
                case "TECH" -> RISK_TECH;
                default -> RISK_DEFAULT;
            };
            estimatedCost += (long) (mileage * riskMultiplier);
        }

        return Math.max(estimatedCost, 250);
    }

    private long calculateDefaultRepairCost(VehicleType type, long mileage) {
        long baseCost = switch (type) {
            case LUXURY -> 1200;
            case PERFORMANCE -> 1500;
            case TRUCK_VAN -> 800;
            default -> 600;
        };

        if (mileage > 100000) {
            baseCost += (long) ((mileage - 100000) * RISK_DEFAULT);
        }

        return Math.max(baseCost, 250);
    }

    // === Public API Methods ===

    public long getBasePrice(VehicleType type) {
        return switch (type) {
            case SUV -> BASE_SUV;
            case TRUCK_VAN -> BASE_TRUCK;
            case LUXURY -> BASE_LUXURY;
            case PERFORMANCE -> BASE_PERFORMANCE;
            default -> BASE_SEDAN;
        };
    }

    public Optional<CarBrandData> getBrandData(String brand) {
        return Optional.ofNullable(brandDataMap.get(normalizeBrandKey(brand)));
    }

    public boolean isValidBrand(String brand) {
        return brandDataMap.containsKey(normalizeBrandKey(brand));
    }

    public List<String> getAllBrandKeys() {
        return brandDataMap.keySet().stream().sorted().toList();
    }

    public List<CarModel> getModelsByBrand(String brand) {
        return carDataService.getModelsByBrand(brand);
    }

    public Optional<ModelMarket> getMarketData(String modelName) {
        return carDataService.getAllModels().stream()
                .filter(m -> m.model().equalsIgnoreCase(modelName))
                .findFirst()
                .flatMap(m -> carDataService.findMarketByModelId(m.id()));
    }

    /**
     * Get comprehensive valuation context for a vehicle.
     * Useful for pSEO pages and detailed reports.
     */
    public ValuationContext getValuationContext(String brand, String modelName, int year, long mileage) {
        String normalizedBrand = normalizeBrandKey(brand);
        Optional<CarModel> carModel = findCarModel(normalizedBrand, modelName);
        Optional<ModelMarket> market = carModel.flatMap(m -> carDataService.findMarketByModelId(m.id()));
        Optional<ModelReliability> reliability = carModel.flatMap(m -> carDataService.findReliabilityByModelId(m.id()));
        Optional<CarBrandData> brandData = getBrandData(brand);

        // Calculate confidence level
        int confidence = calculateConfidence(market.isPresent(), reliability.isPresent(), brandData.isPresent());

        // Get mileage-specific context
        String mileageContext = getMileageContext(reliability.orElse(null), mileage);

        return new ValuationContext(
                carModel.orElse(null),
                market.orElse(null),
                reliability.orElse(null),
                brandData.orElse(null),
                confidence,
                mileageContext);
    }

    private int calculateConfidence(boolean hasMarket, boolean hasReliability, boolean hasBrand) {
        int confidence = 50; // Base
        if (hasMarket)
            confidence += 30;
        if (hasReliability)
            confidence += 15;
        if (hasBrand)
            confidence += 5;
        return Math.min(confidence, 100);
    }

    private String getMileageContext(ModelReliability reliability, long mileage) {
        if (reliability == null || reliability.mileageLogicText() == null) {
            return getDefaultMileageContext(mileage);
        }

        Map<String, String> logicText = reliability.mileageLogicText();
        String key = findClosestMileageKey(logicText, mileage);
        return key != null ? logicText.get(key) : getDefaultMileageContext(mileage);
    }

    private String findClosestMileageKey(Map<String, String> logicText, long mileage) {
        String closestKey = null;
        long closestDiff = Long.MAX_VALUE;

        for (String key : logicText.keySet()) {
            try {
                long keyMileage = Long.parseLong(key);
                long diff = Math.abs(keyMileage - mileage);
                if (diff < closestDiff && diff < 25000) { // Within 25k miles
                    closestDiff = diff;
                    closestKey = key;
                }
            } catch (NumberFormatException ignored) {
            }
        }

        return closestKey;
    }

    private String getDefaultMileageContext(long mileage) {
        if (mileage < 50000) {
            return "Low mileage vehicle with minimal wear. Major repairs unlikely.";
        } else if (mileage < 100000) {
            return "Moderate mileage. Standard maintenance items may be due.";
        } else if (mileage < 150000) {
            return "Higher mileage zone. Inspect drivetrain components carefully.";
        } else {
            return "High mileage vehicle. Major component failures become more probable.";
        }
    }

    // === Helper Methods ===

    private Optional<CarModel> findCarModel(String brand, String modelName) {
        if (modelName == null || modelName.isEmpty()) {
            return Optional.empty();
        }
        return carDataService.findCarBySlug(brand, modelName);
    }

    private String normalizeBrandKey(String brand) {
        if (brand == null)
            return "";
        return brand.toUpperCase()
                .replace("-", "_")
                .replace(" ", "_")
                .trim();
    }

    // === Context Record ===

    public record ValuationContext(
            CarModel carModel,
            ModelMarket marketData,
            ModelReliability reliabilityData,
            CarBrandData brandData,
            int confidence,
            String mileageContext) {
        public boolean hasModelData() {
            return carModel != null && marketData != null;
        }

        public boolean hasReliabilityData() {
            return reliabilityData != null;
        }
    }
}
