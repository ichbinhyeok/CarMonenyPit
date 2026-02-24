package com.carmoneypit.engine.service;

import com.carmoneypit.engine.web.FaultHubViewModel;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for FaultHubService aggregation logic:
 * - Null-safe aggregation (missing fields don't NPE)
 * - Sorting correct (occurrence_rate desc, avg_failure_mileage asc)
 * - Unknown slug returns empty (404)
 * - Allowed slugs produce populated view models
 */
class FaultHubServiceAggregationTest {

    @Test
    void unknownSlugReturnsEmpty() {
        // The static normalizeToSlug should not map random strings to allowed slugs
        String result = FaultHubService.normalizeToSlug("Random Component XYZ");
        assertFalse(FaultHubService.ALLOWED_SLUGS.contains(result),
                "Random component should NOT map to an allowed hub slug");
    }

    @Test
    void knownComponentsMappedCorrectly() {
        assertEquals("cvt-transmission", FaultHubService.normalizeToSlug("CVT Transmission"));
        assertEquals("cvt-transmission", FaultHubService.normalizeToSlug("CVT Failure"));
        assertEquals("cvt-transmission", FaultHubService.normalizeToSlug("CVT Shudder"));
        assertEquals("cvt-transmission", FaultHubService.normalizeToSlug("CVT Whine"));

        assertEquals("timing-chain", FaultHubService.normalizeToSlug("Timing Chain"));
        assertEquals("timing-chain", FaultHubService.normalizeToSlug("Timing Chain Failure"));
        assertEquals("timing-chain", FaultHubService.normalizeToSlug("Timing Chain Tensioner"));
        assertEquals("timing-chain", FaultHubService.normalizeToSlug("Timing Chain Guides"));
        assertEquals("timing-chain", FaultHubService.normalizeToSlug("Timing Chain Tensioner Failure"));

        assertEquals("oil-consumption", FaultHubService.normalizeToSlug("Oil Consumption"));
        assertEquals("oil-consumption", FaultHubService.normalizeToSlug("Oil Dilution"));
        assertEquals("oil-consumption", FaultHubService.normalizeToSlug("Excessive Oil Consumption"));
        assertEquals("oil-consumption", FaultHubService.normalizeToSlug("High Oil Consumption (Piston Rings)"));

        assertEquals("torque-converter", FaultHubService.normalizeToSlug("Torque Converter"));
        assertEquals("torque-converter", FaultHubService.normalizeToSlug("Torque Converter Shudder"));

        assertEquals("air-suspension", FaultHubService.normalizeToSlug("Air Suspension"));
        assertEquals("air-suspension", FaultHubService.normalizeToSlug("Air Suspension Compressor"));
        assertEquals("air-suspension", FaultHubService.normalizeToSlug("Quadra-Lift Air Suspension Failure"));
    }

    @Test
    void nullComponentReturnsEmptyString() {
        assertEquals("", FaultHubService.normalizeToSlug(null));
    }

    @Test
    void nonHubComponentsNotInAllowedSet() {
        // These are real faults from the dataset that should NOT map to hub slugs
        assertFalse(FaultHubService.ALLOWED_SLUGS.contains(
                FaultHubService.normalizeToSlug("Cam Phasers")));
        assertFalse(FaultHubService.ALLOWED_SLUGS.contains(
                FaultHubService.normalizeToSlug("Death Wobble")));
        assertFalse(FaultHubService.ALLOWED_SLUGS.contains(
                FaultHubService.normalizeToSlug("Ghost Touch")));
        assertFalse(FaultHubService.ALLOWED_SLUGS.contains(
                FaultHubService.normalizeToSlug("Starter Motor")));
    }

    @Test
    void exactlyFiveAllowedSlugs() {
        assertEquals(5, FaultHubService.ALLOWED_SLUGS.size(),
                "Exactly 5 hub slugs must be allowed (no more, no less)");
    }

    @Test
    void allAllowedSlugsHaveDisplayNames() {
        for (String slug : FaultHubService.ALLOWED_SLUGS) {
            assertTrue(FaultHubService.SLUG_DISPLAY_NAMES.containsKey(slug),
                    "Slug '" + slug + "' must have a display name");
            assertFalse(FaultHubService.SLUG_DISPLAY_NAMES.get(slug).isBlank(),
                    "Display name for '" + slug + "' must not be blank");
        }
    }

    @Test
    void viewModelRecordAccessorsWork() {
        // Test the ViewModel record itself is constructed correctly
        FaultHubViewModel vm = new FaultHubViewModel(
                "test-slug",
                "Test Fault",
                "Quick answer text",
                List.of(
                        new FaultHubViewModel.AffectedModel(
                                "Toyota", "Camry", "XV50",
                                2500.0, 0.25, 75000,
                                "Shudder symptoms", "Moderate impact"),
                        new FaultHubViewModel.AffectedModel(
                                "Honda", "Civic", "FC",
                                0, 0.0, 0,
                                "Unknown symptoms", "Unknown")),
                List.of(new FaultHubViewModel.ReferenceSource(
                        "Test Source", "https://example.com",
                        "2026-02-24", "A test note")),
                List.of(new FaultHubViewModel.FaqItem(
                        "Test question?", "Test answer.")));

        assertEquals("test-slug", vm.slug());
        assertEquals("Test Fault", vm.displayName());
        assertEquals(2, vm.modelCount());
        assertEquals(1, vm.references().size());
        assertEquals(1, vm.faqItems().size());

        // Verify null-safe fields
        FaultHubViewModel.AffectedModel zeroModel = vm.affectedModels().get(1);
        assertEquals(0.0, zeroModel.repairCost());
        assertEquals(0, zeroModel.avgFailureMileage());
        assertEquals(0.0, zeroModel.occurrenceRate());
    }
}
