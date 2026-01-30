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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

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
                        @RequestParam(value = "year", required = false) Integer yearParam,
                        @RequestParam(value = "repairQuoteUsd", required = false) Long repairQuoteParam,
                        @RequestParam(value = "pSEO", required = false) Boolean fromPSEO,
                        Model model) {

                if (brandParam != null && fromPSEO != null && fromPSEO) {
                        model.addAttribute("prefillBrand", brandParam);
                        model.addAttribute("prefillModel", modelParam);
                        model.addAttribute("prefillYear", yearParam);
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

        @GetMapping(value = "/favicon.ico", produces = "image/x-icon")
        @ResponseBody
        public Resource getFaviconIco() {
                return new ClassPathResource("static/favicon.ico");
        }

        @GetMapping(value = "/favicon.png", produces = MediaType.IMAGE_PNG_VALUE)
        @ResponseBody
        public Resource getFaviconPng() {
                return new ClassPathResource("static/favicon.png");
        }

        @PostMapping("/analyze")
        public String analyzeLoading(
                        @RequestParam("brand") CarBrand brand,
                        @RequestParam(value = "model", required = false) String carModel,
                        @RequestParam(value = "year", defaultValue = "2018") int year,
                        @RequestParam(value = "vehicleType", required = false) VehicleType vehicleType,
                        @RequestParam("mileage") long mileage,
                        @RequestParam(value = "repairQuoteUsd", required = false) Long repairQuoteUsd,
                        @RequestParam(value = "isQuoteMissing", defaultValue = "false") boolean isQuoteMissing,
                        @RequestParam(value = "currentValueUsd", required = false) Long currentValueUsd,
                        Model model) {

                VehicleType effectiveType = (vehicleType != null) ? vehicleType : VehicleType.SEDAN;

                long effectiveValue = (currentValueUsd != null && currentValueUsd > 0)
                                ? currentValueUsd
                                : valuationService.estimateValue(brand, carModel, effectiveType, year, mileage);
                boolean isEstimated = (currentValueUsd == null || currentValueUsd <= 0);

                long effectiveRepairQuote = (repairQuoteUsd != null && repairQuoteUsd > 0)
                                ? repairQuoteUsd
                                : valuationService.estimateRepairCost(brand, effectiveType, mileage);

                model.addAttribute("brand", brand);
                model.addAttribute("modelName", carModel != null ? carModel : "Other");
                model.addAttribute("year", year);
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

                response.setHeader("X-Robots-Tag", "noindex, nofollow");

                try {
                        EngineInput input = presenter.decodeToken(token);
                        VerdictResult result = decisionEngine.evaluate(input);

                        SimulationControls sharedControls = new SimulationControls(
                                        FailureSeverity.GENERAL_UNKNOWN,
                                        MobilityStatus.DRIVABLE,
                                        HassleTolerance.NEUTRAL,
                                        null);

                        presenter.populateModel(model, input, result, sharedControls, "RECEIPT", token);
                        return "result";
                } catch (Exception e) {
                        log.error("Invalid token provided: {}", token);
                        return "redirect:/";
                }
        }

        @RequestMapping(value = "/analyze-final", method = { RequestMethod.GET, RequestMethod.POST })
        public String analyzeFinal(
                        @RequestParam("brand") CarBrand brand,
                        @RequestParam(value = "model", required = false) String carModel,
                        @RequestParam(value = "year", defaultValue = "2018") int year,
                        @RequestParam(value = "vehicleType", required = false) VehicleType vehicleType,
                        @RequestParam("mileage") long mileage,
                        @RequestParam(value = "repairQuoteUsd", required = false) Long repairQuoteUsd,
                        @RequestParam(value = "isQuoteMissing", defaultValue = "false") boolean isQuoteMissing,
                        @RequestParam(value = "currentValueUsd", required = false) Long currentValueUsd,
                        @RequestParam(value = "isValueEstimated", defaultValue = "false") boolean isValueEstimated,
                        @RequestParam(value = "isQuoteEstimated", defaultValue = "false") boolean isQuoteEstimated,
                        Model model,
                        jakarta.servlet.http.HttpServletResponse response) {

                VehicleType effectiveType = (vehicleType != null) ? vehicleType : VehicleType.SEDAN;

                long effectiveValue = (currentValueUsd != null && currentValueUsd > 0)
                                ? currentValueUsd
                                : valuationService.estimateValue(brand, carModel, effectiveType, year, mileage);
                boolean finalIsEstimated = isValueEstimated || (currentValueUsd == null || currentValueUsd <= 0);

                long effectiveRepairQuote = (repairQuoteUsd != null && repairQuoteUsd > 0)
                                ? repairQuoteUsd
                                : valuationService.estimateRepairCost(brand, effectiveType, mileage);
                boolean finalIsQuoteEstimated = isQuoteEstimated || isQuoteMissing
                                || (repairQuoteUsd == null || repairQuoteUsd <= 0);

                EngineInput input = new EngineInput(carModel != null ? carModel : "Other", effectiveType, brand,
                                year,
                                mileage,
                                effectiveRepairQuote, effectiveValue,
                                finalIsQuoteEstimated, finalIsEstimated);

                String shareToken = presenter.encodeToken(input);
                response.setHeader("HX-Location", "/report?token=" + shareToken);
                return "";
        }

        @GetMapping("/report")
        public String getReport(
                        @RequestParam("token") String token,
                        Model model,
                        jakarta.servlet.http.HttpServletResponse response) {

                response.setHeader("X-Robots-Tag", "noindex, nofollow");

                try {
                        EngineInput input = presenter.decodeToken(token);
                        VerdictResult result = decisionEngine.evaluate(input);

                        SimulationControls defaultControls = new SimulationControls(
                                        FailureSeverity.GENERAL_UNKNOWN,
                                        MobilityStatus.DRIVABLE,
                                        HassleTolerance.NEUTRAL,
                                        null);

                        presenter.populateModel(model, input, result, defaultControls, "OWNER", token);
                        return "result";
                } catch (Exception e) {
                        log.error("Invalid report token: {}", token);
                        return "redirect:/";
                }
        }

        @PostMapping("/simulate")
        public String simulate(
                        @RequestParam(value = "model", required = false) String carModel,
                        @RequestParam(value = "year", defaultValue = "2018") int year,
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
                EngineInput input = new EngineInput(carModel != null ? carModel : "Other", vehicleType, brand, year,
                                mileage,
                                repairQuoteUsd, currentValueUsd,
                                false, isValueEstimated);
                SimulationControls controls = new SimulationControls(failureSeverity, mobilityStatus, hassleTolerance,
                                retentionHorizon);

                VerdictResult result = decisionEngine.simulate(input, controls);

                presenter.populateModel(model, input, result, controls, "OWNER", null);

                return "simulation_response";
        }
}
