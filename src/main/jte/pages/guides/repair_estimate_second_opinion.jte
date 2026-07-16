@template.layout(
    title = "Car Repair Estimate Second Opinion Checklist | AutoMoneyPit",
    description = "Use this repair-estimate second-opinion checklist to verify the diagnosis, compare itemized quotes, and avoid approving unnecessary assembly replacement.",
    canonical = "https://automoneypit.com/guides/car-repair-estimate-second-opinion",
    content = @`
        <article style="max-width:860px; margin:2rem auto 4rem; padding:0 1rem;">
            <header style="margin-bottom:2rem;">
                <div style="font-size:0.75rem; font-weight:800; color:#0f766e; letter-spacing:0.08em; text-transform:uppercase; margin-bottom:0.5rem;">Print or save this checklist</div>
                <h1 style="font-size:clamp(2.2rem,7vw,3.8rem); line-height:1.04; letter-spacing:-0.04em; margin:0 0 1rem;">Get a real second opinion on a car repair estimate</h1>
                <p style="font-size:1.08rem; line-height:1.7; color:#475569; margin:0;">
                    The goal is not to find the cheapest number. It is to make two shops quote the same confirmed problem, repair scope, warranty, and completion standard.
                </p>
            </header>

            <section style="background:#0f172a; color:#fff; border-radius:20px; padding:1.4rem; margin-bottom:1.25rem;">
                <h2 style="margin:0 0 0.8rem; font-size:1.3rem;">Send this exact request to the second shop</h2>
                <blockquote id="quoteRequest" style="margin:0; padding:1rem; background:rgba(255,255,255,0.08); border-left:4px solid #5eead4; color:#e2e8f0; line-height:1.7;">
                    I have a written estimate for a major repair. Please diagnose the vehicle independently before reviewing the first shop's conclusion. I need the failed test, code, measurement, or visible damage; the smallest viable repair; an itemized parts-and-labor quote; and the parts/labor warranty. Please also state what could change after teardown.
                </blockquote>
                <button id="copyQuoteRequest" type="button" style="margin-top:0.85rem; border:0; border-radius:10px; padding:0.75rem 1rem; background:#fff; color:#0f172a; font-weight:800; cursor:pointer;">
                    Copy request
                </button>
            </section>

            <div style="display:grid; grid-template-columns:repeat(auto-fit,minmax(260px,1fr)); gap:1rem;">
                <section style="border:1px solid #cbd5e1; border-radius:16px; padding:1.2rem;">
                    <h2 style="font-size:1.1rem; margin:0 0 0.75rem;">1. Diagnosis evidence</h2>
                    <ul style="margin:0; padding-left:1.2rem; color:#475569; line-height:1.75;">
                        <li>Exact complaint reproduced by the technician</li>
                        <li>Stored and pending DTCs before codes are cleared</li>
                        <li>Pressure, compression, leak-down, voltage, play, or fluid test results</li>
                        <li>Photos or video of visible damage or leakage</li>
                        <li>Engine, drivetrain, VIN, build date, and bulletin applicability</li>
                    </ul>
                </section>
                <section style="border:1px solid #cbd5e1; border-radius:16px; padding:1.2rem;">
                    <h2 style="font-size:1.1rem; margin:0 0 0.75rem;">2. Same repair scope</h2>
                    <ul style="margin:0; padding-left:1.2rem; color:#475569; line-height:1.75;">
                        <li>New, remanufactured, used, or owner-supplied parts</li>
                        <li>Assembly replacement versus component-level repair</li>
                        <li>Labor hours and hourly rate</li>
                        <li>Fluids, seals, programming, alignment, calibration, and disposal</li>
                        <li>Taxes, diagnostic charge, storage, towing, and rental cost</li>
                    </ul>
                </section>
                <section style="border:1px solid #cbd5e1; border-radius:16px; padding:1.2rem;">
                    <h2 style="font-size:1.1rem; margin:0 0 0.75rem;">3. Risk after the repair</h2>
                    <ul style="margin:0; padding-left:1.2rem; color:#475569; line-height:1.75;">
                        <li>Parts and labor warranty in writing</li>
                        <li>What the warranty excludes</li>
                        <li>Expected completion date and comeback policy</li>
                        <li>What could increase the quote after teardown</li>
                        <li>Whether another known repair is likely within 12 months</li>
                    </ul>
                </section>
            </div>

            <section style="margin-top:1.25rem; background:#f8fafc; border:1px solid #e2e8f0; border-radius:16px; padding:1.25rem;">
                <h2 style="font-size:1.25rem; margin:0 0 0.75rem;">Red flags that justify pausing</h2>
                <ul style="margin:0; padding-left:1.2rem; color:#475569; line-height:1.75;">
                    <li>The shop cannot show a failed test or reproduce the symptom.</li>
                    <li>The quote replaces a complete engine, transmission, battery, or suspension assembly without discussing a smaller repair.</li>
                    <li>Bulletin or warranty coverage is claimed without checking the VIN, engine, build date, and diagnostic criteria.</li>
                    <li>The estimate is verbal, non-itemized, or open-ended after teardown.</li>
                    <li>The repair is a large share of the car's value and no one has listed the rest of the near-term backlog.</li>
                </ul>
            </section>

            <div style="display:flex; gap:0.75rem; flex-wrap:wrap; margin-top:1.25rem;">
                <a href="/tools/repair-or-sell-calculator" data-analytics-event="repair_sell_tool_open" data-analytics-label="second_opinion_guide"
                   style="text-decoration:none; background:#0f766e; color:#fff; border-radius:10px; padding:0.85rem 1rem; font-weight:800;">
                    Compare the full repair decision
                </a>
                <button type="button" onclick="window.print()" style="border:1px solid #cbd5e1; background:#fff; color:#0f172a; border-radius:10px; padding:0.85rem 1rem; font-weight:800; cursor:pointer;">
                    Print checklist
                </button>
            </div>
        </article>

        <script>
            (function () {
                var button = document.getElementById("copyQuoteRequest");
                button.addEventListener("click", function () {
                    var text = document.getElementById("quoteRequest").textContent.trim();
                    navigator.clipboard.writeText(text).then(function () {
                        button.textContent = "Copied";
                        window.ampTrack("second_opinion_request_copied", { event_category: "engagement" });
                    });
                });
            })();
        </script>
    `
)
