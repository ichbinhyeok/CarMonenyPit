package com.carmoneypit.engine.api;

public record FinancialLineItem(
        String label, // e.g., "Future Transmission Risk"
        String amount, // e.g., "~$2,200"
        String description, // e.g., "30% probability of failure within 12 months"
        boolean isNegative // true if it represents a cost/loss (red), false if gain/saving (green)
) {
}
