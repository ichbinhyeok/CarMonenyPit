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
    private final ValuationService valuationService;

    public RegretCalculator(com.carmoneypit.engine.service.CarDataService carDataService,
            ValuationService valuationService) {
        this.carDataService = carDataService;
        this.valuationService = valuationService;
    }

    public record RegretDetail(double score, List<FinancialLineItem> items) {
    }

    // --- DECISION ENGINE TUNING CONSTANTS ---
    // Objective Maintenance & Risk
    private static final double MAJOR_FAILURE_ENGINE = 4500.0;
    private static final double MAJOR_FAILURE_SUSPENSION = 1800.0;
    private static final double MAJOR_FAILURE_GENERAL = 800.0;
    private static final double OPPORTUNITY_COST_RATE = 0.05; // 5% of value

    // Stress & Lifestyle (Reduced Bias)
    private static final double RAGE_COST = 1500.0; // Reduced from 2000
    private static final double MILEAGE_PAIN_COEFFICIENT = 0.01; // 1/100
    private static final double MILEAGE_THRHESOLD = 100000.0;
    private static final double MILEAGE_STRESS_GROWTH = 1.5; // Reduced from 3.0x jump
    private static final double REPAIR_PAIN_COEFFICIENT = 0.05; // 5% of quote (Reduced from 0.10)

    // Replacement Economics
    private static final double NEW_CAR_FIRST_YEAR_DEPRECIATION = 0.12; // 12% off-the-lot hit
    private static final double FRESH_START_MULTIPLIER = 0.85; // Multiplier for "Fresh Start" gain (Reduced from 1.25)
    private static final double TRANSACTION_FRICTION_DEFAULT = 2500.0;
    private static final double TRANSACTION_FRICTION_HASSLE = 5500.0;
    private static final double TRANSACTION_FRICTION_WANT_NEW = 800.0;
    private static final double TRADE_IN_SPREAD_RATE = 0.15; // 15% dealer margin

    public RegretDetail calculateRF(EngineInput input, SimulationControls controls) {
        List<FinancialLineItem> items = new ArrayList<>();
        double totalScore = 0;

        // 1. Current Repair Cost
        double repairCost = (double) input.repairQuoteUsd();
        items.add(new FinancialLineItem("Immediate Repair Bill", repairCost,
                "Immediate cash liquidity required for the current restoration.", ItemCategory.STAY));
        totalScore += repairCost;

        // 2. Future Failure Probability
        double failureProb = 0.10;
        if (input.mileage() > 150000) {
            failureProb = 0.55;
        } else if (input.mileage() > 100000) {
            failureProb = 0.30;
        }

        double majorCostBase = MAJOR_FAILURE_GENERAL;

        // DYNAMIC FAULT LOOKUP
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
            if (controls.failureSeverity() == FailureSeverity.ENGINE_TRANSMISSION) {
                majorCostBase = Math.max(majorCostBase, MAJOR_FAILURE_ENGINE);
                failureProb = Math.min(failureProb * 1.5, 0.90);
            } else if (controls.failureSeverity() == FailureSeverity.SUSPENSION_BRAKES) {
                majorCostBase = Math.max(majorCostBase, MAJOR_FAILURE_SUSPENSION);
            }
        }

        double riskRegret = failureProb * majorCostBase;
        items.add(new FinancialLineItem("Risk of Next Breakdown", riskRegret,
                String.format("[Actuarial] %d%% probability of adjacent system failure.",
                        (int) (failureProb * 100)),
                ItemCategory.STAY));
        totalScore += riskRegret;

        // 3. Asset Opportunity Cost
        double timingCost = input.currentValueUsd() * OPPORTUNITY_COST_RATE;
        items.add(new FinancialLineItem("Opportunity Cost of Delay", timingCost,
                "The economic cost of holding a depreciating asset instead of cash.", ItemCategory.STAY));
        totalScore += timingCost;

        // 4. Stress & Downtime (Logarithmic/Smoothed Growth)
        double basePain = (input.repairQuoteUsd() * REPAIR_PAIN_COEFFICIENT);
        double mileagePain = (input.mileage() * MILEAGE_PAIN_COEFFICIENT);
        if (input.mileage() > MILEAGE_THRHESOLD) {
            // Apply a smoother transition than 3x
            mileagePain *= MILEAGE_STRESS_GROWTH;
        }

        double painScore = basePain + mileagePain;
        if (controls != null && controls.mobilityStatus() == MobilityStatus.NEEDS_TOW) {
            painScore += RAGE_COST;
        }

        items.add(new FinancialLineItem("Stress & Reliability Tax", painScore,
                "Valuation of unplanned downtime and quality of life impact.", ItemCategory.STAY));
        totalScore += painScore;

        // 5. Retention Horizon & Amortization
        if (controls != null && controls.retentionHorizon() != null) {
            double horizonCost = 0;
            double horizonYears = 1.0;
            switch (controls.retentionHorizon()) {
                case MONTHS_6 -> {
                    horizonCost = 800.0;
                    horizonYears = 0.5;
                }
                case YEARS_1 -> {
                    horizonCost = 1200.0;
                    horizonYears = 1.0;
                }
                case YEARS_3 -> {
                    horizonCost = 2500.0;
                    horizonYears = 3.0;
                }
                case YEARS_5 -> {
                    horizonCost = 3000.0;
                    horizonYears = 5.0;
                }
            }
            items.add(new FinancialLineItem("Maintenance Horizon Liability", horizonCost,
                    "Projected wear-and-tear costs over the selected ownership window.", ItemCategory.STAY));
            totalScore += horizonCost;

            // AMORTIZATION BENEFIT: If you keep it, the repair cost is "spent" over time.
            if (horizonYears >= 1.0) {
                double amortBenefit = -(repairCost * (1.0 - (1.0 / horizonYears)));
                items.add(new FinancialLineItem("Amortization Benefit", amortBenefit,
                        String.format("Spreading the repair cost over %.1f years of utility.", horizonYears),
                        ItemCategory.STAY));
                totalScore += amortBenefit;
            }
        }

        return new RegretDetail(totalScore, items);
    }

    public RegretDetail calculateRM(EngineInput input, SimulationControls controls) {
        List<FinancialLineItem> items = new ArrayList<>();
        double totalScore = 0;

        // 1. Transaction Costs
        double friction = TRANSACTION_FRICTION_DEFAULT;
        if (controls != null) {
            if (controls.hassleTolerance() == HassleTolerance.HATE_SWITCHING)
                friction = TRANSACTION_FRICTION_HASSLE;
            else if (controls.hassleTolerance() == HassleTolerance.WANT_NEW_CAR)
                friction = TRANSACTION_FRICTION_WANT_NEW;
        }
        items.add(new FinancialLineItem("Transaction Friction", friction,
                "Taxes, fees, and acquisition labor loss.", ItemCategory.MOVE));
        totalScore += friction;

        // 2. Trade-in Spread
        double tradeInSpread = input.currentValueUsd() * TRADE_IN_SPREAD_RATE;
        items.add(new FinancialLineItem("Liquidation Loss", tradeInSpread,
                "Market loss when converting a vehicle asset to immediate cash.", ItemCategory.MOVE));
        totalScore += tradeInSpread;

        // 3. NEW CAR DEPRECIATION HIT (The Big Miss)
        long replacementPrice = valuationService.getBasePrice(input.vehicleType());
        double depreciationHit = replacementPrice * NEW_CAR_FIRST_YEAR_DEPRECIATION;
        items.add(new FinancialLineItem("Replacement Asset Burn", depreciationHit,
                "Projected first-year depreciation on the replacement vehicle.", ItemCategory.MOVE));
        totalScore += depreciationHit;

        if (controls != null && controls.mobilityStatus() == MobilityStatus.NEEDS_TOW) {
            items.add(new FinancialLineItem("Liquidity Penalty (Towing)", 1500.0,
                    "Reduced disposal value due to mechanical failure.", ItemCategory.MOVE));
            totalScore += 1500.0;
        }

        items.add(new FinancialLineItem("Sunk Cost Attachment", 400.0,
                "Psychological resistance to asset termination.", ItemCategory.MOVE));
        totalScore += 400.0;

        // 4. Peace of Mind Dividend
        double basePain = (input.repairQuoteUsd() * REPAIR_PAIN_COEFFICIENT)
                + (input.mileage() * MILEAGE_PAIN_COEFFICIENT);
        if (input.mileage() > MILEAGE_THRHESOLD)
            basePain *= MILEAGE_STRESS_GROWTH;

        double dividend = -(basePain * FRESH_START_MULTIPLIER);
        items.add(new FinancialLineItem("Peace of Mind Dividend", dividend,
                "Psychological gain from a reliable replacement asset.", ItemCategory.MOVE));
        totalScore += dividend;

        return new RegretDetail(totalScore, items);
    }
}
