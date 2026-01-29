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

    // --- Asset Value Benchmarks ---
    private static final long BASE_SEDAN = 18_000;
    private static final long BASE_SUV = 24_000;
    private static final long BASE_TRUCK = 32_000;
    private static final long BASE_LUXURY = 28_000;
    private static final long BASE_PERFORMANCE = 35_000;
    private static final long SCRAP_VALUE = 500;

    // --- Heuristic Factors ---
    private static final double FACTOR_LOYALTY_KEEPER = 1.15; // Toyota/Lexus Tax
    private static final double FACTOR_DEPRECIATION_LEASER = 0.85; // Luxury Cliff
    private static final double FACTOR_WORKHORSE_TRUCK = 1.20; // Truck value retention

    private static final double DECAY_DEFAULT = 0.90;
    private static final double DECAY_FLAT = 0.94;
    private static final double DECAY_STEEP = 0.88;
    private static final double DECAY_CLIFF_LOW = 0.90;
    private static final double DECAY_CLIFF_HIGH = 0.85;
    private static final double DECAY_REAL_DATA_SOFTEN = 0.92;

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

    public long estimateValue(CarBrand brand, String modelName, VehicleType type, long mileage) {
        long basePrice;
        Optional<com.carmoneypit.engine.service.CarDataService.ModelMarket> marketData = (modelName != null)
                ? getMarketData(modelName)
                : Optional.empty();

        boolean usingRealMarketData = false;
        if (marketData.isPresent()) {
            basePrice = marketData.get().jan2026AvgPrice();
            usingRealMarketData = true;
        } else {
            basePrice = getBasePrice(type);
        }

        CarBrandData data = brandDataMap.get(brand);
        double brandFactor = 1.0;

        if (!usingRealMarketData && data != null) {
            if ("KEEPER".equals(data.segment))
                brandFactor = FACTOR_LOYALTY_KEEPER;
            else if ("LEASER".equals(data.segment))
                brandFactor = FACTOR_DEPRECIATION_LEASER;
            else if ("WORKHORSE".equals(data.segment) && type == VehicleType.TRUCK_VAN)
                brandFactor = FACTOR_WORKHORSE_TRUCK;
        }

        // 2. Mileage Decay Curve
        double decayRate = DECAY_DEFAULT;
        if (data != null) {
            switch (data.depreciationCurve) {
                case "FLAT" -> decayRate = DECAY_FLAT;
                case "STEEP" -> decayRate = DECAY_STEEP;
                case "CLIFF" -> decayRate = mileage > 50000 ? DECAY_CLIFF_HIGH : DECAY_CLIFF_LOW;
                default -> {
                    if (usingRealMarketData)
                        decayRate = DECAY_REAL_DATA_SOFTEN;
                }
            }
        }

        double timeUnits = (double) mileage / 12000.0; // Normalized years
        double estimatedValue = basePrice * brandFactor * Math.pow(decayRate, timeUnits);

        // 3. Repair Debt Deduction (Uncertainty Penalty)
        if (data != null && data.majorIssues != null) {
            for (CarBrandData.MajorIssue issue : data.majorIssues) {
                if (mileage > issue.mileage) {
                    estimatedValue -= (issue.cost * UNCERTAINTY_PENALTY_RATE);
                }
            }
        }

        return Math.max((long) estimatedValue, SCRAP_VALUE);
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

    private long getBasePrice(VehicleType type) {
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
