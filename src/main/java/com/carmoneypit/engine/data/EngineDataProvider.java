package com.carmoneypit.engine.data;

import com.carmoneypit.engine.model.EngineDataModels.EngineData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

@Component
public class EngineDataProvider {

    private static final String DATA_FILE_PATH = "engine_data.v1.json";
    private EngineData engineData;
    private final ObjectMapper objectMapper;

    public EngineDataProvider(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void loadData() {
        try {
            File file = new File(DATA_FILE_PATH);
            // Fallback for running in IDE/Test vs packaged
            if (!file.exists()) {
                // Try absolute path if relative fails (assuming project root)
                file = new File("c:\\Development\\Owner\\CarMoneyPit\\engine_data.v1.json");
            }

            if (!file.exists()) {
                throw new RuntimeException("Engine data file not found at: " + file.getAbsolutePath());
            }

            this.engineData = objectMapper.readValue(file, EngineData.class);
            System.out.println("Engine data loaded successfully.");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load engine data", e);
        }
    }

    public EngineData getData() {
        if (engineData == null) {
            throw new IllegalStateException("Engine data not loaded.");
        }
        return engineData;
    }
}
