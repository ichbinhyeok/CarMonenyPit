package com.carmoneypit.engine.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

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

    public static class MajorIssue {
        public long mileage;
        public String part;
        public long cost;
        @JsonProperty("risk_msg")
        public String riskMsg;
    }
}
