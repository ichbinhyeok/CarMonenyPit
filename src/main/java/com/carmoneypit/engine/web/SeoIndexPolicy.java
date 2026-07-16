package com.carmoneypit.engine.web;

import com.carmoneypit.engine.service.CarDataService.CarModel;
import com.carmoneypit.engine.service.CarDataService.Fault;
import com.carmoneypit.engine.service.CarDataService.ModelReliability;

import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;

/**
 * Single source of truth for SEO representative URLs and index eligibility.
 */
public final class SeoIndexPolicy {

    private static final Map<String, Integer> REPRESENTATIVE_YEAR_OVERRIDES = Map.ofEntries(
            Map.entry("toyota|camry", 2014),
            Map.entry("nissan|altima", 2014),
            Map.entry("honda|accord", 2015),
            Map.entry("honda|cr-v", 2013),
            Map.entry("mazda|cx-5", 2015),
            Map.entry("toyota|corolla", 2014),
            Map.entry("ford|escape", 2014),
            Map.entry("ford|fusion", 2013),
            Map.entry("volkswagen|jetta", 2014),
            Map.entry("nissan|rogue", 2017),
            Map.entry("hyundai|elantra", 2017));

    private static final Set<String> INDEXABLE_DECISION_MODELS = Set.of(
            "toyota|camry",
            "nissan|altima",
            "honda|accord",
            "honda|cr-v",
            "mazda|cx-5",
            "toyota|corolla",
            "ford|escape",
            "ford|fusion",
            "volkswagen|jetta");

    private static final Map<String, Set<Integer>> INDEXABLE_MILEAGE_PAGES = Map.ofEntries(
            Map.entry("volvo|xc90", Set.of(50000, 150000, 200000)),
            Map.entry("tesla|model-3", Set.of(150000, 200000)),
            Map.entry("tesla|model-y", Set.of(100000, 200000)),
            Map.entry("chrysler|pacifica", Set.of(200000)),
            Map.entry("hyundai|palisade", Set.of(100000, 175000)),
            Map.entry("toyota|camry", Set.of(200000)),
            Map.entry("toyota|corolla", Set.of(50000, 100000, 200000)),
            Map.entry("subaru|forester", Set.of(100000, 150000)),
            Map.entry("subaru|outback", Set.of(150000, 200000)),
            Map.entry("honda|pilot", Set.of(150000, 200000)),
            Map.entry("honda|odyssey", Set.of(150000, 200000)),
            Map.entry("cadillac|escalade", Set.of(50000, 100000, 125000, 150000, 200000)),
            Map.entry("mazda|cx-5", Set.of(100000, 150000, 200000)),
            Map.entry("nissan|rogue", Set.of(150000)));

    private SeoIndexPolicy() {
    }

    public static int representativeYear(CarModel car, ModelReliability reliability) {
        Integer override = REPRESENTATIVE_YEAR_OVERRIDES.get(modelKey(car));
        if (override != null && override >= car.startYear() && override <= car.endYear()) {
            return override;
        }
        if (reliability != null && reliability.bestYears() != null && !reliability.bestYears().isEmpty()) {
            return reliability.bestYears().stream()
                    .filter(year -> year >= car.startYear() && year <= car.endYear())
                    .max(Integer::compareTo)
                    .orElse(car.endYear() > 0 ? car.endYear() : car.startYear());
        }
        return car.endYear() > 0 ? car.endYear() : car.startYear();
    }

    public static boolean hasRepresentativeYearOverride(CarModel car) {
        Integer override = REPRESENTATIVE_YEAR_OVERRIDES.get(modelKey(car));
        return override != null && override >= car.startYear() && override <= car.endYear();
    }

    public static boolean isIndexableDecision(CarModel car) {
        return INDEXABLE_DECISION_MODELS.contains(modelKey(car)) && hasRepresentativeYearOverride(car);
    }

    public static boolean isIndexableMileage(CarModel car, int mileage) {
        return INDEXABLE_MILEAGE_PAGES.getOrDefault(modelKey(car), Set.of()).contains(mileage);
    }

    public static OptionalInt preferredMileage(CarModel car) {
        return INDEXABLE_MILEAGE_PAGES.getOrDefault(modelKey(car), Set.of()).stream()
                .mapToInt(Integer::intValue)
                .sorted()
                .findFirst();
    }

    public static boolean isIndexableFault(CarModel car, Fault fault) {
        // The available Hyundai bulletin applies to the prior MD/UD generation,
        // not the AD generation represented by this URL.
        return !("hyundai|elantra".equals(modelKey(car))
                && "piston-slap".equals(faultSlug(fault.component())));
    }

    public static String decisionPath(CarModel car, ModelReliability reliability) {
        return "/should-i-fix/" + representativeYear(car, reliability) + "-"
                + normalize(car.brand()) + "-" + normalize(car.model());
    }

    public static String normalize(String input) {
        if (input == null) {
            return "";
        }
        return input.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }

    public static String faultSlug(String component) {
        if (component == null) {
            return "";
        }
        return component.toLowerCase()
                .replace(" ", "-")
                .replaceAll("[^a-z0-9-]", "")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    private static String modelKey(CarModel car) {
        return normalize(car.brand()) + "|" + normalize(car.model());
    }
}
