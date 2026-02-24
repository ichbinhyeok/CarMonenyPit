@import com.carmoneypit.engine.api.OutputModels.VerdictResult
@import com.carmoneypit.engine.api.OutputModels.VerdictState

@param VerdictResult result
@param String verdictTitle
@param String verdictExplanation
@param String verdictAction
@param String verdictCss
@param String leadLabel
@param String leadDescription
@param String leadUrl
@param boolean isValueEstimated = false

<div id="verdict-card" class="fade-in">

    <!-- Information Transparency Disclaimer -->
    @if(result != null && result.confidence() < 75)
    <div style="background: #fffbeb; border: 1px solid #fde68a; padding: 1rem 1.5rem; border-radius: 12px; margin-bottom: 2rem; display: flex; align-items: flex-start; gap: 1rem;" id="transparency-disclaimer">
        <span style="font-size: 1.25rem;">⚠️</span>
        <div style="font-size: 0.85rem; color: #92400e; line-height: 1.4;">
            <strong>Data Disclosure:</strong> Lower-confidence estimate — real-world costs may vary significantly by trim, condition, and location. This is not financial advice.
        </div>
    </div>
    @else
    <div style="background: #f8fafc; border: 1px solid #e2e8f0; padding: 1rem 1.5rem; border-radius: 12px; margin-bottom: 2rem; display: flex; align-items: flex-start; gap: 1rem;" id="transparency-disclaimer">
        <span style="font-size: 1.25rem;">ℹ️</span>
        <div style="font-size: 0.85rem; color: #475569; line-height: 1.4;">
            <strong>Data Disclosure:</strong> This verdict is generated using statistical models and historical market data. It is intended for informational purposes only and does not constitute mechanical diagnosis, financial advice, or legal appraisal. Actual costs and values vary.
        </div>
    </div>
    @endif

    <style>
        .verdict-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(min(100%, 320px), 1fr));
            gap: 1.5rem;
            margin: 2rem 0;
        }
        @media (max-width: 1024px) {
            .verdict-grid {
                grid-template-columns: 1fr;
            }
        }
        @media (max-width: 768px) {
            .verdict-hero h1 {
                font-size: clamp(2.5rem, 10vw, 3.5rem) !important;
            }
        }
        .delta-container {
            background: #000;
            color: #fff;
            padding: 2.5rem;
            border-radius: 32px;
            margin-bottom: 2rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
            position: relative;
            overflow: hidden;
        }
        .delta-label {
            font-size: 0.8rem;
            font-weight: 800;
            text-transform: uppercase;
            letter-spacing: 0.1em;
            opacity: 0.7;
        }
        .delta-value {
            font-size: 3rem;
            font-weight: 800;
            letter-spacing: -0.04em;
            color: #ef4444;
        }
        .bleed-gauge-container {
            background: #fff5f5;
            padding: 2rem;
            border-radius: 24px;
            border: 1px solid #feb2b2;
            margin-bottom: 1.5rem;
            position: relative;
        }
        .bleed-gauge-bar {
            height: 8px;
            background: #fee2e2;
            border-radius: 100px;
            margin: 1rem 0;
            overflow: hidden;
        }
        .bleed-gauge-fill {
            height: 100%;
            background: linear-gradient(90deg, #ef4444, #b91c1c);
            width: 85%; /* Dynamic later? */
            border-radius: 100px;
        }
        .strategic-insight-row {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 1.5rem;
            margin-bottom: 2rem;
        }
        .insight-card {
            background: #f8fafc;
            padding: 1.5rem;
            border-radius: 24px;
            border: 1px solid #e2e8f0;
        }
        .insight-val {
            font-size: 1.75rem;
            font-weight: 800;
            margin-bottom: 0.25rem;
            display: flex;
            align-items: baseline;
            gap: 0.4rem;
        }
        .insight-label {
            font-size: 0.75rem;
            font-weight: 800;
            color: var(--text-secondary);
            text-transform: uppercase;
            letter-spacing: 0.05em;
        }
        @media (max-width: 900px) {
            .strategic-insight-row {
                grid-template-columns: 1fr;
            }
        }

        /* --- Score Comparison Chart --- */
        .regret-dashboard { 
            grid-column: span 12; 
            display: flex; 
            flex-direction: column; 
            gap: 2rem; 
            padding: 2.5rem;
            background: linear-gradient(145deg, #ffffff, #f8fafc);
            border: 1px solid #e2e8f0;
            border-radius: 32px;
            margin-bottom: 2rem;
        }
        .dashboard-header { text-align: center; margin-bottom: 1rem; }
        .dashboard-header h2 { font-size: 1.5rem; font-weight: 800; letter-spacing: -0.03em; color: #0f172a; margin: 0; }
        .dashboard-header p { color: #64748b; font-size: 0.9rem; margin-top: 0.25rem; }

        .comparison-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 2.5rem;
            position: relative;
        }
        .comparison-grid::after {
            content: 'VS';
            position: absolute;
            left: 50%;
            top: 50%;
            transform: translate(-50%, -50%);
            background: #fff;
            width: 36px;
            height: 36px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 0.7rem;
            font-weight: 900;
            color: #94a3b8;
            border: 1px solid #e2e8f0;
            box-shadow: 0 4px 12px rgba(0,0,0,0.05);
            z-index: 2;
        }

        .regret-bar-container { display: flex; flex-direction: column; gap: 0.75rem; }
        .regret-label { display: flex; justify-content: space-between; font-weight: 800; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 0.05em; }
        .label-stay { color: #10b981; }
        .label-move { color: #6366f1; }
        
        .bar-outer { 
            height: 40px; 
            background: #f1f5f9; 
            border-radius: 10px; 
            overflow: hidden; 
            position: relative;
            border: 1px solid rgba(0,0,0,0.05);
        }
        .bar-inner { 
            height: 100%; 
            transition: width 0.6s cubic-bezier(0.16, 1, 0.3, 1);
            display: flex;
            align-items: center;
            padding-right: 1rem;
            justify-content: flex-end;
            font-weight: 800;
            color: #fff;
            font-size: 1rem;
        }
        .bar-stay { background: linear-gradient(90deg, #10b981, #34d399); }
        .bar-move { background: linear-gradient(90deg, #6366f1, #818cf8); }

        .regret-description { font-size: 0.8rem; color: #64748b; line-height: 1.5; font-weight: 500; margin: 0; }

        @media (max-width: 768px) {
            .comparison-grid { grid-template-columns: 1fr; gap: 1.5rem; }
            .comparison-grid::after { display: none; }
            .regret-dashboard { padding: 1.5rem; }
        }
    </style>

    <!-- Main Verdict Hero -->
    <div class="verdict-hero" style="margin-bottom: 3.5rem;">
        <div style="display: flex; align-items: center; gap: 0.75rem; margin-bottom: 1rem;">
            <div style="width: 14px; height: 14px; border-radius: 50%; background: ${verdictCss.contains("liquidate") ? "#ef4444" : (verdictCss.contains("sustain") ? "#10b981" : "#f59e0b")}; box-shadow: 0 0 15px rgba(0,0,0,0.1);"></div>
            <span style="text-transform: uppercase; font-size: 0.8rem; font-weight: 800; letter-spacing: 0.1em; color: var(--text-secondary);">
                System Diagnostic Verdict
            </span>
        </div>
        <h1 class="main-verdict-title" style="font-weight: 800; letter-spacing: -0.05em; margin-bottom: 1.5rem; line-height: 0.9; color: #000;">
            ${verdictTitle}
        </h1>
        <p class="verdict-intro" style="color: var(--text-main); font-weight: 500; max-width: 750px; line-height: 1.5; margin-bottom: 3rem;">
            "${verdictExplanation}"
        </p>
        <style>
        .main-verdict-title { font-size: clamp(3rem, 12vw, 5rem); }
        .verdict-intro { font-size: 1.25rem; }
        @media (max-width: 768px) {
            .main-verdict-title { font-size: clamp(2.25rem, 10vw, 3rem) !important; margin-bottom: 1rem !important; }
            .verdict-intro { font-size: 1.1rem !important; margin-bottom: 1.5rem !important; padding: 0 0.5rem; }
            .delta-container { padding: 1.25rem !important; flex-direction: column; align-items: flex-start !important; gap: 0.75rem; }
            .delta-value { font-size: 2rem !important; }
        }
        </style>

        @if(result.stayTotal() > result.moveTotal())
            <div class="delta-container">
                <div>
                    <div class="delta-label">Equity Protection Gap</div>
                    <div style="font-size: 1.1rem; font-weight: 700;">Cost of Inaction (1 Yr)</div>
                </div>
                <div class="delta-value">
                    -$${String.format("%,.0f", result.stayTotal() - result.moveTotal())}
                </div>
                <div style="position: absolute; right: -10px; bottom: -10px; font-size: 8rem; font-weight: 900; opacity: 0.05; pointer-events: none;">LOSS</div>
            </div>
        @endif

        <!-- Strategic Insights Row -->
        <div class="strategic-insight-row">
            <!-- 1. Peer Comparison (PROMINENT) -->
            <div class="insight-card" style="background: linear-gradient(135deg, #fef2f2, #fef2f2); border: 2px solid #fecaca;">
                <div class="insight-label" style="color: #dc2626;">What Others Did</div>
                <div class="insight-val" style="color: #dc2626;">
                    ${result.peerData().sellPercentage()}% 
                    <span style="font-size: 0.9rem; font-weight: 700;">SOLD</span>
                </div>
                <p style="font-size: 0.85rem; margin: 0; color: #991b1b; font-weight: 600;">
                    of owners in your situation chose to sell.
                </p>
            </div>

            <!-- 2. Replacement Economics -->
            <div class="insight-card">
                <div class="insight-label">Switching Friction</div>
                <div class="insight-val">
                    $${String.format("%,d", result.economicContext().totalSwitchingFriction())}
                </div>
                <p style="font-size: 0.8rem; margin: 0; color: var(--text-secondary); font-weight: 600;">
                    Estimated fees and taxes to secure a replacement.
                </p>
            </div>

            <!-- 3. Diagnostic Confidence -->
            <div class="insight-card">
                <div class="insight-label">Data Quality</div>
                <div class="insight-val" style="color: ${result.confidence() >= 85 ? "#10b981" : (result.confidence() >= 70 ? "#f59e0b" : "#64748b")};">
                    ${result.confidence() >= 90 ? "STRONG" : (result.confidence() >= 75 ? "GOOD" : "FAIR")}
                </div>
                <p style="font-size: 0.8rem; margin: 0; color: var(--text-secondary); font-weight: 600;">
                    Based on ${result.confidence() >= 85 ? "model-specific market data." : "brand-level historical trends."}
                </p>
            </div>
        </div>

        <!-- Decision Matrix -->
        <div class="regret-dashboard">
            <div class="dashboard-header">
                <h2>Cost Comparison</h2>
                <p>Total projected costs for each path. <strong style="color: #ef4444;">Lower is better.</strong></p>
            </div>

            <div class="comparison-grid">
                <!-- Path: Fix -->
                <div class="regret-bar-container">
                    <div class="regret-label label-stay">
                        <span>🔧 COST TO KEEP</span>
                        <span>$${String.format("%,.0f", result.stayTotal())}</span>
                    </div>
                    <div class="bar-outer">
                        <div class="bar-inner bar-stay" style="width: ${Math.max(15, Math.min(100, (result.stayTotal() / Math.max(result.stayTotal(), result.moveTotal())) * 100))}%"></div>
                    </div>
                    <p class="regret-description">
                        Repair + future breakdown risk + depreciation while you own it.
                    </p>
                </div>

                <!-- Path: Move -->
                <div class="regret-bar-container">
                    <div class="regret-label label-move">
                        <span>💰 COST TO SELL</span>
                        <span>$${String.format("%,.0f", result.moveTotal())}</span>
                    </div>
                    <div class="bar-outer">
                        <div class="bar-inner bar-move" style="width: ${Math.max(15, Math.min(100, (result.moveTotal() / Math.max(result.stayTotal(), result.moveTotal())) * 100))}%"></div>
                    </div>
                    <p class="regret-description">
                        Selling fees + depreciation loss + replacement costs.
                    </p>
                </div>
            </div>
            
            <!-- Winner Indicator -->
            <div style="text-align: center; margin-top: 1.5rem; padding: 1rem; background: ${result.stayTotal() > result.moveTotal() ? "#fef2f2" : "#f0fdf4"}; border-radius: 12px;">
                <span style="font-size: 0.8rem; font-weight: 700; color: ${result.stayTotal() > result.moveTotal() ? "#dc2626" : "#16a34a"};">
                    ${result.stayTotal() > result.moveTotal() ? "💡 Selling costs less — consider letting go" : "💡 Fixing costs less — worth keeping"}
                </span>
            </div>
        </div>
    </div>

    <!-- Data Breakdown -->
    <div style="margin-top: 3rem; display: flex; flex-direction: column; gap: 2rem;">
        
        <!-- Asset Bleed Priority -->
        <div class="bleed-gauge-container" style="max-width: 900px; margin: 0 auto; width: 100%;">
            <div style="display: flex; justify-content: space-between; align-items: center;">
                <div class="delta-label" style="color: #c53030; opacity: 1;">Projected Asset Bleed</div>
                <div style="font-size: 1.25rem; font-weight: 800; color: #ef4444; display: flex; flex-direction: column; align-items: flex-end;">
                    <span>-$${String.format("%,d", result.assetBleedAmount())}</span>
                    <span style="font-size: 0.6rem; color: #b91c1c; opacity: 0.8; font-weight: 600;">(Est. 180-Day Melt)</span>
                </div>
            </div>
            <div class="bleed-gauge-bar">
                <div class="bleed-gauge-fill" style="width: ${Math.min(100, (result.assetBleedAmount() / 5000.0) * 100)}%;"></div>
            </div>
            <p style="font-size: 0.85rem; color: #742a2a; font-weight: 600; line-height: 1.4; margin: 0; opacity: 0.8;">
                Estimated capital loss over the next 6 months based on historical depreciation curves.
            </p>
        </div>

        <div class="verdict-grid" style="align-items: start;">
            <!-- Left Column: The Audit Receipt -->
            <div style="background: transparent; padding: 0;">
                <h3 style="font-size: 0.8rem; font-weight: 800; text-transform: uppercase; letter-spacing: 0.1em; margin-bottom: 1.5rem; color: var(--text-secondary); text-align: center;">
                    Financial Decision Audit
                </h3>
                @template.fragments.smart_receipt(items = result.costBreakdown())
            </div>

            <!-- Right Column: Strategic Directive & Actions -->
            <div style="display: flex; flex-direction: column; gap: 1.5rem;">
                <!-- Directive -->
                <div style="background: #fff; padding: 2.5rem; border-radius: 32px; border-left: 8px solid #000; box-shadow: 0 4px 30px rgba(0,0,0,0.03);">
                    <h4 style="font-size: 0.75rem; font-weight: 800; margin-bottom: 1rem; text-transform: uppercase; color: var(--text-secondary); letter-spacing: 0.1em;">Strategic Directive</h4>
                    <p style="font-size: 1.25rem; margin: 0; color: var(--text-main); font-weight: 800; line-height: 1.4; letter-spacing: -0.02em;">${verdictAction}</p>
                </div>

            <!-- Exit Ramp / Protection Plan -->
            @if(verdictCss.contains("liquidate"))
                <div style="border-top: 2px dashed #cbd5e0; padding-top: 2rem; margin-top: 2rem;">
                    <div style="font-size: 0.8rem; color: #718096; margin-bottom: 1rem;">
                        ✓ Analysis Complete → Your Action Plan:
                    </div>
                    
                    <!-- Step-by-step Action Guide -->
                    <div style="background: #f8fafc; border-radius: 24px; padding: 2rem; margin-bottom: 1.5rem;">
                        <h4 style="font-size: 1rem; font-weight: 800; margin-bottom: 1.5rem;">What To Do Now</h4>
                        
                        <div style="display: flex; flex-direction: column; gap: 1rem;">
                            <div style="display: flex; gap: 1rem; align-items: flex-start;">
                                <div style="width: 28px; height: 28px; background: #3b82f6; color: #fff; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: 800; font-size: 0.8rem; flex-shrink: 0;">1</div>
                                <div>
                                    <div style="font-weight: 700;">Get 2-3 offers today</div>
                                    <div style="font-size: 0.85rem; color: #64748b;">Peddle, CarMax, and local dealers</div>
                                </div>
                            </div>
                            <div style="display: flex; gap: 1rem; align-items: flex-start;">
                                <div style="width: 28px; height: 28px; background: #3b82f6; color: #fff; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: 800; font-size: 0.8rem; flex-shrink: 0;">2</div>
                                <div>
                                    <div style="font-weight: 700;">Compare offers vs repair cost</div>
                                    <div style="font-size: 0.85rem; color: #64748b;">Best offer - repair cost = your decision</div>
                                </div>
                            </div>
                            <div style="display: flex; gap: 1rem; align-items: flex-start;">
                                <div style="width: 28px; height: 28px; background: #3b82f6; color: #fff; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: 800; font-size: 0.8rem; flex-shrink: 0;">3</div>
                                <div>
                                    <div style="font-weight: 700;">Take the best offer this week</div>
                                    <div style="font-size: 0.85rem; color: #64748b;">Your car loses ~$${Math.round(result.assetBleedAmount() / 180)}/day in value</div>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <div class="cta-card" style="background: linear-gradient(135deg, #1a1a1a 0%, #DC2626 100%); 
                                 padding: 2rem; border-radius: 24px; color: #fff; 
                                 box-shadow: 0 20px 60px rgba(220, 38, 38, 0.4);
                                 border: 2px solid rgba(239, 68, 68, 0.3);">
                        
                        <div style="text-align: center; margin-bottom: 1rem;">
                            <span style="font-size: 0.8rem; opacity: 0.8;">START HERE →</span>
                        </div>
                        
                        <a href="${leadUrl}" 
                           style="display: block; background: #fff; color: #DC2626; 
                                  padding: 1.25rem; border-radius: 16px; text-decoration: none; 
                                  font-weight: 900; font-size: 1.1rem; text-align: center;
                                  box-shadow: 0 0 30px rgba(255,255,255,0.3);">
                            ${leadLabel}
                        </a>
                        
                        <p style="font-size: 0.8rem; text-align: center; opacity: 0.7; margin: 1rem 0 0;">
                            Free instant offer • No obligation
                        </p>
                    </div>
                </div>
            @else
                <div style="border-top: 2px dashed #cbd5e0; padding-top: 2rem; margin-top: 2rem;">
                    <div style="font-size: 0.8rem; color: #718096; margin-bottom: 1rem;">
                        ✓ Analysis Complete → Recommended Action:
                    </div>
                    
                    <div style="background: linear-gradient(135deg, #0EA5E9 0%, #3B82F6 100%); 
                                 padding: 2.5rem; border-radius: 24px; color: #fff; 
                                 box-shadow: 0 20px 60px rgba(14, 165, 233, 0.3);">
                        
                        <div style="display: flex; align-items: center; gap: 0.5rem; margin-bottom: 1rem;">
                            <span style="font-size: 1.5rem;">✓</span>
                            <span style="font-size: 0.9rem; font-weight: 700; opacity: 0.9;">
                                ASSET SUSTAINABILITY VERIFIED
                            </span>
                        </div>
                        
                        <h4 style="font-size: 1.8rem; font-weight: 800; margin-bottom: 0.75rem; line-height: 1.2;">
                            Audit Quote with a<br>Service Expert
                        </h4>
                        
                        <p style="font-size: 1rem; opacity: 0.9; margin-bottom: 1.5rem;">
                            The data supports keeping the vehicle. Connect with a specialist to verify the fair market cost of your repair.
                        </p>
                        
                        <a href="${leadUrl}" 
                           style="display: block; background: #fff; color: #0EA5E9; 
                                  padding: 1.25rem; border-radius: 16px; text-decoration: none; 
                                  font-weight: 900; font-size: 1.1rem; text-align: center;">
                            ${leadLabel}
                        </a>
                </div>
            </div>
        @endif

                <!-- Methodology Section -->
                <div style="margin-top: 1.5rem; padding: 1.5rem; background: #f8fafc; border-radius: 24px; border: 1px solid #e2e8f0; overflow: hidden;" id="methodology-section">
                    <div style="display: flex; align-items: center; gap: 0.75rem; margin-bottom: 1rem;">
                        <span style="font-size: 1.2rem;">📊</span>
                        <h4 style="font-size: 0.9rem; font-weight: 800; color: #0f172a; margin: 0;">How We Calculate This</h4>
                    </div>
                    
                    <p style="font-size: 0.8rem; color: #475569; line-height: 1.5; margin-bottom: 1rem;">
                        Our estimates are based on publicly available data from trusted automotive sources:
                    </p>
                    
                    <!-- Data Source Badges (E-E-A-T) -->
                    <div style="display: flex; flex-wrap: wrap; gap: 0.5rem; margin-bottom: 1.5rem;">
                        <span style="background: #dbeafe; color: #1e40af; padding: 0.25rem 0.75rem; border-radius: 99px; font-size: 0.7rem; font-weight: 700;">📘 NADA Guides</span>
                        <span style="background: #dcfce7; color: #166534; padding: 0.25rem 0.75rem; border-radius: 99px; font-size: 0.7rem; font-weight: 700;">📗 Kelley Blue Book</span>
                        <span style="background: #fef3c7; color: #92400e; padding: 0.25rem 0.75rem; border-radius: 99px; font-size: 0.7rem; font-weight: 700;">🛡️ NHTSA Data</span>
                    </div>

                    <div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 1rem; margin-bottom: 1.5rem;">
                        <div>
                            <span style="font-size: 0.6rem; font-weight: 800; color: #94a3b8; text-transform: uppercase;">Value Source</span>
                            <div style="font-size: 0.7rem; font-weight: 700; color: #1e293b;">Market Avg.</div>
                        </div>
                        <div>
                            <span style="font-size: 0.6rem; font-weight: 800; color: #94a3b8; text-transform: uppercase;">Reliability</span>
                            <div style="font-size: 0.7rem; font-weight: 700; color: #1e293b;">Historical</div>
                        </div>
                        <div>
                            <span style="font-size: 0.6rem; font-weight: 800; color: #94a3b8; text-transform: uppercase;">Confidence</span>
                            <div style="font-size: 0.7rem; font-weight: 700; color: ${result.confidence() >= 85 ? "#10b981" : (result.confidence() >= 70 ? "#f59e0b" : "#64748b")};">${result.confidence() >= 90 ? "Very High" : (result.confidence() >= 75 ? "High" : "Moderate")}</div>
                        </div>
                    </div>

                    <div style="padding-top: 0.75rem; border-top: 1px solid #e2e8f0; display: flex; align-items: center; justify-content: space-between;">
                        <span style="color: #64748b; font-size: 0.65rem;">Updated Jan 2026 • For informational purposes only</span>
                        <a href="/methodology" style="font-size: 0.7rem; font-weight: 700; color: #6366f1; text-decoration: none;">Full Methodology →</a>
                    </div>
                </div>
            </div>
    </div>
</div>

    <footer style="margin-top: 4rem; padding-top: 2rem; border-top: 1px solid #e2e8f0; display: flex; flex-wrap: wrap; justify-content: space-between; align-items: center; gap: 1.5rem;">
        <div style="display: flex; gap: 3rem;">
            <div>
                <div style="font-size: 0.75rem; font-weight: 800; color: var(--text-secondary); text-transform: uppercase; margin-bottom: 0.4rem;">Model Integrity</div>
                <div style="font-size: 1rem; font-weight: 700; color: #10b981;">VERIFIED_AUDIT</div>
            </div>
            <div>
                <div style="font-size: 0.75rem; font-weight: 800; color: var(--text-secondary); text-transform: uppercase; margin-bottom: 0.4rem;">Asset Liquidity</div>
                <div style="font-size: 1rem; font-weight: 700;">${result.visualizationHint().moneyPitState().replace("_", " ")}</div>
            </div>
        </div>
        <div style="font-size: 0.8rem; font-weight: 700; color: var(--text-secondary); opacity: 0.6;">
            [ 2026_AUTO_DECISION_ENGINE ]
        </div>
    </footer>
</div>

