package com.carmoneypit.engine.data;

import com.carmoneypit.engine.service.CarDataService;
import com.carmoneypit.engine.service.CarDataService.CarModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class DataIntegrityTest {

    private CarDataService carDataService;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        carDataService = new CarDataService(objectMapper);
    }

    @Test
    public void testDataLoadingSuccess() {
        List<CarModel> models = carDataService.getAllModels();
        assertFalse(models.isEmpty(), "Car models should not be empty. JSON parsing might have failed.");
    }

    @Test
    public void testModelIdReferentialIntegrity() {
        List<CarModel> models = carDataService.getAllModels();
        Set<String> modelIds = models.stream().map(CarModel::id).collect(Collectors.toSet());

        // Check Major Faults
        models.forEach(model -> {
            carDataService.findFaultsByModelId(model.id()).ifPresent(faults -> {
                assertTrue(modelIds.contains(faults.modelIdRef()),
                        "Foreign key modelIdRef missing in car_models: " + faults.modelIdRef());

                // Assert no thin data
                assertFalse(faults.faults().isEmpty(), "Major faults list should not be empty if defined.");
                faults.faults().forEach(fault -> {
                    assertFalse(fault.component() == null || fault.component().isBlank(), "Fault component missing");
                    assertFalse(fault.symptoms() == null || fault.symptoms().isBlank(), "Fault symptoms missing");
                    assertFalse(fault.verdictImplication() == null || fault.verdictImplication().isBlank(),
                            "Verdict implication missing");
                    assertTrue(fault.occurrenceRate() >= 0.0 && fault.occurrenceRate() <= 1.0,
                            "Occurrence rate must be between 0.0 and 1.0. Found: " + fault.occurrenceRate());
                    assertTrue(fault.avgFailureMileage() >= 0, "Average failure mileage must be >= 0");
                });
            });

            // Check Reliability
            carDataService.findReliabilityByModelId(model.id()).ifPresent(reliability -> {
                assertTrue(modelIds.contains(reliability.modelId()),
                        "Foreign key modelId missing for reliability: " + reliability.modelId());
                // Mild warning if common Trouble spots missing (but we assert true here or just
                // ensure it's loaded)
            });

            // Check Market Context
            carDataService.findMarketByModelId(model.id()).ifPresent(market -> {
                assertTrue(modelIds.contains(market.modelId()),
                        "Foreign key modelId missing for market data: " + market.modelId());
            });
        });
    }

    @Test
    public void testSlugGenerationUniqueness() {
        List<CarModel> models = carDataService.getAllModels();
        java.util.Map<String, Long> countMap = models.stream()
                .collect(Collectors.groupingBy(
                        c -> normalize(c.brand()) + "|" + normalize(c.model()),
                        Collectors.counting()));

        List<String> duplicates = countMap.entrySet().stream()
                .filter(e -> e.getValue() > 1)
                .map(java.util.Map.Entry::getKey)
                .toList();

        if (!duplicates.isEmpty()) {
            System.err.println("Duplicate slugs found: " + duplicates);
        }

        assertEquals(models.size(), countMap.size(),
                "There are duplicate (brand, model) slug combinations: " + duplicates);
    }

    private String normalize(String input) {
        return input == null ? "" : input.toLowerCase().replaceAll("[^a-z0-9]", "");
    }
}
