package com.carmoneypit.engine.api;

import java.util.List;

public class OutputModels {

        public record VerdictResult(
                        VerdictState verdictState,
                        String narrativeContext,
                        VisualizationHint visualizationHint,
                        List<FinancialLineItem> costBreakdown,
                        long assetBleedAmount,
                        double stayTotal,
                        double moveTotal,
                        int confidence,
                        PeerData peerData,
                        EconomicContext economicContext) {
        }

        public record PeerData(
                        int sellPercentage,
                        int repairPercentage,
                        double avgSatisfaction) {
        }

        public record EconomicContext(
                        long totalSwitchingFriction,
                        long avgMonthlyPayment,
                        long breakdownThreshold) {
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
