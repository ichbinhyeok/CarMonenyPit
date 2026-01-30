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
                font-size: clamp(1rem, 4vw, 1.25rem);
                color: var(--text-secondary);
                font-weight: 500;
                max-width: 600px;
                margin: 0 auto 3rem;
                line-height: 1.5;
                padding: 0 1rem;
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
                .modern-form-container { padding: 1.5rem; border-radius: 24px; }
                .form-grid-modern { grid-template-columns: 1fr; gap: 1.5rem; }
                .analyze-footer { grid-column: span 1; }
                .hero-title { font-size: clamp(2.5rem, 12vw, 4rem); }
                .hero-section { padding: 2rem 1rem 4rem; }
            }
        </style>
    `,
    content = @`
        <section class="hero-section">
            <div class="hero-badge">
                <span style="font-size: 1.1rem;">🔧</span> 30-Second Decision Tool
            </div>
            <h1 class="hero-title">Fix It or<br>Sell It?</h1>
            <p class="hero-subtitle">
                Got a repair quote that made you gasp? Let's find out if it's worth it — or if you should cut your losses.
            </p>

            <!-- Situation Selection Cards -->
            <div class="situation-cards" style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 1rem; margin-bottom: 2.5rem; max-width: 700px; margin-left: auto; margin-right: auto;">
                <button type="button" class="situation-card active" data-situation="quote" onclick="selectSituation(this, 'quote')">
                    <span style="font-size: 2rem; margin-bottom: 0.5rem;">💰</span>
                    <span style="font-weight: 700; font-size: 0.85rem;">I got a repair quote</span>
                </button>
                <button type="button" class="situation-card" data-situation="breakdown" onclick="selectSituation(this, 'breakdown')">
                    <span style="font-size: 2rem; margin-bottom: 0.5rem;">🔧</span>
                    <span style="font-weight: 700; font-size: 0.85rem;">My car broke down</span>
                </button>
                <button type="button" class="situation-card" data-situation="old" onclick="selectSituation(this, 'old')">
                    <span style="font-size: 2rem; margin-bottom: 0.5rem;">⏰</span>
                    <span style="font-weight: 700; font-size: 0.85rem;">My car is getting old</span>
                </button>
            </div>

            <style>
                .situation-card {
                    display: flex;
                    flex-direction: column;
                    align-items: center;
                    justify-content: center;
                    padding: 1.25rem 1rem;
                    background: #f8fafc;
                    border: 2px solid #e2e8f0;
                    border-radius: 20px;
                    cursor: pointer;
                    transition: all 0.3s ease;
                    text-align: center;
                }
                .situation-card:hover {
                    border-color: #94a3b8;
                    transform: translateY(-2px);
                }
                .situation-card.active {
                    background: #eff6ff;
                    border-color: #3b82f6;
                    box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.1);
                }
                @media (max-width: 600px) {
                    .situation-cards { grid-template-columns: 1fr !important; }
                }
            </style>

            <article class="modern-form-container" id="form-container">
                <!-- Progress Indicator -->
                <div style="margin-bottom: 2rem;">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.5rem;">
                        <span style="font-size: 0.75rem; font-weight: 700; color: var(--text-secondary);">YOUR VEHICLE</span>
                        <span style="font-size: 0.75rem; font-weight: 600; color: #3b82f6;">Step 1 of 2</span>
                    </div>
                    <div style="height: 4px; background: #e2e8f0; border-radius: 100px; overflow: hidden;">
                        <div style="width: 50%; height: 100%; background: linear-gradient(90deg, #3b82f6, #60a5fa); border-radius: 100px;"></div>
                    </div>
                </div>

                <form hx-post="/analyze" hx-target="#form-container" hx-swap="outerHTML show:window:top" hx-push-url="false" class="form-grid-modern">
                    <input type="hidden" name="situation" id="situationInput" value="quote">
                    
                    <!-- Row 1: Year (First!) -->
                    <div class="input-group">
                        <label>Vehicle Year</label>
                        <select name="year" id="year-select" required>
                            <option value="">Select year...</option>
                            @for(int y = 2026; y >= 1995; y--)
                                <option value="${y}">${y}</option>
                            @endfor
                            <option value="1994">1994 or older</option>
                        </select>
                    </div>

                    <!-- Row 2: Brand -->
                    <div class="input-group">
                        <label>Brand</label>
                        <select name="brand" id="brand-select"
                                hx-get="/api/models" 
                                hx-target="#model-select" 
                                hx-swap="innerHTML"
                                hx-trigger="change"
                                hx-include="#year-select"
                                hx-indicator=".integrity-dot"
                                required>
                            <option value="">Select brand...</option>
                            @for(CarBrand brand : CarBrand.values())
                                @if(prefillBrand != null && brand.name().equals(prefillBrand))
                                    <option value="${brand}" selected>${brand}</option>
                                @else
                                    <option value="${brand}">${brand}</option>
                                @endif
                            @endfor
                        </select>
                    </div>

                    <!-- Row 3: Model -->
                    <div class="input-group">
                        <label>Model</label>
                        <select name="model" id="model-select" required>
                            @if(prefillModel != null)
                                <option value="${prefillModel}" selected>${prefillModel}</option>
                            @else
                                <option value="">Select brand first...</option>
                            @endif
                        </select>
                    </div>

                    <!-- Row 4: Mileage (Range Dropdown) -->
                    <div class="input-group">
                        <label>Mileage</label>
                        <select name="mileage" id="mileage-select" required>
                            <option value="">Select range...</option>
                            <option value="25000">Under 30,000 miles</option>
                            <option value="45000">30,000 – 60,000 miles</option>
                            <option value="75000">60,000 – 90,000 miles</option>
                            <option value="105000">90,000 – 120,000 miles</option>
                            <option value="135000">120,000 – 150,000 miles</option>
                            <option value="175000">150,000 – 200,000 miles</option>
                            <option value="225000">200,000+ miles</option>
                        </select>
                    </div>

                    <!-- Row 5: Repair Quote (Conditional based on situation) -->
                    <div class="input-group" id="quote-section">
                        <div style="display: flex; justify-content: space-between; align-items: center;">
                            <label>Repair Quote ($)</label>
                            <label style="display: flex; align-items: center; gap: 4px; font-size: 0.7rem; cursor: pointer; text-transform: none; color: var(--text-secondary); font-weight: 600;">
                                <input type="checkbox" id="noQuoteToggle" onchange="toggleQuoteInput(this)" style="width: auto; margin: 0; padding: 0;">
                                I Don't Have a Quote
                            </label>
                        </div>
                        @if(prefillQuote != null)
                            <input type="number" id="repairQuoteInput" name="repairQuoteUsd" placeholder="e.g. 2500" value="${prefillQuote}" required>
                        @else
                            <input type="number" id="repairQuoteInput" name="repairQuoteUsd" placeholder="e.g. 2500" required>
                        @endif
                        <input type="hidden" id="isQuoteMissing" name="isQuoteMissing" value="false">
                    </div>

                    <div class="analyze-footer">
                        <button type="submit" class="btn-cinematic">
                            Get My Verdict <span>→</span>
                        </button>
                        
                        <div class="market-integrity">
                            <span class="integrity-dot"></span>
                            Powered by market data & historical patterns
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
                            input.placeholder = "We'll estimate for you...";
                            hidden.value = "true";
                        } else {
                            input.disabled = false;
                            input.required = true;
                            input.placeholder = "e.g. 2500";
                            hidden.value = "false";
                        }
                    }

                    function selectSituation(btn, situation) {
                        // Update active state
                        document.querySelectorAll('.situation-card').forEach(c => c.classList.remove('active'));
                        btn.classList.add('active');
                        document.getElementById('situationInput').value = situation;

                        // Adjust form based on situation
                        const quoteSection = document.getElementById('quote-section');
                        const noQuoteToggle = document.getElementById('noQuoteToggle');
                        
                        if (situation === 'quote') {
                            quoteSection.style.display = 'flex';
                            noQuoteToggle.checked = false;
                            toggleQuoteInput(noQuoteToggle);
                        } else if (situation === 'breakdown' || situation === 'old') {
                            // Auto-check "no quote" for these situations
                            noQuoteToggle.checked = true;
                            toggleQuoteInput(noQuoteToggle);
                        }
                    }
                </script>
            </article>
        </section>

        <!-- Bento Secondary Cards -->
        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(min(100%, 280px), 1fr)); gap: 1.5rem; max-width: 1000px; margin: 0 auto 10rem; padding: 0 1rem;">
            <div class="card info-card">
                <div>
                    <div class="info-title">Market Data Correlated</div>
                    <div class="info-desc">Cross-referenced against standardized actuarial grids and NADA/KBB market frameworks.</div>
                </div>
            </div>

            <div class="card info-card" style="background: linear-gradient(135deg, #0066ff05, #6366f108);">
                <div>
                    <div class="info-title">Actuarial Precision</div>
                    <div class="info-desc">Not a guess. A specialized decision framework that balances repair cost vs. equity bleed.</div>
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
