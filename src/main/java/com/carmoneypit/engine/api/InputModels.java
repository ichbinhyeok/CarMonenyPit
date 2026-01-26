package com.carmoneypit.engine.api;

public class InputModels {
    public record EngineInput(
            VehicleType vehicleType,
            long mileage,
            long repairQuoteUsd) {
    }

    public record SimulationControls(
            FailureSeverity failureSeverity,
            MobilityStatus mobilityStatus,
            HassleTolerance hassleTolerance) {
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
}
