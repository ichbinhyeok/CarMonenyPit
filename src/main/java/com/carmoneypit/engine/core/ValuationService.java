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

    // Base prices for reference (approximate 5-year old market value)
    private static final long BASE_SEDAN = 18_000;
    private static final long BASE_SUV = 24_000;
    private static final long BASE_TRUCK = 32_000;
    private static final long BASE_LUXURY = 28_000;
    private static final long BASE_PERFORMANCE = 35_000;

    private static final long SCRAP_VALUE = 500;

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

    public long estimateValue(CarBrand brand, VehicleType type, long mileage) {
        long basePrice = getBasePrice(type);
        CarBrandData data = brandDataMap.get(brand);

        // 1. Brand Factor (Reliability Premium / Luxury Tax)
        double brandFactor = 1.0;
        if (data != null) {
            if ("KEEPER".equals(data.segment)) {
                brandFactor = 1.15; // Toyota Tax
            } else if ("LEASER".equals(data.segment)) {
                brandFactor = 0.85; // Depreciation Cliff
            } else if ("WORKHORSE".equals(data.segment) && type == VehicleType.TRUCK_VAN) {
                brandFactor = 1.2; // Truck value retention
            }
        }

        // 2. Mileage Decay Curve
        double decayRate = 0.90; // Standard per 10k miles
        if (data != null) {
            switch (data.depreciationCurve) {
                case "FLAT" -> decayRate = 0.94; // Holds value well
                case "STEEP" -> decayRate = 0.88; // Drops fast
                case "CLIFF" -> decayRate = mileage > 50000 ? 0.85 : 0.90; // Warranty cliff
                default -> decayRate = 0.90;
            }
        }

        double timeUnits = (double) mileage / 12000.0; // Approx years equivalent
        double estimatedValue = basePrice * brandFactor * Math.pow(decayRate, timeUnits);

        // 3. Repair Debt Deduction (Virtual)
        // If the car is in the "Danger Zone" (passed major issue mileage), assume value
        // is hit
        if (data != null && data.majorIssues != null) {
            for (CarBrandData.MajorIssue issue : data.majorIssues) {
                if (mileage > issue.mileage) {
                    // If mileage is past the failure point, market assumes it might be broken or
                    // soon to break
                    // We penalize the value by 50% of the repair cost (uncertainty penalty)
                    estimatedValue -= (issue.cost * 0.6);
                }
            }
        }

        return Math.max((long) estimatedValue, SCRAP_VALUE);
    }

    private long getBasePrice(VehicleType type) {
        switch (type) {
            case SUV:
                return BASE_SUV;
            case TRUCK_VAN:
                return BASE_TRUCK;
            case LUXURY:
                return BASE_LUXURY;
            case PERFORMANCE:
                return BASE_PERFORMANCE;
            case SEDAN:
            default:
                return BASE_SEDAN;
        }
    }

    public long estimateRepairCost(CarBrand brand, VehicleType type, long mileage) {
        CarBrandData data = brandDataMap.get(brand);
        long estimatedCost = 0;

        // 1. Base Maintenance (Tires, Brakes, Fluids)
        long yearlyBase = (data != null) ? data.maintenanceCostYearly : 1000;
        // Accumulate "Deferred" maintenance assumption based on mileage
        // Assume users often defer ~30% of maintenance in the last year
        estimatedCost += (long) (yearlyBase * 0.4);

        // 2. Major Issues (The Killers)
        boolean hasMajorIssue = false;
        if (data != null && data.majorIssues != null) {
            for (CarBrandData.MajorIssue issue : data.majorIssues) {
                // Trigger logic: If mileage is within -5k to +20k of the failure point
                if (mileage >= issue.mileage - 5000 && mileage <= issue.mileage + 20000) {
                    estimatedCost += issue.cost;
                    hasMajorIssue = true;
                }
            }
        }

        // 3. Generic High Mileage Penalty if no specific major issue found yet
        if (!hasMajorIssue && mileage > 100000) {
            long highMileageRisk = (long) (mileage * 0.015); // $1500 for 100k miles
            // Brand multiplier for parts
            if (data != null && "LEASER".equals(data.segment)) {
                highMileageRisk *= 1.5;
            }
            estimatedCost += highMileageRisk;
        }

        return Math.max(estimatedCost, 250); // Minimum visit cost
    }

    public Optional<CarBrandData> getBrandData(CarBrand brand) {
        return Optional.ofNullable(brandDataMap.get(brand));
    }

    public List<com.carmoneypit.engine.service.CarDataService.CarModel> getModelsByBrand(String brand) {
        return carDataService.getModelsByBrand(brand);
    }

    public Optional<com.carmoneypit.engine.service.CarDataService.ModelMarket> getMarketData(String modelName) {
        // Need to find model ID from name
        return carDataService.getAllModels().stream()
                .filter(m -> m.model().equalsIgnoreCase(modelName))
                .findFirst()
                .flatMap(m -> carDataService.findMarketByModelId(m.id()));
    }
}
