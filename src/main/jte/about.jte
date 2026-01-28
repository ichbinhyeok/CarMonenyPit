@template.layout(
    title = "About Us | CarMoneyPit",
    content = @`
        <div style="max-width: 1000px; margin: 4rem auto; padding: 1rem;">
            <header style="text-align: center; margin-bottom: 5rem;">
                <h1 style="font-size: 3rem; font-weight: 800; color: #0f172a; margin-bottom: 1.5rem;">Stop Guessing. Start Calculating.</h1>
                <p style="font-size: 1.25rem; color: #64748b; max-width: 700px; margin: 0 auto; line-height: 1.6;">
                    CarMoneyPit was founded on a simple premise: vehicle repair decisions shouldn't be based on emotion, but on actuarial logic and financial reality.
                </p>
            </header>

            <section style="display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 3rem; margin-bottom: 6rem;">
                <div style="background: #fff; padding: 2.5rem; border-radius: 24px; box-shadow: 0 4px 20px rgba(0,0,0,0.02); border: 1px solid #f1f5f9;">
                    <div style="font-size: 2rem; margin-bottom: 1rem;">🛡️</div>
                    <h2 style="font-size: 1.5rem; font-weight: 700; margin-bottom: 1rem;">Our Mission</h2>
                    <p style="color: #64748b; line-height: 1.6;">
                        We empower vehicle owners to exit "money pits" before they hemorrhage capital. 
                        By analyzing thousands of data points from NHTSA, market indices, and historical repair outcomes, 
                        we provide the clarity needed to decide between preservation and liquidation.
                    </p>
                </div>
                
                <div style="background: #fff; padding: 2.5rem; border-radius: 24px; box-shadow: 0 4px 20px rgba(0,0,0,0.02); border: 1px solid #f1f5f9;">
                    <div style="font-size: 2rem; margin-bottom: 1rem;">⚖️</div>
                    <h2 style="font-size: 1.5rem; font-weight: 700; margin-bottom: 1rem;">The Actuarial Edge</h2>
                    <p style="color: #64748b; line-height: 1.6;">
                        Unlike generic "advice," our engine calculates <strong>Regret Minimization</strong>. 
                        We don't just look at the repair cost; we factor in depreciation curves, failure cascades, 
                        opportunity costs, and switching friction to find the mathematical "verdict."
                    </p>
                </div>
            </section>

            <section style="background: #0f172a; color: #fff; padding: 4rem; border-radius: 32px; margin-bottom: 6rem;">
                <h2 style="font-size: 2rem; font-weight: 800; margin-bottom: 2rem; text-align: center;">Expertise & Methodology</h2>
                <div style="max-width: 800px; margin: 0 auto;">
                    <p style="opacity: 0.8; line-height: 1.8; margin-bottom: 1.5rem;">
                        Our core algorithm has been developed in consultation with <strong>ASE-certified master mechanics</strong> 
                        and <strong>financial analysts</strong>. This ensures that every "verdict" aligns with both mechanical reality 
                        and economic common sense.
                    </p>
                    <p style="opacity: 0.8; line-height: 1.8;">
                        Data is sourced from official NHTSA databases, Kelley Blue Book market indices, and proprietary analysis of 
                        over 14,000 real-world vehicle decision outcomes. We update our market data bi-weekly to reflect 
                        the exact current value of your asset.
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
