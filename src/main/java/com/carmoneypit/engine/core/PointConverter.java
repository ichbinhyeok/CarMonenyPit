package com.carmoneypit.engine.core;

import com.carmoneypit.engine.data.EngineDataProvider;
import com.carmoneypit.engine.model.EngineDataModels.EngineData;
import org.springframework.stereotype.Component;

@Component
public class PointConverter {

    private final EngineDataProvider dataProvider;

    public PointConverter(EngineDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    /**
     * Converts USD Repair Quote to internal "Repair Points".
     * 
     * TUNING NOTE:
     * This conversation logic is a purely scaling layer.
     * The standard_divisor is defined in engine_data.v1.json (conversion_rules).
     * 
     * It does NOT represent "1 point = $10".
     * It scales the infinite USD range into a manageable integer score for the
     * Decision Engine.
     */
    public int convertUsdToPoints(long quoteUsd) {
        EngineData data = dataProvider.getData();
        int divisor = data.conversion_rules().standard_divisor();

        if (divisor <= 0) {
            throw new IllegalStateException("Invalid standard_divisor in engine data: " + divisor);
        }

        return (int) (quoteUsd / divisor);
    }
}
