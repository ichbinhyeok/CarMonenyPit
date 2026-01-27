package com.carmoneypit.engine.api;

public record FinancialLineItem(
                String label,
                double amount,
                String description,
                boolean isNegative,
                ItemCategory category) {
        // Convenience constructor
        public FinancialLineItem(String label, double amount, String description, ItemCategory category) {
                this(label, amount, description, true, category);
        }
}
