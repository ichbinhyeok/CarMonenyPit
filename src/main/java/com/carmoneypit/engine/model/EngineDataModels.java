package com.carmoneypit.engine.model;

import java.util.Map;

public class EngineDataModels {

        public record EngineData(
                        FailureCascadeProbability failure_cascade_probability,
                        ConversionRules conversion_rules,
                        Map<String, CostRange> major_repair_cost_range,
                        Map<String, Double> depreciation_multipliers,
                        Map<String, Double> sellability_score,
                        Map<String, PainIndex> pain_index,
                        Map<String, Map<String, Double>> residual_value_ratio,
                        Map<String, Double> brand_reputation_group_multiplier,
                        OpportunityCostFloor opportunity_cost_floor) {
        }

        public record FailureCascadeProbability(
                        Map<String, Map<String, RiskProbability>> mileage_intervals,
                        double cascade_multiplier) {
        }

        public record RiskProbability(
                        String risk_level,
                        double base_prob) {
        }

        public record ConversionRules(
                        int standard_divisor,
                        String description) {
        }

        public record CostRange(
                        int min_points,
                        int max_points) {
        }

        public record PainIndex(
                        int downtime_days_avg,
                        double stress_score_coeff) {
        }

        public record OpportunityCostFloor(
                        double value_ratio,
                        String loss_state_on_failure,
                        String exit_window) {
        }

        // Empty constructor to prevent instantiation if needed, but not strictly
        // necessary for data holder
}
