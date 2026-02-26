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
@param String leadUrlInline
@param String leadUrlSticky
@param List<Fault> relatedFaults
@param List<Breadcrumb> breadcrumbs
@param String ogImage = null

@template.layout(
    title = car.brand() + " " + car.model() + " " + fault.component() + " Repair ($" + String.format("%,.0f", fault.repairCost()) + "): Fix or Sell? [2026 Data]",
    description = metaDescription,
    canonical = canonicalUrl,
    schemaJson = schemaJson,
    ogImage = ogImage,
    content = @`
    <div class="pseo-container" style="max-width: 900px; margin: 0 auto; padding: 4rem 1rem; width: 100%;">
        
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

        <header style="text-align: center; margin-bottom: 3rem;">
            <div style="display: inline-flex; align-items: center; gap: 0.5rem; background: #f0fdf4; border: 1px solid #bbf7d0; padding: 0.5rem 1rem; border-radius: 99px; color: #166534; font-size: 0.75rem; font-weight: 700; margin-bottom: 1.5rem;">
                <span style="font-size: 0.9rem;">✓</span> DATA-DRIVEN ANALYSIS APPLIED
            </div>
            <div style="font-size: 0.8rem; color: #d91b1b; font-weight: 700; text-transform: uppercase; letter-spacing: 1.5px; margin-bottom: 0.75rem;">
                Market Analysis for ${car.brand()} Owners
            </div>
            <h1 class="hero-title" style="font-weight: 800; line-height: 1.1; margin-bottom: 2rem; letter-spacing: -0.02em;">
                $unsafe{headline}
            </h1>
            <div style="font-size: 0.9rem; color: #64748b; margin-bottom: 2rem; display: flex; justify-content: center; gap: 1rem; flex-wrap: wrap; font-weight: 500;">
                <span>📊 Data Engine by AutoMoneyPit</span>
                <span style="opacity: 0.5;">|</span>
                <span>🔍 Methodology: Algorithm-driven</span>
                <span style="opacity: 0.5;">|</span>
                <span>🔄 Updated: 2026</span>
            </div>
            
            <div class="repair-card" style="display: inline-flex; flex-direction: column; align-items: center; background: #fff; border: 2px solid #000; padding: 1.5rem 2.5rem; border-radius: 16px; box-shadow: 6px 6px 0px #000;">
                <span style="font-size: 0.85rem; color: #666; font-weight: 600; text-transform: uppercase; letter-spacing: 0.05em;">Estimated Repair Cost</span>
                <span style="font-size: 3.5rem; font-weight: 900; color: #000; line-height: 1; margin: 0.5rem 0;">$${String.format("%,.0f", fault.repairCost())}</span>
                <span style="font-size: 0.65rem; color: #999; font-weight: 600;">(Est. National Avg.)</span>
            </div>
        </header>

        <!-- TL;DR AEO Summary -->
        <div class="tldr-summary" style="background: #eef2ff; border-left: 4px solid #4f46e5; padding: 1.5rem; border-radius: 8px; margin-bottom: 3rem;">
            <h2 style="font-size: 1.1rem; font-weight: 800; color: #312e81; margin-bottom: 0.5rem; display: flex; align-items: center; gap: 0.5rem;">
                <span style="font-size: 1.25rem;">⚡</span> AI Diagnostic Summary
            </h2>
            !{
                double ratio = fault.repairCost() / (double) details.market().jan2026AvgPrice();
                String recommendation;
                if (ratio > 0.5) {
                    recommendation = "This repair represents " + String.format("%.0f", ratio * 100) + "% of the car's current estimated value. Data indicates proceeding with this repair carries a high risk of financial regret. We strongly recommend considering a sale or trade-in.";
                } else if (ratio > 0.25) {
                    recommendation = "Costing around " + String.format("%.0f", ratio * 100) + "% of the vehicle's residual value, this is a borderline case. Evaluate if the car has other impending issues before committing to this repair.";
                } else {
                    recommendation = "At merely " + String.format("%.0f", ratio * 100) + "% of the ongoing market value, repairing the " + fault.component() + " is generally a financially sound decision to preserve your equity.";
                }
            }
            <p style="font-size: 1rem; color: #3730a3; line-height: 1.6; margin: 0;">
                The estimated cost to repair the <strong>${fault.component()}</strong> on a <strong>${car.brand()} ${car.model()}</strong> is approximately <strong>$${String.format("%,.0f", fault.repairCost())}</strong>. 
                With the vehicle's current market value estimated at <strong>$${String.format("%,d", details.market().jan2026AvgPrice())}</strong>, ${recommendation}
            </p>
        </div>

        <style>
            .hero-title { font-size: 3.5rem; }
            @media (max-width: 768px) {
                .hero-title { font-size: 2.25rem; }
                .repair-card { padding: 1.25rem 2rem !important; transform: scale(0.9); }
                .repair-card span:nth-child(2) { font-size: 2.75rem !important; }
            }
        </style>

        <!-- Market Context (dataset-driven, no external pulse) -->

        <!-- 2. Vehicle Financial Profile (New Section) -->
        @if(details != null)
        <div class="profile-section" style="background: #fff; border: 1px solid #e5e7eb; border-radius: 24px; padding: 2rem; margin-bottom: 4rem; box-shadow: 0 4px 20px rgba(0,0,0,0.04);">
            <h2 style="font-size: 1.25rem; font-weight: 700; margin-bottom: 2rem; display: flex; align-items: center; gap: 0.5rem;">
                 📊 ${car.brand()} ${car.model()} Profile (Jan 2026 Data)
            </h2>
            
            <!-- Stats Row -->
            <div class="profile-stats">
                <div class="stat-box">
                    <span class="stat-label">Market Value</span>
                    <span class="stat-value green">$${String.format("%,d", details.market().jan2026AvgPrice())}</span>
                </div>
                <div class="stat-box">
                    <span class="stat-label">Expected Lifespan</span>
                    <span class="stat-value">${String.format("%,d", details.reliability().lifespanMiles())} mi</span>
                </div>
                <div class="stat-box">
                    <span class="stat-label">Depreciation Rate</span>
                    <span class="stat-value orange">${String.format("%.0f", details.market().depreciationRate() * 100)}%/yr</span>
                </div>
                <div class="stat-box">
                    <span class="stat-label">Avg. Annual Repair</span>
                    <span class="stat-value red">$${String.format("%,d", details.market().avgAnnualRepairCost())}</span>
                </div>
            </div>
            
            <!-- Maintenance Alert -->
            <div style="margin-top: 1.5rem; padding-top: 1.5rem; border-top: 1px solid #f1f5f9;">
                @if(details.reliability().criticalMilestones() != null && !details.reliability().criticalMilestones().isEmpty())
                <div class="maintenance-alert">
                    <div class="alert-icon">🔧</div>
                    <div class="alert-content">
                        <span class="alert-badge">Upcoming Service</span>
                        <p>At <strong>${String.format("%,d", details.reliability().criticalMilestones().get(0).mileage())} miles</strong>, 
                        budget <strong class="cost">$${String.format("%,d", details.reliability().criticalMilestones().get(0).estCost())}</strong> 
                        for ${details.reliability().criticalMilestones().get(0).description()}.</p>
                    </div>
                </div>
                @else
                <div class="maintenance-alert good">
                    <div class="alert-icon">✅</div>
                    <div class="alert-content">
                        <span class="alert-badge good">All Clear</span>
                        <p>No major scheduled maintenance expected soon.</p>
                    </div>
                </div>
                @endif
            </div>
        </div>
        <style>
            .profile-stats {
                display: grid;
                grid-template-columns: repeat(4, 1fr);
                gap: 1rem;
            }
            .stat-box {
                background: #f8fafc;
                padding: 1.25rem;
                border-radius: 16px;
                text-align: center;
            }
            .stat-label {
                display: block;
                font-size: 0.7rem;
                color: #64748b;
                text-transform: uppercase;
                font-weight: 600;
                letter-spacing: 0.05em;
                margin-bottom: 0.5rem;
            }
            .stat-value {
                font-size: 1.35rem;
                font-weight: 800;
                color: #1e293b;
            }
            .stat-value.green { color: #16a34a; }
            .stat-value.orange { color: #ea580c; }
            .stat-value.red { color: #dc2626; }
            
            .maintenance-alert {
                display: flex;
                gap: 1rem;
                background: #eff6ff;
                padding: 1.25rem;
                border-radius: 16px;
                align-items: flex-start;
            }
            .maintenance-alert.good {
                background: #f0fdf4;
            }
            .alert-icon {
                font-size: 1.5rem;
                flex-shrink: 0;
            }
            .alert-content {
                flex: 1;
            }
            .alert-badge {
                display: inline-block;
                font-size: 0.65rem;
                font-weight: 700;
                text-transform: uppercase;
                letter-spacing: 0.05em;
                background: #3b82f6;
                color: #fff;
                padding: 0.25rem 0.5rem;
                border-radius: 4px;
                margin-bottom: 0.5rem;
            }
            .alert-badge.good {
                background: #22c55e;
            }
            .alert-content p {
                margin: 0;
                font-size: 0.9rem;
                color: #475569;
                line-height: 1.5;
            }
            .alert-content .cost {
                color: #dc2626;
            }
            
            @media (max-width: 768px) {
                .profile-stats { grid-template-columns: repeat(2, 1fr); }
                .profile-section { padding: 1.5rem !important; }
                .stat-value { font-size: 1.1rem; }
            }
        </style>
        @endif

        <!-- Mileage-Based Expert Insight (NEW SECTION) -->
        @if(details != null && details.reliability().mileageLogicText() != null && !details.reliability().mileageLogicText().isEmpty())
        <div class="mileage-insight-section" style="background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%); border-radius: 24px; padding: 2.5rem; margin-bottom: 4rem; color: #fff;">
            <div style="display: flex; align-items: center; gap: 0.75rem; margin-bottom: 2rem;">
                <span style="font-size: 1.5rem;">🎯</span>
                <h2 style="font-size: 1.35rem; font-weight: 800; margin: 0; color: #fff;">
                    ${car.brand()} ${car.model()} Mileage Breakdown
                </h2>
            </div>
            
            <p style="font-size: 0.9rem; color: #94a3b8; margin-bottom: 2rem; line-height: 1.6;">
                Expert insights based on real-world failure patterns. Know what to expect at each milestone.
            </p>
            
            <div class="mileage-timeline" style="display: flex; flex-direction: column; gap: 1rem;">
                @for(var entry : details.reliability().mileageLogicText().entrySet())
                !{
                    int miles = Integer.parseInt(entry.getKey());
                    String colorClass = miles <= 80000 ? "#22c55e" : (miles <= 120000 ? "#eab308" : "#ef4444");
                    String label = miles <= 80000 ? "Safe Zone" : (miles <= 120000 ? "Caution Zone" : "Critical Zone");
                }
                <div style="background: rgba(255,255,255,0.05); border-radius: 16px; padding: 1.5rem; border-left: 4px solid ${colorClass};">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.75rem;">
                        <span style="font-size: 1.25rem; font-weight: 800; color: #fff;">${String.format("%,d", miles)} Miles</span>
                        <span style="font-size: 0.7rem; font-weight: 700; padding: 0.25rem 0.75rem; border-radius: 99px; background: ${colorClass}20; color: ${colorClass}; text-transform: uppercase; letter-spacing: 0.05em;">${label}</span>
                    </div>
                    <p style="font-size: 0.95rem; color: #cbd5e1; line-height: 1.6; margin: 0;">
                        ${entry.getValue()}
                    </p>
                </div>
                @endfor
            </div>
            
            <!-- Failure Probability Badge -->
            @if(fault.occurrenceRate() > 0)
            !{
                String rateColor = fault.occurrenceRate() >= 0.4 ? "#ef4444" : (fault.occurrenceRate() >= 0.25 ? "#eab308" : "#22c55e");
                String rateBg = fault.occurrenceRate() >= 0.4 ? "rgba(239,68,68,0.15)" : (fault.occurrenceRate() >= 0.25 ? "rgba(234,179,8,0.15)" : "rgba(34,197,94,0.15)");
            }
            <div style="margin-top: 2rem; padding-top: 1.5rem; border-top: 1px solid rgba(255,255,255,0.1);">
                <div style="display: flex; align-items: center; gap: 1rem;">
                    <div style="background: ${rateBg}; padding: 1rem 1.5rem; border-radius: 12px; text-align: center;">
                        <span style="font-size: 2rem; font-weight: 900; color: ${rateColor};">${String.format("%.0f", fault.occurrenceRate() * 100)}%</span>
                        <span style="display: block; font-size: 0.7rem; color: #94a3b8; text-transform: uppercase; letter-spacing: 0.05em; margin-top: 0.25rem;">Failure Rate</span>
                    </div>
                    <div style="flex: 1;">
                        <p style="font-size: 0.9rem; color: #cbd5e1; margin: 0; line-height: 1.5;">
                            <strong style="color: #fff;">${String.format("%.0f", fault.occurrenceRate() * 100)}%</strong> of ${car.brand()} ${car.model()} owners report 
                            <strong style="color: #fff;">${fault.component()}</strong> issues by ${String.format("%,d", details.reliability().lifespanMiles() / 2)} miles.
                        </p>
                    </div>
                </div>
            </div>
            @endif
        </div>
        
        <style>
            .mileage-timeline > div:hover { background: rgba(255,255,255,0.08); transform: translateX(4px); transition: all 0.2s ease; }
            @media (max-width: 768px) { .mileage-insight-section { padding: 1.5rem !important; } }
        </style>
        @endif

        <!-- Common Trouble Spots Section (NEW) -->
        @if(details != null && details.reliability().commonTroubleSpots() != null && !details.reliability().commonTroubleSpots().isEmpty())
        <div class="trouble-spots-section" style="background: #fff; border: 1px solid #e5e7eb; border-radius: 24px; padding: 2rem; margin-bottom: 4rem;">
            <div style="display: flex; align-items: center; gap: 0.75rem; margin-bottom: 1.5rem;">
                <span style="font-size: 1.5rem;">🔍</span>
                <h2 style="font-size: 1.25rem; font-weight: 800; margin: 0; color: #1e293b;">
                    Known Trouble Spots: ${car.brand()} ${car.model()}
                </h2>
            </div>
            
            <p style="font-size: 0.9rem; color: #64748b; margin-bottom: 1.5rem; line-height: 1.6;">
                Based on owner reports and repair data, these are the most common problem areas for the ${car.brand()} ${car.model()} (${car.generation()}).
            </p>
            
            <div class="trouble-spots-grid" style="display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 1rem;">
                @for(String spot : details.reliability().commonTroubleSpots())
                <div class="trouble-spot-item" style="background: #f8fafc; padding: 1rem 1.25rem; border-radius: 12px; border-left: 4px solid #f59e0b; display: flex; align-items: center; gap: 0.75rem;">
                    <span style="font-size: 1.25rem;">⚠️</span>
                    <span style="font-weight: 600; color: #1e293b; font-size: 0.9rem;">${spot}</span>
                </div>
                @endfor
            </div>
            
            <p style="font-size: 0.8rem; color: #94a3b8; margin-top: 1.5rem; line-height: 1.5;">
                💡 <strong>Pro Tip:</strong> When inspecting a ${car.model()}, pay special attention to these areas. 
                Always get a pre-purchase inspection that specifically checks these known trouble spots.
            </p>
        </div>
        @endif

        <!-- 3. The Agitation (Symptoms & Risk) -->
        <div class="agitation-grid" style="display: grid; grid-template-columns: 1fr 1fr; gap: 2rem; margin-bottom: 4rem;">
            <!-- Symptoms Card -->
            <div style="background: #fff; padding: 2rem; border-radius: 12px; border: 1px solid #eee;">
                <h3 style="font-size: 1.1rem; font-weight: 700; margin-bottom: 1rem; display: flex; align-items: center;">
                    <span style="background: #FFF4F4; color: #D91B1B; width: 32px; height: 32px; display: inline-flex; align-items: center; justify-content: center; border-radius: 50%; margin-right: 0.8rem; font-size: 1rem;">⚠</span> 
                    Warning Signs
                </h3>
                <p style="font-size: 1rem; color: #444; line-height: 1.6;">
                    ${fault.symptoms()}
                </p>
            </div>

            <!-- Risk Card -->
            <div style="background: #F8F9FA; padding: 2rem; border-radius: 12px; border: 1px solid #eee;">
                <h3 style="font-size: 1.1rem; font-weight: 700; margin-bottom: 1rem; display: flex; align-items: center;">
                    <span style="background: #E8F0FE; color: #1967D2; width: 32px; height: 32px; display: inline-flex; align-items: center; justify-content: center; border-radius: 50%; margin-right: 0.8rem; font-size: 1rem;">ℹ</span> 
                    Market Implication
                </h3>
                <p style="font-size: 1rem; color: #444; line-height: 1.6; font-style: italic;">
                    "${fault.verdictImplication()}"
                </p>
            </div>
        </div>
        <style>
            @media (max-width: 768px) {
                .agitation-grid { grid-template-columns: 1fr !important; gap: 1rem !important; }
            }
        </style>

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
        <div style="margin-bottom: 4rem;">
             <h2 class="section-title" style="font-weight: 800; margin-bottom: 1.5rem;">The "Sunk Cost" Trap</h2>
             <p style="font-size: 1.05rem; color: #444; line-height: 1.6; margin-bottom: 1.5rem;">
                Many ${car.brand()} owners fall into the trap of thinking, "I've already spent so much maintenance, I have to keep it." This is financially dangerous.
             </p>
             <p style="font-size: 1.05rem; color: #444; line-height: 1.6;">
                AutoMoneyPit was founded on a simple premise: vehicle repair decisions should be informed by data-driven logic and financial frameworks, not just emotion.
                Our data suggests that if a repair costs more than 50% of your vehicle's current estimated market value, or if your total monthly repair average exceeds a new car payment, it may be time to exit.
                Always consult with a certified mechanic and financial advisor for personalized advice.
             </p>
        </div>

        <!-- Visual Decision Flowchart -->
        <div class="decision-flowchart">
            <div class="flow-header">
                <span class="flow-icon">🤔</span>
                <div>
                    <h2>Should You Repair or Sell?</h2>
                    <p>A common decision framework to compare repair burden vs. vehicle value</p>
                </div>
            </div>
            
            <!-- Question 1 -->
            <div class="flow-question">
                <div class="q-number">1</div>
                <div class="q-content">
                    <p class="q-text">Is this repair a <strong>significant portion</strong> of your car's value?</p>
                    <p class="q-sub">Your car: ~$${String.format("%,d", details.market().jan2026AvgPrice())} · Repair: $${String.format("%,.0f", fault.repairCost())} · <em>Compare the ratio</em></p>
                </div>
            </div>
            
            <div class="flow-answers">
                <div class="flow-answer sell">
                    <span class="answer-label">YES</span>
                    <span class="answer-result">Consider Selling</span>
                    <p>Repair exceeds financial logic</p>
                </div>
                <div class="flow-answer keep">
                    <span class="answer-label">NO</span>
                    <span class="answer-result">Proceed to Q2 ↓</span>
                </div>
            </div>
            
            <!-- Question 2 -->
            <div class="flow-question q2">
                <div class="q-number">2</div>
                <div class="q-content">
                    <p class="q-text">Will your <strong>annual repair costs</strong> exceed what you'd spend on a replacement vehicle?</p>
                    <p class="q-sub">Factor in both purchase payments and insurance changes</p>
                </div>
            </div>
            
            <div class="flow-final">
                <div class="final-box sell">
                    <span>YES</span>
                    <strong>🚗 Time to Sell</strong>
                </div>
                <div class="final-box keep">
                    <span>NO</span>
                    <strong>✅ Repair & Keep</strong>
                </div>
            </div>
            
            <!-- Trust Signals (Factual) -->
            <div class="flow-social fact-based">
                <span>📊 Analysis based on our curated dataset of repair costs, reliability scores, and market values</span>
                <span>🔍 Covering multiple car models and documented fault patterns</span>
                <span>⚙️ Estimates vary by trim, condition, and location</span>
            </div>
        </div>
        
        <style>
            .decision-flowchart {
                background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
                border-radius: 24px;
                padding: 2.5rem;
                margin-bottom: 4rem;
                border: 1px solid #e2e8f0;
                box-shadow: 0 8px 30px rgba(0,0,0,0.04);
            }
            .flow-header {
                display: flex;
                gap: 1rem;
                align-items: center;
                margin-bottom: 2rem;
            }
            .flow-icon {
                font-size: 2.5rem;
            }
            .flow-header h2 {
                font-size: 1.5rem;
                font-weight: 800;
                color: #1e293b;
                margin: 0 0 0.25rem 0;
            }
            .flow-header p {
                font-size: 0.9rem;
                color: #64748b;
                margin: 0;
            }
            
            .flow-question {
                display: flex;
                gap: 1rem;
                background: #f1f5f9;
                padding: 1.5rem;
                border-radius: 20px;
                margin-bottom: 1rem;
                align-items: center;
            }
            .flow-question.q2 {
                margin-top: 1.5rem;
            }
            .q-number {
                width: 48px;
                height: 48px;
                background: linear-gradient(135deg, #3b82f6, #1d4ed8);
                color: #fff;
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 1.25rem;
                font-weight: 800;
                flex-shrink: 0;
            }
            .q-text {
                font-size: 1.1rem;
                font-weight: 600;
                color: #1e293b;
                margin: 0 0 0.25rem 0;
            }
            .q-sub {
                font-size: 0.85rem;
                color: #64748b;
                margin: 0;
            }
            
            .flow-answers {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 1rem;
            }
            .flow-answer {
                padding: 1.25rem;
                border-radius: 16px;
                text-align: center;
            }
            .flow-answer.sell {
                background: linear-gradient(135deg, #fef2f2, #fee2e2);
                border: 1px solid #fecaca;
            }
            .flow-answer.keep {
                background: linear-gradient(135deg, #f0fdf4, #dcfce7);
                border: 1px solid #bbf7d0;
            }
            .answer-label {
                display: inline-block;
                font-size: 0.7rem;
                font-weight: 800;
                padding: 0.25rem 0.75rem;
                border-radius: 99px;
                margin-bottom: 0.5rem;
            }
            .flow-answer.sell .answer-label {
                background: #ef4444;
                color: #fff;
            }
            .flow-answer.keep .answer-label {
                background: #22c55e;
                color: #fff;
            }
            .answer-result {
                display: block;
                font-weight: 700;
                font-size: 1rem;
                margin-bottom: 0.25rem;
            }
            .flow-answer.sell .answer-result { color: #b91c1c; }
            .flow-answer.keep .answer-result { color: #166534; }
            .flow-answer p {
                margin: 0;
                font-size: 0.8rem;
                color: #64748b;
            }
            
            .flow-final {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 1rem;
                margin-top: 1rem;
            }
            .final-box {
                padding: 1rem;
                border-radius: 12px;
                text-align: center;
            }
            .final-box.sell {
                background: #ef4444;
                color: #fff;
            }
            .final-box.keep {
                background: #22c55e;
                color: #fff;
            }
            .final-box span {
                display: block;
                font-size: 0.7rem;
                opacity: 0.8;
                margin-bottom: 0.25rem;
            }
            .final-box strong {
                font-size: 0.95rem;
            }
            
            .flow-social {
                margin-top: 2rem;
                padding-top: 1.5rem;
                border-top: 1px solid #e2e8f0;
                display: flex;
                justify-content: center;
                gap: 2rem;
                flex-wrap: wrap;
                font-size: 0.85rem;
                color: #64748b;
            }
            .flow-social .live {
                color: #22c55e;
            }
            
            @media (max-width: 768px) {
                .decision-flowchart { padding: 1.5rem; }
                .flow-answers, .flow-final { grid-template-columns: 1fr; }
                .flow-question { flex-direction: column; text-align: center; }
                .flow-header { flex-direction: column; text-align: center; }
            }
        </style>
        <style>
            .section-title { font-size: 2rem; }
            @media (max-width: 768px) {
                .section-title { font-size: 1.5rem; }
            }
        </style>

        <!-- 4. The Solution (CTA) -->
        <div class="cta-banner" style="background: #1a1a1a; color: #fff; padding: 4rem 2rem; border-radius: 24px; text-align: center; position: relative; overflow: hidden; margin-bottom: 4rem;">
            <div style="position: relative; z-index: 2;">
                <h2 style="font-weight: 800; margin-bottom: 1rem; line-height: 1.2;">Is your ${car.model()} an Asset or a Liability?</h2>
                <p style="font-size: 1.1rem; color: #aaa; margin-bottom: 2.5rem; max-width: 600px; margin-left: auto; margin-right: auto; line-height: 1.5;">
                    Verify how this estimated $${String.format("%,.0f", fault.repairCost())} repair bill impacts your core equity. Consult our partners to confirm actual market viability.
                </p>
                
                <a id="inlineCta" href="${leadUrlInline}" class="btn-cinematic" style="display: inline-block; text-decoration: none; font-size: 1.1rem; padding: 1.2rem 2.5rem; background: #2563EB; color: #ffffff; border-radius: 12px; font-weight: 700; box-shadow: 0 10px 20px rgba(37, 99, 235, 0.3);">
                    Get Expert Assessment <span>→</span>
                </a>
            </div>
        </div>
        <style>
            .cta-banner h2 { font-size: 2.25rem; }
            @media (max-width: 768px) {
                .cta-banner { padding: 3rem 1.5rem !important; }
                .cta-banner h2 { font-size: 1.75rem; }
                .cta-banner p { font-size: 1rem; }
            }
        </style>

        <!-- Related Problems Section (Internal Linking) -->
        @if(relatedFaults != null && !relatedFaults.isEmpty())
        <div class="related-section" style="margin: 4rem 0; background: #F9FAFB; padding: 2.5rem; border-radius: 24px;">
            <h2 style="font-size: 1.35rem; font-weight: 800; margin-bottom: 2rem; text-align: center; color: #1f2937;">
                ${car.brand()} ${car.model()} Owners Also Researched:
            </h2>
            <div class="related-grid">
                @for(var related : relatedFaults)
                <a href="/verdict/${car.brand().toLowerCase().replaceAll("[^a-z0-9]", "")}/${car.model().toLowerCase().replaceAll("[^a-z0-9]", "")}/${related.component().toLowerCase().replace(" ", "-").replaceAll("[^a-z0-9-]", "")}" 
                   class="related-card">
                    <h3 style="font-size: 1rem; font-weight: 700; color: #1F2937; margin-bottom: 0.75rem; line-height: 1.3;">
                        ${related.component()}
                    </h3>
                    <div style="display: flex; flex-direction: column; gap: 0.25rem;">
                        <span style="font-size: 0.8rem; color: #6B7280; font-weight: 600;">Avg. Repair Cost</span>
                        <span style="font-size: 1.5rem; font-weight: 800; color: #EF4444;">$${String.format("%,.0f", related.repairCost())}</span>
                    </div>
                </a>
                @endfor
                
                <!-- CTA card when few related items (fills empty space) -->
                @if(relatedFaults.size() == 1)
                <a href="/?brand=${car.brand().replace(" ", "+")}&model=${car.model().replace(" ", "+")}" class="related-card cta-card">
                    <h3 style="font-size: 1rem; font-weight: 700; color: #3b82f6; margin-bottom: 0.75rem; line-height: 1.3;">
                        🔍 Analyze Another Repair
                    </h3>
                    <p style="font-size: 0.85rem; color: #64748b; margin: 0;">Get a personalized verdict for any repair quote on your ${car.model()}</p>
                </a>
                @endif
            </div>
            <p style="font-size: 0.8rem; color: #6B7280; text-align: center; margin-top: 2rem; line-height: 1.6; max-width: 500px; margin-left: auto; margin-right: auto;">
                Data is sourced from available market frameworks. Our estimates reflect 
                dynamic depreciation logic, component failure probabilities, and labor rate averages for your specific query.
                Always cross-check with a local certified mechanic before making absolute financial decisions.
            </p>
        </div>
        <style>
            .related-grid {
                display: grid;
                grid-template-columns: repeat(2, 1fr);
                gap: 1rem;
            }
            .related-card {
                text-decoration: none;
                background: #fff;
                padding: 1.5rem;
                border-radius: 16px;
                border: 1px solid #E5E7EB;
                transition: all 0.2s;
                display: flex;
                flex-direction: column;
            }
            .related-card:hover {
                border-color: #3b82f6;
                box-shadow: 0 4px 12px rgba(59, 130, 246, 0.15);
                transform: translateY(-2px);
            }
            .related-card.cta-card {
                background: linear-gradient(135deg, #eff6ff, #dbeafe);
                border: 1px dashed #93c5fd;
            }
            .related-card.cta-card:hover {
                border-style: solid;
                background: linear-gradient(135deg, #dbeafe, #bfdbfe);
            }
            @media (max-width: 768px) {
                .related-section { padding: 1.5rem !important; }
                .related-grid { grid-template-columns: 1fr !important; gap: 0.75rem; }
                .related-card { padding: 1.25rem !important; }
            }
        </style>
        @endif

        <!-- 5. Visible FAQ -->
        <div style="margin-bottom: 3rem;">
            <h2 style="font-size: 2rem; font-weight: 800; margin-bottom: 2rem;">Frequently Asked Questions</h2>
            
            <div style="margin-bottom: 2rem;">
                <h3 style="font-size: 1.3rem; font-weight: 700; margin-bottom: 0.5rem;">Is it worth fixing the ${fault.component()} on a ${car.brand()} ${car.model()}?</h3>
                <p style="color: #555; line-height: 1.6;">
                    Whether it's worth fixing the <strong>${fault.component()}</strong> on your <strong>${car.brand()} ${car.model()}</strong> depends on your mileage, overall condition, and market value. Compare the estimated $${String.format("%,.0f", fault.repairCost())} repair cost against the vehicle's ~$${String.format("%,d", details.market().jan2026AvgPrice())} value to evaluate the best option for your situation.
                </p>
            </div>
            
            <div style="margin-bottom: 2rem;">
                <h3 style="font-size: 1.3rem; font-weight: 700; margin-bottom: 0.5rem;">How much does it cost to fix the ${fault.component()} on a ${car.brand()} ${car.model()}?</h3>
                <p style="color: #555; line-height: 1.6;">
                    The average cost to fix the <strong>${fault.component()}</strong> on a <strong>${car.brand()} ${car.model()}</strong> is approximately <strong>$${String.format("%,.0f", fault.repairCost())}</strong>. This estimate includes both parts and labor based on national averages.
                </p>
            </div>
        </div>

        <!-- Mileage Analysis Links -->
        <div style="margin-bottom: 3rem; background: linear-gradient(135deg, #f0f9ff, #e0f2fe); padding: 2rem; border-radius: 20px; border: 1px solid #bae6fd;">
            <h3 style="font-size: 1.1rem; font-weight: 700; margin-bottom: 1.25rem; color: #0369a1; display: flex; align-items: center; gap: 0.5rem;">
                <span>📍</span> Mileage-Based Analysis
            </h3>
            <p style="font-size: 0.9rem; color: #64748b; margin-bottom: 1.25rem; line-height: 1.6;">
                Your car's mileage significantly impacts this repair decision. See what to expect at different stages:
            </p>
            <div style="display: flex; flex-wrap: wrap; gap: 0.75rem;">
                <a href="/verdict/${car.brand().toLowerCase().replaceAll("[^a-z0-9]", "")}/${car.model().toLowerCase().replaceAll("[^a-z0-9]", "")}/75000-miles" 
                   style="text-decoration: none; background: #fff; padding: 0.6rem 1rem; border-radius: 10px; font-size: 0.85rem; font-weight: 600; color: #0369a1; border: 1px solid #bae6fd; transition: all 0.2s;">
                    75K miles
                </a>
                <a href="/verdict/${car.brand().toLowerCase().replaceAll("[^a-z0-9]", "")}/${car.model().toLowerCase().replaceAll("[^a-z0-9]", "")}/100000-miles" 
                   style="text-decoration: none; background: #fff; padding: 0.6rem 1rem; border-radius: 10px; font-size: 0.85rem; font-weight: 600; color: #0369a1; border: 1px solid #bae6fd; transition: all 0.2s;">
                    100K miles
                </a>
                <a href="/verdict/${car.brand().toLowerCase().replaceAll("[^a-z0-9]", "")}/${car.model().toLowerCase().replaceAll("[^a-z0-9]", "")}/150000-miles" 
                   style="text-decoration: none; background: #fff; padding: 0.6rem 1rem; border-radius: 10px; font-size: 0.85rem; font-weight: 600; color: #0369a1; border: 1px solid #bae6fd; transition: all 0.2s;">
                    150K miles
                </a>
                <a href="/verdict/${car.brand().toLowerCase().replaceAll("[^a-z0-9]", "")}/${car.model().toLowerCase().replaceAll("[^a-z0-9]", "")}/200000-miles" 
                   style="text-decoration: none; background: #fff; padding: 0.6rem 1rem; border-radius: 10px; font-size: 0.85rem; font-weight: 600; color: #0369a1; border: 1px solid #bae6fd; transition: all 0.2s;">
                    200K miles
                </a>
            </div>
        </div>

        <!-- Compare Similar Models Section (SEO Internal Linking) -->
        <div style="margin-bottom: 3rem; background: #f8fafc; padding: 2rem; border-radius: 20px; border: 1px solid #e2e8f0;">
            <h3 style="font-size: 1.1rem; font-weight: 700; margin-bottom: 1.25rem; color: #334155; display: flex; align-items: center; gap: 0.5rem;">
                <span>🔄</span> Compare Similar Models
            </h3>
            <p style="font-size: 0.9rem; color: #64748b; margin-bottom: 1.25rem; line-height: 1.6;">
                Considering alternatives? See reliability data for competing models in the same segment:
            </p>
            <div style="display: flex; flex-wrap: wrap; gap: 0.75rem;">
                !{
                    // Define similar models based on vehicle type - inline so no need for controller changes
                    java.util.Map<String, String[]> similarModels = new java.util.HashMap<>();
                    
                    // Trucks
                    similarModels.put("F-150", new String[]{"chevrolet/silverado1500:Silverado 1500", "ram/1500:RAM 1500", "toyota/tundra:Tundra", "gmc/sierra1500:Sierra 1500"});
                    similarModels.put("Silverado 1500", new String[]{"ford/f150:F-150", "ram/1500:RAM 1500", "toyota/tundra:Tundra", "gmc/sierra1500:Sierra 1500"});
                    similarModels.put("1500", new String[]{"ford/f150:F-150", "chevrolet/silverado1500:Silverado 1500", "toyota/tundra:Tundra"});
                    similarModels.put("Tundra", new String[]{"ford/f150:F-150", "chevrolet/silverado1500:Silverado 1500", "ram/1500:RAM 1500"});
                    similarModels.put("Tacoma", new String[]{"chevrolet/colorado:Colorado", "ford/ranger:Ranger", "nissan/frontier:Frontier"});
                    
                    // Mid-size Sedans
                    similarModels.put("Camry", new String[]{"honda/accord:Accord", "nissan/altima:Altima", "mazda/mazda6:Mazda6", "hyundai/sonata:Sonata"});
                    similarModels.put("Accord", new String[]{"toyota/camry:Camry", "nissan/altima:Altima", "mazda/mazda6:Mazda6", "hyundai/sonata:Sonata"});
                    similarModels.put("Altima", new String[]{"toyota/camry:Camry", "honda/accord:Accord", "hyundai/sonata:Sonata", "kia/optima:Optima"});
                    similarModels.put("Sonata", new String[]{"toyota/camry:Camry", "honda/accord:Accord", "kia/optima:K5", "hyundai/elantra:Elantra"});
                    
                    // Compact Sedans
                    similarModels.put("Civic", new String[]{"toyota/corolla:Corolla", "mazda/mazda3:Mazda3", "hyundai/elantra:Elantra", "volkswagen/jetta:Jetta"});
                    similarModels.put("Corolla", new String[]{"honda/civic:Civic", "mazda/mazda3:Mazda3", "hyundai/elantra:Elantra", "nissan/sentra:Sentra"});
                    similarModels.put("Elantra", new String[]{"honda/civic:Civic", "toyota/corolla:Corolla", "kia/forte:Forte", "mazda/mazda3:Mazda3"});
                    
                    // Compact SUVs
                    similarModels.put("CR-V", new String[]{"toyota/rav4:RAV4", "mazda/cx5:CX-5", "nissan/rogue:Rogue", "subaru/forester:Forester"});
                    similarModels.put("RAV4", new String[]{"honda/crv:CR-V", "mazda/cx5:CX-5", "nissan/rogue:Rogue", "subaru/forester:Forester"});
                    similarModels.put("Rogue", new String[]{"toyota/rav4:RAV4", "honda/crv:CR-V", "mazda/cx5:CX-5", "hyundai/tucson:Tucson"});
                    similarModels.put("CX-5", new String[]{"toyota/rav4:RAV4", "honda/crv:CR-V", "subaru/forester:Forester", "hyundai/tucson:Tucson"});
                    similarModels.put("Forester", new String[]{"toyota/rav4:RAV4", "honda/crv:CR-V", "mazda/cx5:CX-5", "subaru/outback:Outback"});
                    similarModels.put("Tucson", new String[]{"hyundai/santafe:Santa Fe", "kia/sportage:Sportage", "honda/crv:CR-V", "toyota/rav4:RAV4"});
                    similarModels.put("Sportage", new String[]{"hyundai/tucson:Tucson", "kia/sorento:Sorento", "honda/crv:CR-V", "mazda/cx5:CX-5"});
                    
                    // Mid-size SUVs
                    similarModels.put("Highlander", new String[]{"honda/pilot:Pilot", "chevrolet/traverse:Traverse", "ford/explorer:Explorer", "mazda/cx9:CX-9"});
                    similarModels.put("Pilot", new String[]{"toyota/highlander:Highlander", "chevrolet/traverse:Traverse", "ford/explorer:Explorer", "acura/mdx:MDX"});
                    similarModels.put("Explorer", new String[]{"toyota/highlander:Highlander", "honda/pilot:Pilot", "chevrolet/traverse:Traverse", "ford/edge:Edge"});
                    similarModels.put("4Runner", new String[]{"toyota/highlander:Highlander", "jeep/grandcherokee:Grand Cherokee", "chevrolet/tahoe:Tahoe", "ford/bronco:Bronco"});
                    similarModels.put("Grand Cherokee", new String[]{"toyota/4runner:4Runner", "ford/explorer:Explorer", "chevrolet/tahoe:Tahoe", "dodge/durango:Durango"});
                    similarModels.put("Palisade", new String[]{"kia/telluride:Telluride", "toyota/highlander:Highlander", "honda/pilot:Pilot", "chevrolet/traverse:Traverse"});
                    similarModels.put("Telluride", new String[]{"hyundai/palisade:Palisade", "toyota/highlander:Highlander", "honda/pilot:Pilot", "ford/explorer:Explorer"});
                    
                    // Luxury
                    similarModels.put("3 Series", new String[]{"mercedes/cclass:C-Class", "audi/a4:A4", "lexus/is:IS", "genesis/g70:G70"});
                    similarModels.put("C-Class", new String[]{"bmw/3series:3 Series", "audi/a4:A4", "lexus/es:ES", "genesis/g70:G70"});
                    similarModels.put("A4", new String[]{"bmw/3series:3 Series", "mercedes/cclass:C-Class", "lexus/is:IS", "volvo/s60:S60"});
                    similarModels.put("RX", new String[]{"bmw/x5:X5", "mercedes/glc:GLC", "audi/q5:Q5", "acura/mdx:MDX"});
                    similarModels.put("X5", new String[]{"mercedes/gle:GLE", "audi/q7:Q7", "lexus/rx:RX", "porsche/cayenne:Cayenne"});
                    
                    // Jeeps
                    similarModels.put("Wrangler", new String[]{"toyota/4runner:4Runner", "ford/bronco:Bronco", "jeep/gladiator:Gladiator"});
                    similarModels.put("Gladiator", new String[]{"toyota/tacoma:Tacoma", "ford/ranger:Ranger", "jeep/wrangler:Wrangler"});
                    
                    // Default for models not in map
                    String[] modelLinks = similarModels.getOrDefault(car.model(), new String[]{});
                }
                @if(modelLinks.length > 0)
                    @for(String link : modelLinks)
                        !{
                            String[] parts = link.split(":");
                            String url = parts[0];
                            String name = parts[1];
                        }
                        <a href="/models/${url}" 
                           style="text-decoration: none; background: #fff; padding: 0.6rem 1rem; border-radius: 10px; font-size: 0.85rem; font-weight: 600; color: #475569; border: 1px solid #e2e8f0; transition: all 0.2s;">
                            ${name}
                        </a>
                    @endfor
                @else
                    <a href="/models/${car.brand().toLowerCase().replaceAll("[^a-z0-9]", "")}" 
                       style="text-decoration: none; background: #fff; padding: 0.6rem 1rem; border-radius: 10px; font-size: 0.85rem; font-weight: 600; color: #475569; border: 1px solid #e2e8f0; transition: all 0.2s;">
                        All ${car.brand()} Models
                    </a>
                @endif
            </div>
        </div>

        <!-- Mobile Sticky CTA (Hidden when inline CTA is visible) -->
        <div id="stickyCta" style="position: fixed; bottom: 0; left: 0; width: 100%; background: #ffffff; border-top: 1px solid #e2e8f0; padding: 1rem; box-shadow: 0 -4px 20px rgba(0,0,0,0.08); display: flex; justify-content: space-between; align-items: center; z-index: 1000; transform: translateY(100%); transition: transform 0.3s ease-in-out;">
            <div>
                <div style="font-weight: 800; color: #1e293b; font-size: 0.95rem;">${car.brand()} ${car.model()}</div>
                <div style="font-size: 0.75rem; color: #64748b; font-weight: 600;">Data-Driven Diagnostic</div>
            </div>
            <a href="${leadUrlSticky}" class="btn-cinematic" style="background: #2563EB; color: #fff; text-decoration: none; padding: 0.75rem 1.25rem; border-radius: 8px; font-weight: 700; font-size: 0.9rem;">
                View Verdict
            </a>
        </div>
        
        <script>
            document.addEventListener("DOMContentLoaded", function() {
                var inlineCta = document.getElementById('inlineCta');
                var stickyCta = document.getElementById('stickyCta');
                
                if (inlineCta && stickyCta && window.IntersectionObserver) {
                    var observer = new IntersectionObserver(function(entries) {
                        if (entries[0].isIntersecting) {
                            stickyCta.style.transform = 'translateY(100%)';
                        } else {
                            stickyCta.style.transform = 'translateY(0)';
                        }
                    }, { rootMargin: "0px" });
                    observer.observe(inlineCta);
                } else if (stickyCta) {
                    stickyCta.style.transform = 'translateY(0)';
                }
            });
        </script>
        
    </div>
`)
