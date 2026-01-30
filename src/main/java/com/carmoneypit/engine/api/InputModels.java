package com.carmoneypit.engine.api;

public class InputModels {
    public record EngineInput(
            String model,
            VehicleType vehicleType,
            CarBrand brand,
            int year,
            long mileage,
            long repairQuoteUsd,
            long currentValueUsd,
            boolean isQuoteEstimated,
            boolean isValueEstimated) {
    }

    public record SimulationControls(
            FailureSeverity failureSeverity,
            MobilityStatus mobilityStatus,
            HassleTolerance hassleTolerance,
            RetentionHorizon retentionHorizon) {
    }

    public enum CarBrand {
        TOYOTA, HONDA, LEXUS,
        BMW, MERCEDES, AUDI, VOLKSWAGEN, PORSCHE, LAND_ROVER,
        FORD, CHEVROLET, RAM, JEEP, TESLA,
        HYUNDAI, KIA, NISSAN, SUBARU
    }

    public enum VehicleType {
        SEDAN, SUV, TRUCK_VAN, LUXURY, PERFORMANCE
    }

    public enum FailureSeverity {
        GENERAL_UNKNOWN,
        SUSPENSION_BRAKES,
        ENGINE_TRANSMISSION
    }

    public enum MobilityStatus {
        DRIVABLE,
        NEEDS_TOW
    }

    public enum HassleTolerance {
        HATE_SWITCHING,
        NEUTRAL,
        WANT_NEW_CAR
    }

    public enum RetentionHorizon {
        MONTHS_6,
        YEARS_1,
        YEARS_3,
        YEARS_5
    }
}
