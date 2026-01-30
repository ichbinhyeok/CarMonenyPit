package com.carmoneypit.engine.core;

import com.carmoneypit.engine.api.InputModels.CarBrand;
import com.carmoneypit.engine.api.InputModels.VehicleType;
import com.carmoneypit.engine.data.CarBrandData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ValuationService {

    private final ObjectMapper objectMapper;
    private final com.carmoneypit.engine.service.CarDataService carDataService;
    private Map<CarBrand, CarBrandData> brandDataMap = new HashMap<>();

    // --- Asset Value Benchmarks (2026 MSRP Proxy) ---
    private static final long BASE_SEDAN = 32_000;
    private static final long BASE_SUV = 38_000;
    private static final long BASE_TRUCK = 55_000;
    private static final long BASE_LUXURY = 65_000;
    private static final long BASE_PERFORMANCE = 85_000;
    private static final long SCRAP_VALUE = 500;

    // --- Heuristic Factors ---
    private static final double FACTOR_LOYALTY_KEEPER = 1.15; // Toyota/Lexus Tax
    private static final double FACTOR_DEPRECIATION_LEASER = 0.85; // Luxury Cliff
    private static final double FACTOR_WORKHORSE_TRUCK = 1.20; // Truck value retention

    // --- Depreciation Curves (Retention Rates) ---
    private static final double RETENTION_FLAT = 0.94; // Toyota, Lexus fits here
    private static final double RETENTION_NORMAL = 0.88; // Ford, Chevy
    private static final double RETENTION_STEEP = 0.85; // Nissan, Hyundai
    private static final double RETENTION_CLIFF = 0.80; // BMW, Land Rover

    private static final double UNCERTAINTY_PENALTY_RATE = 0.60;
    private static final double DEFERRED_MAINTENANCE_RATE = 0.40;

    // --- Risk Multipliers (per mile) ---
    private static final double RISK_DEFAULT = 0.015;
    private static final double RISK_KEEPER = 0.008;
    private static final double RISK_WORKHORSE = 0.012;
    private static final double RISK_PERFORMANCE = 0.025;
    private static final double RISK_TECH = 0.020;

    public ValuationService(ObjectMapper objectMapper, com.carmoneypit.engine.service.CarDataService carDataService) {
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

                for (Map.Entry<String, CarBrandData> entry : data.entrySet()) {
                    try {
                        CarBrand brand = CarBrand.valueOf(entry.getKey());
                        brandDataMap.put(brand, entry.getValue());
                    } catch (IllegalArgumentException e) {
                        System.err.println("Warning: JSON contains brand not in Enum: " + entry.getKey());
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load car_brands.json", e);
        }
    }

    public long estimateValue(CarBrand brand, String modelName, VehicleType type, int year, long mileage) {
        // 1. Determine Base MSRP
        long msrp = getBasePrice(type);

        // Boost MSRP based on Brand Segment
        CarBrandData data = brandDataMap.get(brand);

        // 2. Calculate Age
        int currentYear = 2026;
        int age = Math.max(1, currentYear - year); // Min 1 year old

        // 3. Determine Retention Rate (Depreciation Curve)
        double retentionRate = RETENTION_NORMAL;
        if (data != null) {
            switch (data.depreciationCurve) {
                case "FLAT" -> retentionRate = RETENTION_FLAT;
                case "STEEP" -> retentionRate = RETENTION_STEEP;
                case "CLIFF" -> retentionRate = RETENTION_CLIFF;
                default -> retentionRate = RETENTION_NORMAL;
            }
        }

        // 4. Calculate Age-Based Value
        double baseValue = msrp * Math.pow(retentionRate, age);

        // 5. Mileage Adjuster (The "Usage" Factor)
        // Standard usage: 12,000 miles / year
        double expectedMileage = age * 12_000.0;
        double usageRatio = (double) mileage / Math.max(1, expectedMileage);

        // Formula: Adjuster = 1.0 + (1.0 - Ratio) * 0.15
        // Example: Ratio 0.5 (Half usage) -> 1.0 + 0.5*0.15 = 1.075 (+7.5%)
        // Example: Ratio 2.0 (Double usage) -> 1.0 - 1.0*0.15 = 0.85 (-15%)
        double mileageAdjuster = 1.0 + (1.0 - usageRatio) * 0.15;

        // Cap the adjuster to avoid extreme multipliers (Safe Range: 0.5x to 1.25x)
        mileageAdjuster = Math.max(0.5, Math.min(1.25, mileageAdjuster));

        // 6. Final Calculation with Brand Heuristics
        double brandFactor = 1.0;
        if (data != null) {
            if ("KEEPER".equals(data.segment))
                brandFactor = 1.10; // Extra loyalty bump
            if ("CULT".equals(data.segment))
                brandFactor = 1.15; // Jeep/Subaru hold value insanely well
        }

        double finalValue = baseValue * mileageAdjuster * brandFactor;

        // 7. Repair Debt (If known major issues based on mileage)
        if (data != null && data.majorIssues != null) {
            for (CarBrandData.MajorIssue issue : data.majorIssues) {
                if (mileage > issue.mileage) {
                    finalValue -= (issue.cost * UNCERTAINTY_PENALTY_RATE);
                }
            }
        }

        return Math.max((long) finalValue, SCRAP_VALUE);
    }

    public long estimateRepairCost(CarBrand brand, VehicleType type, long mileage) {
        CarBrandData data = brandDataMap.get(brand);
        long estimatedCost = 0;

        // 1. Deferred Maintenance Assumption
        long yearlyBase = (data != null) ? data.maintenanceCostYearly : 1000;
        estimatedCost += (long) (yearlyBase * DEFERRED_MAINTENANCE_RATE);

        // 2. Failure Points (The Killers)
        boolean hasMajorIssue = false;
        if (data != null && data.majorIssues != null) {
            for (CarBrandData.MajorIssue issue : data.majorIssues) {
                if (mileage >= issue.mileage - 5000 && mileage <= issue.mileage + 20000) {
                    estimatedCost += issue.cost;
                    hasMajorIssue = true;
                }
            }
        }

        // 3. Segment-Aware High Mileage Risk
        if (!hasMajorIssue && mileage > 100000) {
            double riskMultiplier = RISK_DEFAULT;
            if (data != null) {
                switch (data.segment) {
                    case "KEEPER" -> riskMultiplier = RISK_KEEPER;
                    case "WORKHORSE" -> riskMultiplier = RISK_WORKHORSE;
                    case "LEASER", "PERFORMANCE" -> riskMultiplier = RISK_PERFORMANCE;
                    case "TECH" -> riskMultiplier = RISK_TECH;
                }
            }
            estimatedCost += (long) (mileage * riskMultiplier);
        }

        return Math.max(estimatedCost, 250);
    }

    public long getBasePrice(VehicleType type) {
        return switch (type) {
            case SUV -> BASE_SUV;
            case TRUCK_VAN -> BASE_TRUCK;
            case LUXURY -> BASE_LUXURY;
            case PERFORMANCE -> BASE_PERFORMANCE;
            case SEDAN -> BASE_SEDAN;
            default -> BASE_SEDAN;
        };
    }

    public Optional<CarBrandData> getBrandData(CarBrand brand) {
        return Optional.ofNullable(brandDataMap.get(brand));
    }

    public List<com.carmoneypit.engine.service.CarDataService.CarModel> getModelsByBrand(String brand) {
        return carDataService.getModelsByBrand(brand);
    }

    public Optional<com.carmoneypit.engine.service.CarDataService.ModelMarket> getMarketData(String modelName) {
        return carDataService.getAllModels().stream()
                .filter(m -> m.model().equalsIgnoreCase(modelName))
                .findFirst()
                .flatMap(m -> carDataService.findMarketByModelId(m.id()));
    }
}
