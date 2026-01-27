@import com.carmoneypit.engine.api.InputModels.VehicleType

@template.layout(
    title = "Car Money Pit | Precision Actuarial Analysis",
    extraStyles = @`
        <style>
            main {
                display: flex !important;
                flex-direction: column !important;
                justify-content: center !important;
                align-items: center !important;
            }
            .bento-grid {
                display: grid;
                grid-template-columns: repeat(12, 1fr);
                grid-template-rows: auto auto;
                gap: 1.5rem;
                max-width: 1100px;
                width: 100%;
                animation: bentoEntrance 0.8s cubic-bezier(0.16, 1, 0.3, 1);
            }

            .card {
                background: var(--futurist-card);
                backdrop-filter: blur(24px);
                -webkit-backdrop-filter: blur(24px);
                border: 1px solid rgba(255, 255, 255, 0.7);
                border-radius: 32px;
                padding: 2.5rem;
                box-shadow: 0 4px 24px rgba(0, 0, 0, 0.015);
                transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
            }

            .card:hover {
                transform: translateY(-4px) scale(1.005);
                box-shadow: 0 12px 48px rgba(0, 102, 255, 0.06);
                border-color: rgba(0, 102, 255, 0.15);
            }

            /* Branding Card */
            .hero-card {
                grid-column: span 12;
                display: flex;
                flex-direction: column;
                justify-content: center;
                text-align: center;
                padding: 4rem 2rem;
            }

            .hero-tag {
                color: var(--futurist-blue);
                font-size: 0.85rem;
                font-weight: 800;
                text-transform: uppercase;
                letter-spacing: 0.15em;
                margin-bottom: 1.5rem;
            }

            h1 {
                font-size: 4rem;
                font-weight: 800;
                letter-spacing: -0.04em;
                margin-bottom: 1.5rem;
                line-height: 1;
                color: #000;
            }

            h1 span {
                background: linear-gradient(90deg, #0066ff, #6366f1);
                -webkit-background-clip: text;
                -webkit-text-fill-color: transparent;
            }

            .hero-sub {
                font-size: 1.25rem;
                color: var(--text-secondary);
                font-weight: 500;
                max-width: 600px;
                margin: 0 auto;
                line-height: 1.5;
            }

            /* Form Card */
            .form-card {
                grid-column: span 12;
                background: #fff; /* High contrast for the primary action area */
            }

            .form-grid {
                display: grid;
                grid-template-columns: 1.2fr 1fr 1.2fr auto;
                gap: 1.5rem;
                align-items: flex-end;
            }

            .input-group {
                display: flex;
                flex-direction: column;
                gap: 0.6rem;
            }

            .input-group label {
                font-size: 0.75rem;
                font-weight: 700;
                color: var(--text-secondary);
                text-transform: uppercase;
                letter-spacing: 0.08em;
                padding-left: 4px;
            }

            input, select {
                width: 100%;
                background: #f8fafc;
                border: 1px solid transparent;
                border-radius: 18px;
                padding: 1.1rem 1.4rem;
                font-family: inherit;
                font-size: 1rem;
                font-weight: 600;
                transition: all 0.2s;
                outline: none;
            }

            input:focus, select:focus {
                background: #fff;
                border-color: var(--futurist-blue);
                box-shadow: 0 0 0 5px var(--accent-glow);
            }

            .btn-futuristic {
                background: #000;
                color: #fff;
                border: none;
                border-radius: 20px;
                padding: 0 2.5rem;
                height: 60px;
                font-size: 1.1rem;
                font-weight: 700;
                cursor: pointer;
                transition: all 0.3s;
                display: flex;
                align-items: center;
                gap: 0.8rem;
            }

            .btn-futuristic:hover {
                transform: scale(1.02);
                background: #1e293b;
                box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
            }

            /* Extra Info Cards */
            .info-card {
                grid-column: span 4;
                padding: 2rem;
                display: flex;
                flex-direction: column;
                justify-content: space-between;
            }

            .info-title { font-size: 1.1rem; font-weight: 700; margin-bottom: 0.5rem; }
            .info-body { font-size: 0.9rem; color: var(--text-secondary); line-height: 1.4; }

            @media (max-width: 640px) {
                .form-grid { grid-template-columns: 1fr; }
                .info-card { grid-column: span 12; }
                h1 { font-size: 2.5rem; }
            }
        </style>
    `,
    content = @`
        <div class="bento-grid">
            <!-- Dashboard Header / Hero -->
            <article class="card hero-card">
                <span class="hero-tag">2026 Predictive Logic Engine</span>
                <h1>Analyze the <span>Money Pit</span></h1>
                <p class="hero-sub">
                    Advanced actuarial processing for vehicle health and equity. Stop the financial bleed with data-driven precision.
                </p>
            </article>

            <!-- The Main Tool -->
            <article class="card form-card" id="form-container">
                <form hx-post="/analyze" hx-target="#form-container" hx-swap="outerHTML" hx-push-url="false" class="form-grid">
                    <div class="input-group">
                        <label>Vehicle Architecture</label>
                        <select name="vehicleType" required>
                            @for(VehicleType type : VehicleType.values())
                                <option value="${type}">${type}</option>
                            @endfor
                        </select>
                    </div>

                    <div class="input-group">
                        <label>Current Usage (Miles)</label>
                        <input type="number" name="mileage" placeholder="120000" required>
                    </div>

                    <div class="input-group">
                        <div style="display: flex; justify-content: space-between; align-items: center;">
                            <label>Requested Service ($)</label>
                            <label style="display: flex; align-items: center; gap: 4px; font-size: 0.7rem; cursor: pointer; text-transform: none; color: var(--text-secondary); font-weight: 600;">
                                <input type="checkbox" id="noQuoteToggle" onchange="toggleQuoteInput(this)" style="width: auto; margin: 0; padding: 0;">
                                No Quote
                            </label>
                        </div>
                        <input type="number" id="repairQuoteInput" name="repairQuoteUsd" placeholder="1500" required>
                        <input type="hidden" id="isQuoteMissing" name="isQuoteMissing" value="false">
                    </div>

                    <button type="submit" class="btn-futuristic">
                        Analyze Now <span>→</span>
                    </button>
                </form>

                <script>
                    function toggleQuoteInput(checkbox) {
                        const input = document.getElementById('repairQuoteInput');
                        const hidden = document.getElementById('isQuoteMissing');
                        if (checkbox.checked) {
                            input.value = '';
                            input.disabled = true;
                            input.required = false;
                            input.placeholder = "Auto-Estimating...";
                            hidden.value = "true";
                        } else {
                            input.disabled = false;
                            input.required = true;
                            input.placeholder = "1500";
                            hidden.value = "false";
                        }
                    }
                </script>
            </article>

            <!-- Bento Secondary Cards -->
            <div class="card info-card">
                <div>
                    <div class="info-title">Global Data Correlated</div>
                    <p class="info-body">Synchronizing with 18,204 actuarial maintenance data points across all segments.</p>
                </div>
            </div>

            <div class="card info-card" style="background: linear-gradient(135deg, #0066ff05, #6366f108);">
                <div>
                    <div class="info-title">Instant Decisioning</div>
                    <p class="info-body">Zero wait time. Actuarial logic computed at the edge for immediate conviction.</p>
                </div>
            </div>

            <div class="card info-card">
                <div>
                    <div class="info-title">Equity Protection</div>
                    <p class="info-body">We prioritize your long-term wealth over emotional vehicle attachment.</p>
                </div>
            </div>
        </div>
    `
)
