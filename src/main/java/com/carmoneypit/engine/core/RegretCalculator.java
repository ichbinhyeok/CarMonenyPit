package com.carmoneypit.engine.core;

import com.carmoneypit.engine.api.FinancialLineItem;
import com.carmoneypit.engine.api.ItemCategory;
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

    private final com.carmoneypit.engine.service.CarDataService carDataService;

    public RegretCalculator(com.carmoneypit.engine.service.CarDataService carDataService) {
        this.carDataService = carDataService;
    }

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
        items.add(new FinancialLineItem("Retained Asset Repair Outlay", repairCost,
                "Immediate cash liquidity required for the current restoration.", ItemCategory.STAY));
        totalScore += repairCost;

        // 2. Future Failure Probability (Mileage Based + Severity Boost)
        double failureProb = 0.10; // Default
        if (input.mileage() > 150000) {
            failureProb = 0.60;
        } else if (input.mileage() > 100000) {
            failureProb = 0.35;
        }

        double majorCostBase = MAJOR_FAILURE_GENERAL;

        // --- DYNAMIC FAULT LOOKUP (The Expert Move) ---
        if (input.model() != null && !input.model().isBlank()) {
            var modelOpt = carDataService.getAllModels().stream()
                    .filter(m -> m.model().equalsIgnoreCase(input.model()))
                    .findFirst();

            if (modelOpt.isPresent()) {
                var faultsOpt = carDataService.findFaultsByModelId(modelOpt.get().id());
                if (faultsOpt.isPresent()) {
                    var faultsList = faultsOpt.get().faults();
                    if (!faultsList.isEmpty()) {
                        majorCostBase = faultsList.stream()
                                .mapToDouble(f -> f.repairCost())
                                .max().orElse(MAJOR_FAILURE_GENERAL);
                    }
                }
            }
        }

        if (controls != null) {
            // Severity Boosts Probability
            if (controls.failureSeverity() == FailureSeverity.ENGINE_TRANSMISSION) {
                // If we didn't find a monster fault in JSON, ensure it's at least the base
                majorCostBase = Math.max(majorCostBase, MAJOR_FAILURE_ENGINE);
                failureProb *= 1.5;
                failureProb = Math.min(failureProb, 0.95);
            } else if (controls.failureSeverity() == FailureSeverity.SUSPENSION_BRAKES) {
                majorCostBase = Math.max(majorCostBase, MAJOR_FAILURE_SUSPENSION);
            }
        }

        double riskRegret = failureProb * majorCostBase;
        items.add(new FinancialLineItem("Secondary failure risk", riskRegret,
                String.format("[Fault DB] %d%% actuarial probability of adjacent system collapse.",
                        (int) (failureProb * 100)),
                ItemCategory.STAY));
        totalScore += riskRegret;

        // 3. Lost Exit Timing (Opportunity Cost of not selling now)
        double timingCost = input.currentValueUsd() * 0.05; // 5% penalty for missing the current 'working condition'
                                                            // sale window
        items.add(new FinancialLineItem("Opportunity Cost of Delay", timingCost,
                "[Market Index] The economic cost of missing the optimal asset disposal window.", ItemCategory.STAY));
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

        items.add(new FinancialLineItem("Operational Life Friction", painScore,
                "Monetary valuation of unplanned downtime and quality of life impact.", ItemCategory.STAY));
        totalScore += painScore;

        // [LOGIC FIX 4] Retention Horizon Logic Trap
        if (controls != null && controls.retentionHorizon() != null) {
            // No matter the horizon, add a "trap" cost
            double horizonCost = 0;
            String horizonNote = "";
            switch (controls.retentionHorizon()) {
                case MONTHS_6:
                    horizonCost = 1200.0; // Short term: "Too expensive for short utility"
                    horizonNote = "Low utility-to-cost ratio for short-term retention.";
                    break;
                case YEARS_1:
                    horizonCost = 1800.0;
                    horizonNote = "Accelerated asset value erosion over 12-month window.";
                    break;
                case YEARS_3:
                case YEARS_5:
                    horizonCost = 3500.0; // Long term: "Guaranteed failure cascade"
                    horizonNote = "Guaranteed compounding liability over extended hold period.";
                    break;
            }
            items.add(
                    new FinancialLineItem("Retention Horizon Liability", horizonCost,
                            "[Actuarial Bench] " + horizonNote, ItemCategory.STAY));
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
        items.add(new FinancialLineItem("Market Transaction Overhead", friction,
                "Estimated loss from taxes, dealer margins, and acquisition labor.", ItemCategory.MOVE));
        totalScore += friction;

        // 2. Temporary Inconvenience (Mobility Based)
        double mobilityRegret = 0;
        if (controls != null && controls.mobilityStatus() == MobilityStatus.NEEDS_TOW) {
            mobilityRegret = 1800.0;
            items.add(new FinancialLineItem("Asset Liquidity Penalty", mobilityRegret,
                    "Immediate reduction in disposal value due to non-operational status.", ItemCategory.MOVE));
        } else {
            items.add(new FinancialLineItem("Disposal Leverage Benefit", 0.0,
                    "Current operational status minimizes loss during asset liquidation.", ItemCategory.MOVE));
        }
        totalScore += mobilityRegret;

        // 3. Premature Exit Anxiety
        double anxiety = 400.0;
        items.add(new FinancialLineItem("Sunk Cost Attachment Tax", anxiety,
                "Irrational psychological resistance to terminating current ownership.", ItemCategory.MOVE));
        totalScore += anxiety;

        return new RegretDetail(totalScore, items);
    }
}
