package com.carmoneypit.engine.data;

import com.carmoneypit.engine.service.CarDataService;
import com.carmoneypit.engine.service.CarDataService.CarModel;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataCoverageAuditTest {

    private CarDataService carDataService;

    @BeforeEach
    void setUp() {
        carDataService = new CarDataService(new ObjectMapper());
    }

    @Test
    void sourceCoverageShouldStayAboveCurrentBaselines() {
        List<CarModel> models = carDataService.getAllModels();
        long reliabilityCount = models.stream()
                .filter(model -> carDataService.findReliabilityByModelId(model.id()).isPresent())
                .count();
        long marketCount = models.stream()
                .filter(model -> carDataService.findMarketByModelId(model.id()).isPresent())
                .count();
        long faultCount = models.stream()
                .filter(model -> carDataService.findFaultsByModelId(model.id()).isPresent())
                .count();

        assertEquals(models.size(), reliabilityCount, "Every model should have reliability data.");
        assertEquals(models.size(), marketCount, "Every model should have market data.");
        assertTrue(faultCount >= Math.round(models.size() * 0.75),
                "At least 75% of models should have major fault coverage. Found " + faultCount + " of " + models.size());
    }

    @Test
    void fortyThousandDollarPlusModelsShouldHaveFaultCoverage() {
        List<CarModel> models = carDataService.getAllModels();
        List<String> uncoveredHighValueModels = models.stream()
                .filter(model -> carDataService.findMarketByModelId(model.id())
                        .map(market -> market.jan2026AvgPrice() >= 40000)
                        .orElse(false))
                .filter(model -> carDataService.findFaultsByModelId(model.id()).isEmpty())
                .map(CarModel::id)
                .toList();

        assertTrue(uncoveredHighValueModels.isEmpty(),
                "High-value models should not ship without major fault coverage. Missing: " + uncoveredHighValueModels);
    }

    @Test
    void sourceFilesShouldStayReferentiallyConsistent() throws IOException {
        List<CarModel> models = carDataService.getAllModels();
        Set<String> modelIds = models.stream()
                .map(CarModel::id)
                .collect(Collectors.toSet());

        JsonNode reliabilityRecords = readArray("data/model_reliability.json");
        JsonNode marketRecords = readArray("data/model_market.json");
        JsonNode faultRecords = readArray("data/major_faults.json");

        assertAllIdsResolve(reliabilityRecords, "model_id", modelIds, "Reliability");
        assertAllIdsResolve(marketRecords, "model_id", modelIds, "Market");
        assertAllIdsResolve(faultRecords, "model_id_ref", modelIds, "Fault");
    }

    @Test
    void numericRangesShouldLookCommerciallyPlausible() {
        for (CarModel model : carDataService.getAllModels()) {
            carDataService.findReliabilityByModelId(model.id()).ifPresent(reliability -> {
                assertTrue(reliability.lifespanMiles() >= 100000 && reliability.lifespanMiles() <= 450000,
                        "Lifespan miles out of expected range for " + model.id());
            });

            carDataService.findMarketByModelId(model.id()).ifPresent(market -> {
                assertTrue(market.jan2026AvgPrice() >= 1000 && market.jan2026AvgPrice() <= 200000,
                        "Market value out of expected range for " + model.id());
                assertTrue(market.depreciationRate() > 0.0 && market.depreciationRate() <= 0.35,
                        "Depreciation rate out of expected range for " + model.id());
                assertTrue(market.avgAnnualRepairCost() >= 250 && market.avgAnnualRepairCost() <= 5000,
                        "Annual repair cost out of expected range for " + model.id());
            });

            carDataService.findFaultsByModelId(model.id()).ifPresent(faults -> faults.faults().forEach(fault -> {
                assertTrue(fault.repairCost() >= 0 && fault.repairCost() <= 20000,
                        "Fault repair cost out of expected range for " + model.id() + ": " + fault.component());
                assertTrue(fault.avgFailureMileage() >= 0 && fault.avgFailureMileage() <= 300000,
                        "Fault mileage out of expected range for " + model.id() + ": " + fault.component());
            }));
        }
    }

    private JsonNode readArray(String classpathLocation) throws IOException {
        return new ObjectMapper().readTree(new ClassPathResource(classpathLocation).getInputStream());
    }

    private void assertAllIdsResolve(JsonNode records, String idField, Set<String> modelIds, String label) {
        for (JsonNode record : records) {
            String id = record.path(idField).asText("");
            assertTrue(!id.isBlank(), label + " record is missing " + idField + ".");
            assertTrue(modelIds.contains(id), label + " record points at unknown model id: " + id);
        }
    }
}
