@import com.carmoneypit.engine.service.CarDataService.CarModel
@import com.carmoneypit.engine.service.CarDataService.Fault
@import com.carmoneypit.engine.web.PSeoController.ProfileViewModel
@import com.carmoneypit.engine.web.PSeoController.Breadcrumb
@import java.util.List
@param CarModel car
@param Fault fault
@param ProfileViewModel details
@param String schemaJson
@param String metaDescription
@param String canonicalUrl
@param String ctaUrl
@param List<Fault> relatedFaults
@param String marketPulse
@param List<Breadcrumb> breadcrumbs
@param String ogImage = null

@template.layout(
    title = car.brand() + " " + car.model() + " " + fault.component() + " Repair ($" + String.format("%,.0f", fault.repairCost()) + "): Fix or Sell? [2026 Data]",
    description = metaDescription,
    canonical = canonicalUrl,
    schemaJson = schemaJson,
    ogImage = ogImage,
    content = @`
    <div class="pseo-container" style="max-width: 900px; margin: 0 auto; padding: 4rem 1rem;">
        
        <!-- Breadcrumbs (SEO + UX) -->
        <nav style="margin-bottom: 2rem; font-size: 0.85rem; color: #666; font-weight: 500;">
            @for(int i = 0; i < breadcrumbs.size(); i++)
                @if(i > 0) <span style="margin: 0 0.5rem; opacity: 0.5;">/</span> @endif
                @if(breadcrumbs.get(i).url().equals("#"))
                    <span style="color: #333; font-weight: 700;">${breadcrumbs.get(i).label()}</span>
                @else
                    <a href="${breadcrumbs.get(i).url()}" style="color: #666; text-decoration: none; transition: color 0.2s; border-bottom: 1px solid transparent;">
                        ${breadcrumbs.get(i).label()}
                    </a>
                @endif
            @endfor
        </nav>
        
        <!-- 1. The Hook (Hero) - Spintax Randomization -->
        !{
            // Simple deterministic randomization based on model name length to keep it consistent per page but varied across pages
            int spinIndex = (car.model().length() + fault.component().length()) % 3;
            String[] headlines = {
                "Should You Fix the <span style=\"background: linear-gradient(120deg, #d91b1b33 0%, #d91b1b33 100%); background-repeat: no-repeat; background-size: 100% 40%; background-position: 0 90%;\">" + fault.component() + "</span><br>on Your " + car.model() + "?",
                "Is the " + car.model() + " <span style=\"background: linear-gradient(120deg, #d91b1b33 0%, #d91b1b33 100%); background-repeat: no-repeat; background-size: 100% 40%; background-position: 0 90%;\">" + fault.component() + "</span><br>Worth Repairing in 2026?",
                "<span style=\"background: linear-gradient(120deg, #d91b1b33 0%, #d91b1b33 100%); background-repeat: no-repeat; background-size: 100% 40%; background-position: 0 90%;\">" + fault.component() + "</span> Failure:<br>Fix or Sell Your " + car.model() + "?"
            };
            String headline = headlines[spinIndex];
        }

        <header style="text-align: center; margin-bottom: 5rem;">
            <div style="display: inline-flex; align-items: center; gap: 0.5rem; background: #f0fdf4; border: 1px solid #bbf7d0; padding: 0.5rem 1rem; border-radius: 99px; color: #166534; font-size: 0.8rem; font-weight: 700; margin-bottom: 2rem;">
                <span style="font-size: 1rem;">✓</span> ACTUARIAL ALGORITHM APPLIED
            </div>
            <div style="font-size: 0.9rem; color: #d91b1b; font-weight: 700; text-transform: uppercase; letter-spacing: 2px; margin-bottom: 1rem;">
                Actuarial Verdict for ${car.brand()} Owners
            </div>
            <h1 style="font-size: 3rem; font-weight: 800; line-height: 1.1; margin-bottom: 2rem; letter-spacing: -0.02em;">
                $unsafe{headline}
            </h1>
            
            <div style="display: inline-flex; flex-direction: column; align-items: center; background: #fff; border: 2px solid #000; padding: 1.5rem 3rem; border-radius: 16px; box-shadow: 8px 8px 0px #000;">
                <span style="font-size: 1rem; color: #666; font-weight: 600; text-transform: uppercase;">Estimated Repair Cost</span>
                <span style="font-size: 3.5rem; font-weight: 900; color: #000; line-height: 1;">$${String.format("%,.0f", fault.repairCost())}</span>
                <span style="font-size: 0.7rem; color: #999; margin-top: 0.5rem; font-weight: 600;">(Est. National Avg.)</span>
            </div>
        </header>

        <!-- Bi-weekly Market Pulse (SEO Freshness) -->
        $unsafe{marketPulse}

        <!-- 2. Vehicle Financial Profile (New Section) -->
        @if(details != null)
        <div style="background: #fdfdfd; border: 1px solid #e0e0e0; border-radius: 16px; padding: 2rem; margin-bottom: 5rem;">
            <h2 style="font-size: 1.5rem; font-weight: 700; margin-bottom: 2rem; display: flex; align-items: center;">
                 📊 ${car.brand()} ${car.model()} Profile (Jan 2026 Data)
            </h2>
            
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 2rem;">
                <!-- Left: Market Data -->
                <div>
                     <div style="margin-bottom: 1.5rem;">
                        <span style="display: block; font-size: 0.9rem; color: #666; text-transform: uppercase; font-weight: 600;">Avg. Market Value</span>
                        <span style="font-size: 2rem; font-weight: 800; color: #2E7D32;">$${String.format("%,d", details.market().jan2026AvgPrice())}</span>
                     </div>
                     <div>
                        <span style="display: block; font-size: 0.9rem; color: #666; text-transform: uppercase; font-weight: 600;">Expected Lifespan</span>
                        <span style="font-size: 1.5rem; font-weight: 700; color: #333;">${String.format("%,d", details.reliability().lifespanMiles())} Miles</span>
                     </div>
                </div>

                <!-- Right: Analysis -->
                <div>
                    @if(!details.reliability().worstYears().isEmpty())
                    <div style="margin-bottom: 1.5rem; padding: 1rem; background: #FFF4F4; border-left: 4px solid #D91B1B; border-radius: 4px;">
                        <strong style="color: #D91B1B;">Avoid Years:</strong>
                         ${String.join(", ", details.reliability().worstYears().stream().map(Object::toString).toList())}
                    </div>
                    @endif
                    
                    <div style="font-size: 0.9rem; color: #555;">
                        <strong>Next Big Hit:</strong> 
                        @if(!details.reliability().criticalMilestones().isEmpty())
                            At ${String.format("%,d", details.reliability().criticalMilestones().get(0).mileage())} miles, expect a 
                            $${details.reliability().criticalMilestones().get(0).estCost()} service (${details.reliability().criticalMilestones().get(0).description()}).
                        @endif
                    </div>
                </div>
            </div>
        </div>
        @endif

        <!-- 3. The Agitation (Symptoms & Risk) -->
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 2rem; margin-bottom: 5rem;">
            <!-- Symptoms Card -->
            <div style="background: #fff; padding: 2rem; border-radius: 12px; border: 1px solid #eee;">
                <h3 style="font-size: 1.2rem; font-weight: 700; margin-bottom: 1rem; display: flex; align-items: center;">
                    <span style="background: #FFF4F4; color: #D91B1B; width: 32px; height: 32px; display: inline-flex; align-items: center; justify-content: center; border-radius: 50%; margin-right: 0.8rem;">⚠</span> 
                    Warning Signs
                </h3>
                <p style="font-size: 1.1rem; color: #444; line-height: 1.6;">
                    ${fault.symptoms()}
                </p>
            </div>

            <!-- Risk Card -->
            <div style="background: #F8F9FA; padding: 2rem; border-radius: 12px; border: 1px solid #eee;">
                <h3 style="font-size: 1.2rem; font-weight: 700; margin-bottom: 1rem; display: flex; align-items: center;">
                    <span style="background: #E8F0FE; color: #1967D2; width: 32px; height: 32px; display: inline-flex; align-items: center; justify-content: center; border-radius: 50%; margin-right: 0.8rem;">ℹ</span> 
                    Our Verdict
                </h3>
                <p style="font-size: 1.1rem; color: #444; line-height: 1.6; font-style: italic;">
                    "${fault.verdictImplication()}"
                </p>
            </div>
        </div>

        <!-- 3. Symptom Matching (Instant Trust / UX) -->
        <div style="background: #fff; border-left: 6px solid #F59E0B; padding: 2rem; margin-bottom: 4rem; text-align: left; box-shadow: 0 10px 30px rgba(0,0,0,0.03); border-radius: 8px;">
            <h3 style="font-size: 1.25rem; font-weight: 800; color: #78350F; margin-bottom: 1rem; display: flex; align-items: center; gap: 0.5rem;">
                <span style="font-size: 1.5rem;">🩺</span> Does it sound like this?
            </h3>
            <p style="font-size: 1.1rem; line-height: 1.6; color: #1F2937; margin-bottom: 0;">
                Common symptoms for <strong>${fault.component()}</strong> failures in this model include: <br>
                <span style="display: block; margin-top: 0.75rem; padding: 1rem; background: #FFFBEB; border-radius: 8px; font-weight: 600; font-style: italic; color: #92400E;">
                    "${fault.symptoms()}"
                </span>
            </p>
        </div>

        <!-- 3. The Financial Trap (Educational Content) -->
        <div style="margin-bottom: 5rem;">
             <h2 style="font-size: 2rem; font-weight: 800; margin-bottom: 1.5rem;">The "Sunk Cost" Trap</h2>
             <p style="font-size: 1.1rem; color: #444; line-height: 1.6; margin-bottom: 1.5rem;">
                Many ${car.brand()} owners fall into the trap of thinking, "I've already spent so much maintenance, I have to keep it." This is financially dangerous.
             </p>
             <p style="font-size: 1.1rem; color: #444; line-height: 1.6; margin-bottom: 1.5rem;">
                <strong>The 50% Rule:</strong> If a repair costs more than 50% of your vehicle's current market value, or if your total monthly repair average exceeds a new car payment, it is time to exit.
             </p>
        </div>

        <!-- 4. The Solution (CTA) -->
        <div style="background: #1a1a1a; color: #fff; padding: 4rem 2rem; border-radius: 24px; text-align: center; position: relative; overflow: hidden; margin-bottom: 5rem;">
            <div style="position: relative; z-index: 2;">
                <h2 style="font-size: 2.5rem; font-weight: 800; margin-bottom: 1rem;">Is your ${car.model()} an Asset or a Liability?</h2>
                <p style="font-size: 1.3rem; color: #aaa; margin-bottom: 3rem; max-width: 700px; margin-left: auto; margin-right: auto;">
                    We compare your specific vehicle's real-time market value against this $${String.format("%,.0f", fault.repairCost())} repair bill. The math doesn't lie.
                </p>
                
                <a href="${ctaUrl}" class="btn-cinematic" style="display: inline-block; text-decoration: none; font-size: 1.2rem; padding: 1.2rem 3rem; background: #2563EB; color: #ffffff; border-radius: 8px; font-weight: 700;">
                    Pre-fill My Analysis <span>→</span>
                </a>
            </div>
        </div>

        <!-- Related Problems Section (Internal Linking) -->
        @if(relatedFaults != null && !relatedFaults.isEmpty())
        <div style="margin: 4rem 0; background: #F9FAFB; padding: 2.5rem; border-radius: 24px;">
            <h2 style="font-size: 1.5rem; font-weight: 800; margin-bottom: 2rem; text-align: center;">
                ${car.brand()} ${car.model()} Owners Also Researched:
            </h2>
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 1.5rem;">
                @for(var related : relatedFaults)
                <a href="/verdict/${car.brand().toLowerCase().replaceAll("[^a-z0-9]", "")}/${car.model().toLowerCase().replaceAll("[^a-z0-9]", "")}/${related.component().toLowerCase().replace(" ", "-").replaceAll("[^a-z0-9-]", "")}" 
                   style="text-decoration: none; background: #fff; padding: 1.5rem; border-radius: 16px; border: 1px solid #E5E7EB; transition: all 0.2s; display: block;">
                    <h3 style="font-size: 1.1rem; font-weight: 700; color: #1F2937; margin-bottom: 0.5rem;">
                        ${related.component()}
                    </h3>
                    <div style="display: flex; justify-content: space-between; align-items: center;">
                        <span style="font-size: 0.9rem; color: #6B7280; font-weight: 600;">Avg. Repair Cost</span>
                        <span style="font-size: 1.3rem; font-weight: 800; color: #EF4444;">$${String.format("%,.0f", related.repairCost())}</span>
                    </div>
                </a>
                @endfor
            </div>
        </div>
        @endif

        <!-- 5. Visible FAQ -->
        <div style="margin-bottom: 3rem;">
            <h2 style="font-size: 2rem; font-weight: 800; margin-bottom: 2rem;">Frequently Asked Questions</h2>
            
            <div style="margin-bottom: 2rem;">
                <h3 style="font-size: 1.3rem; font-weight: 700; margin-bottom: 0.5rem;">Is it worth fixing the ${fault.component()}?</h3>
                <p style="color: #555; line-height: 1.6;">
                    It depends significantly on your vehicle's mileage. If your ${car.model()} has over 120,000 miles, a $${String.format("%,.0f", fault.repairCost())} repair might not be recoverable in resale value.
                </p>
            </div>
            
            <div style="margin-bottom: 2rem;">
                <h3 style="font-size: 1.3rem; font-weight: 700; margin-bottom: 0.5rem;">How much does it cost to fix?</h3>
                <p style="color: #555; line-height: 1.6;">
                    The average market rate for a ${fault.component()} replacement on a ${car.brand()} ${car.model()} is approximately <strong>$${String.format("%,.0f", fault.repairCost())}</strong>.
                </p>
            </div>
        </div>

    </div>
`)
