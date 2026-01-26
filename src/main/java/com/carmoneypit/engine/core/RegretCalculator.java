package com.carmoneypit.engine.core;

import com.carmoneypit.engine.api.FinancialLineItem;
import com.carmoneypit.engine.api.InputModels.EngineInput;
import com.carmoneypit.engine.api.InputModels.SimulationControls;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RegretCalculator {

    public record RegretDetail(double score, List<FinancialLineItem> items) {
    }

    public RegretDetail calculateRF(EngineInput input, SimulationControls controls) {
        // Placeholder implementation - will be replaced with real logic
        double score = input.repairQuoteUsd() * 1.5;
        List<FinancialLineItem> items = new ArrayList<>();
        items.add(new FinancialLineItem("Repair Cost", (double) input.repairQuoteUsd(),
                "The tangible cost of the repair."));
        items.add(new FinancialLineItem("Future Risk", score - input.repairQuoteUsd(),
                "Estimated future failure costs."));
        return new RegretDetail(score, items);
    }

    public RegretDetail calculateRM(EngineInput input, SimulationControls controls) {
        // Placeholder implementation - will be replaced with real logic
        double score = 3000.0;
        List<FinancialLineItem> items = new ArrayList<>();
        items.add(new FinancialLineItem("Switching Cost", 3000.0, "Cost to replace the vehicle."));
        return new RegretDetail(score, items);
    }
}
