@template.layout(
    title = "About Us | CarMoneyPit",
    content = @`
        <div style="max-width: 1000px; margin: 4rem auto; padding: 1rem;">
            <header style="text-align: center; margin-bottom: 5rem;">
                <h1 style="font-size: 3rem; font-weight: 800; color: #0f172a; margin-bottom: 1.5rem;">Stop Guessing. Start Calculating.</h1>
                    CarMoneyPit was founded on a simple premise: vehicle repair decisions should be informed by actuarial logic and financial frameworks, not just emotion.
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
                    <h2 style="font-size: 1.5rem; font-weight: 700; margin-bottom: 1rem;">The Algorithm Edge</h2>
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
                    <div class="bg-white rounded-2xl p-8 border border-slate-100 shadow-sm relative overflow-hidden group hover:border-blue-100 transition-colors">
                <div class="absolute top-0 right-0 p-8 opacity-5 group-hover:opacity-10 transition-opacity">
                    <svg xmlns="http://www.w3.org/2000/svg" width="120" height="120" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2v20"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/></svg>
                </div>
                <h3 class="text-xl font-bold text-slate-900 mb-3 flex items-center gap-2">
                    <span class="w-8 h-8 rounded-lg bg-blue-100 flex items-center justify-center text-blue-600 overflow-hidden">
                        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2v20"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/></svg>
                    </span>
                    Liquidation Valuation
                </h3>
                <p class="text-slate-600 leading-relaxed mb-4">
                    The core of the engine. We apply simplified market liquidation models to determine the "Financial Death" of a vehicle.
                </p>
            </div>
                    <p style="opacity: 0.8; line-height: 1.8;">
                        Data is sourced from official databases and market frameworks. We update our market projections regularly to reflect estimated trends for your asset.
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
