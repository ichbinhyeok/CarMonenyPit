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
    
    // Baseline costs for major repairs (National Averages)
    private static final double MAJOR_FAILURE_ENGINE = 4500.0;
    private static final double MAJOR_FAILURE_SUSPENSION = 1800.0;
    private static final double MAJOR_FAILURE_GENERAL = 800.0;
    
    // Diagnostic buffer for unknown issues (credibility factor)
    private static final double DIAGNOSTIC_BUFFER = 150.0;

    // Opportunity cost of capital (5% annual rate, standard index ROI)
    private static final double OPPORTUNITY_COST_RATE = 0.05;

    // Stress & Lifestyle Metrics (Subjective but statistically significant)
    private static final double RAGE_COST = 1200.0; // Cost of unplanned downtime and frustration
    private static final double MILEAGE_PAIN_COEFFICIENT = 0.01; // Stress increase per mile
    private static final double MILEAGE_THRHESOLD = 100000.0;
    private static final double MILEAGE_STRESS_GROWTH = 1.3; // Stress scaling for high-mileage assets
    private static final double REPAIR_PAIN_COEFFICIENT = 0.05; // Stress relative to the size of the repair bill

    // Replacement Economics
    private static final double NEW_CAR_FIRST_YEAR_DEPRECIATION = 0.12; // Typical off-the-lot hit for replacement asset
    private static final double FRESH_START_MULTIPLIER = 0.80; // Utility gain from a reliable new asset
    private static final double TRANSACTION_FRICTION_DEFAULT = 2500.0; // Taxes + Dealer Fees + Registration
    private static final double TRANSACTION_FRICTION_HASSLE = 5000.0; // High friction for those who hate switching
    private static final double TRANSACTION_FRICTION_WANT_NEW = 1200.0; // Lower perceived friction for enthusiasts
    private static final double TRADE_IN_SPREAD_RATE = 0.15; // Realistic dealer margin on trade-in assets
    private static final double PSYCHOLOGICAL_ATTACHMENT_FLOOR = 400.0; // Inherent bias toward current asset

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
        
        // AGE FACTOR: Rubber Rot & Seal degradation (Physical aging vs Usage aging)
        int currentYear = 2026;
        int age = Math.max(0, currentYear - input.year());
        if (age > 10) {
            failureProb = Math.min(failureProb + 0.15, 0.95);
        }

        double majorCostBase = MAJOR_FAILURE_GENERAL;

        // DYNAMIC FAULT LOOKUP
        if (input.model() != null && !input.model().isBlank()) {
            var modelOpt = carDataService.findCarBySlug(null, input.model()); // Using pre-optimized map search if available
            
            // Fallback to stream if direct lookup fails in current version
            if (modelOpt.isEmpty()) {
                modelOpt = carDataService.getAllModels().stream()
                        .filter(m -> m.model().equalsIgnoreCase(input.model()))
                        .findFirst();
            }

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
            } else if (controls.failureSeverity() == FailureSeverity.GENERAL_UNKNOWN) {
                // Diagnostic Mystery Premium
                items.add(new FinancialLineItem("Diagnostic Buffer", DIAGNOSTIC_BUFFER,
                        "Estimated cost for identifying root causes of unspecified mechanical signals.", ItemCategory.STAY));
                totalScore += DIAGNOSTIC_BUFFER;
            }
        }

        double riskRegret = failureProb * majorCostBase;
        items.add(new FinancialLineItem("Risk of Next Breakdown", riskRegret,
                String.format("[Statistical] %d%% probability of adjacent system failure within current usage window.",
                        (int) (failureProb * 100)),
                ItemCategory.STAY));
        totalScore += riskRegret;

        // 3. Asset Opportunity Cost
        double timingCost = input.currentValueUsd() * OPPORTUNITY_COST_RATE;
        items.add(new FinancialLineItem("Opportunity Cost of Delay", timingCost,
                "The economic loss of holding a depreciating asset instead of diversified capital.", ItemCategory.STAY));
        totalScore += timingCost;

        // 4. Stress & Downtime
        double basePain = (input.repairQuoteUsd() * REPAIR_PAIN_COEFFICIENT);
        double mileagePain = (input.mileage() * MILEAGE_PAIN_COEFFICIENT);
        if (input.mileage() > MILEAGE_THRHESOLD) {
            mileagePain *= MILEAGE_STRESS_GROWTH;
        }

        double painScore = basePain + mileagePain;
        
        // HASSLE TOLERANCE WEIGHTING: Emotional fatigue toward current asset
        if (controls != null) {
            if (controls.hassleTolerance() == HassleTolerance.WANT_NEW_CAR) {
                painScore *= 1.25; // Impatience with existing asset
            }
            
            if (controls.mobilityStatus() == MobilityStatus.NEEDS_TOW) {
                painScore += RAGE_COST;
            }
        }

        items.add(new FinancialLineItem("Stress & Reliability Tax", painScore,
                "Valuation of unplanned downtime and psychological burden of asset insecurity.", ItemCategory.STAY));
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
            items.add(new FinancialLineItem("Ownership Window Contingency", horizonCost,
                    "Projected wear-and-tear liabilities over the selected retention period.", ItemCategory.STAY));
            totalScore += horizonCost;

            // AMORTIZATION BENEFIT: Current repair utility spread across time
            if (horizonYears >= 1.0) {
                double amortBenefit = -(repairCost * (1.0 - (1.0 / horizonYears)));
                items.add(new FinancialLineItem("Utility Amortization", amortBenefit,
                        String.format("Spreading current repair overhead over %.1f years of continued asset utility.", horizonYears),
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
                "Estimated taxes, dealer fees, and procurement labor required to secure a replacement.", ItemCategory.MOVE));
        totalScore += friction;

        // 2. Trade-in Spread
        double tradeInSpread = input.currentValueUsd() * TRADE_IN_SPREAD_RATE;
        items.add(new FinancialLineItem("Liquidation Margin Loss", tradeInSpread,
                "Market inefficiency incurred when liquidating an asset to a dealer vs private party.", ItemCategory.MOVE));
        totalScore += tradeInSpread;

        // 3. REPLACEMENT ASSET DEPRECIATION (The "Off-The-Lot" Burn)
        long replacementPrice = valuationService.getBasePrice(input.vehicleType());
        double depreciationHit = replacementPrice * NEW_CAR_FIRST_YEAR_DEPRECIATION;
        
        // RETENTION HORIZON IMPACT: Long-term owners diffuse the depreciation hit
        if (controls != null && controls.retentionHorizon() != null) {
            double horizonYears = switches(controls.retentionHorizon());
            if (horizonYears > 1.0) {
                // If staying 3+ years, the "ouch" of first-year depreciation is divided by years of enjoyment
                depreciationHit = depreciationHit / Math.sqrt(horizonYears);
            }
        }
        
        items.add(new FinancialLineItem("New Asset Burn", depreciationHit,
                "Projected first-year depreciation overhead for the replacement vehicle.", ItemCategory.MOVE));
        totalScore += depreciationHit;

        if (controls != null && controls.mobilityStatus() == MobilityStatus.NEEDS_TOW) {
            items.add(new FinancialLineItem("Non-Drivable Penalty", 1500.0,
                    "Immediate reduction in disposal value due to critical mechanical failure.", ItemCategory.MOVE));
            totalScore += 1500.0;
        }

        items.add(new FinancialLineItem("Psychological Pivot Cost", PSYCHOLOGICAL_ATTACHMENT_FLOOR,
                "Inherent bias toward asset continuity and familiar systems.", ItemCategory.MOVE));
        totalScore += PSYCHOLOGICAL_ATTACHMENT_FLOOR;

        // 4. Peace of Mind Dividend
        double basePain = (input.repairQuoteUsd() * REPAIR_PAIN_COEFFICIENT)
                + (input.mileage() * MILEAGE_PAIN_COEFFICIENT);
        if (input.mileage() > MILEAGE_THRHESOLD)
            basePain *= MILEAGE_STRESS_GROWTH;

        // Cap basePain to prevent extreme values from skewing results
        double cappedPain = Math.min(basePain, 5000.0);
        double dividend = -(cappedPain * FRESH_START_MULTIPLIER);
        items.add(new FinancialLineItem("Peace of Mind Dividend", dividend,
                "Emotional utility gain from transitioning to a more reliable replacement asset.", ItemCategory.MOVE));
        totalScore += dividend;

        return new RegretDetail(totalScore, items);
    }
    
    private double switches(com.carmoneypit.engine.api.InputModels.RetentionHorizon h) {
        if (h == null) return 1.0;
        return switch (h) {
            case MONTHS_6 -> 0.5;
            case YEARS_1 -> 1.0;
            case YEARS_3 -> 3.0;
            case YEARS_5 -> 5.0;
        };
    }
}
