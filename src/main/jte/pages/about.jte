@template.layout(
    title = "About Us | AutoMoneyPit",
    content = @`
        <div style="max-width: 1000px; margin: 4rem auto; padding: 1rem;">
            <header style="text-align: center; margin-bottom: 5rem;">
                <h1 style="font-size: 3rem; font-weight: 800; color: #0f172a; margin-bottom: 1.5rem;">Stop Guessing. Start Calculating.</h1>
                    AutoMoneyPit was founded on a simple premise: vehicle repair decisions should be informed by data-driven logic and financial frameworks, not just emotion.
            </header>

            <section style="display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 3rem; margin-bottom: 6rem;">
                <div style="background: #fff; padding: 2.5rem; border-radius: 24px; box-shadow: 0 4px 20px rgba(0,0,0,0.02); border: 1px solid #f1f5f9;">
                    <div style="font-size: 2rem; margin-bottom: 1rem;">🛡️</div>
                    <h2 style="font-size: 1.5rem; font-weight: 700; margin-bottom: 1rem;">Our Mission</h2>
                    <p style="color: #64748b; line-height: 1.6;">
                        We empower vehicle owners to exit "money pits" before they hemorrhage capital. 
                        By analyzing depreciation models, component failure data, and historical repair cost records, 
                        we provide the clarity needed to decide between preservation and liquidation.
                    </p>
                </div>
                
                <div style="background: #fff; padding: 2.5rem; border-radius: 24px; box-shadow: 0 4px 20px rgba(0,0,0,0.02); border: 1px solid #f1f5f9;">
                    <div style="font-size: 2rem; margin-bottom: 1rem;">⚖️</div>
                    <h2 style="font-size: 1.5rem; font-weight: 700; margin-bottom: 1rem;">The Algorithm Edge</h2>
                    <p style="color: #64748b; line-height: 1.6;">
                        Unlike generic "advice," our engine calculates <strong>Regret Minimization</strong>. 
                        We don't just look at the repair cost; we factor in depreciation curves, failure cascades, 
                        opportunity costs, and switching friction to find the mathematical "verdict."
                    </p>
                </div>
            </section>

            <section style="background: #0f172a; color: #fff; padding: 4rem; border-radius: 32px; margin-bottom: 6rem;">
                <h2 style="font-size: 2rem; font-weight: 800; margin-bottom: 3rem; text-align: center;">Our Methodology</h2>
                <div style="max-width: 850px; margin: 0 auto; display: grid; gap: 2.5rem;">
                    
                    <div style="background: rgba(255,255,255,0.05); padding: 2rem; border-radius: 20px; border: 1px solid rgba(255,255,255,0.1);">
                        <h3 style="font-size: 1.25rem; font-weight: 700; margin-bottom: 1rem; color: #3b82f6;">01. The Regret Matrix (RF vs RM)</h3>
                        <p style="opacity: 0.8; line-height: 1.6; font-size: 0.95rem;">
                            Our engine doesn't just look at a repair bill. it compares the <strong>Regret of Fixing (RF)</strong> against the <strong>Regret of Moving (RM)</strong>. 
                            If the cost of transaction friction and new asset depreciation outweighs the repair pain, the engine signals a "Sustain" verdict.
                        </p>
                    </div>

                    <div style="background: rgba(255,255,255,0.05); padding: 2rem; border-radius: 20px; border: 1px solid rgba(255,255,255,0.1);">
                        <h3 style="font-size: 1.25rem; font-weight: 700; margin-bottom: 1rem; color: #3b82f6;">02. Replacement Asset Burn</h3>
                        <p style="opacity: 0.8; line-height: 1.6; font-size: 0.95rem;">
                            Most owners forget the "hidden tax" of buying a new car. The moment a replacement vehicle leaves the lot, it loses ~12% of its value. 
                            We factor this <strong>Asset Burn</strong> into every "Move" decision to prevent premature liquidation of reliable assets.
                        </p>
                    </div>

                    <div style="background: rgba(255,255,255,0.05); padding: 2rem; border-radius: 20px; border: 1px solid rgba(255,255,255,0.1);">
                        <h3 style="font-size: 1.25rem; font-weight: 700; margin-bottom: 1rem; color: #3b82f6;">03. Repair Amortization</h3>
                        <p style="opacity: 0.8; line-height: 1.6; font-size: 0.95rem;">
                            A $1,500 repair is an investment. If that repair grants you 3 more years of utility, the amortized cost is only $500/year. 
                            Our algorithm calculates this <strong>Amortization Benefit</strong> based on your intended retention horizon.
                        </p>
                    </div>

                    <p style="text-align: center; opacity: 0.6; font-size: 0.85rem; margin-top: 1rem;">
                        Data is sourced from available market frameworks and historical models.
                    </p>
                </div>
            </section>

            <footer style="text-align: center;">
                <h3 style="font-size: 1.5rem; font-weight: 700; margin-bottom: 1.5rem;">Ready to find your verdict?</h3>
                <a href="/" style="display: inline-block; background: #0066ff; color: #fff; padding: 1rem 2.5rem; border-radius: 12px; text-decoration: none; font-weight: 700; transition: transform 0.2s;">
                    Run Analysis Now <span>→</span>
                </a>
            </footer>
        </div>
    `
)
