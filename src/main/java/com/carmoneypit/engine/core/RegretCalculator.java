package com.carmoneypit.engine.core;

import com.carmoneypit.engine.api.FinancialLineItem;
import com.carmoneypit.engine.api.InputModels.EngineInput;
import com.carmoneypit.engine.api.InputModels.SimulationControls;
import com.carmoneypit.engine.api.InputModels.FailureSeverity;
import com.carmoneypit.engine.api.InputModels.MobilityStatus;
import com.carmoneypit.engine.api.InputModels.HassleTolerance;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RegretCalculator {

    public record RegretDetail(double score, List<FinancialLineItem> items) {
    }

    // Major Repair Cost Constants for Future Risk
    private static final double MAJOR_FAILURE_ENGINE = 4500.0;
    private static final double MAJOR_FAILURE_SUSPENSION = 1800.0;
    private static final double MAJOR_FAILURE_GENERAL = 800.0;

    public RegretDetail calculateRF(EngineInput input, SimulationControls controls) {
        List<FinancialLineItem> items = new ArrayList<>();
        double totalScore = 0;

        // 1. Current Repair Cost
        double repairCost = (double) input.repairQuoteUsd();
        items.add(new FinancialLineItem("Repair Outlay", repairCost, "Immediate cash drain for the quoted service."));
        totalScore += repairCost;

        // 2. Future Failure Probability (Mileage Based + Severity Boost)
        double failureProb = 0.10; // Default
        if (input.mileage() > 150000) {
            failureProb = 0.60;
        } else if (input.mileage() > 100000) {
            failureProb = 0.35;
        }

        double majorCostBase = MAJOR_FAILURE_GENERAL;
        if (controls != null) {
            // [LOGIC FIX 3] Severity Boosts Probability, not just Cost
            if (controls.failureSeverity() == FailureSeverity.ENGINE_TRANSMISSION) {
                majorCostBase = MAJOR_FAILURE_ENGINE;
                failureProb *= 1.5; // "Engine failure implies systemic rot"
                failureProb = Math.min(failureProb, 0.95); // Cap at 95%
            } else if (controls.failureSeverity() == FailureSeverity.SUSPENSION_BRAKES) {
                majorCostBase = MAJOR_FAILURE_SUSPENSION;
            }
        }

        double riskRegret = failureProb * majorCostBase;
        items.add(new FinancialLineItem("Probabilistic Risk", riskRegret,
                String.format("%.0f%% chance of secondary system collapse.", failureProb * 100)));
        totalScore += riskRegret;

        // 3. Lost Exit Timing (Opportunity Cost of not selling now)
        double timingCost = input.currentValueUsd() * 0.05; // 5% penalty for missing the current 'working condition'
                                                            // sale window
        items.add(new FinancialLineItem("Lost Sales Velocity", timingCost,
                "Depreciation penalty for selling a repaired car vs selling now."));
        totalScore += timingCost;

        // 4. Pain Score (Time/Stress + Tow Truck Rage)
        // [LOGIC FIX 2] Mileage Pain Multiplier
        double basePain = (input.repairQuoteUsd() / 10.0);
        double mileagePain = (input.mileage() / 100.0);
        if (input.mileage() > 100000) {
            mileagePain *= 3.0; // "Old cars cause 3x anxiety"
        }

        double painScore = basePain + mileagePain;

        // [LOGIC FIX 1] Tow Truck Paradox (Breakdown Rage)
        if (controls != null && controls.mobilityStatus() == MobilityStatus.NEEDS_TOW) {
            double rageCost = 2000.0;
            painScore += rageCost;
        }

        items.add(new FinancialLineItem("Cognitive Friction", painScore,
                "Actuarial weight for vehicle-related stress and downtime."));
        totalScore += painScore;

        // [LOGIC FIX 4] Retention Horizon Logic Trap
        if (controls != null && controls.retentionHorizon() != null) {
            // No matter the horizon, add a "trap" cost
            double horizonCost = 0;
            String horizonNote = "";
            switch (controls.retentionHorizon()) {
                case MONTHS_6:
                    horizonCost = 1200.0; // Short term: "Too expensive for short utility"
                    horizonNote = "Amortization penalty: Repair costs exceed 6-month utility value.";
                    break;
                case YEARS_1:
                    horizonCost = 1800.0;
                    horizonNote = "Depreciation acceleration during 1-year hold window.";
                    break;
                case YEARS_3:
                case YEARS_5:
                    horizonCost = 3500.0; // Long term: "Guaranteed failure cascade"
                    horizonNote = "Long-term hold maximizes exposure to catastrophic failure nodes.";
                    break;
            }
            items.add(new FinancialLineItem("Retention Exposure", horizonCost, horizonNote));
            totalScore += horizonCost;
        }

        return new RegretDetail(totalScore, items);
    }

    public RegretDetail calculateRM(EngineInput input, SimulationControls controls) {
        List<FinancialLineItem> items = new ArrayList<>();
        double totalScore = 0;

        // 1. Switching Friction (Hassle Tolerance)
        double friction = 2500.0; // NEUTRAL
        if (controls != null) {
            if (controls.hassleTolerance() == HassleTolerance.HATE_SWITCHING)
                friction = 5500.0;
            else if (controls.hassleTolerance() == HassleTolerance.WANT_NEW_CAR)
                friction = 800.0;
        }
        items.add(new FinancialLineItem("Acquisition Friction", friction,
                "Cost of searching, negotiating, and registering a new vehicle."));
        totalScore += friction;

        // 2. Temporary Inconvenience (Mobility Based)
        double mobilityRegret = 0;
        if (controls != null && controls.mobilityStatus() == MobilityStatus.NEEDS_TOW) {
            mobilityRegret = 1800.0; // High regret for switching when car is offline (bad leverage)
            items.add(new FinancialLineItem("Offline Leverage Loss", mobilityRegret,
                    "Reduced trade-in value and urgency penalty for non-runners."));
        } else {
            items.add(new FinancialLineItem("Market Leverage", 0.0,
                    "Maintain leverage by switching while the asset is mobile."));
        }
        totalScore += mobilityRegret;

        // 3. Premature Exit Anxiety
        double anxiety = 400.0;
        items.add(new FinancialLineItem("Sunk Cost Anxiety", anxiety,
                "Psychological weight of leaving a familiar asset prematurely."));
        totalScore += anxiety;

        return new RegretDetail(totalScore, items);
    }
}
