package com.carmoneypit.engine.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CarBrandData {
    public String name;
    public String segment; // KEEPER, LEASER, etc
    @JsonProperty("reliability_score")
    public double reliabilityScore;
    @JsonProperty("maintenance_cost_yearly")
    public long maintenanceCostYearly;
    @JsonProperty("depreciation_curve")
    public String depreciationCurve; // FLAT, STEEP, CLIFF
    @JsonProperty("death_mileage")
    public long deathMileage;
    @JsonProperty("verdict_bias")
    public String verdictBias; // FIX, SELL
    @JsonProperty("market_perception")
    public String marketPerception;
    @JsonProperty("major_issues")
    public List<MajorIssue> majorIssues;
    @JsonProperty("avg_switching_friction")
    public long avgSwitchingFriction;
    @JsonProperty("avg_new_monthly")
    public int avgNewMonthly;
    @JsonProperty("sell_stat_pct")
    public int sellStatPct;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MajorIssue {
        public long mileage;
        public String part;
        public long cost;
        @JsonProperty("risk_msg")
        public String riskMsg;
    }
}
