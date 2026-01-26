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
@param String leadLabel
@param String leadDescription

@template.layout(
    title = "Analysis Engine | Future Verdict",
    extraStyles = @`
        <style>
            .container {
                max-width: 1100px;
                margin: 0 auto;
                display: grid;
                grid-template-columns: repeat(12, 1fr);
                gap: 1.5rem;
                animation: bentoEntrance 0.8s cubic-bezier(0.16, 1, 0.3, 1);
            }

            .bento-card {
                background: var(--futurist-card);
                backdrop-filter: blur(24px);
                -webkit-backdrop-filter: blur(24px);
                border: 1px solid rgba(255, 255, 255, 0.7);
                border-radius: 32px;
                padding: 2.5rem;
                box-shadow: 0 4px 24px rgba(0, 0, 0, 0.01);
                transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
            }

            .verdict-cell { grid-column: span 12; }
            .sim-cell { grid-column: span 12; background: #fff; }

            /* Verdict Colors */
            .verdict-liquidate { border-top: 6px solid var(--red-alert); }
            .verdict-sustain { border-top: 6px solid var(--green-safe); }
            .verdict-risk_alert { border-top: 6px solid var(--orange-warn); }

            /* Sim Lab Grid */
            .sim-grid { display: grid; grid-template-columns: 1fr 1fr 1.5fr; gap: 2rem; margin-top: 2rem; }
            .sim-item label { display: block; font-size: 0.75rem; font-weight: 700; color: var(--text-secondary); text-transform: uppercase; margin-bottom: 1rem; letter-spacing: 0.05em; }

            .pill-group {
                display: flex;
                background: #f1f5f9;
                padding: 4px;
                border-radius: 16px;
                gap: 4px;
            }

            .pill { flex: 1; cursor: pointer; position: relative; }
            .pill input { position: absolute; opacity: 0; }
            .pill span {
                display: block;
                padding: 0.75rem;
                text-align: center;
                font-size: 0.85rem;
                font-weight: 700;
                border-radius: 12px;
                transition: all 0.2s;
                color: var(--text-secondary);
            }
            .pill input:checked + span { background: #fff; color: #000; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }

            @media (max-width: 1024px) {
                .sim-grid { grid-template-columns: 1fr; }
            }
        </style>
    `,
    content = @`
        <main class="container">
            <!-- Verdict Dashboard -->
            <section class="bento-card verdict-cell ${verdictCss}">
                @template.fragments.verdict_card(result = result, verdictTitle = verdictTitle, verdictExplanation = verdictExplanation, verdictAction = verdictAction, verdictCss = verdictCss, leadLabel = leadLabel, leadDescription = leadDescription)
            </section>

            <!-- Simulation Parameters (Linear Style) -->
            <section class="bento-card sim-cell">
                <h3 style="font-size: 1.25rem; font-weight: 800; letter-spacing: -0.02em;">Stress-Test Simulation</h3>
                <p style="color: var(--text-secondary); font-size: 0.95rem; margin-top: 0.5rem; font-weight: 500;">
                    Manipulate environmental variables to observe decision-tree variance.
                </p>
                
                <form hx-post="/simulate" hx-target="#verdict-card" hx-trigger="change" hx-swap="outerHTML">
                    <input type="hidden" name="vehicleType" value="${input.vehicleType()}">
                    <input type="hidden" name="mileage" value="${input.mileage()}">
                    <input type="hidden" name="repairQuoteUsd" value="${input.repairQuoteUsd()}">

                    <div class="sim-grid">
                        <div class="sim-item">
                            <label>Component Failure Severity</label>
                            <div class="pill-group">
                                @for(FailureSeverity val : FailureSeverity.values())
                                    <label class="pill">
                                        <input type="radio" name="failureSeverity" value="${val}" checked="${val == controls.failureSeverity()}">
                                        <span>${val == FailureSeverity.ENGINE_TRANSMISSION ? "Critical" : (val == FailureSeverity.SUSPENSION_BRAKES ? "Moderate" : "General")}</span>
                                    </label>
                                @endfor
                            </div>
                        </div>
                        
                        <div class="sim-item">
                            <label>Operational Mobility</label>
                            <div class="pill-group">
                                @for(MobilityStatus val : MobilityStatus.values())
                                    <label class="pill">
                                        <input type="radio" name="mobilityStatus" value="${val}" checked="${val == controls.mobilityStatus()}">
                                        <span>${val == MobilityStatus.NEEDS_TOW ? "Offline" : "Operational"}</span>
                                    </label>
                                @endfor
                            </div>
                        </div>
                        
                        <div class="sim-item">
                            <label>Switching Friction Threshold</label>
                            <div style="display: flex; align-items: center; gap: 1rem; padding: 0.5rem 0;">
                                <span style="font-size: 0.75rem; font-weight: 700; color: var(--text-secondary);">Conservative</span>
                                <input type="range" name="hassleToleranceSlider" min="0" max="2" step="1" 
                                       style="flex: 1; accent-color: var(--futurist-blue);"
                                       value="${controls.hassleTolerance() == HassleTolerance.HATE_SWITCHING ? 0 : (controls.hassleTolerance() == HassleTolerance.NEUTRAL ? 1 : 2)}" 
                                       oninput="document.getElementById('hassleToleranceHidden').value = this.value == 0 ? 'HATE_SWITCHING' : (this.value == 1 ? 'NEUTRAL' : 'WANT_NEW_CAR'); this.dispatchEvent(new Event('change', {bubbles: true}));">
                                <input type="hidden" id="hassleToleranceHidden" name="hassleTolerance" value="${controls.hassleTolerance()}">
                                <span style="font-size: 0.75rem; font-weight: 700; color: var(--text-secondary);">Aggressive</span>
                            </div>
                        </div>
                    </div>
                </form>
            </section>
        </main>
    `
)
