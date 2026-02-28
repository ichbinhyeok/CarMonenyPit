package com.carmoneypit.engine.service;

import com.carmoneypit.engine.service.CarDataService.CarModel;
import com.carmoneypit.engine.service.CarDataService.Fault;
import org.springframework.stereotype.Service;

/**
 * Generates market context descriptions for pSEO pages.
 * Generates educational market context descriptions for pSEO pages.
 * Provides deterministic, dataset-derived cost ranges and pricing context.
 */
@Service
public class MarketContextService {

    /**
     * Generates a deterministic market context block.
     * Provides educational text explaining why prices fluctuate
     * (dealership vs independent shop, condition, regional differences).
     */
    public String generateMarketContext(CarModel car, Fault fault) {

        long baseRepair = Math.round(fault.repairCost());
        long lowRange = Math.round(baseRepair * 0.85 / 50.0) * 50; // Round to nearest $50
        long highRange = Math.round(baseRepair * 1.15 / 50.0) * 50;

        String liquidityRating = getLiquidityRating(car);

        return "<div style=\"background: linear-gradient(135deg, #EFF6FF 0%, #DBEAFE 100%); " +
                "padding: 1.5rem; border-radius: 16px; border-left: 4px solid #3B82F6; margin: 2rem 0;\">" +
                "<div style=\"display: flex; align-items: center; gap: 0.5rem; margin-bottom: 0.75rem;\">" +
                "<span style=\"font-size: 1.2rem;\">📊</span>" +
                "<strong style=\"font-size: 1rem; color: #1E40AF;\">Market & Pricing Context</strong>" +
                "</div>" +
                "<ul style=\"margin: 0; padding-left: 1.5rem; font-size: 0.9rem; color: #1E3A8A; line-height: 1.6;\">" +
                "<li><strong>" + car.brand() + " " + car.model()
                + "</strong> secondary market liquidity is historically <strong>" + liquidityRating + "</strong>.</li>"
                +
                "<li>Used car values fluctuate. For the most accurate estimate of your exact trim and condition, compare KBB, NADA, and local dealer listings.</li>"
                +
                "<li>Repair quotes for " + fault.component() + " fall in a rough range of <strong>$"
                + String.format("%,d", lowRange) + " - $"
                + String.format("%,d", highRange)
                + "</strong>. Factors like going to a dealership vs an independent shop, and local labor rates strongly impact the final bill.</li>"
                +
                "</ul></div>";
    }

    private String getLiquidityRating(CarModel car) {
        String brand = car.brand() != null ? car.brand().toUpperCase() : "";
        if ("HONDA".equals(brand) || "TOYOTA".equals(brand) || "FORD".equals(brand) || "CHEVROLET".equals(brand)
                || "NISSAN".equals(brand)) {
            return "High";
        }
        if ("BMW".equals(brand) || "MERCEDES-BENZ".equals(brand) || "AUDI".equals(brand) || "PORSCHE".equals(brand)
                || "LAND ROVER".equals(brand)) {
            return "Variable (Condition Dependent)";
        }
        return "Moderate";
    }
}
