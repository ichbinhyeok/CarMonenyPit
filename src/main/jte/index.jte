@import com.carmoneypit.engine.api.InputModels.VehicleType
@import com.carmoneypit.engine.api.InputModels.CarBrand
@param String prefillBrand = null
@param String prefillModel = null
@param Long prefillQuote = null

@template.layout(
    title = "Auto Money Pit | Market Liquidation Value Analysis",
    extraStyles = @`
        <style>
            .hero-section {
                text-align: center;
                padding: 4rem 1rem 6rem;
                max-width: 1000px;
                margin: 0 auto;
            }

            .hero-badge {
                display: inline-flex;
                align-items: center;
                gap: 0.5rem;
                background: var(--accent-glow);
                padding: 0.5rem 1rem;
                border-radius: 100px;
                font-size: 0.75rem;
                font-weight: 800;
                color: var(--futurist-blue);
                margin-bottom: 2rem;
                border: 1px solid rgba(0, 102, 255, 0.1);
            }

            .hero-title {
                font-size: clamp(3rem, 8vw, 5.5rem);
                font-weight: 800;
                line-height: 0.95;
                letter-spacing: -0.04em;
                margin-bottom: 1.5rem;
                color: #000;
            }

            .hero-subtitle {
                font-size: 1.25rem;
                color: var(--text-secondary);
                font-weight: 500;
                max-width: 600px;
                margin: 0 auto 4rem;
                line-height: 1.5;
            }

            /* Modern Grid Layout */
            .modern-form-container {
                max-width: 1000px;
                margin: 0 auto;
                background: var(--futurist-card);
                backdrop-filter: blur(40px);
                -webkit-backdrop-filter: blur(40px);
                border: 1px solid rgba(255, 255, 255, 0.7);
                border-radius: 40px;
                padding: 3rem;
                box-shadow: 0 40px 100px rgba(0, 0, 0, 0.03);
            }

            .form-grid-modern {
                display: grid;
                grid-template-columns: repeat(2, 1fr);
                gap: 2.5rem;
            }

            .input-group {
                display: flex;
                flex-direction: column;
                gap: 0.75rem;
            }

            .input-group label {
                font-size: 0.75rem;
                font-weight: 800;
                text-transform: uppercase;
                letter-spacing: 0.1em;
                color: var(--text-secondary);
            }

            .input-group select, .input-group input {
                width: 100%;
                background: #fff;
                border: 1px solid rgba(0,0,0,0.08);
                border-radius: 16px;
                padding: 1.25rem 1.5rem;
                font-size: 1.1rem;
                font-weight: 700;
                font-family: inherit;
                transition: all 0.3s;
                color: #000;
            }

            .input-group select:focus, .input-group input:focus {
                outline: none;
                border-color: var(--futurist-blue);
                box-shadow: 0 0 0 4px var(--accent-glow);
                background: #fff;
            }

            .analyze-footer {
                grid-column: span 2;
                margin-top: 1rem;
                display: flex;
                flex-direction: column;
                align-items: center;
                gap: 2rem;
            }

            .btn-cinematic {
                background: #000;
                color: #fff;
                width: 100%;
                max-width: 400px;
                padding: 1.5rem;
                border: none;
                border-radius: 20px;
                font-size: 1.25rem;
                font-weight: 800;
                cursor: pointer;
                transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
                display: flex;
                justify-content: center;
                align-items: center;
                gap: 1rem;
                box-shadow: 0 20px 40px rgba(0,0,0,0.1);
            }

            .btn-cinematic:hover {
                transform: translateY(-5px);
                box-shadow: 0 30px 60px rgba(0,0,0,0.2);
                background: #111;
            }

            .btn-cinematic span {
                font-size: 1.5rem;
                transition: transform 0.3s;
            }

            .btn-cinematic:hover span {
                transform: translateX(5px);
            }

            .market-integrity {
                display: flex;
                align-items: center;
                gap: 0.6rem;
                font-size: 0.75rem;
                font-weight: 700;
                color: var(--text-secondary);
                opacity: 0.6;
            }

            .integrity-dot {
                width: 8px;
                height: 8px;
                background: #10b981;
                border-radius: 50%;
            }

            @media (max-width: 768px) {
                .form-grid-modern { grid-template-columns: 1fr; }
                .analyze-footer { grid-column: span 1; }
                .hero-title { font-size: 3.5rem; }
            }
        </style>
    `,
    content = @`
        <section class="hero-section">
            <div class="hero-badge">
                <span style="font-size: 1.1rem;">🛡️</span> Data-Driven Financial Logic
            </div>
            <h1 class="hero-title">Your Asset Is<br>Hemorrhaging.</h1>
            <p class="hero-subtitle">
                Stop guessing. Connect your vehicle data to our global actuarial grid for a definitive financial verdict in 30 seconds.
            </p>

            <article class="modern-form-container" id="form-container">
                <form hx-post="/analyze" hx-target="#form-container" hx-swap="outerHTML show:window:top" hx-push-url="false" class="form-grid-modern">
                    <div class="input-group">
                        <label>Manufacturer</label>
                        <select name="brand" 
                                hx-get="/api/models" 
                                hx-target="#model-select" 
                                hx-swap="innerHTML"
                                hx-trigger="change, load"
                                hx-indicator=".integrity-dot"
                                required>
                            @for(CarBrand brand : CarBrand.values())
                                @if(prefillBrand != null && brand.name().equals(prefillBrand))
                                    <option value="${brand}" selected>${brand}</option>
                                @else
                                    <option value="${brand}">${brand}</option>
                                @endif
                            @endfor
                        </select>
                    </div>

                    <div class="input-group">
                        <label>Vehicle Model</label>
                        <select name="model" id="model-select" required>
                            @if(prefillModel != null)
                                <option value="${prefillModel}" selected>${prefillModel}</option>
                            @else
                                <option value="">Select a brand first</option>
                            @endif
                        </select>
                    </div>

                    <div class="input-group">
                        <label>Mileage</label>
                        <input type="number" name="mileage" placeholder="120,000" required>
                    </div>

                    <div class="input-group">
                        <div style="display: flex; justify-content: space-between; align-items: center;">
                            <label>Repair Quote ($)</label>
                            <label style="display: flex; align-items: center; gap: 4px; font-size: 0.7rem; cursor: pointer; text-transform: none; color: var(--text-secondary); font-weight: 600;">
                                <input type="checkbox" id="noQuoteToggle" onchange="toggleQuoteInput(this)" style="width: auto; margin: 0; padding: 0;">
                                I Don't Have a Quote
                            </label>
                        </div>
                        @if(prefillQuote != null)
                            <input type="number" id="repairQuoteInput" name="repairQuoteUsd" placeholder="1,500" value="${prefillQuote}" required>
                        @else
                            <input type="number" id="repairQuoteInput" name="repairQuoteUsd" placeholder="1,500" required>
                        @endif
                        <input type="hidden" id="isQuoteMissing" name="isQuoteMissing" value="false">
                    </div>

                    <div class="analyze-footer">
                        <button type="submit" class="btn-cinematic">
                            Run Market Analysis <span>→</span>
                        </button>
                        
                        <div class="market-integrity">
                            <span class="integrity-dot"></span>
                            MARKET: GLOBAL REAL-TIME SYNC // [0.08ms Latency]
                        </div>
                    </div>
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
        </section>

        <!-- Bento Secondary Cards -->
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 1.5rem; max-width: 1000px; margin: 0 auto 10rem;">
            <div class="card info-card">
                <div>
                    <div class="info-title">Global Data Correlated</div>
                    <p class="info-body">Synchronizing with 18,204 actuarial maintenance data points across all segments.</p>
                </div>
            </div>

            <div class="card info-card" style="background: linear-gradient(135deg, #0066ff05, #6366f108);">
                <div>
                    <div class="info-title">Instant Decisioning</div>
                    <p class="info-body">Zero wait time. Valuation logic computed at the edge for immediate conviction.</p>
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
