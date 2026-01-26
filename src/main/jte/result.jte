@import com.carmoneypit.engine.api.InputModels.EngineInput
@import com.carmoneypit.engine.api.InputModels.SimulationControls
@import com.carmoneypit.engine.api.OutputModels.VerdictResult
@import com.carmoneypit.engine.api.InputModels.FailureSeverity
@import com.carmoneypit.engine.api.InputModels.MobilityStatus
@import com.carmoneypit.engine.api.InputModels.HassleTolerance

@param EngineInput input
@param VerdictResult result
@param SimulationControls controls
@param String verdictTitle
@param String verdictExplanation
@param String verdictAction
@param String verdictCss

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Analysis Result</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@picocss/pico@2/css/pico.min.css">
    <script src="https://unpkg.com/htmx.org@1.9.10"></script>
    <style>
        body { padding-top: 2rem; }
        .container { max-width: 800px; }
        .sim-lab { background-color: #f4f4f5; padding: 2rem; border-radius: 8px; margin-top: 2rem; }
        /* Simple fade in animation */
        .fade-in.htmx-added { opacity: 0; }
        .fade-in { opacity: 1; transition: opacity 0.5s ease-in; }
        .verdict-terminate { border-left: 5px solid #ff4757; padding-left: 1rem; }
        .verdict-sustain { border-left: 5px solid #2ed573; padding-left: 1rem; }
        .verdict-probation { border-left: 5px solid #ffa502; padding-left: 1rem; }
    </style>
</head>
<body>
    <main class="container">
        <a href="/" role="button" class="secondary outline">← Start Over</a>
        
        <hr>

        <!-- Dynamic Verdict Section -->
        @template.fragments.verdict_card(result = result, verdictTitle = verdictTitle, verdictExplanation = verdictExplanation, verdictAction = verdictAction, verdictCss = verdictCss)

        <!-- Simulation Lab -->
        <section class="sim-lab">
            <h3>Simulation Lab</h3>
            <p><small>Adjust the sliders below to see how changes in your situation affect the verdict.</small></p>
            
            <form hx-post="/simulate" hx-target="#verdict-card" hx-trigger="change">
                <!-- Immutable Inputs (Hidden) -->
                <input type="hidden" name="vehicleType" value="${input.vehicleType()}">
                <input type="hidden" name="mileage" value="${input.mileage()}">
                <input type="hidden" name="repairQuoteUsd" value="${input.repairQuoteUsd()}">

                <div class="grid">
                    <div>
                        <label for="failureSeverity">Failure Severity</label>
                        <select name="failureSeverity">
                            @for(FailureSeverity val : FailureSeverity.values())
                                <option value="${val}" selected="${val == controls.failureSeverity()}">${val}</option>
                            @endfor
                        </select>
                    </div>
                    
                    <div>
                        <label for="mobilityStatus">Vehicle Status</label>
                        <select name="mobilityStatus">
                            @for(MobilityStatus val : MobilityStatus.values())
                                <option value="${val}" selected="${val == controls.mobilityStatus()}">${val}</option>
                            @endfor
                        </select>
                    </div>
                    
                    <div>
                        <label for="hassleTolerance">Switching Tolerance</label>
                        <select name="hassleTolerance">
                            @for(HassleTolerance val : HassleTolerance.values())
                                <option value="${val}" selected="${val == controls.hassleTolerance()}">${val}</option>
                            @endfor
                        </select>
                    </div>
                </div>
            </form>
        </section>
    </main>
</body>
</html>
