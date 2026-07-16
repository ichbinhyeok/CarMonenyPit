@template.layout(
    title = "Repair or Sell Calculator: Compare the Full Cost | AutoMoneyPit",
    description = "Compare a repair quote, near-term maintenance, downtime, and replacement switching costs before deciding whether to repair or sell your car.",
    canonical = "https://automoneypit.com/tools/repair-or-sell-calculator",
    content = @`
        <div style="max-width:980px; margin:2rem auto 4rem; padding:0 1rem;">
            <header style="max-width:760px; margin-bottom:2rem;">
                <div style="font-size:0.75rem; font-weight:800; color:#0f766e; letter-spacing:0.08em; text-transform:uppercase; margin-bottom:0.5rem;">Free decision tool</div>
                <h1 style="font-size:clamp(2.2rem,7vw,4rem); line-height:1.02; letter-spacing:-0.045em; margin:0 0 1rem;">Repair the car or sell it?</h1>
                <p style="font-size:1.08rem; color:#475569; line-height:1.7; margin:0;">
                    A repair quote is only one side of the decision. Compare the full cost of keeping the car with the taxes, fees, financing, and immediate work that come with replacing it.
                </p>
            </header>

            <div style="display:grid; grid-template-columns:minmax(0,1.05fr) minmax(300px,0.95fr); gap:1.25rem; align-items:start;" class="decision-tool-grid">
                <form id="repairSellForm" style="background:#fff; border:1px solid #cbd5e1; border-radius:20px; padding:1.35rem;">
                    <div style="display:grid; grid-template-columns:repeat(2,minmax(0,1fr)); gap:1rem;" class="tool-fields">
                        <label style="display:block; color:#334155; font-weight:700;">
                            Current car value
                            <input id="carValue" type="number" min="0" step="100" value="10000" inputmode="numeric"
                                   style="width:100%; box-sizing:border-box; margin-top:0.4rem; padding:0.85rem; border:1px solid #cbd5e1; border-radius:10px; font-size:1rem;">
                        </label>
                        <label style="display:block; color:#334155; font-weight:700;">
                            Today's repair quote
                            <input id="repairQuote" type="number" min="0" step="50" value="2500" inputmode="numeric"
                                   style="width:100%; box-sizing:border-box; margin-top:0.4rem; padding:0.85rem; border:1px solid #cbd5e1; border-radius:10px; font-size:1rem;">
                        </label>
                        <label style="display:block; color:#334155; font-weight:700;">
                            Next 12 months of known work
                            <input id="repairBacklog" type="number" min="0" step="50" value="800" inputmode="numeric"
                                   style="width:100%; box-sizing:border-box; margin-top:0.4rem; padding:0.85rem; border:1px solid #cbd5e1; border-radius:10px; font-size:1rem;">
                        </label>
                        <label style="display:block; color:#334155; font-weight:700;">
                            Downtime / rental / towing
                            <input id="downtimeCost" type="number" min="0" step="25" value="250" inputmode="numeric"
                                   style="width:100%; box-sizing:border-box; margin-top:0.4rem; padding:0.85rem; border:1px solid #cbd5e1; border-radius:10px; font-size:1rem;">
                        </label>
                        <label style="display:block; color:#334155; font-weight:700;">
                            Replacement taxes and fees
                            <input id="replacementFees" type="number" min="0" step="50" value="1200" inputmode="numeric"
                                   style="width:100%; box-sizing:border-box; margin-top:0.4rem; padding:0.85rem; border:1px solid #cbd5e1; border-radius:10px; font-size:1rem;">
                        </label>
                        <label style="display:block; color:#334155; font-weight:700;">
                            Immediate work on replacement
                            <input id="replacementWork" type="number" min="0" step="50" value="700" inputmode="numeric"
                                   style="width:100%; box-sizing:border-box; margin-top:0.4rem; padding:0.85rem; border:1px solid #cbd5e1; border-radius:10px; font-size:1rem;">
                        </label>
                    </div>
                    <button type="submit" style="width:100%; margin-top:1.15rem; border:0; border-radius:12px; padding:1rem; background:#0f766e; color:#fff; font-size:1rem; font-weight:900; cursor:pointer;">
                        Compare the decision
                    </button>
                </form>

                <aside aria-live="polite" style="background:#0f172a; color:#fff; border-radius:20px; padding:1.4rem; position:sticky; top:6rem;">
                    <div style="font-size:0.72rem; font-weight:800; color:#5eead4; letter-spacing:0.08em; text-transform:uppercase;">Screening result</div>
                    <h2 id="resultHeading" style="font-size:1.7rem; margin:0.55rem 0 0.8rem;">Enter your real numbers</h2>
                    <p id="resultBody" style="color:#cbd5e1; line-height:1.65; margin:0 0 1rem;">The tool will compare the all-in keep cost with the friction cost of switching vehicles.</p>
                    <div style="display:grid; grid-template-columns:1fr 1fr; gap:0.75rem;">
                        <div style="background:rgba(255,255,255,0.08); border-radius:12px; padding:0.9rem;">
                            <span style="display:block; color:#94a3b8; font-size:0.75rem;">Repair-to-value</span>
                            <strong id="ratioResult" style="font-size:1.4rem;">-</strong>
                        </div>
                        <div style="background:rgba(255,255,255,0.08); border-radius:12px; padding:0.9rem;">
                            <span style="display:block; color:#94a3b8; font-size:0.75rem;">12-month keep cost</span>
                            <strong id="keepCostResult" style="font-size:1.4rem;">-</strong>
                        </div>
                        <div style="background:rgba(255,255,255,0.08); border-radius:12px; padding:0.9rem;">
                            <span style="display:block; color:#94a3b8; font-size:0.75rem;">Switching friction</span>
                            <strong id="switchCostResult" style="font-size:1.4rem;">-</strong>
                        </div>
                        <div style="background:rgba(255,255,255,0.08); border-radius:12px; padding:0.9rem;">
                            <span style="display:block; color:#94a3b8; font-size:0.75rem;">Value left after keep cost</span>
                            <strong id="equityResult" style="font-size:1.4rem;">-</strong>
                        </div>
                    </div>
                    <a href="/guides/car-repair-estimate-second-opinion" data-analytics-event="second_opinion_guide_open" data-analytics-label="calculator_result"
                       style="display:block; margin-top:1rem; text-align:center; text-decoration:none; color:#0f172a; background:#fff; border-radius:10px; padding:0.8rem; font-weight:800;">
                        Verify the quote before deciding
                    </a>
                </aside>
            </div>

            <section style="margin-top:1.5rem; border:1px solid #e2e8f0; border-radius:16px; padding:1.2rem; color:#475569; line-height:1.65;">
                <strong style="color:#0f172a;">How to read this:</strong> under 20% repair-to-value is often a reasonable repair candidate when the diagnosis is confirmed and backlog is low. Between 20% and 40% needs a whole-car comparison. Above 40% is a strong reason to get a second opinion and real sale offers. These are screening bands, not a mechanic's diagnosis or financial advice.
            </section>
        </div>

        <style>
            @media (max-width: 760px) {
                .decision-tool-grid { grid-template-columns:1fr !important; }
                .tool-fields { grid-template-columns:1fr !important; }
                .decision-tool-grid aside { position:static !important; }
            }
        </style>

        <script>
            (function () {
                var form = document.getElementById("repairSellForm");
                var money = new Intl.NumberFormat("en-US", { style: "currency", currency: "USD", maximumFractionDigits: 0 });

                function number(id) {
                    return Math.max(0, Number(document.getElementById(id).value) || 0);
                }

                function calculate(event) {
                    if (event) event.preventDefault();
                    var value = number("carValue");
                    var quote = number("repairQuote");
                    var backlog = number("repairBacklog");
                    var downtime = number("downtimeCost");
                    var replacementFees = number("replacementFees");
                    var replacementWork = number("replacementWork");
                    var keepCost = quote + backlog + downtime;
                    var switchCost = replacementFees + replacementWork;
                    var ratio = value > 0 ? quote / value : 0;
                    var heading;
                    var body;

                    if (value <= 0) {
                        heading = "Add a realistic car value";
                        body = "Use a local offer or condition-adjusted comparable so the ratio is meaningful.";
                    } else if (ratio < 0.2 && keepCost < value * 0.3) {
                        heading = "Repair may be the lower-friction option";
                        body = "The quote is modest relative to value. Confirm the diagnosis and make sure the backlog is complete.";
                    } else if (ratio < 0.4 && keepCost < value * 0.55) {
                        heading = "This is a comparison case";
                        body = "Do not decide from the repair quote alone. Get a second diagnosis and at least two real sale or trade offers.";
                    } else {
                        heading = "Pause before approving the repair";
                        body = "The repair consumes a large share of value. Verify the failure, compare the full backlog, and price the replacement path.";
                    }

                    document.getElementById("resultHeading").textContent = heading;
                    document.getElementById("resultBody").textContent = body;
                    document.getElementById("ratioResult").textContent = value > 0 ? Math.round(ratio * 100) + "%" : "-";
                    document.getElementById("keepCostResult").textContent = money.format(keepCost);
                    document.getElementById("switchCostResult").textContent = money.format(switchCost);
                    document.getElementById("equityResult").textContent = money.format(value - keepCost);

                    window.ampTrack("decision_tool_calculated", {
                        event_category: "engagement",
                        repair_to_value_band: value <= 0 ? "unknown" : ratio < 0.2 ? "under_20" : ratio < 0.4 ? "20_to_40" : "over_40",
                        keep_cost_band: keepCost < 2000 ? "under_2k" : keepCost < 5000 ? "2k_to_5k" : "over_5k"
                    });
                }

                form.addEventListener("submit", calculate);
                calculate();
            })();
        </script>
    `
)
