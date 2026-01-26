package com.carmoneypit.engine.api;

public class OutputModels {
    public record VerdictResult(
            VerdictState verdictState,
            String narrativeContext, // Generated dynamic sentence
            VisualizationHint visualizationHint) {
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
