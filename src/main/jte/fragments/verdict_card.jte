@import com.carmoneypit.engine.api.OutputModels.VerdictResult
@import com.carmoneypit.engine.api.OutputModels.VerdictState

@param VerdictResult result
@param String verdictTitle
@param String verdictExplanation
@param String verdictAction
@param String verdictCss
@param String leadLabel
@param String leadDescription

<div id="verdict-card" class="fade-in">
    <!-- Main Verdict Hero -->
    <div style="margin-bottom: 3.5rem;">
        <div style="display: flex; align-items: center; gap: 0.75rem; margin-bottom: 1rem;">
            <div style="width: 12px; height: 12px; border-radius: 50%; background: ${verdictCss.contains("liquidate") ? "var(--red-alert)" : (verdictCss.contains("sustain") ? "var(--green-safe)" : "var(--orange-warn)")}; shadow: 0 0 10px rgba(0,0,0,0.1);"></div>
            <span style="text-transform: uppercase; font-size: 0.75rem; font-weight: 800; letter-spacing: 0.1em; color: var(--text-secondary); opacity: 0.7;">
                Primary Computation Result
            </span>
        </div>
        <h1 style="font-size: 5rem; font-weight: 800; letter-spacing: -0.05em; margin-bottom: 1.5rem; line-height: 0.9; color: #000;">
            ${verdictTitle}
        </h1>
        <p style="font-size: 1.2rem; color: var(--text-main); font-weight: 500; max-width: 700px; line-height: 1.5;">
            "${verdictExplanation}"
        </p>
    </div>

    <!-- Data Breakdown Grid -->
    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 2rem; margin: 3rem 0;">
        <div style="background: #fff; padding: 2rem; border-radius: 24px; border: 1px solid var(--border-subtle); box-shadow: 0 4px 12px rgba(0,0,0,0.01);">
            <h3 style="font-size: 0.85rem; font-weight: 800; text-transform: uppercase; letter-spacing: 0.05em; margin-bottom: 1.5rem; color: var(--text-secondary);">
                Actuarial Line Items
            </h3>
            @template.fragments.smart_receipt(items = result.costBreakdown())
            
            <!-- Asset Bleed Section -->
            <div style="margin-top: 2rem; padding-top: 1.5rem; border-top: 1px dashed var(--border-subtle);">
                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.5rem;">
                    <div style="font-size: 0.8rem; font-weight: 800; color: var(--red-alert); text-transform: uppercase; letter-spacing: 0.05em;">
                        Projected Asset Bleed (6mo)
                    </div>
                    <div style="font-size: 1.2rem; font-weight: 800; color: var(--red-alert);">
                        -$${String.format("%,d", result.assetBleedAmount())}
                    </div>
                </div>
                <div style="font-size: 0.85rem; color: var(--text-secondary); font-weight: 500; line-height: 1.4;">
                    Loss from inaction: Estimated depreciation + risk probability.
                </div>
            </div>
        </div>

        <div style="display: flex; flex-direction: column; gap: 1.5rem;">
            <!-- Recommended Action -->
            <div style="background: #f8fafc; padding: 2rem; border-radius: 24px; border-left: 4px solid #000;">
                <h4 style="font-size: 0.8rem; font-weight: 800; margin-bottom: 0.5rem; text-transform: uppercase; color: var(--text-secondary);">Executive Summary</h4>
                <p style="font-size: 1.05rem; margin: 0; color: var(--text-main); font-weight: 600; line-height: 1.4;">${verdictAction}</p>
            </div>

            <!-- Exit Ramp -->
            <div style="background: var(--futurist-blue); padding: 2rem; border-radius: 24px; color: #fff; box-shadow: 0 10px 30px rgba(0, 102, 255, 0.2);">
                <div style="font-size: 0.75rem; font-weight: 800; text-transform: uppercase; margin-bottom: 0.5rem; opacity: 0.8;">Actionable Response</div>
                <h4 style="font-size: 1.25rem; font-weight: 800; margin-bottom: 1rem; line-height: 1.2;">${leadLabel}</h4>
                <p style="font-size: 0.9rem; margin-bottom: 1.5rem; opacity: 0.9; font-weight: 500;">${leadDescription}</p>
                <a href="#" style="display: inline-flex; align-items: center; justify-content: center; background: #fff; color: var(--futurist-blue); padding: 0.8rem 1.5rem; border-radius: 14px; text-decoration: none; font-weight: 800; font-size: 0.95rem; transition: transform 0.2s;">
                    PROCEED <span>→</span>
                </a>
            </div>
        </div>
    </div>

    <footer style="margin-top: 3rem; padding-top: 2rem; border-top: 1px solid #f1f5f9; display: flex; justify-content: space-between; align-items: center;">
        <div style="display: flex; gap: 2.5rem;">
            <div>
                <div style="font-size: 0.7rem; font-weight: 800; color: var(--text-secondary); text-transform: uppercase; margin-bottom: 0.25rem;">Analytics Confidence</div>
                <div style="font-size: 0.9rem; font-weight: 700; color: var(--green-safe);">STATISTICALLY_HIGH</div>
            </div>
            <div>
                <div style="font-size: 0.7rem; font-weight: 800; color: var(--text-secondary); text-transform: uppercase; margin-bottom: 0.25rem;">Equity Index</div>
                <div style="font-size: 0.9rem; font-weight: 700;">${result.visualizationHint().moneyPitState().replace("_", " ")}</div>
            </div>
        </div>
        <div style="font-size: 0.75rem; font-weight: 600; color: var(--text-secondary);">© 2026 CAR_PIT_QUANT_ENGINE</div>
    </footer>
</div>
