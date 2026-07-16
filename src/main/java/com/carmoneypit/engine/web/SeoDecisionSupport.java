package com.carmoneypit.engine.web;

import com.carmoneypit.engine.service.CarDataService.CarModel;
import com.carmoneypit.engine.service.CarDataService.Fault;
import com.carmoneypit.engine.service.CarDataService.ModelMarket;
import com.carmoneypit.engine.service.CarDataService.ModelReliability;

import java.util.List;

/**
 * Search-intent support for high-value fault and mileage pages.
 *
 * The content is deliberately diagnostic and conditional. It helps an owner
 * verify a quote without presenting a bulletin, estimate, or internal risk
 * weight as proof that every vehicle has the same defect.
 */
public final class SeoDecisionSupport {

    private SeoDecisionSupport() {
    }

    public record FaultSupport(
            String searchIntentAnswer,
            List<String> diagnosisChecks,
            List<String> quoteChecks,
            List<String> decisionSignals,
            boolean priority) {
    }

    public record MileageSupport(
            String searchIntentAnswer,
            List<String> inspectionChecks,
            List<String> decisionSignals) {
    }

    public static FaultSupport forFault(CarModel car, Fault fault, ModelMarket market) {
        String key = SeoIndexPolicy.normalize(car.brand()) + "|"
                + SeoIndexPolicy.normalize(car.model()) + "|"
                + SeoIndexPolicy.faultSlug(fault.component());

        List<String> diagnosisChecks = switch (key) {
            case "nissan|rogue|cvt-transmission" -> List.of(
                    "Ask for the stored transmission DTCs. Nissan NTB15-084d specifically addresses P17F0 or P17F1 on covered 2014-2016 Rogue vehicles.",
                    "Confirm whether the complaint is judder, reduced acceleration, overheating, or another symptom that could have a different cause.",
                    "Have a Nissan dealer check VIN-specific eligibility for the Rogue CVT warranty extension before authorizing customer-pay replacement.");
            case "ford|fusion|15l20l-coolant-intrusion" -> List.of(
                    "Confirm the installed engine, model year, build date, and VIN before applying a bulletin. The 1.5L and 2.0L procedures are not interchangeable.",
                    "Ask the shop to document coolant loss, white exhaust smoke, rough running, cylinder leakage, and any relevant misfire or coolant-temperature DTCs.",
                    "Compare the proposed repair scope with the applicable Ford procedure: qualifying cases can call for short-block or long-block replacement.");
            case "ford|escape|coolant-intrusion" -> List.of(
                    "Confirm whether the vehicle has the 1.5L, 1.6L, or 2.0L engine and record the VIN and build date.",
                    "Ask for evidence of internal coolant intrusion, not only a low reservoir: pressure-test results, cylinder leakage, white smoke, rough start, and DTCs matter.",
                    "Check whether Ford TSB 19-2375, TSB 19-2172, or customer-satisfaction program 21N12 applies to this exact vehicle.");
            case "tesla|model-3|control-arms" -> List.of(
                    "Separate a low-speed squeak or creak from the high-speed vibration and cabin-noise condition described in Tesla bulletin SB-18-31-002.",
                    "Confirm the model year and build applicability. The cited bulletin is for certain 2017 Model 3 vehicles, not every Model 3.",
                    "Ask the technician to identify the exact joint or bushing with play or noise before replacing multiple suspension assemblies.");
            case "ram|1500|exhaust-manifold-bolts" -> List.of(
                    "Verify that the cold-start tick is coming from the exhaust-manifold area and fades as the engine warms.",
                    "Ask for a visual or smoke-test confirmation of a manifold leak or broken fastener.",
                    "Rule out valvetrain, lifter, injector, and accessory noise before approving manifold work.");
            default -> List.of(
                    "Ask the shop to write down the failed test, code, measurement, or visible damage that supports the diagnosis.",
                    "Confirm the exact engine, drivetrain, model year, build date, and VIN before relying on a model-wide repair pattern.",
                    "Request a second diagnosis when the quote replaces an assembly without showing why a smaller repair is not viable.");
        };

        double ratio = market != null && market.jan2026AvgPrice() > 0
                ? fault.repairCost() / market.jan2026AvgPrice()
                : -1;
        String ratioSignal = ratio < 0
                ? "Compare the complete out-the-door quote with the vehicle's realistic private-party and trade-in value."
                : "The midpoint estimate is about " + Math.round(ratio * 100)
                        + "% of the model's current average value; use your vehicle's actual condition-adjusted value before deciding.";

        String confidenceSignal = "high".equalsIgnoreCase(fault.confidence())
                ? "The page has strong source support, but applicability still depends on symptoms, powertrain, model year, and VIN."
                : "Evidence is incomplete or model-wide. Treat the estimate as a screening range and require vehicle-specific diagnosis.";

        boolean priority = switch (key) {
            case "nissan|rogue|cvt-transmission",
                    "ford|fusion|15l20l-coolant-intrusion",
                    "ford|escape|coolant-intrusion",
                    "tesla|model-3|control-arms",
                    "ram|1500|exhaust-manifold-bolts" -> true;
            default -> false;
        };

        return new FaultSupport(
                "Do not decide from the model name alone. Verify the exact failure, bulletin or warranty applicability, and repair scope, then compare the all-in quote with the car's current value and the next 12 months of likely repairs.",
                diagnosisChecks,
                List.of(
                        "Get itemized parts, labor hours, diagnostic fees, taxes, programming, fluids, and alignment or calibration charges.",
                        "Ask what lower-cost repair was considered and why it was rejected.",
                        "Confirm parts and labor warranty, comeback policy, completion time, and whether the quote can increase after teardown."),
                List.of(
                        ratioSignal,
                        confidenceSignal,
                        "Repairing becomes harder to justify when this quote plus near-term backlog approaches the cost of switching to a replacement vehicle."),
                priority);
    }

    public static MileageSupport forMileage(
            CarModel car,
            int mileage,
            ModelReliability reliability,
            ModelMarket market) {
        double lifespanShare = reliability.lifespanMiles() > 0
                ? (double) mileage / reliability.lifespanMiles()
                : 0;
        int estimatedValue = (int) Math.max(
                market.commonJunkValue(),
                market.jan2026AvgPrice() * Math.max(0.15, 1.0 - (mileage * 0.000003)));

        String answer = "At " + String.format("%,d", mileage) + " miles, the odometer alone is not a sell signal. "
                + "Base the decision on inspection results, maintenance history, current value, and the combined cost of the quoted repair plus the next 12 months of known work.";

        String lifespanSignal = lifespanShare >= 0.75
                ? "This mileage is at least 75% of the model's estimated lifespan, so require a broader inspection before approving a major repair."
                : lifespanShare >= 0.5
                        ? "This mileage is in the middle-to-late ownership window; compare the quote with other deferred maintenance before deciding."
                        : "This mileage is below half of the model's estimated lifespan, so condition and diagnosis usually matter more than mileage alone.";

        return new MileageSupport(
                answer,
                List.of(
                        "Scan all modules and keep the codes and readiness-monitor report before anything is cleared.",
                        "Inspect fluid condition and leaks, cooling-system pressure, charging system, tires, brakes, suspension play, and corrosion.",
                        "Review maintenance records and list every due item for the next 12 months, not just today's repair.",
                        "Get a condition-adjusted value using the actual trim, mileage, title status, body condition, and local market."),
                List.of(
                        "The page's condition-adjusted screening value is about $" + String.format("%,d", estimatedValue)
                                + "; replace it with a real offer or local comparable before making the final call.",
                        lifespanSignal,
                        "Compare total keep cost with replacement switching cost, including taxes, registration, financing, inspection, and immediate maintenance on the replacement."));
    }
}
