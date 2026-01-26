package com.carmoneypit.engine.web;

import com.carmoneypit.engine.api.InputModels.EngineInput;
import com.carmoneypit.engine.api.InputModels.SimulationControls;
import com.carmoneypit.engine.api.InputModels.VehicleType;
import com.carmoneypit.engine.api.InputModels.FailureSeverity;
import com.carmoneypit.engine.api.InputModels.MobilityStatus;
import com.carmoneypit.engine.api.InputModels.HassleTolerance;
import com.carmoneypit.engine.api.OutputModels.VerdictResult;
import com.carmoneypit.engine.core.DecisionEngine;
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

    public CarDecisionController(DecisionEngine decisionEngine, VerdictPresenter presenter) {
        this.decisionEngine = decisionEngine;
        this.presenter = presenter;
    }

    @GetMapping("/")
    public String index() {
        return "index"; // Renders src/main/jte/index.jte
    }

    @PostMapping("/analyze")
    public String analyze(
            @RequestParam("vehicleType") VehicleType vehicleType,
            @RequestParam("mileage") long mileage,
            @RequestParam("repairQuoteUsd") long repairQuoteUsd,
            Model model) {
        EngineInput input = new EngineInput(vehicleType, mileage, repairQuoteUsd);
        VerdictResult result = decisionEngine.evaluate(input);

        log.info("Analysis Result: State={}, RF={}, RM={}", result.verdictState(), result.visualizationHint().rfScore(),
                result.visualizationHint().rmScore());
        String title = presenter.getVerdictTitle(result.verdictState());
        String expl = presenter.getLawyerExplanation(result.verdictState());
        log.info("Presentation Data: Title='{}', Expl='{}'", title, expl);

        // Pass input back for sliders initialization
        model.addAttribute("input", input);
        model.addAttribute("result", result);

        // Presentation Logic
        model.addAttribute("verdictTitle", presenter.getVerdictTitle(result.verdictState()));
        model.addAttribute("verdictExplanation", presenter.getLawyerExplanation(result.verdictState()));
        model.addAttribute("verdictAction", presenter.getActionPlan(result.verdictState()));
        model.addAttribute("verdictCss", presenter.getCssClass(result.verdictState()));

        // Initialize default controls for the view
        model.addAttribute("controls", new SimulationControls(
                FailureSeverity.GENERAL_UNKNOWN,
                MobilityStatus.DRIVABLE,
                HassleTolerance.NEUTRAL));

        return "result"; // Renders src/main/jte/result.jte
    }

    @PostMapping("/simulate")
    public String simulate(
            @RequestParam("vehicleType") VehicleType vehicleType,
            @RequestParam("mileage") long mileage,
            @RequestParam("repairQuoteUsd") long repairQuoteUsd,
            @RequestParam("failureSeverity") FailureSeverity failureSeverity,
            @RequestParam("mobilityStatus") MobilityStatus mobilityStatus,
            @RequestParam("hassleTolerance") HassleTolerance hassleTolerance,
            Model model) {
        EngineInput input = new EngineInput(vehicleType, mileage, repairQuoteUsd);
        SimulationControls controls = new SimulationControls(failureSeverity, mobilityStatus, hassleTolerance);

        VerdictResult result = decisionEngine.simulate(input, controls);

        model.addAttribute("input", input);
        model.addAttribute("result", result);
        model.addAttribute("controls", controls);

        // Presentation Logic
        model.addAttribute("verdictTitle", presenter.getVerdictTitle(result.verdictState()));
        model.addAttribute("verdictExplanation", presenter.getLawyerExplanation(result.verdictState()));
        model.addAttribute("verdictAction", presenter.getActionPlan(result.verdictState()));
        model.addAttribute("verdictCss", presenter.getCssClass(result.verdictState()));

        // HTMX fragment response: only render the card part
        // We can define a fragment inside result.jte or use a separate file.
        // For simplicity with JTE, let's use a dedicated fragment file or just return
        // the card template.
        // Assuming result.jte includes the card, we'll try to use a dedicated template
        // for the card.
        return "fragments/verdict_card";
    }
}
