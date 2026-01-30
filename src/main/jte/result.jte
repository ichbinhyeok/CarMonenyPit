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
    title = (ogTitle != null ? ogTitle : "AutoMoneyPit Market Liquidation Engine"),
    noindex = true,
    ogTitle = ogTitle,
    ogDescription = verdictExplanation,
    content = @`
        <style>
            .container {
                max-width: 1100px;
                margin: 0 auto;
                display: grid;
                grid-template-columns: repeat(12, 1fr);
                gap: 1.5rem;
                margin-bottom: 5rem;
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

            @media (max-width: 768px) {
                .container { gap: 1rem; margin-bottom: 3rem; }
                .bento-card { padding: 1.5rem; border-radius: 24px; }
            }

            .verdict-cell { grid-column: span 12; }
            .sim-cell { grid-column: span 12; background: #fff; }

            /* Verdict Colors */
            .verdict-liquidate { border-top: 6px solid var(--red-alert); }
            .verdict-sustain { border-top: 6px solid var(--green-safe); }
            .verdict-risk_alert { border-top: 6px solid var(--orange-warn); }

            /* Sim Lab Layout */
            .sim-grid-revamped { 
                display: grid; 
                grid-template-columns: repeat(auto-fit, minmax(min(100%, 280px), 1fr)); 
                gap: 1.5rem; 
                margin-top: 2rem; 
            }
            .sim-column { display: flex; flex-direction: column; gap: 1.5rem; }
            
            .sim-item { display: flex; flex-direction: column; min-width: 0; }
            .sim-item label { display: block; font-size: 0.75rem; font-weight: 700; color: var(--text-secondary); text-transform: uppercase; margin-bottom: 0.75rem; letter-spacing: 0.05em; }

            .pill-group {
                display: flex;
                background: #f1f5f9;
                padding: 4px;
                border-radius: 12px;
                gap: 4px;
                width: 100%;
            }

            .pill { 
                flex: 1; 
                cursor: pointer; 
                position: relative; 
                margin: 0; 
            }
            
            .pill input { display: none !important; }
            
            .pill span {
                display: block;
                padding: 0.6rem 0.5rem;
                text-align: center;
                font-size: 0.75rem;
                font-weight: 700;
                border-radius: 8px;
                transition: all 0.2s;
                color: #64748b;
                background: transparent;
                border: 1px solid transparent;
            }
            
            .pill input:checked + span { 
                background: #fff !important; 
                color: #0f172a !important; 
                box-shadow: 0 2px 8px rgba(0,0,0,0.05) !important; 
                border-color: rgba(0,0,0,0.05);
            }

            /* Vertical Pills for Severity */
            .vertical-pills { display: flex; flex-direction: column; gap: 0.5rem; }
            .pill-row { cursor: pointer; display: block; }
            .pill-row input { display: none; }
            .pill-text {
                display: block;
                padding: 0.8rem 1rem;
                background: #f8fafc;
                border: 1px solid #e2e8f0;
                border-radius: 12px;
                font-size: 0.85rem;
                font-weight: 600;
                color: #475569;
                transition: all 0.2s;
            }
            .pill-row input:checked + .pill-text {
                background: #eff6ff;
                border-color: var(--futurist-blue);
                color: var(--futurist-blue);
                box-shadow: 0 0 0 1px var(--futurist-blue);
            }

            /* Slider Styling */
            .slider-container { position: relative; padding: 0 0.5rem; }
            input[type=range] {
                -webkit-appearance: none;
                width: 100%;
                background: transparent;
            }
            input[type=range]::-webkit-slider-thumb {
                -webkit-appearance: none;
                height: 24px;
                width: 24px;
                border-radius: 50%;
                background: #0f172a;
                cursor: pointer;
                margin-top: -10px;
                box-shadow: 0 4px 10px rgba(0,0,0,0.2);
            }
            input[type=range]::-webkit-slider-runnable-track {
                width: 100%;
                height: 4px;
                cursor: pointer;
                background: #e2e8f0;
                border-radius: 2px;
            }
            .slider-labels {
                display: flex;
                justify-content: space-between;
                margin-top: 0.75rem;
                font-size: 0.7rem;
                font-weight: 700;
                color: var(--text-secondary);
                text-transform: uppercase;
                letter-spacing: 0.05em;
            }

            .est-badge {
                background: var(--futurist-blue);
                color: #fff;
                padding: 0.2rem 0.6rem;
                border-radius: 6px;
                font-size: 0.65rem;
                font-weight: 800;
                letter-spacing: 0.05em;
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
                .sim-grid-revamped { grid-template-columns: 1fr; gap: 1.5rem; }
            }

            /* --- UI Constants --- */
            :root {
                --z-sticky: 2147483647;
                --sticky-width: auto;
                --sticky-max-width: 800px;
                --header-height-offset: 80px;
            }

            /* --- Sticky Bar System --- */
            .sticky-bar {
                position: fixed;
                left: 50%;
                transform: translateX(-50%);
                width: var(--sticky-width);
                max-width: var(--sticky-max-width);
                background: rgba(255, 255, 255, 0.95);
                backdrop-filter: blur(8px);
                -webkit-backdrop-filter: blur(8px);
                border: 1px solid rgba(0,0,0,0.08);
                border-radius: 100px;
                padding: 0.4rem 1rem 0.4rem 1.4rem;
                box-shadow: 0 4px 12px rgba(0,0,0,0.08); 
                z-index: var(--z-sticky) !important;
                transition: opacity 0.3s ease-out, transform 0.4s cubic-bezier(0.16, 1, 0.3, 1);
                opacity: 0;
                transform: translateX(-50%) translateY(-20px);
                pointer-events: none;
                visibility: hidden;
                display: flex; /* Always flex but hidden */
            }

            .sticky-bar.is-visible {
                visibility: visible;
                opacity: 1;
                transform: translateX(-50%) translateY(0);
                pointer-events: all;
            }

            .sticky-content { display: flex; align-items: center; gap: 1.5rem; }
            .sticky-left { display: flex; align-items: center; gap: 0.8rem; }
            .status-dot { width: 8px; height: 8px; border-radius: 50%; }
            
            .verdict-liquidate .status-dot { background: var(--red-alert); box-shadow: 0 0 8px rgba(239, 68, 68, 0.4); }
            .verdict-sustain .status-dot { background: var(--green-safe); box-shadow: 0 0 8px rgba(16, 185, 129, 0.4); }
            .verdict-risk_alert .status-dot { background: var(--orange-warn); box-shadow: 0 0 8px rgba(245, 158, 11, 0.4); }
            
            .sticky-title { font-weight: 800; font-size: 0.9rem; letter-spacing: -0.01em; color: var(--text-main); }
            .sticky-action-text { font-size: 0.8rem; color: var(--text-secondary); font-weight: 600; border-left: 2px solid #e2e8f0; padding-left: 0.8rem; }
            
            .sticky-cta {
                background: var(--text-main); color: #fff; padding: 0.5rem 1rem; border-radius: 20px; text-decoration: none; 
                font-weight: 700; font-size: 0.8rem; display: flex; align-items: center; gap: 0.4rem; transition: transform 0.2s; white-space: nowrap;
            }
            .sticky-cta:hover { transform: scale(1.05); }

            /* --- Dashboard Actions --- */
            .verdict-actions { display: flex; gap: 1rem; margin-top: 2rem; justify-content: center; }
            .btn-secondary { background: transparent; border: 1px solid #e2e8f0; padding: 0.8rem 1.5rem; border-radius: 12px; font-weight: 700; color: var(--text-secondary); cursor: pointer; }
            .btn-primary-dark { background: #111; color: #fff; padding: 0.8rem 1.5rem; border-radius: 12px; font-weight: 700; cursor: pointer; display: flex; align-items: center; gap: 0.5rem; border: none; }

            /* --- Responsive --- */
            @media (max-width: 1024px) {
                .sticky-action-text { display: none; }
            }

            @media (max-width: 768px) {
                .sticky-bar {
                    top: auto !important;
                    bottom: 20px;
                    border-radius: 20px;
                    padding: 0.8rem 1.2rem;
                    width: 90%;
                    max-width: 400px;
                    transform: translateX(-50%) translateY(20px);
                }
                .sticky-bar.is-visible { transform: translateX(-50%) translateY(0); }
                .sticky-content { justify-content: space-between; width: 100%; }
                .sticky-left { flex-direction: row; gap: 0.8rem; }
                .sticky-text { display: flex; flex-direction: column; align-items: flex-start; }
                .sticky-title { font-size: 0.9rem; margin-bottom: 2px; }
                .sticky-action-text { display: none; }
                .status-dot { margin-top: 2px; }
            }

            @media (max-width: 640px) {
                 .verdict-actions { flex-direction: column; }
                 .verdict-actions button { width: 100%; }
            }
            /* --- Regret Comparison Chart --- */
            .regret-dashboard { 
                grid-column: span 12; 
                display: flex; 
                flex-direction: column; 
                gap: 2rem; 
                padding: 3rem;
                background: linear-gradient(145deg, #ffffff, #f8fafc);
                border: 1px solid #e2e8f0;
            }
            .dashboard-header { text-align: center; margin-bottom: 1rem; }
            .dashboard-header h2 { font-size: 1.75rem; font-weight: 800; letter-spacing: -0.03em; color: #0f172a; }
            .dashboard-header p { color: #64748b; font-size: 1rem; margin-top: 0.5rem; }

            .comparison-grid {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 3rem;
                position: relative;
            }
            .comparison-grid::after {
                content: 'VS';
                position: absolute;
                left: 50%;
                top: 50%;
                transform: translate(-50%, -50%);
                background: #fff;
                width: 40px;
                height: 40px;
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 0.75rem;
                font-weight: 900;
                color: #94a3b8;
                border: 1px solid #e2e8f0;
                box-shadow: 0 4px 12px rgba(0,0,0,0.05);
                z-index: 2;
            }

            .regret-bar-container { display: flex; flex-direction: column; gap: 1rem; }
            .regret-label { display: flex; justify-content: space-between; font-weight: 800; font-size: 0.9rem; text-transform: uppercase; letter-spacing: 0.05em; }
            .label-stay { color: #10b981; }
            .label-move { color: #6366f1; }
            
            .bar-outer { 
                height: 48px; 
                background: #f1f5f9; 
                border-radius: 12px; 
                overflow: hidden; 
                position: relative;
                border: 1px solid rgba(0,0,0,0.05);
            }
            .bar-inner { 
                height: 100%; 
                transition: width 1s cubic-bezier(0.16, 1, 0.3, 1);
                display: flex;
                align-items: center;
                padding-right: 1rem;
                justify-content: flex-end;
                font-weight: 800;
                color: #fff;
                font-size: 1.1rem;
            }
            .bar-stay { background: linear-gradient(90deg, #10b981, #34d399); }
            .bar-move { background: linear-gradient(90deg, #6366f1, #818cf8); }

            .regret-description { font-size: 0.85rem; color: #64748b; line-height: 1.5; font-weight: 500; }

            @media (max-width: 768px) {
                .comparison-grid { grid-template-columns: 1fr; gap: 2rem; }
                .comparison-grid::after { display: none; }
                .regret-dashboard { padding: 1.5rem; }
            }
        </style>

        @template.fragments.sticky_bar(result = result, verdictTitle = verdictTitle, verdictExplanation = verdictExplanation, verdictAction = verdictAction, verdictCss = verdictCss, leadLabel = leadLabel, leadDescription = leadDescription, leadUrl = leadUrl, isValueEstimated = isValueEstimated, oob = false)

        <main class="container">
            <!-- Verdict Dashboard -->
            <section class="bento-card verdict-cell ${verdictCss}">
                @if(viewMode.equals("RECEIPT"))
                    <div style="background: #eff6ff; color: #1e3a8a; padding: 0.75rem; border-radius: 12px; text-align: center; margin-bottom: 1.5rem; font-weight: 700; font-size: 0.9rem;">
                        SHARED DIAGNOSTIC RECEIPT • READ ONLY
                    </div>
                @endif
                
                <div style="text-align: center; font-size: 0.75rem; color: var(--text-secondary); margin-bottom: 1.5rem; opacity: 0.8; line-height: 1.4;">
                    <span style="display: inline-block; background: #f8fafc; padding: 4px 12px; border-radius: 99px; border: 1px solid #e2e8f0;">
                         * Estimates based on national averages. Actual values vary. Not financial advice.
                    </span>
                </div>
                
                @template.fragments.verdict_card(result = result, verdictTitle = verdictTitle, verdictExplanation = verdictExplanation, verdictAction = verdictAction, verdictCss = verdictCss, leadLabel = leadLabel, leadDescription = leadDescription, leadUrl = leadUrl, isValueEstimated = isValueEstimated)
                
                <!-- Viral & Explanation Actions -->
                <div class="verdict-actions">
                    <button onclick="document.getElementById('explanation-modal').showModal()" class="btn-secondary">
                        Why this verdict?
                    </button>
                    
                    @if(viewMode.equals("OWNER") && shareToken != null)
                        <button onclick="shareResult('${shareToken}')" class="btn-primary-dark">
                            <span>Share Verdict</span>
                            <svg width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path d="M4 12v8a2 2 0 002 2h12a2 2 0 002-2v-8m-4-6l-4-4-4 4m4-4v13"></path></svg>
                        </button>
                    @endif
                </div>

                @if(viewMode.equals("RECEIPT"))
                    <!-- Receipt Mode: Static Summary -->
                    <div style="text-align: center; padding: 2rem;">
                        <h3 style="font-size: 1.5rem; font-weight: 800; margin-bottom: 1rem;">Is your car a Money Pit too?</h3>
                        <p style="color: var(--text-secondary); margin-bottom: 2rem;">Run your own data-driven analysis in 30 seconds.</p>
                        <a href="/" style="background: var(--futurist-blue); color: #fff; padding: 1rem 2rem; border-radius: 16px; text-decoration: none; font-weight: 800; font-size: 1.1rem; display: inline-block;">
                            Run My Own Diagnostic
                        </a>
                    </div>
                @else
                    <!-- Owner Mode: Interactive Lab -->
                    <h3 style="font-size: 1.25rem; font-weight: 800; letter-spacing: -0.02em;">Stress-Test Simulation</h3>
                    <p style="color: var(--text-secondary); font-size: 0.95rem; margin-top: 0.5rem; font-weight: 500;">
                        Adjust variables to see if the verdict holds up under pressure.
                    </p>
                    
                    <form hx-post="/simulate" hx-target="#verdict-card" hx-trigger="change delay:300ms, submit" hx-swap="outerHTML" hx-sync="this:replace" hx-indicator="#sim-loading" onsubmit="event.preventDefault(); return false;">
                        <input type="hidden" name="brand" value="${input.brand()}">
                        <input type="hidden" name="model" value="${input.model()}">
                        <input type="hidden" name="vehicleType" value="${input.vehicleType()}">
                        <input type="hidden" name="mileage" value="${input.mileage()}">
                        <input type="hidden" name="repairQuoteUsd" value="${input.repairQuoteUsd()}">
                        <input type="hidden" name="isValueEstimated" value="${isValueEstimated}" id="isEstInput">

                        <div class="sim-grid-revamped">
                            
                            <!-- Column 1: Asset Reality -->
                            <div class="sim-column">
                                <h4 style="color:var(--text-secondary); text-transform:uppercase; font-size: 0.75rem; letter-spacing:0.1em; border-bottom: 1px solid #e2e8f0; padding-bottom: 0.5rem;">Asset Reality</h4>
                                
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

                                <div class="sim-item">
                                    <label>Current Mobility</label>
                                    <div class="pill-group">
                                        @for(MobilityStatus val : MobilityStatus.values())
                                            <label class="pill">
                                                <input type="radio" name="mobilityStatus" value="${val}" checked="${val == controls.mobilityStatus()}">
                                                <span>${val == MobilityStatus.NEEDS_TOW ? "Offline / Tow" : "Drivable"}</span>
                                            </label>
                                        @endfor
                                    </div>
                                </div>
                            </div>

                            <!-- Column 2: Diagnosis -->
                            <div class="sim-column">
                                <h4 style="color:var(--text-secondary); text-transform:uppercase; font-size: 0.75rem; letter-spacing:0.1em; border-bottom: 1px solid #e2e8f0; padding-bottom: 0.5rem;">Diagnosis</h4>
                                
                                <div class="sim-item">
                                    <label>Failure Severity</label>
                                    <div class="vertical-pills">
                                         @for(FailureSeverity val : FailureSeverity.values())
                                            <label class="pill-row">
                                                <input type="radio" name="failureSeverity" value="${val}" checked="${val == controls.failureSeverity()}">
                                                <span class="pill-text">${val == FailureSeverity.ENGINE_TRANSMISSION ? "Critical (Engine/Trans)" : (val == FailureSeverity.SUSPENSION_BRAKES ? "Major (Suspension/Brakes)" : "General Wear")}</span>
                                            </label>
                                        @endfor
                                    </div>
                                </div>
                            </div>

                            <!-- Column 3: Strategy & Willingness -->
                            <div class="sim-column">
                                <h4 style="color:var(--text-secondary); text-transform:uppercase; font-size: 0.75rem; letter-spacing:0.1em; border-bottom: 1px solid #e2e8f0; padding-bottom: 0.5rem;">Strategy</h4>

                                <div style="display: inline-flex; align-items: center; gap: 0.5rem; padding: 0.25rem 0.75rem; border-radius: 9999px; background-color: #eff6ff; border: 1px solid #dbeafe; color: #1d4ed8; font-size: 0.75rem; font-weight: 600; text-transform: uppercase; letter-spacing: 0.05em; margin-bottom: 1rem;">
                                    <span style="width: 0.5rem; height: 0.5rem; border-radius: 50%; background-color: #2563eb;"></span>
                                    Market Liquidation Analysis
                                </div>
                                <div class="sim-item">
                                    <label>Target Retention</label>
                                    <div class="pill-group">
                                        <label class="pill"><input type="radio" name="retentionHorizon" value="MONTHS_6" checked="${controls.retentionHorizon() == RetentionHorizon.MONTHS_6}"><span>6 Mo</span></label>
                                        <label class="pill"><input type="radio" name="retentionHorizon" value="YEARS_1" checked="${controls.retentionHorizon() == RetentionHorizon.YEARS_1}"><span>1 Yr</span></label>
                                        <label class="pill"><input type="radio" name="retentionHorizon" value="YEARS_3" checked="${controls.retentionHorizon() == RetentionHorizon.YEARS_3}"><span>3+ Yrs</span></label>
                                    </div>
                                </div>

                                <div class="sim-item" style="margin-top: 1.5rem;">
                                    <label>Willingness to Walk Away</label>
                                    <div class="slider-container">
                                        <input type="range" min="0" max="2" step="1"
                                            value="${controls.hassleTolerance() == HassleTolerance.HATE_SWITCHING ? 0 : (controls.hassleTolerance() == HassleTolerance.WANT_NEW_CAR ? 2 : 1)}"
                                            onchange="updateTolerance(this.value)">
                                        <div class="slider-labels">
                                            <span>Low</span>
                                            <span>Neutral</span>
                                            <span>High</span>
                                        </div>
                                    </div>
                                    <input type="hidden" name="hassleTolerance" id="hassleToleranceInput" value="${controls.hassleTolerance()}">
                                </div>
                            </div>

                        </div>
                    </form>
                    
                    <script>
                        function updateTolerance(val) {
                            const input = document.getElementById('hassleToleranceInput');
                            if (val == 0) input.value = 'HATE_SWITCHING';
                            else if (val == 1) input.value = 'NEUTRAL';
                            else input.value = 'WANT_NEW_CAR';
                            
                            // Dispatch event for HTMX
                            const event = new Event('change', { bubbles: true });
                            input.dispatchEvent(event);
                        }
                    </script>
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
                Our analysis engine calculated a <strong>Cost of Inaction</strong> that exceeds your repair quote.
                The <strong>Retention Exposure</strong> (visible in the breakdown) indicates that holding this asset requires taking on disproportionate risk.
            </p>
            <form method="dialog">
                <button>I Understand</button>
            </form>
        </dialog>

        <script>
            /**
             * StickyBarController: Manages visibility and positioning of the floating verdict bar.
             */
            const StickyBarController = {
                bar: null,
                threshold: 50,
                initialized: false,
                
                init() {
                    this.bar = document.getElementById('sticky-verdict-bar');
                    if (!this.bar) return;

                    this.hoist();
                    
                    if (!this.initialized) {
                        this.bindEvents();
                        this.initialized = true;
                    }

                    this.update();
                    
                    // Fail-safe retries for dynamic loads
                    [100, 500, 2000].forEach(ms => setTimeout(() => this.update(), ms));
                },

                hoist() {
                    if (this.bar.parentElement !== document.body) {
                        document.body.appendChild(this.bar);
                    }
                },

                bindEvents() {
                    window.addEventListener('scroll', () => this.update(), { passive: true });
                    window.addEventListener('resize', () => this.update(), { passive: true });
                    document.body.addEventListener('htmx:afterSettle', () => this.init());
                },

                update() {
                    if (!this.bar) return;
                    
                    const scrollPos = window.scrollY || window.pageYOffset || document.documentElement.scrollTop;
                    const isMobile = window.innerWidth <= 768;
                    
                    // Dynamic Positioning
                    if (!isMobile) {
                        this.bar.style.top = '80px';
                    } else {
                        this.bar.style.top = 'auto'; // Reset top for mobile
                    }

                    if (scrollPos > this.threshold) {
                        this.bar.classList.add('is-visible');
                    } else {
                        this.bar.classList.remove('is-visible');
                    }
                }
            };

            // Global Share Function
            function shareResult(token) {
                const url = window.location.origin + "/report?token=" + token;
                navigator.clipboard.writeText(url).then(() => {
                    alert('Link copied to clipboard! Share this diagnostic receipt.');
                });
            }

            // Bootstrap
            StickyBarController.init();
        </script>
    `
)
