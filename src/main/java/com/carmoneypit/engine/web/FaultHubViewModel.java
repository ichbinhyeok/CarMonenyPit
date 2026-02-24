package com.carmoneypit.engine.web;

import java.util.List;

/**
 * Immutable view model for fault hub pages.
 */
public record FaultHubViewModel(
        String slug,
        String displayName,
        String quickAnswer,
        List<AffectedModel> affectedModels,
        List<ReferenceSource> references,
        List<FaqItem> faqItems) {

    public record AffectedModel(
            String brand,
            String model,
            String generation,
            double repairCost,
            double occurrenceRate,
            int avgFailureMileage,
            String symptoms,
            String verdictImplication) {
    }

    public record ReferenceSource(
            String name,
            String url,
            String retrievedAt,
            String note) {
    }

    public record FaqItem(
            String question,
            String answer) {
    }

    public int modelCount() {
        return affectedModels != null ? affectedModels.size() : 0;
    }
}
