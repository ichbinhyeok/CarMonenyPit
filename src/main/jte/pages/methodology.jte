@template.layout(
    title = "Methodology | The Data-Driven Logic | AutoMoneyPit",
    content = @`
        <div style="max-width: 1000px; margin: 4rem auto; padding: 1rem;">
            <header style="text-align: center; margin-bottom: 5rem;">
                <div style="display: inline-block; background: #eff6ff; color: #3b82f6; padding: 0.5rem 1.25rem; border-radius: 99px; font-size: 0.8rem; font-weight: 800; letter-spacing: 0.1em; text-transform: uppercase; margin-bottom: 1.5rem;">
                    Scientific Framework v2.4
                </div>
                <h1 style="font-size: clamp(2.5rem, 8vw, 4rem); font-weight: 800; color: #0f172a; margin-bottom: 1.5rem; letter-spacing: -0.04em; line-height: 0.95;">
                    The Math of<br><span style="color: #3b82f6;">Regret Minimization.</span>
                </h1>
                <p style="font-size: 1.25rem; color: #64748b; max-width: 700px; margin: 0 auto; line-height: 1.6; font-weight: 500;">
                    We don't provide opinions. We provide data-driven analysis by calculating the financial friction of every possible path.
                </p>
            </header>

            <!-- The Core Equation Section -->
            <section style="background: #0f172a; color: #fff; padding: 4rem; border-radius: 48px; margin-bottom: 6rem; position: relative; overflow: hidden;">
                <div style="position: absolute; top: -100px; right: -100px; width: 300px; height: 300px; background: radial-gradient(circle, rgba(59, 130, 246, 0.15) 0%, transparent 70%);"></div>
                
                <h2 style="font-size: 2rem; font-weight: 800; margin-bottom: 3rem; text-align: center;">The Mathematical Verdict</h2>
                
                <div style="display: grid; grid-template-columns: 1fr; gap: 4rem;">
                    <!-- RF Formula -->
                    <div>
                        <div style="display: flex; align-items: center; gap: 1rem; margin-bottom: 1.5rem;">
                            <div style="width: 48px; height: 48px; background: #10b981; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-weight: 900; color: #fff;">RF</div>
                            <h3 style="font-size: 1.5rem; font-weight: 700;">Regret of Fixing</h3>
                        </div>
                        <div style="background: rgba(255,255,255,0.05); padding: 2.5rem; border-radius: 24px; border: 1px solid rgba(255,255,255,0.1); font-family: 'Courier New', monospace;">
                             <div style="font-size: 1.1rem; line-height: 1.8; color: #10b981; font-weight: 700;">
                                RF = [ Repair_Cost ] + [ Risk_Exposure ] + [ Opp_Cost ] - [ Amort_Benefit ]
                             </div>
                        </div>
                        <p style="margin-top: 1.5rem; font-size: 0.95rem; opacity: 0.7; line-height: 1.6;">
                            RF calculates the total liability of retention. We factor in the immediate bill, the statistical probability of a failure cascade (P_fail), the opportunity cost of capital lock-up, and the amortization benefit of spreading the cost over the remaining utility years.
                        </p>
                    </div>

                    <!-- RM Formula -->
                    <div>
                        <div style="display: flex; align-items: center; gap: 1rem; margin-bottom: 1.5rem;">
                            <div style="width: 48px; height: 48px; background: #6366f1; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-weight: 900; color: #fff;">RM</div>
                            <h3 style="font-size: 1.5rem; font-weight: 700;">Regret of Moving</h3>
                        </div>
                        <div style="background: rgba(255,255,255,0.05); padding: 2.5rem; border-radius: 24px; border: 1px solid rgba(255,255,255,0.1); font-family: 'Courier New', monospace;">
                             <div style="font-size: 1.1rem; line-height: 1.8; color: #818cf8; font-weight: 700;">
                                RM = [ Friction ] + [ Asset_Burn ] + [ Liq_Loss ] - [ PoM_Dividend ]
                             </div>
                        </div>
                        <p style="margin-top: 1.5rem; font-size: 0.95rem; opacity: 0.7; line-height: 1.6;">
                            RM calculates the friction of replacement. It includes transaction taxes/fees (Friction), the immediate 12% off-the-lot value drop of a new asset (Asset Burn), the spread between retail price and trade-in value (Liq_Loss), offset only by the psychological "Peace of Mind" premium.
                        </p>
                    </div>
                </div>
            </section>

            <!-- Key Calculation Constants -->
            <section style="margin-bottom: 6rem;">
                <h2 style="font-size: 2rem; font-weight: 800; margin-bottom: 3rem; text-align: center; letter-spacing: -0.02em;">Calibration Benchmarks</h2>
                <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: 1.5rem;">
                    <div style="padding: 2.5rem; border-radius: 32px; background: #fff; border: 1px solid #e2e8f0;">
                         <div style="font-size: 0.75rem; font-weight: 800; color: #3b82f6; text-transform: uppercase; margin-bottom: 0.5rem;">Constant v2.1</div>
                         <h4 style="font-size: 1.25rem; font-weight: 800; margin-bottom: 1rem;">Asset Burn Rate (12%)</h4>
                         <p style="color: #64748b; font-size: 0.95rem; line-height: 1.6;">
                            We apply a default 12% "Replacement Asset Burn" coefficient to all new car acquisitions, reflecting the immediate liquidity loss upon acquisition.
                         </p>
                    </div>
                    <div style="padding: 2.5rem; border-radius: 32px; background: #fff; border: 1px solid #e2e8f0;">
                         <div style="font-size: 0.75rem; font-weight: 800; color: #10b981; text-transform: uppercase; margin-bottom: 0.5rem;">Equation v1.8</div>
                         <h4 style="font-size: 1.25rem; font-weight: 800; margin-bottom: 1rem;">Repair Amortization</h4>
                         <p style="color: #64748b; font-size: 0.95rem; line-height: 1.6;">
                            If retention horizon ≥ 1 year, repair costs are amortized (Cost / Years), reducing the immediate RF score to reflect long-term utility gain.
                         </p>
                    </div>
                    <div style="padding: 2.5rem; border-radius: 32px; background: #fff; border: 1px solid #e2e8f0;">
                         <div style="font-size: 0.75rem; font-weight: 800; color: #f59e0b; text-transform: uppercase; margin-bottom: 0.5rem;">Algorithm v4.0</div>
                         <h4 style="font-size: 1.25rem; font-weight: 800; margin-bottom: 1rem;">Risk Probability (P)</h4>
                         <p style="color: #64748b; font-size: 0.95rem; line-height: 1.6;">
                            Failure cascades are weighted by mileage. The logarithmic stress tax starts at 100k miles, adjusting for the non-linear nature of aging assets.
                         </p>
                    </div>
                </div>
            </section>

            <footer style="text-align: center; background: #f8fafc; padding: 4rem; border-radius: 48px;">
                <h3 style="font-size: 1.75rem; font-weight: 800; margin-bottom: 1rem;">Empirical Proof.</h3>
                <p style="color: #64748b; margin-bottom: 2.5rem; font-weight: 500;">
                    Our methodology ensures that every verdict is rooted in financial preservation, not car-fever.
                </p>
                <a href="/" style="display: inline-block; background: #0f172a; color: #fff; padding: 1.25rem 3rem; border-radius: 16px; text-decoration: none; font-weight: 800; font-size: 1.1rem; box-shadow: 0 10px 25px rgba(0,0,0,0.1);">
                    Start Your Diagnostic
                </a>
            </footer>

        </div>
    `
)
