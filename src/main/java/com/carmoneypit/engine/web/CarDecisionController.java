package com.carmoneypit.engine.web;

import com.carmoneypit.engine.api.InputModels.CarBrand;
import com.carmoneypit.engine.api.InputModels.EngineInput;
import com.carmoneypit.engine.api.InputModels.SimulationControls;
import com.carmoneypit.engine.api.InputModels.VehicleType;
import com.carmoneypit.engine.api.InputModels.FailureSeverity;
import com.carmoneypit.engine.api.InputModels.MobilityStatus;
import com.carmoneypit.engine.api.InputModels.HassleTolerance;
import com.carmoneypit.engine.api.OutputModels.VerdictResult;
import com.carmoneypit.engine.core.DecisionEngine;
import com.carmoneypit.engine.core.ValuationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class CarDecisionController {

        private static final Logger log = LoggerFactory.getLogger(CarDecisionController.class);

        private final DecisionEngine decisionEngine;
        private final VerdictPresenter presenter;
        private final ValuationService valuationService;

        public CarDecisionController(DecisionEngine decisionEngine, VerdictPresenter presenter,
                        ValuationService valuationService) {
                this.decisionEngine = decisionEngine;
                this.presenter = presenter;
                this.valuationService = valuationService;
        }

        @GetMapping("/")
        public String index(
                        @RequestParam(value = "brand", required = false) String brandParam,
                        @RequestParam(value = "model", required = false) String modelParam,
                        @RequestParam(value = "repairQuoteUsd", required = false) Long repairQuoteParam,
                        @RequestParam(value = "pSEO", required = false) Boolean fromPSEO,
                        Model model) {

                if (brandParam != null && fromPSEO != null && fromPSEO) {
                        model.addAttribute("prefillBrand", brandParam);
                        model.addAttribute("prefillModel", modelParam);
                        model.addAttribute("prefillQuote", repairQuoteParam);
                }
                return "index"; // Renders src/main/jte/index.jte
        }

        @GetMapping("/api/models")
        public String getModelsByBrand(@RequestParam("brand") String brand, Model model) {
                var models = valuationService.getModelsByBrand(brand);
                model.addAttribute("models", models);
                return "fragments/model_options";
        }

        @PostMapping("/analyze")
        public String analyzeLoading(
                        @RequestParam("brand") CarBrand brand,
                        @RequestParam(value = "model", required = false) String carModel,
                        @RequestParam(value = "vehicleType", required = false) VehicleType vehicleType,
                        @RequestParam("mileage") long mileage,
                        @RequestParam(value = "repairQuoteUsd", required = false) Long repairQuoteUsd,
                        @RequestParam(value = "isQuoteMissing", defaultValue = "false") boolean isQuoteMissing,
                        @RequestParam(value = "currentValueUsd", required = false) Long currentValueUsd,
                        Model model) {

                VehicleType effectiveType = (vehicleType != null) ? vehicleType : VehicleType.SEDAN;

                long effectiveValue = (currentValueUsd != null && currentValueUsd > 0)
                                ? currentValueUsd
                                : valuationService.estimateValue(brand, effectiveType, mileage);
                boolean isEstimated = (currentValueUsd == null || currentValueUsd <= 0);

                long effectiveRepairQuote = (repairQuoteUsd != null && repairQuoteUsd > 0)
                                ? repairQuoteUsd
                                : valuationService.estimateRepairCost(brand, effectiveType, mileage);

                model.addAttribute("brand", brand);
                model.addAttribute("modelName", carModel != null ? carModel : "Other");
                model.addAttribute("vehicleType", effectiveType);
                model.addAttribute("mileage", mileage);
                model.addAttribute("repairQuoteUsd", effectiveRepairQuote);
                model.addAttribute("currentValueUsd", effectiveValue);
                model.addAttribute("isValueEstimated", isEstimated);
                model.addAttribute("isQuoteEstimated", isQuoteMissing);
                return "fragments/loading";
        }

        @GetMapping("/verdict")
        public String shareVerdict(
                        @RequestParam("token") String token,
                        Model model,
                        jakarta.servlet.http.HttpServletResponse response) {

                // Anti-Viral Safety: Ensure this page is NEVER indexed
                response.setHeader("X-Robots-Tag", "noindex, nofollow");

                try {
                        EngineInput input = presenter.decodeToken(token);
                        VerdictResult result = decisionEngine.evaluate(input);

                        // Default simulation controls for a "shared" view
                        SimulationControls sharedControls = new SimulationControls(
                                        com.carmoneypit.engine.api.InputModels.FailureSeverity.GENERAL_UNKNOWN,
                                        com.carmoneypit.engine.api.InputModels.MobilityStatus.DRIVABLE,
                                        com.carmoneypit.engine.api.InputModels.HassleTolerance.NEUTRAL,
                                        null);

                        model.addAttribute("input", input);
                        model.addAttribute("result", result);
                        model.addAttribute("verdictTitle", presenter.getVerdictTitle(result.verdictState()));
                        model.addAttribute("verdictExplanation",
                                        presenter.getLawyerExplanation(result.verdictState(), input));
                        model.addAttribute("verdictAction", presenter.getActionPlan(result.verdictState()));
                        model.addAttribute("verdictCss", presenter.getCssClass(result.verdictState()));
                        model.addAttribute("leadLabel",
                                        presenter.getLeadLabel(result.verdictState(), input, sharedControls));
                        model.addAttribute("leadDescription",
                                        presenter.getLeadDescription(result.verdictState(), input, sharedControls));
                        model.addAttribute("leadUrl",
                                        presenter.getLeadUrl(result.verdictState(), input, sharedControls));

                        // Shared View Mode
                        model.addAttribute("viewMode", "RECEIPT");
                        model.addAttribute("shareToken", token);
                        model.addAttribute("ogTitle", presenter.getViralOgTitle(result.verdictState()));

                        model.addAttribute("controls", sharedControls);

                        return "result";
                } catch (Exception e) {
                        log.error("Invalid token provided: {}", token);
                        return "redirect:/";
                }
        }

        @PostMapping("/analyze-final")
        public String analyzeFinal(
                        @RequestParam("brand") CarBrand brand,
                        @RequestParam(value = "model", required = false) String carModel,
                        @RequestParam(value = "vehicleType", required = false) VehicleType vehicleType,
                        @RequestParam("mileage") long mileage,
                        @RequestParam(value = "repairQuoteUsd", required = false) Long repairQuoteUsd,
                        @RequestParam(value = "isQuoteMissing", defaultValue = "false") boolean isQuoteMissing,
                        @RequestParam(value = "currentValueUsd", required = false) Long currentValueUsd,
                        @RequestParam(value = "isValueEstimated", defaultValue = "false") boolean isValueEstimated,
                        @RequestParam(value = "isQuoteEstimated", defaultValue = "false") boolean isQuoteEstimated,
                        Model model) {

                VehicleType effectiveType = (vehicleType != null) ? vehicleType : VehicleType.SEDAN;

                long effectiveValue = (currentValueUsd != null && currentValueUsd > 0)
                                ? currentValueUsd
                                : valuationService.estimateValue(brand, effectiveType, mileage);
                boolean finalIsEstimated = isValueEstimated || (currentValueUsd == null || currentValueUsd <= 0);

                long effectiveRepairQuote = (repairQuoteUsd != null && repairQuoteUsd > 0)
                                ? repairQuoteUsd
                                : valuationService.estimateRepairCost(brand, effectiveType, mileage);
                boolean finalIsQuoteEstimated = isQuoteEstimated || isQuoteMissing
                                || (repairQuoteUsd == null || repairQuoteUsd <= 0);

                EngineInput input = new EngineInput(carModel != null ? carModel : "Other", effectiveType, brand,
                                mileage,
                                effectiveRepairQuote, effectiveValue,
                                finalIsQuoteEstimated);
                VerdictResult result = decisionEngine.evaluate(input);

                SimulationControls defaultControls = new SimulationControls(
                                FailureSeverity.GENERAL_UNKNOWN,
                                MobilityStatus.DRIVABLE,
                                HassleTolerance.NEUTRAL,
                                null);

                model.addAttribute("input", input);
                model.addAttribute("result", result);
                model.addAttribute("verdictTitle", presenter.getVerdictTitle(result.verdictState()));
                model.addAttribute("verdictExplanation", presenter.getLawyerExplanation(result.verdictState(), input));
                model.addAttribute("verdictAction", presenter.getActionPlan(result.verdictState()));
                model.addAttribute("verdictCss", presenter.getCssClass(result.verdictState()));
                model.addAttribute("leadLabel", presenter.getLeadLabel(result.verdictState(), input, defaultControls));
                model.addAttribute("leadDescription",
                                presenter.getLeadDescription(result.verdictState(), input, defaultControls));
                model.addAttribute("leadUrl", presenter.getLeadUrl(result.verdictState(), input, defaultControls));
                model.addAttribute("isValueEstimated", finalIsEstimated);
                model.addAttribute("isQuoteEstimated", finalIsQuoteEstimated);

                model.addAttribute("viewMode", "OWNER");

                String shareToken = presenter.encodeToken(input);
                model.addAttribute("shareToken", shareToken);

                model.addAttribute("controls", defaultControls);

                return "result";
        }

        @PostMapping("/simulate")
        public String simulate(
                        @RequestParam(value = "model", required = false) String carModel,
                        @RequestParam("vehicleType") VehicleType vehicleType,
                        @RequestParam("brand") com.carmoneypit.engine.api.InputModels.CarBrand brand,
                        @RequestParam("mileage") long mileage,
                        @RequestParam("repairQuoteUsd") long repairQuoteUsd,
                        @RequestParam("currentValueUsd") long currentValueUsd,
                        @RequestParam("failureSeverity") FailureSeverity failureSeverity,
                        @RequestParam("mobilityStatus") MobilityStatus mobilityStatus,
                        @RequestParam("hassleTolerance") HassleTolerance hassleTolerance,
                        @RequestParam(value = "retentionHorizon", required = false) com.carmoneypit.engine.api.InputModels.RetentionHorizon retentionHorizon,
                        @RequestParam(value = "isValueEstimated", defaultValue = "false") boolean isValueEstimated,
                        Model model) {
                EngineInput input = new EngineInput(carModel != null ? carModel : "Other", vehicleType, brand, mileage,
                                repairQuoteUsd, currentValueUsd,
                                false); // Simulation default to false
                SimulationControls controls = new SimulationControls(failureSeverity, mobilityStatus, hassleTolerance,
                                retentionHorizon);

                VerdictResult result = decisionEngine.simulate(input, controls);

                model.addAttribute("input", input);
                model.addAttribute("result", result);
                model.addAttribute("controls", controls);
                model.addAttribute("isValueEstimated", isValueEstimated);

                // Presentation Logic
                model.addAttribute("verdictTitle", presenter.getVerdictTitle(result.verdictState()));
                model.addAttribute("verdictExplanation", presenter.getLawyerExplanation(result.verdictState(), input));
                model.addAttribute("verdictAction", presenter.getActionPlan(result.verdictState()));
                model.addAttribute("verdictCss", presenter.getCssClass(result.verdictState()));
                model.addAttribute("leadLabel", presenter.getLeadLabel(result.verdictState(), input, controls));
                model.addAttribute("leadDescription",
                                presenter.getLeadDescription(result.verdictState(), input, controls));
                model.addAttribute("leadUrl", presenter.getLeadUrl(result.verdictState(), input, controls));

                return "simulation_response";
        }
}
