package com.carmoneypit.engine.api;

import java.util.List;

public class OutputModels {

    public record VerdictResult(
            VerdictState verdictState,
            String narrativeContext, // Generated dynamic sentence
            VisualizationHint visualizationHint,
            List<FinancialLineItem> costBreakdown) {
    }

    public record VisualizationHint(
            double rfScore,
            double rmScore,
            String moneyPitState // e.g., "Early Stage", "Deep Pit"
    ) {
    }

    public enum VerdictState {
        STABLE, // RF <= RM (significantly)
        BORDERLINE, // RF ~= RM
        TIME_BOMB // RF > RM (significantly)
    }
}
