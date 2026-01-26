package com.carmoneypit.engine.api;

public record FinancialLineItem(
                String label,
                double amount,
                String description,
                boolean isNegative) {
        // Convenience constructor
        public FinancialLineItem(String label, double amount, String description) {
                this(label, amount, description, true);
        }
}
