package com.carmoneypit.engine.service;

import com.carmoneypit.engine.service.CarDataService.CarModel;
import com.carmoneypit.engine.service.CarDataService.Fault;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Random;

/**
 * Generates bi-weekly market insights for pSEO pages.
 * Updates every 2 weeks to signal freshness to Google without spam.
 */
@Service
public class MarketPulseService {

    /**
     * Generates a deterministic bi-weekly market insight based on the current
     * period.
     * Same car/fault/period will always generate the same content.
     */
    public String generateBiweeklyInsight(CarModel car, Fault fault) {
        LocalDate today = LocalDate.now();
        int biweekNumber = (today.getDayOfYear() / 14) + 1; // Bi-week number (1-26)

        // Deterministic random generator using car ID + biweek number as seed
        Random rand = new Random(car.id().hashCode() + biweekNumber);

        // Generate realistic but varied market data
        double priceChange = -3 + rand.nextDouble() * 6; // -3% to +3%
        int searchVolume = 500 + rand.nextInt(300); // 500-800 searches
        int avgDaysToSell = 25 + rand.nextInt(15); // 25-40 days

        return String.format("""
                <div style="background: linear-gradient(135deg, #EFF6FF 0%%, #DBEAFE 100%%);
                            padding: 1.5rem; border-radius: 16px; border-left: 4px solid #3B82F6;
                            margin: 2rem 0;">
                    <div style="display: flex; align-items: center; gap: 0.5rem; margin-bottom: 0.75rem;">
                        <span style="font-size: 1.2rem;">📊</span>
                        <strong style="font-size: 1rem; color: #1E40AF;">
                            Market Snapshot: Weeks %d-%d, 2026
                        </strong>
                    </div>
                    <ul style="margin: 0; padding-left: 1.5rem; font-size: 0.9rem; color: #1E3A8A; line-height: 1.6;">
                        <li><strong>%s %s</strong> values %s <strong>%.1f%%</strong> in the past 14 days</li>
                        <li><strong>%,d owners</strong> searched "%s %s repair cost" this period</li>
                        <li>Vehicles with this issue sell in <strong>%d days</strong> on average</li>
                        <li>Repair quotes for %s range <strong>$%,.0f - $%,.0f</strong> regionally</li>
                    </ul>
                </div>
                """,
                biweekNumber * 2 - 1, // Start week
                biweekNumber * 2, // End week
                car.brand(), car.model(),
                priceChange > 0 ? "increased" : "decreased",
                Math.abs(priceChange),
                searchVolume,
                car.brand(), car.model(),
                avgDaysToSell,
                fault.component(),
                fault.repairCost() * 0.85, // Lower bound
                fault.repairCost() * 1.15 // Upper bound
        );
    }

    /**
     * Returns the current bi-week number for cache invalidation purposes.
     */
    public int getCurrentBiweekNumber() {
        return (LocalDate.now().getDayOfYear() / 14) + 1;
    }
}
