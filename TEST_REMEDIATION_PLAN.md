# Test Remediation Plan

The application build (`./gradlew build`) is currently failing due to compilation errors in the test suite. The main application code is healthy and compiles correctly, but the tests have drifted from the evolving codebase.

## Summary of Issues
There are approximately 24 compilation errors in `src/test/java`, primarily due to:
1.  **Constructor Mismatches**: `DecisionEngine`, `EngineInput`, and `VerdictResult` constructors have been updated in the main code (adding new fields/dependencies) but not in the tests.
2.  **Method Signature Changes**: `ValuationService.estimateValue` and `VerdictPresenter` methods (like `getLeadLabel`) now require different or additional arguments (e.g., `CarBrand`, `EngineInput`).
3.  **Invalid Instantiation**: `new ValuationService()` is used in tests, but the class now likely requires dependency injection (constructor args).

## Affected Files
1.  `src/test/java/com/carmoneypit/engine/web/WebLayerTest.java`
2.  `src/test/java/com/carmoneypit/engine/EngineTest.java`
3.  `src/test/java/com/carmoneypit/engine/core/ValuationServiceTest.java`

## Recommended Actions
To restore the build health:

1.  **Update `ValuationServiceTest`**:
    *   Mock `ObjectMapper` and `CarDataService` and pass them to the `ValuationService` constructor.
    *   Update `estimateValue` calls to include `CarBrand` (e.g., `CarBrand.TOYOTA`).

2.  **Update `EngineTest`**:
    *   Mock `ValuationService` and pass it to the `DecisionEngine` constructor.
    *   Update `EngineInput` instantiation to match the new record components (likely adding `InputModels` fields).

3.  **Update `WebLayerTest`**:
    *   Update `VerdictResult` mock objects to include all new fields (financial line items, peer data, economic context).
    *   Update `VerdictPresenter` mock expectations to accept `any(EngineInput.class)` and `any(SimulationControls.class)`.

## Temporary Workaround
Until these tests are refactored, you can build the application without running tests using:
```bash
./gradlew build -x test
```
