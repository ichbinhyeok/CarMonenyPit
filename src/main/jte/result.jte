@import com.carmoneypit.engine.api.InputModels.EngineInput
@import com.carmoneypit.engine.api.InputModels.SimulationControls
@import com.carmoneypit.engine.api.OutputModels.VerdictResult
@import com.carmoneypit.engine.api.InputModels.FailureSeverity
@import com.carmoneypit.engine.api.InputModels.MobilityStatus
@import com.carmoneypit.engine.api.InputModels.HassleTolerance
@import com.carmoneypit.engine.api.InputModels.RetentionHorizon

@param EngineInput input
@param VerdictResult result
@param SimulationControls controls
@param String verdictTitle
@param String verdictExplanation
@param String verdictAction
@param String verdictCss
@param String leadLabel
@param String leadDescription
@param String leadUrl
@param String viewMode = "OWNER"
@param String shareToken = null
@param String ogTitle = null
@param boolean isValueEstimated = false

@template.layout(
    title = (ogTitle != null ? ogTitle : "Analysis Engine | Future Verdict"),
    noindex = true,
    ogTitle = ogTitle,
    ogDescription = verdictExplanation,
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

            .pill { 
                flex: 1; 
                cursor: pointer; 
                position: relative; 
                margin: 0; 
            }
            
            .pill input { 
                position: absolute; 
                opacity: 0; 
                width: 0; height: 0; margin: 0;
            }
            
            .pill span {
                display: block;
                padding: 0.75rem;
                text-align: center;
                font-size: 0.85rem;
                font-weight: 700;
                border-radius: 12px;
                transition: all 0.2s;
                color: var(--text-secondary);
                background: transparent;
            }
            
            .pill input:checked + span { 
                background: #fff; 
                color: #000; 
                box-shadow: 0 4px 12px rgba(0,0,0,0.05); 
            }

            /* Modal Styles */
            dialog {
                border: none;
                border-radius: 24px;
                padding: 2.5rem;
                max-width: 600px;
                width: 90%;
                box-shadow: 0 20px 50px rgba(0,0,0,0.2);
                background: #fff;
            }
            dialog::backdrop {
                background: rgba(0,0,0,0.5);
                backdrop-filter: blur(4px);
            }

            @media (max-width: 1024px) {
                .sim-grid { grid-template-columns: 1fr; }
            }

            /* Sticky Bar Styles */
            .sticky-bar {
                position: fixed;
                top: 20px;
                left: 50%;
                transform: translateX(-50%) translateY(-20px);
                width: 90%;
                max-width: 700px;
                background: rgba(255, 255, 255, 0.95);
                backdrop-filter: blur(12px);
                -webkit-backdrop-filter: blur(12px);
                border: 1px solid rgba(0,0,0,0.08);
                border-radius: 100px;
                padding: 0.75rem 1.5rem;
                box-shadow: 0 20px 60px rgba(0,0,0,0.15);
                z-index: 9999;
                transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
                opacity: 0;
                pointer-events: none;
            }
            .sticky-bar.visible {
                opacity: 1;
                pointer-events: all;
                transform: translateX(-50%) translateY(0);
            }
            .sticky-bar .sticky-content { display: flex; justify-content: space-between; align-items: center; width: 100%; }
            .sticky-bar .sticky-left { display: flex; align-items: center; gap: 1rem; }
            .sticky-bar .status-dot { width: 10px; height: 10px; border-radius: 50%; }
            .sticky-bar.verdict-liquidate .status-dot { background: #ef4444; box-shadow: 0 0 12px rgba(239, 68, 68, 0.6); }
            .sticky-bar.verdict-sustain .status-dot { background: #10b981; box-shadow: 0 0 12px rgba(16, 185, 129, 0.6); }
            .sticky-bar.verdict-risk_alert .status-dot { background: #f59e0b; box-shadow: 0 0 12px rgba(245, 158, 11, 0.6); }
            
            .sticky-bar .sticky-title { font-weight: 800; font-size: 1rem; letter-spacing: -0.02em; color: #1e293b; }
            .sticky-bar .sticky-action-text { font-size: 0.85rem; color: var(--text-secondary); font-weight: 500; border-left: 1px solid #e2e8f0; padding-left: 1rem; }
            
            .sticky-bar .sticky-cta {
                background: #0f172a; color: #fff; padding: 0.6rem 1.2rem; border-radius: 20px; text-decoration: none; font-weight: 700; font-size: 0.85rem; display: flex; align-items: center; gap: 0.4rem; transition: transform 0.2s;
            }
            .sticky-bar .sticky-cta:hover { transform: scale(1.05); }

            @media (max-width: 640px) {
                .sticky-bar .sticky-action-text { display: none; }
                .sticky-bar { padding: 0.6rem 1rem; width: 95%; }
            }
        </style>
    `,
    content = @`
        @template.fragments.sticky_bar(result = result, verdictTitle = verdictTitle, verdictExplanation = verdictExplanation, verdictAction = verdictAction, verdictCss = verdictCss, leadLabel = leadLabel, leadDescription = leadDescription, leadUrl = leadUrl, isValueEstimated = isValueEstimated, oob = false)

        <main class="container">
            <!-- Verdict Dashboard -->
            <section class="bento-card verdict-cell ${verdictCss}">
                @if(viewMode.equals("RECEIPT"))
                    <div style="background: #eff6ff; color: #1e3a8a; padding: 0.75rem; border-radius: 12px; text-align: center; margin-bottom: 1.5rem; font-weight: 700; font-size: 0.9rem;">
                        SHARED DIAGNOSTIC RECEIPT • READ ONLY
                    </div>
                @endif
                
                @template.fragments.verdict_card(result = result, verdictTitle = verdictTitle, verdictExplanation = verdictExplanation, verdictAction = verdictAction, verdictCss = verdictCss, leadLabel = leadLabel, leadDescription = leadDescription, leadUrl = leadUrl, isValueEstimated = isValueEstimated)
                
                <!-- Viral & Explanation Actions -->
                <div style="display: flex; gap: 1rem; margin-top: 2rem; justify-content: center;">
                    <button onclick="document.getElementById('explanation-modal').showModal()" 
                            style="background: transparent; border: 1px solid #e2e8f0; padding: 0.8rem 1.5rem; border-radius: 12px; font-weight: 700; color: var(--text-secondary); cursor: pointer;">
                        Why this verdict?
                    </button>
                    
                    @if(viewMode.equals("OWNER") && shareToken != null)
                        <button onclick="shareResult('${shareToken}')" 
                                style="background: #111; color: #fff; padding: 0.8rem 1.5rem; border-radius: 12px; font-weight: 700; cursor: pointer; display: flex; align-items: center; gap: 0.5rem;">
                            <span>Share Verdict</span>
                            <svg width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path d="M4 12v8a2 2 0 002 2h12a2 2 0 002-2v-8m-4-6l-4-4-4 4m4-4v13"></path></svg>
                        </button>
                    @endif
                </div>
            </section>

            <!-- Simulation Parameters -->
            <section class="bento-card sim-cell">
                @if(viewMode.equals("RECEIPT"))
                    <!-- Receipt Mode: Static Summary -->
                    <div style="text-align: center; padding: 2rem;">
                        <h3 style="font-size: 1.5rem; font-weight: 800; margin-bottom: 1rem;">Is your car a Money Pit too?</h3>
                        <p style="color: var(--text-secondary); margin-bottom: 2rem;">Run your own precision actuarial diagnostic in 30 seconds.</p>
                        <a href="/" style="background: var(--futurist-blue); color: #fff; padding: 1rem 2rem; border-radius: 16px; text-decoration: none; font-weight: 800; font-size: 1.1rem; display: inline-block;">
                            Run My Own Diagnostic
                        </a>
                    </div>
                @else
                    <!-- Owner Mode: Interactive Lab -->
                    <h3 style="font-size: 1.25rem; font-weight: 800; letter-spacing: -0.02em;">Stress-Test Simulation</h3>
                    <p style="color: var(--text-secondary); font-size: 0.95rem; margin-top: 0.5rem; font-weight: 500;">
                        Manipulate variables to challenge the verdict.
                    </p>
                    
                    <form hx-post="/simulate" hx-target="#verdict-card" hx-trigger="change, submit" hx-swap="outerHTML" onsubmit="event.preventDefault(); return false;">
                        <input type="hidden" name="vehicleType" value="${input.vehicleType()}">
                        <input type="hidden" name="mileage" value="${input.mileage()}">
                        <input type="hidden" name="repairQuoteUsd" value="${input.repairQuoteUsd()}">
                        <input type="hidden" name="isValueEstimated" value="${isValueEstimated}" id="isEstInput">

                        <div class="sim-grid">
                            <!-- Market Value Input (New) -->
                            <div class="sim-item">
                                <label style="display: flex; justify-content: space-between; align-items: center;">
                                    Market Value
                                    @if(isValueEstimated)
                                        <span class="est-badge" style="background: #fbbf24; color: #fff; padding: 2px 6px; border-radius: 4px; font-size: 0.6rem; margin-left:8px;">ESTIMATED</span>
                                    @endif
                                </label>
                                <div style="position: relative;">
                                    <span style="position: absolute; left: 1rem; top: 50%; transform: translateY(-50%); color: var(--text-secondary); font-weight: 700;">$</span>
                                    <input type="number" name="currentValueUsd" value="${input.currentValueUsd()}"
                                           style="width: 100%; padding: 0.8rem 1rem 0.8rem 2.5rem; border-radius: 12px; border: 1px solid #e2e8f0; font-weight: 700; font-size: 1rem;"
                                           oninput="document.getElementById('isEstInput').value = 'false';">
                                </div>
                            </div>
                            <!-- Retention Horizon (New) -->
                            <div class="sim-item">
                                <label>Retention Plans</label>
                                <div class="pill-group">
                                    <label class="pill">
                                        <input type="radio" name="retentionHorizon" value="MONTHS_6">
                                        <span>6 Mo</span>
                                    </label>
                                    <label class="pill">
                                        <input type="radio" name="retentionHorizon" value="YEARS_1">
                                        <span>1 Yr</span>
                                    </label>
                                    <label class="pill">
                                        <input type="radio" name="retentionHorizon" value="YEARS_3">
                                        <span>3 Yrs</span>
                                    </label>
                                </div>
                            </div>

                            <div class="sim-item">
                                <label>Severity</label>
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
                                <label>Mobility</label>
                                <div class="pill-group">
                                    @for(MobilityStatus val : MobilityStatus.values())
                                        <label class="pill">
                                            <input type="radio" name="mobilityStatus" value="${val}" checked="${val == controls.mobilityStatus()}">
                                            <span>${val == MobilityStatus.NEEDS_TOW ? "Offline" : "Operational"}</span>
                                        </label>
                                    @endfor
                                </div>
                            </div>
                        </div>

                        <!-- Hassle Tolerance Slider -->
                        <div style="margin-top: 2rem;">
                             <label style="display: block; font-size: 0.75rem; font-weight: 700; color: var(--text-secondary); text-transform: uppercase; margin-bottom: 1rem;">Tolerance Threshold</label>
                             <div style="display: flex; align-items: center; gap: 1rem;">
                                <span style="font-size: 0.75rem; font-weight: 700; color: var(--text-secondary);">Conservative</span>
                                <input type="range" name="hassleToleranceSlider" min="0" max="2" step="1" 
                                       style="flex: 1; accent-color: var(--futurist-blue); cursor: pointer;"
                                       value="${controls.hassleTolerance() == HassleTolerance.HATE_SWITCHING ? 0 : (controls.hassleTolerance() == HassleTolerance.NEUTRAL ? 1 : 2)}" 
                                       oninput="document.getElementById('hassleToleranceHidden').value = this.value == 0 ? 'HATE_SWITCHING' : (this.value == 1 ? 'NEUTRAL' : 'WANT_NEW_CAR'); this.dispatchEvent(new Event('change', {bubbles: true}));">
                                <input type="hidden" id="hassleToleranceHidden" name="hassleTolerance" value="${controls.hassleTolerance()}">
                                <span style="font-size: 0.75rem; font-weight: 700; color: var(--text-secondary);">Aggressive</span>
                            </div>
                        </div>
                    </form>
                @endif
            </section>
        </main>

        <!-- Strawman Explanation Modal -->
        <dialog id="explanation-modal">
            <h3 style="font-size: 1.5rem; font-weight: 800; margin-bottom: 1rem;">Why this verdict?</h3>
            <div style="background: #fff5f5; border-left: 4px solid #fc8181; padding: 1rem; margin-bottom: 1.5rem;">
                <h4 style="color: #c53030; font-size: 0.9rem; font-weight: 800; margin-bottom: 0.5rem;">THE STRAWMAN ARGUMENT</h4>
                <p style="font-size: 0.95rem; line-height: 1.5;">
                    "Sure, you strictly <strong>can</strong> repair it. But notice the <strong>Asset Bleed</strong>. 
                    Even if you fix it perfectly, depreciation will erase that value in less than 8 months."
                </p>
            </div>
            <p style="margin-bottom: 1rem;">
                Our actuarial engine calculated a <strong>Cost of Inaction</strong> that exceeds your repair quote.
                The <strong>Retention Exposure</strong> (visible in the breakdown) indicates that holding this asset requires taking on disproportionate risk.
            </p>
            <form method="dialog" style="text-align: right;">
                <button style="background: #111; color: #fff; border: none; padding: 0.8rem 1.5rem; border-radius: 12px; font-weight: 700; cursor: pointer;">I Understand</button>
            </form>
        </dialog>

        <script>
            function shareResult(token) {
                const url = window.location.origin + "/RentVerdict/verdict?token=" + token;
                navigator.clipboard.writeText(url).then(() => {
                    alert('Link copied to clipboard! Share this diagnostic receipt.');
                });
            }

            // Scroll & HTMX Observer for Sticky Bar
            function updateSticky() {
                const nav = document.getElementById('sticky-verdict-bar');
                if (nav) {
                    if (window.scrollY > 300) {
                        nav.classList.add('visible');
                    } else {
                        nav.classList.remove('visible');
                    }
                }
            }
            document.addEventListener('scroll', updateSticky);
            document.body.addEventListener('htmx:afterSettle', updateSticky);
        </script>
    `
)
