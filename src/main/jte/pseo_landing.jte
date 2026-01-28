@import com.carmoneypit.engine.web.PSeoController.CarModelStats
@import com.carmoneypit.engine.web.PSeoController.Fault
@param CarModelStats car
@param Fault fault
@param String schemaJson

@template.layout(content = @`
    <!-- SEO Schema (Invisible) -->
    <script type="application/ld+json">
        $unsafe{schemaJson}
    </script>

    <div class="pseo-container" style="max-width: 900px; margin: 0 auto; padding: 4rem 1rem;">
        
        <!-- 1. The Hook (Header) -->
        <header style="text-align: center; margin-bottom: 4rem;">
            <div style="font-size: 1rem; color: #d91b1b; font-weight: 700; text-transform: uppercase; letter-spacing: 2px; margin-bottom: 1rem;">
                Actuarial Verdict for ${car.brand()} Owners
            </div>
            <h1 style="font-size: 2.8rem; font-weight: 800; line-height: 1.2; margin-bottom: 1.5rem;">
                Should You Fix the <span style="text-decoration: underline; text-decoration-color: #d91b1b;">${fault.component()}</span> on Your ${car.model()}?
            </h1>
            <p style="font-size: 1.5rem; color: #666;">
                Average Repair Cost: <strong style="color: #000;">$${String.format("%,.0f", fault.repairCost())}</strong>
            </p>
        </header>

        <!-- 2. The Agitation (Problem Details) -->
        <div class="fault-card" style="background: #fff; padding: 2.5rem; border-radius: 12px; box-shadow: 0 10px 30px rgba(0,0,0,0.05); border: 1px solid #eee; margin-bottom: 4rem;">
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 2rem;">
                <div>
                    <h3 style="font-size: 1.2rem; font-weight: 700; margin-bottom: 1rem;">Common Symptoms</h3>
                    <ul style="list-style: none; padding: 0;">
                        <li style="padding: 0.8rem 0; border-bottom: 1px solid #f0f0f0; display: flex; align-items: center;">
                            <span style="color: #d91b1b; margin-right: 1rem;">⚠</span> ${fault.symptoms()}
                        </li>
                    </ul>
                </div>
                <div>
                     <h3 style="font-size: 1.2rem; font-weight: 700; margin-bottom: 1rem;">Actuarial Risk</h3>
                     <p style="font-style: italic; color: #555;">"${fault.verdictImplication()}"</p>
                </div>
            </div>
        </div>

        <!-- 3. The Gap (General vs Specific) -->
        <div style="background: #1a1a1a; color: #fff; padding: 3rem; border-radius: 12px; text-align: center; position: relative; overflow: hidden;">
            <div style="position: relative; z-index: 2;">
                <h2 style="font-size: 2rem; font-weight: 800; margin-bottom: 1.5rem;">But is YOUR specific car worth saving?</h2>
                <p style="font-size: 1.2rem; color: #aaa; margin-bottom: 2.5rem; max-width: 600px; margin-left: auto; margin-right: auto;">
                    A $${String.format("%,.0f", fault.repairCost())} repair might make sense at 80,000 miles, but be financial suicide at 150,000 miles. Don't guess. Run the math.
                </p>
                
                <!-- CTA Button -->
                <a href="/?vehicleType=${car.brand()}&model=${car.model()}" class="btn-cinematic" style="display: inline-block; text-decoration: none; font-size: 1.2rem; padding: 1.2rem 3rem;">
                    Run Custom Analysis <span>→</span>
                </a>
            </div>
        </div>

        <!-- 4. Trusted Data Badge -->
        <div style="text-align: center; margin-top: 3rem; color: #888; font-size: 0.9rem;">
            Data sourced from NHTSA, TSBs, and proprietary market analysis. <a href="/methodology" style="color: #666; text-decoration: underline;">See Methodology</a>.
        </div>
    </div>
`)
