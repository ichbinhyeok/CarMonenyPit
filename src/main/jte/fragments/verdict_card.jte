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

    <style>
        .verdict-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
            gap: 2rem;
            margin: 3rem 0;
        }
        @media (max-width: 768px) {
            .verdict-grid {
                grid-template-columns: 1fr;
            }
            .verdict-hero h1 {
                font-size: 3.5rem !important;
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
    </style>

    <!-- Main Verdict Hero -->
    <div class="verdict-hero" style="margin-bottom: 3.5rem;">
        <div style="display: flex; align-items: center; gap: 0.75rem; margin-bottom: 1rem;">
            <div style="width: 14px; height: 14px; border-radius: 50%; background: ${verdictCss.contains("liquidate") ? "#ef4444" : (verdictCss.contains("sustain") ? "#10b981" : "#f59e0b")}; box-shadow: 0 0 15px rgba(0,0,0,0.1);"></div>
            <span style="text-transform: uppercase; font-size: 0.8rem; font-weight: 800; letter-spacing: 0.1em; color: var(--text-secondary);">
                System Diagnostic Verdict
            </span>
        </div>
        <h1 style="font-size: 5rem; font-weight: 800; letter-spacing: -0.05em; margin-bottom: 1.5rem; line-height: 0.9; color: #000;">
            ${verdictTitle}
        </h1>
        <p style="font-size: 1.25rem; color: var(--text-main); font-weight: 500; max-width: 750px; line-height: 1.5; margin-bottom: 3rem;">
            "${verdictExplanation}"
        </p>

        @if(result.stayTotal() > result.moveTotal())
            <div class="delta-container">
                <div>
                    <div class="delta-label">Financial Liability Gap</div>
                    <div style="font-size: 1.1rem; font-weight: 700;">Cost of Inaction (1 Yr)</div>
                </div>
                <div class="delta-value">
                    +$${String.format("%,.0f", result.stayTotal() - result.moveTotal())}
                </div>
                <div style="position: absolute; right: -10px; bottom: -10px; font-size: 8rem; font-weight: 900; opacity: 0.05; pointer-events: none;">LOSS</div>
            </div>
        @endif

        <!-- Strategic Insights Row -->
        <div class="strategic-insight-row">
            <!-- 1. Peer Comparison -->
            <div class="insight-card">
                <div class="insight-label">Community Benchmark</div>
                <div class="insight-val">
                    ${result.peerData().sellPercentage()}% 
                    <span style="font-size: 0.9rem; font-weight: 700; color: #ef4444;">SOLD</span>
                </div>
                <p style="font-size: 0.8rem; margin: 0; color: var(--text-secondary); font-weight: 600;">
                    of owners facing this diagnostic exit the asset.
                </p>
            </div>

            <!-- 2. Replacement Economics -->
            <div class="insight-card">
                <div class="insight-label">Switching Friction</div>
                <div class="insight-val">
                    $${String.format("%,d", result.economicContext().totalSwitchingFriction())}
                </div>
                <p style="font-size: 0.8rem; margin: 0; color: var(--text-secondary); font-weight: 600;">
                    Average tax/fee penalty to replace this asset.
                </p>
            </div>

            <!-- 3. Diagnostic Confidence -->
            <div class="insight-card">
                <div class="insight-label">System Confidence</div>
                <div class="insight-val" style="color: ${result.confidenceScore() > 85 ? "#10b981" : "#f59e0b"};">
                    ${result.confidenceScore()}%
                </div>
                <p style="font-size: 0.8rem; margin: 0; color: var(--text-secondary); font-weight: 600;">
                    Based on actuarial sample size and brand history.
                </p>
            </div>
        </div>
    </div>

    <!-- Data Breakdown Grid -->
    <div class="verdict-grid">
        <!-- Left Column: Receipt -->
        <div style="background: #fff; padding: 2.5rem; border-radius: 32px; border: 1px solid rgba(0,0,0,0.05); box-shadow: 0 4px 20px rgba(0,0,0,0.02);">
            <h3 style="font-size: 0.9rem; font-weight: 800; text-transform: uppercase; letter-spacing: 0.05em; margin-bottom: 2rem; color: var(--text-secondary);">
                Actuarial Risk Mapping
            </h3>
            @template.fragments.smart_receipt(items = result.costBreakdown())
        </div>

        <!-- Right Column: Critical Actions & Bleed -->
        <div style="display: flex; flex-direction: column;">
            
            <!-- Visceral Asset Bleed Gauge -->
            <div class="bleed-gauge-container">
                <div style="display: flex; justify-content: space-between; align-items: center;">
                    <div class="delta-label" style="color: #c53030; opacity: 1;">Asset Bleed Priority</div>
                    <div style="font-size: 1.25rem; font-weight: 800; color: #ef4444;">
                        -$${String.format("%,d", result.assetBleedAmount())}
                    </div>
                </div>
                <div class="bleed-gauge-bar">
                    <div class="bleed-gauge-fill" style="width: ${Math.min(100, (result.assetBleedAmount() / 5000.0) * 100)}%;"></div>
                </div>
                <p style="font-size: 0.8rem; color: #742a2a; font-weight: 600; line-height: 1.4; margin: 0; opacity: 0.8;">
                    Estimated capital melt over the next 180 days based on actuarial depreciation curves.
                </p>
            </div>

            <!-- Recommended Action -->
            <div style="background: #fff; padding: 2rem; border-radius: 24px; border-left: 6px solid #000; box-shadow: 0 4px 15px rgba(0,0,0,0.02); margin-bottom: 1.5rem;">
                <h4 style="font-size: 0.8rem; font-weight: 800; margin-bottom: 0.75rem; text-transform: uppercase; color: var(--text-secondary);">Strategic Directive</h4>
                <p style="font-size: 1.1rem; margin: 0; color: var(--text-main); font-weight: 700; line-height: 1.4;">${verdictAction}</p>
            </div>

            <!-- Exit Ramp / Protection Plan -->
            <div style="background: ${verdictCss.contains("terminate") ? "#000" : "var(--futurist-blue)"}; padding: 2.5rem; border-radius: 32px; color: #fff; box-shadow: 0 15px 40px ${verdictCss.contains("terminate") ? "rgba(0,0,0,0.3)" : "rgba(0, 102, 255, 0.25)"}; position: relative; overflow: hidden;">
                <div style="position: absolute; top: -20px; right: -20px; width: 120px; height: 120px; background: rgba(255,255,255,0.05); border-radius: 50%;"></div>
                <div style="font-size: 0.8rem; font-weight: 800; text-transform: uppercase; margin-bottom: 0.75rem; opacity: 0.8; letter-spacing: 0.05em;">
                    ${verdictCss.contains("terminate") ? "Liquidate Asset" : "Preserve Asset"}
                </div>
                <h4 style="font-size: 1.5rem; font-weight: 800; margin-bottom: 1.25rem; line-height: 1.1;">${leadLabel}</h4>
                <p style="font-size: 1rem; margin-bottom: 2rem; opacity: 0.9; font-weight: 500; line-height: 1.4;">${leadDescription}</p>
                <a href="${leadUrl}" 
                   style="display: inline-flex; align-items: center; justify-content: center; background: #fff; color: ${verdictCss.contains("terminate") ? "#000" : "var(--futurist-blue)"}; width: 100%; padding: 1.25rem; border: none; border-radius: 20px; text-decoration: none; font-weight: 800; font-size: 1.1rem; cursor: pointer; transition: transform 0.2s, box-shadow 0.2s; box-shadow: 0 4px 12px rgba(0,0,0,0.1);">
                    ${verdictCss.contains("terminate") ? "EXECUTE EXIT STRATEGY" : "ACTIVATE PROTECTION"} <span>→</span>
                </a>
            </div>
        </div>
    </div>

    <footer style="margin-top: 4rem; padding-top: 2rem; border-top: 1px solid #e2e8f0; display: flex; flex-wrap: wrap; justify-content: space-between; align-items: center; gap: 1.5rem;">
        <div style="display: flex; gap: 3rem;">
            <div>
                <div style="font-size: 0.75rem; font-weight: 800; color: var(--text-secondary); text-transform: uppercase; margin-bottom: 0.4rem;">Scan Integrity</div>
                <div style="font-size: 1rem; font-weight: 700; color: #10b981;">HIGH_PRECISION</div>
            </div>
            <div>
                <div style="font-size: 0.75rem; font-weight: 800; color: var(--text-secondary); text-transform: uppercase; margin-bottom: 0.4rem;">Asset Liquidity</div>
                <div style="font-size: 1rem; font-weight: 700;">${result.visualizationHint().moneyPitState().replace("_", " ")}</div>
            </div>
        </div>
        <div style="font-size: 0.8rem; font-weight: 700; color: var(--text-secondary); opacity: 0.6;">
            [ 2026_CAR_QUANT_ENGINE // HUD_MODE ]
        </div>
    </footer>
</div>
