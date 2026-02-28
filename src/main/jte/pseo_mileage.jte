@import com.carmoneypit.engine.service.CarDataService.CarModel
@import com.carmoneypit.engine.service.CarDataService.ModelReliability
@import com.carmoneypit.engine.service.CarDataService.ModelMarket
@import com.carmoneypit.engine.service.CarDataService.MajorFaults
@import com.carmoneypit.engine.service.CarDataService.Fault
@import com.carmoneypit.engine.service.CarDataService.Milestone
@import com.carmoneypit.engine.web.PSeoController.Breadcrumb
@import java.util.List
@import java.util.Map

@param CarModel car
@param ModelReliability reliability
@param ModelMarket market
@param MajorFaults majorFaults
@param int targetMileage
@param List<Breadcrumb> breadcrumbs
@param String canonicalUrl
@param String metaDescription
@param String schemaJson
@param String leadUrlInline
@param String leadUrlSticky
@param String datasetVersion = "2026-02-24"

!{
    // Calculate lifespan percentage
    final int lifespanMiles = reliability.lifespanMiles();
    final double lifespanPercent = Math.min(100.0, (double) targetMileage / lifespanMiles * 100);
    final int remainingPercent = (int) Math.max(0, 100 - lifespanPercent);
    
    // Determine risk level based on mileage
    final String riskLevel = lifespanPercent < 40 ? "LOW" : (lifespanPercent < 70 ? "MODERATE" : "HIGH");
    final String riskColor = lifespanPercent < 40 ? "#22c55e" : (lifespanPercent < 70 ? "#eab308" : "#ef4444");
    final String riskBgColor = lifespanPercent < 40 ? "#f0fdf4" : (lifespanPercent < 70 ? "#fefce8" : "#fef2f2");
    
    // Generate verdict text based on data - use final variables
    final String verdictText;
    final String verdictAdvice;
    if (lifespanPercent >= 75) {
        verdictText = "Your " + car.brand() + " " + car.model() + " is in the final quarter of its expected lifespan.";
        verdictAdvice = "Major repairs at this stage often exceed the vehicle's remaining value. Consider selling before a catastrophic failure.";
    } else if (lifespanPercent >= 50) {
        verdictText = "Your " + car.model() + " has used over half of its expected lifespan.";
        verdictAdvice = "This is the critical decision zone. Repairs over $2,000 should be carefully evaluated against market value.";
    } else {
        verdictText = "Your " + car.model() + " still has significant life remaining.";
        verdictAdvice = "Most repairs at this stage are worthwhile. Focus on preventive maintenance to maximize longevity.";
    }
    
    // Calculate estimated current value (depreciation based on mileage)
    final double mileageDepreciation = Math.max(0.15, 1.0 - (targetMileage * 0.000003)); // ~3% per 10k miles, floor at 15%
    final int estimatedValue = (int) Math.max(market.commonJunkValue(), market.jan2026AvgPrice() * mileageDepreciation);
    
    // Find high-risk faults that occur around this mileage
    final java.util.List<Fault> upcomingRisks = new java.util.ArrayList<>();
    final java.util.List<Fault> passedRisks = new java.util.ArrayList<>();
    if (majorFaults != null && majorFaults.faults() != null) {
        for (Fault f : majorFaults.faults()) {
            if (f.avgFailureMileage() > 0) {
                if (f.avgFailureMileage() > targetMileage && f.avgFailureMileage() <= targetMileage + 50000) {
                    upcomingRisks.add(f);
                } else if (f.avgFailureMileage() <= targetMileage) {
                    passedRisks.add(f);
                }
            }
        }
    }
    
    // Find relevant mileage advice - use a holder pattern for final
    String tempMileageAdvice = null;
    if (reliability.mileageLogicText() != null) {
        int closestMileage = 0;
        for (String key : reliability.mileageLogicText().keySet()) {
            int m = Integer.parseInt(key);
            if (m <= targetMileage && m > closestMileage) {
                closestMileage = m;
                tempMileageAdvice = reliability.mileageLogicText().get(key);
            }
        }
    }
    final String mileageAdvice = tempMileageAdvice;
    
    // Get upcoming milestones
    final java.util.List<Milestone> upcomingMilestones = new java.util.ArrayList<>();
    if (reliability.criticalMilestones() != null) {
        for (Milestone m : reliability.criticalMilestones()) {
            if (m.mileage() > targetMileage && m.mileage() <= targetMileage + 50000) {
                upcomingMilestones.add(m);
            }
        }
    }
}

@template.layout(
    title = car.brand() + " " + car.model() + " at " + String.format("%,d", targetMileage) + " Miles: Worth Keeping? [" + datasetVersion + "]",
    description = metaDescription,
    canonical = canonicalUrl,
    schemaJson = schemaJson,
    content = @`
    <div class="pseo-mileage-container">
        
        <!-- Breadcrumbs -->
        <nav class="breadcrumbs">
            @for(int i = 0; i < breadcrumbs.size(); i++)
                @if(i > 0) <span class="separator">/</span> @endif
                @if(breadcrumbs.get(i).url().equals("#"))
                    <span class="current">${breadcrumbs.get(i).label()}</span>
                @else
                    <a href="${breadcrumbs.get(i).url()}">${breadcrumbs.get(i).label()}</a>
                @endif
            @endfor
        </nav>

        <!-- Hero Section -->
        <header class="hero">
            <div class="hero-badge">
                <span class="badge-icon">📊</span>
                <span>MILEAGE ANALYSIS</span>
            </div>
            
            <h1 class="hero-title">
                Is Your ${car.brand()} ${car.model()} Worth Keeping<br>
                at <span class="highlight">${String.format("%,d", targetMileage)} Miles</span>?
            </h1>
            <div style="font-size: 0.9rem; color: #64748b; margin-bottom: 2rem; display: flex; justify-content: center; gap: 1rem; flex-wrap: wrap; font-weight: 500;">
                <span>📊 Data Engine by AutoMoneyPit</span>
                <span style="opacity: 0.5;">|</span>
                <span>🔍 Methodology: Algorithm-driven</span>
                <span style="opacity: 0.5;">|</span>
                <span>🔄 Dataset: ${datasetVersion}</span>
            </div>
            
            <p class="hero-subtitle">
                Data-driven analysis for ${car.brand()} ${car.model()} (${car.startYear()}-${car.endYear()}) owners.
                We analyzed depreciation curves, failure patterns, and market data to answer your question.
            </p>
        </header>

        <!-- TL;DR AEO Summary -->
        <div class="tldr-summary" style="background: #eef2ff; border-left: 4px solid #4f46e5; padding: 1.5rem; border-radius: 8px; margin-bottom: 3rem;">
            <h2 style="font-size: 1.1rem; font-weight: 800; color: #312e81; margin-bottom: 0.5rem; display: flex; align-items: center; gap: 0.5rem;">
                <span style="font-size: 1.25rem;">⚡</span> Vehicle Analysis Summary
            </h2>
            <p style="font-size: 1rem; color: #3730a3; line-height: 1.6; margin: 0;">
                !{
                    String aeoText;
                    if (lifespanPercent >= 75) {
                        aeoText = "At " + String.format("%,d", targetMileage) + " miles, the " + car.brand() + " " + car.model() + " has reached the final quarter of its expected lifespan. With its current market value depreciated to approximately $" + String.format("%,d", estimatedValue) + ", pouring money into major repairs is mathematically inadvisable. Owners should strongly consider liquidating the asset to prevent further financial bleeding.";
                    } else if (lifespanPercent >= 50) {
                        aeoText = "Currently sitting at " + String.format("%,d", targetMileage) + " miles, this " + car.brand() + " " + car.model() + " retains an estimated market value of $" + String.format("%,d", estimatedValue) + ", having consumed over half its lifespan. This places the vehicle in a critical decision window where any repair bill exceeding 25-30% of its residual value should trigger a serious fix-or-sell evaluation.";
                    } else {
                        aeoText = "With " + String.format("%,d", targetMileage) + " miles logged, the " + car.brand() + " " + car.model() + " still retains significant equity (estimated around $" + String.format("%,d", estimatedValue) + "). Given it has plenty of remaining service life, most standard or preventative maintenance costs are worthwhile investments to protect the vehicle's long-term value.";
                    }
                }
                ${aeoText}
            </p>
        </div>

        <!-- Main Analysis Card -->
        <section class="analysis-card">
            <div class="analysis-header">
                <h2>📈 Lifespan Analysis</h2>
                <div class="risk-badge" style="background: ${riskBgColor}; color: ${riskColor};">
                    ${riskLevel} RISK ZONE
                </div>
            </div>
            
            <!-- Lifespan Meter -->
            <div class="lifespan-meter">
                <div class="meter-labels">
                    <span>0 mi</span>
                    <span class="lifespan-label">${String.format("%,d", lifespanMiles)} mi (Expected Lifespan)</span>
                </div>
                <div class="meter-track">
                    <div class="meter-fill" style="width: ${lifespanPercent}%; background: linear-gradient(90deg, #22c55e 0%, #eab308 50%, #ef4444 100%);"></div>
                    <div class="meter-marker" style="left: ${lifespanPercent}%;">
                        <div class="marker-tooltip">
                            <strong>${String.format("%,d", targetMileage)} mi</strong>
                            <span>${String.format("%.0f", lifespanPercent)}% used</span>
                        </div>
                        <div class="marker-line"></div>
                    </div>
                </div>
                <div class="meter-zones">
                    <div class="zone safe">Safe Zone<br><small>0-40%</small></div>
                    <div class="zone caution">Caution<br><small>40-70%</small></div>
                    <div class="zone danger">High Risk<br><small>70-100%</small></div>
                </div>
            </div>
            
            <!-- Key Stats -->
            <div class="stats-grid">
                <div class="stat-card">
                    <span class="stat-icon">🚗</span>
                    <div class="stat-content">
                        <span class="stat-value">${remainingPercent}%</span>
                        <span class="stat-label">Remaining Life</span>
                    </div>
                </div>
                <div class="stat-card">
                    <span class="stat-icon">💰</span>
                    <div class="stat-content">
                        <span class="stat-value">$${String.format("%,d", estimatedValue)}</span>
                        <span class="stat-label">Est. Market Value</span>
                    </div>
                </div>
                <div class="stat-card">
                    <span class="stat-icon">📉</span>
                    <div class="stat-content">
                        <span class="stat-value">${String.format("%.0f", market.depreciationRate() * 100)}%</span>
                        <span class="stat-label">Annual Depreciation</span>
                    </div>
                </div>
                <div class="stat-card">
                    <span class="stat-icon">🔧</span>
                    <div class="stat-content">
                        <span class="stat-value">$${String.format("%,d", market.avgAnnualRepairCost())}</span>
                        <span class="stat-label">Avg. Annual Repairs</span>
                    </div>
                </div>
            </div>
        </section>

        <!-- Expert Verdict Section -->
        <section class="verdict-section">
            <div class="verdict-icon">🎯</div>
            <h2>The Verdict for ${String.format("%,d", targetMileage)} Miles</h2>
            <p class="verdict-text">${verdictText}</p>
            <div class="verdict-advice">
                <strong>💡 Our Advice:</strong> ${verdictAdvice}
            </div>
            
            @if(mileageAdvice != null)
            <div class="expert-insight">
                <div class="insight-header">
                    <span class="insight-icon">🔍</span>
                    <strong>Model-Specific Insight</strong>
                </div>
                <p>${mileageAdvice}</p>
            </div>
            @endif
        </section>

        <!-- High Risk Components Section -->
        @if(!upcomingRisks.isEmpty() || !passedRisks.isEmpty())
        <section class="risk-section">
            <h2>⚠️ Component Risk Analysis</h2>
            <p class="section-intro">
                Based on failure data from thousands of ${car.brand()} ${car.model()} owners, here's what to watch for at your mileage.
            </p>
            
            @if(!upcomingRisks.isEmpty())
            <div class="risk-category upcoming">
                <h3><span class="category-badge warning">UPCOMING</span> Components Approaching Failure Zone</h3>
                <div class="risk-cards">
                    @for(Fault fault : upcomingRisks)
                    <div class="risk-card">
                        <div class="risk-card-header">
                            <span class="component-name">${fault.component()}</span>
                            <span class="failure-rate" style="color: ${fault.occurrenceRate() >= 0.3 ? "#ef4444" : "#eab308"};">
                                ${String.format("%.0f", fault.occurrenceRate() * 100)}% failure rate
                            </span>
                        </div>
                        <div class="risk-card-body">
                            <div class="mileage-indicator">
                                <span class="indicator-label">Typical Failure:</span>
                                <span class="indicator-value">${String.format("%,d", fault.avgFailureMileage())} miles</span>
                                <span class="indicator-distance">(${String.format("%,d", fault.avgFailureMileage() - targetMileage)} miles away)</span>
                            </div>
                            <p class="symptoms"><strong>Watch for:</strong> ${fault.symptoms()}</p>
                            <p class="repair-cost"><strong>Repair Cost:</strong> $${String.format("%,.0f", fault.repairCost())}</p>
                        </div>
                    </div>
                    @endfor
                </div>
            </div>
            @endif
            
            @if(!passedRisks.isEmpty())
            <div class="risk-category passed">
                <h3><span class="category-badge danger">CHECK NOW</span> Components Past Typical Failure Point</h3>
                <div class="risk-cards">
                    @for(Fault fault : passedRisks)
                    <div class="risk-card critical">
                        <div class="risk-card-header">
                            <span class="component-name">${fault.component()}</span>
                            <span class="failure-alert">⚠️ OVERDUE CHECK</span>
                        </div>
                        <div class="risk-card-body">
                            <div class="mileage-indicator overdue">
                                <span class="indicator-label">Typical Failure:</span>
                                <span class="indicator-value">${String.format("%,d", fault.avgFailureMileage())} miles</span>
                                <span class="indicator-overdue">(${String.format("%,d", targetMileage - fault.avgFailureMileage())} miles PAST typical)</span>
                            </div>
                            <p class="symptoms"><strong>Symptoms:</strong> ${fault.symptoms()}</p>
                            <p class="implication">${fault.verdictImplication()}</p>
                            <p class="repair-cost"><strong>If it fails:</strong> $${String.format("%,.0f", fault.repairCost())}</p>
                        </div>
                    </div>
                    @endfor
                </div>
            </div>
            @endif
        </section>
        @else
        <section class="risk-section empty">
            <h2>⚠️ Component Risk Analysis</h2>
            <div class="no-data-card">
                <span class="no-data-icon">✅</span>
                <h3>No Major Component Risks Identified</h3>
                <p>
                    At ${String.format("%,d", targetMileage)} miles, the ${car.brand()} ${car.model()} doesn't have any widely-reported 
                    critical component failures approaching. Continue with regular maintenance and monitor for early warning signs.
                </p>
            </div>
        </section>
        @endif

        <!-- Upcoming Maintenance Milestones -->
        <section class="milestones-section">
            <h2>🔧 Upcoming Maintenance Milestones</h2>
            
            @if(!upcomingMilestones.isEmpty())
            <div class="milestones-timeline">
                @for(Milestone milestone : upcomingMilestones)
                <div class="milestone-item">
                    <div class="milestone-marker"></div>
                    <div class="milestone-content">
                        <div class="milestone-mileage">${String.format("%,d", milestone.mileage())} miles</div>
                        <div class="milestone-description">${milestone.description()}</div>
                        <div class="milestone-cost">Est. Cost: $${String.format("%,d", milestone.estCost())}</div>
                    </div>
                </div>
                @endfor
            </div>
            <div class="milestone-total">
                <strong>Total Upcoming Maintenance:</strong> 
                $${String.format("%,d", upcomingMilestones.stream().mapToInt(m -> m.estCost()).sum())} in the next 50,000 miles
            </div>
            @else
            <div class="no-data-card">
                <span class="no-data-icon">📋</span>
                <h3>No Major Milestones in the Next 50,000 Miles</h3>
                <p>
                    Based on our data, there are no manufacturer-recommended major services for your ${car.model()} 
                    between ${String.format("%,d", targetMileage)} and ${String.format("%,d", targetMileage + 50000)} miles. 
                    Continue with regular oil changes, tire rotations, and inspections.
                </p>
            </div>
            @endif
        </section>

        <!-- Financial Decision Framework -->
        <section class="decision-framework">
            <h2>💡 The Financial Decision Framework</h2>
            <p class="framework-intro">
                Use this framework to evaluate any repair on your ${car.brand()} ${car.model()} at ${String.format("%,d", targetMileage)} miles.
            </p>
            
            <div class="framework-rules">
                <div class="rule">
                    <div class="rule-icon" style="background: #fef2f2; color: #ef4444;">1</div>
                    <div class="rule-content">
                        <h4>Repair-to-Value Ratio</h4>
                        <p>Compare the repair cost against your vehicle's estimated value of <strong>$${String.format("%,d", estimatedValue)}</strong>. When repair costs represent a large share of the car's worth, selling may be more practical.</p>
                    </div>
                </div>
                <div class="rule">
                    <div class="rule-icon" style="background: #fefce8; color: #eab308;">2</div>
                    <div class="rule-content">
                        <h4>Annual Repair Burden</h4>
                        <p>If total yearly repairs significantly exceed typical maintenance costs of <strong>$${String.format("%,d", market.avgAnnualRepairCost())}</strong>/year, the vehicle may be costing more than it's worth.</p>
                    </div>
                </div>
                <div class="rule">
                    <div class="rule-icon" style="background: #f0fdf4; color: #22c55e;">3</div>
                    <div class="rule-content">
                        <h4>The Junk Value Floor</h4>
                        <p>Your ${car.model()}'s minimum scrap value is ~<strong>$${String.format("%,d", market.commonJunkValue())}</strong>. Never spend more than the difference between market value and junk value.</p>
                    </div>
                </div>
            </div>
        </section>

        <!-- CTA Section -->
        <section class="cta-section">
            <div class="cta-content">
                <h2>Get Your Personalized Verdict</h2>
                <p>
                    Have a specific repair quote? Our free calculator analyzes your exact situation — 
                    your mileage, your repair cost, current market value — and tells you whether to fix or sell.
                </p>
                <a id="inlineCta" href="${leadUrlInline}" class="cta-button">
                    <span>Get My Verdict</span>
                    <span class="arrow">→</span>
                </a>
                <p class="cta-subtext">Free • 30 seconds • Data-driven decision</p>
            </div>
        </section>

        <!-- FAQ Section for SEO -->
        <section class="faq-section">
            <h2>Frequently Asked Questions</h2>
            
            <div class="faq-item">
                <h3>Is a ${car.brand()} ${car.model()} with ${String.format("%,d", targetMileage)} miles reliable?</h3>
                <p>
                    Yes, a <strong>${car.brand()} ${car.model()}</strong> with <strong>${String.format("%,d", targetMileage)} miles</strong> is generally considered 
                    @if(lifespanPercent < 50)
                    reliable, as it retains ${remainingPercent}% of its lifespan.
                    @elseif(lifespanPercent < 75)
                    moderately reliable, though dependent on maintenance. Inspect key components like 
                    ${reliability.commonTroubleSpots() != null && !reliability.commonTroubleSpots().isEmpty() ? reliability.commonTroubleSpots().get(0) : "the transmission and engine"}.
                    @else
                    unreliable, as it has exceeded ${String.format("%.0f", lifespanPercent)}% of its expected lifespan. Expect significant repairs past this point.
                    @endif
                </p>
            </div>
            
            <div class="faq-item">
                <h3>How much is a ${car.brand()} ${car.model()} worth at ${String.format("%,d", targetMileage)} miles?</h3>
                <p>
                    Based on January 2026 market data, a ${car.brand()} ${car.model()} (${car.startYear()}-${car.endYear()}) with 
                    ${String.format("%,d", targetMileage)} miles is worth approximately <strong>$${String.format("%,d", estimatedValue)}</strong>. 
                    This accounts for an average depreciation rate of ${String.format("%.0f", market.depreciationRate() * 100)}% per year and 
                    mileage-based depreciation. The junk/trade-in floor value is around $${String.format("%,d", market.commonJunkValue())}.
                </p>
            </div>
            
            <div class="faq-item">
                <h3>What repairs should I expect on a ${car.model()} at this mileage?</h3>
                <p>
                    @if(reliability.commonTroubleSpots() != null && !reliability.commonTroubleSpots().isEmpty())
                    Common trouble spots for the ${car.brand()} ${car.model()} include: 
                    ${String.join(", ", reliability.commonTroubleSpots())}. 
                    @endif
                    @if(!upcomingMilestones.isEmpty())
                    In the next 50,000 miles, budget approximately $${String.format("%,d", upcomingMilestones.stream().mapToInt(m -> m.estCost()).sum())} for scheduled maintenance.
                    @else
                    No major scheduled maintenance is expected immediately, but always plan for unexpected repairs.
                    @endif
                </p>
            </div>
            
            <div class="faq-item">
                <h3>Should I sell my ${car.model()} or keep repairing it?</h3>
                <p>
                    You should sell your <strong>${car.model()}</strong> if any single repair exceeds 50% of your vehicle's value (<strong>$${String.format("%,d", estimatedValue / 2)}</strong>), or if your annual repair costs exceed a new car payment (~$400/month). Otherwise, keep repairing it.
                </p>
            </div>
        </section>

        <!-- Related Links -->
        <section class="related-section">
            <h3>Related Analysis</h3>
            <div class="related-links">
                @if(targetMileage > 50000)
                <a href="/verdict/${car.brand().toLowerCase().replaceAll("[^a-z0-9]", "")}/${car.model().toLowerCase().replaceAll("[^a-z0-9]", "")}/${targetMileage - 50000}-miles" class="related-link">
                    <span>📉</span> ${car.model()} at ${String.format("%,d", targetMileage - 50000)} miles
                </a>
                @endif
                @if(targetMileage < lifespanMiles - 50000)
                <a href="/verdict/${car.brand().toLowerCase().replaceAll("[^a-z0-9]", "")}/${car.model().toLowerCase().replaceAll("[^a-z0-9]", "")}/${targetMileage + 50000}-miles" class="related-link">
                    <span>📈</span> ${car.model()} at ${String.format("%,d", targetMileage + 50000)} miles
                </a>
                @endif
                <a href="/models/${car.brand().toLowerCase().replaceAll("[^a-z0-9]", "")}/${car.model().toLowerCase().replaceAll("[^a-z0-9]", "")}" class="related-link">
                    <span>🔧</span> All ${car.model()} Common Problems
                </a>
                <a href="/models/${car.brand().toLowerCase().replaceAll("[^a-z0-9]", "")}" class="related-link">
                    <span>🚗</span> All ${car.brand()} Models
                </a>
            </div>
        </section>

        <!-- Compare Similar Models Section -->
        <section class="compare-section">
            <h3>🔄 Compare Similar Models</h3>
            <p class="compare-intro">See how other models in this segment compare at ${String.format("%,d", targetMileage)} miles:</p>
            <div class="compare-links">
                !{
                    java.util.Map<String, String[]> similarModels = new java.util.HashMap<>();
                    similarModels.put("F-150", new String[]{"chevrolet/silverado1500:Silverado 1500", "ram/1500:RAM 1500", "toyota/tundra:Tundra"});
                    similarModels.put("Silverado 1500", new String[]{"ford/f150:F-150", "ram/1500:RAM 1500", "toyota/tundra:Tundra"});
                    similarModels.put("1500", new String[]{"ford/f150:F-150", "chevrolet/silverado1500:Silverado 1500", "toyota/tundra:Tundra"});
                    similarModels.put("Camry", new String[]{"honda/accord:Accord", "nissan/altima:Altima", "hyundai/sonata:Sonata"});
                    similarModels.put("Accord", new String[]{"toyota/camry:Camry", "nissan/altima:Altima", "hyundai/sonata:Sonata"});
                    similarModels.put("Civic", new String[]{"toyota/corolla:Corolla", "mazda/mazda3:Mazda3", "hyundai/elantra:Elantra"});
                    similarModels.put("Corolla", new String[]{"honda/civic:Civic", "mazda/mazda3:Mazda3", "nissan/sentra:Sentra"});
                    similarModels.put("CR-V", new String[]{"toyota/rav4:RAV4", "mazda/cx5:CX-5", "nissan/rogue:Rogue"});
                    similarModels.put("RAV4", new String[]{"honda/crv:CR-V", "mazda/cx5:CX-5", "nissan/rogue:Rogue"});
                    similarModels.put("Highlander", new String[]{"honda/pilot:Pilot", "ford/explorer:Explorer", "chevrolet/traverse:Traverse"});
                    similarModels.put("Pilot", new String[]{"toyota/highlander:Highlander", "ford/explorer:Explorer", "chevrolet/traverse:Traverse"});
                    similarModels.put("4Runner", new String[]{"jeep/grandcherokee:Grand Cherokee", "chevrolet/tahoe:Tahoe", "ford/bronco:Bronco"});
                    similarModels.put("Wrangler", new String[]{"toyota/4runner:4Runner", "ford/bronco:Bronco", "jeep/gladiator:Gladiator"});
                    similarModels.put("Palisade", new String[]{"kia/telluride:Telluride", "toyota/highlander:Highlander", "honda/pilot:Pilot"});
                    similarModels.put("Telluride", new String[]{"hyundai/palisade:Palisade", "toyota/highlander:Highlander", "honda/pilot:Pilot"});
                    String[] modelLinks = similarModels.getOrDefault(car.model(), new String[]{});
                }
                @if(modelLinks.length > 0)
                    @for(String link : modelLinks)
                        !{
                            String[] parts = link.split(":");
                            String url = parts[0];
                            String name = parts[1];
                        }
                        <a href="/verdict/${url}/${targetMileage}-miles" class="compare-link">
                            ${name} at ${String.format("%,d", targetMileage)} mi
                        </a>
                    @endfor
                @else
                    <a href="/models/${car.brand().toLowerCase().replaceAll("[^a-z0-9]", "")}" class="compare-link">
                        Explore ${car.brand()} Models
                    </a>
                @endif
            </div>
        </section>

        <!-- Mobile Sticky CTA (Hidden when inline CTA is visible) -->
        <div id="stickyCta" style="position: fixed; bottom: 0; left: 0; width: 100%; background: #ffffff; border-top: 1px solid #e2e8f0; padding: 1rem; box-shadow: 0 -4px 20px rgba(0,0,0,0.08); display: flex; justify-content: space-between; align-items: center; z-index: 1000; transform: translateY(100%); transition: transform 0.3s ease-in-out;">
            <div>
                <div style="font-weight: 800; color: #1e293b; font-size: 0.95rem;">${car.brand()} ${car.model()}</div>
                <div style="font-size: 0.75rem; color: #64748b; font-weight: 600;">Mileage Valuation</div>
            </div>
            <a href="${leadUrlSticky}" class="cta-button" style="padding: 0.75rem 1.25rem; font-size: 0.9rem;">
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
                    // Fallback
                    stickyCta.style.transform = 'translateY(0)';
                }
            });
        </script>

    </div>

    <style>
        .pseo-mileage-container {
            max-width: 900px;
            margin: 0 auto;
            padding: 2rem 1rem 4rem;
        }

        /* Breadcrumbs */
        .breadcrumbs {
            font-size: 0.85rem;
            color: #64748b;
            margin-bottom: 2rem;
        }
        .breadcrumbs a {
            color: #64748b;
            text-decoration: none;
            transition: color 0.2s;
        }
        .breadcrumbs a:hover { color: #3b82f6; }
        .breadcrumbs .separator { margin: 0 0.5rem; opacity: 0.5; }
        .breadcrumbs .current { color: #1e293b; font-weight: 600; }

        /* Hero */
        .hero {
            text-align: center;
            margin-bottom: 3rem;
        }
        .hero-badge {
            display: inline-flex;
            align-items: center;
            gap: 0.5rem;
            background: #eff6ff;
            color: #3b82f6;
            padding: 0.5rem 1rem;
            border-radius: 99px;
            font-size: 0.75rem;
            font-weight: 700;
            margin-bottom: 1.5rem;
            letter-spacing: 0.05em;
        }
        .badge-icon { font-size: 1rem; }
        .hero-title {
            font-size: clamp(2rem, 5vw, 3rem);
            font-weight: 900;
            color: #0f172a;
            line-height: 1.15;
            margin-bottom: 1.5rem;
        }
        .hero-title .highlight {
            background: linear-gradient(120deg, #3b82f620 0%, #3b82f620 100%);
            background-repeat: no-repeat;
            background-size: 100% 40%;
            background-position: 0 90%;
        }
        .hero-subtitle {
            font-size: 1.1rem;
            color: #64748b;
            line-height: 1.6;
            max-width: 650px;
            margin: 0 auto;
        }

        /* Analysis Card */
        .analysis-card {
            background: #fff;
            border: 1px solid #e2e8f0;
            border-radius: 24px;
            padding: 2rem;
            margin-bottom: 2rem;
            box-shadow: 0 4px 20px rgba(0,0,0,0.05);
        }
        .analysis-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 2rem;
            flex-wrap: wrap;
            gap: 1rem;
        }
        .analysis-header h2 {
            font-size: 1.25rem;
            font-weight: 800;
            margin: 0;
        }
        .risk-badge {
            font-size: 0.75rem;
            font-weight: 700;
            padding: 0.5rem 1rem;
            border-radius: 99px;
            letter-spacing: 0.05em;
        }

        /* Lifespan Meter */
        .lifespan-meter {
            margin-bottom: 2rem;
            position: relative;
        }
        .meter-labels {
            display: flex;
            justify-content: space-between;
            font-size: 0.75rem;
            color: #64748b;
            margin-bottom: 0.5rem;
        }
        .lifespan-label {
            text-align: right;
        }
        .meter-track {
            position: relative;
            height: 24px;
            background: #f1f5f9;
            border-radius: 12px;
            overflow: visible;
        }
        .meter-fill {
            height: 100%;
            border-radius: 12px;
            transition: width 1s ease-out;
        }
        .meter-marker {
            position: absolute;
            top: -8px;
            transform: translateX(-50%);
            z-index: 10;
        }
        .marker-tooltip {
            background: #1e293b;
            color: #fff;
            padding: 0.5rem 0.75rem;
            border-radius: 8px;
            font-size: 0.75rem;
            text-align: center;
            white-space: nowrap;
            margin-bottom: 4px;
        }
        .marker-tooltip strong { display: block; font-size: 0.85rem; }
        .marker-tooltip span { opacity: 0.7; font-size: 0.7rem; }
        .marker-line {
            width: 3px;
            height: 40px;
            background: #1e293b;
            margin: 0 auto;
            border-radius: 2px;
        }
        .meter-zones {
            display: grid;
            grid-template-columns: 40% 30% 30%;
            margin-top: 0.75rem;
            font-size: 0.7rem;
            text-align: center;
        }
        .zone { padding: 0.5rem; border-radius: 8px; }
        .zone.safe { background: #f0fdf4; color: #166534; }
        .zone.caution { background: #fefce8; color: #854d0e; }
        .zone.danger { background: #fef2f2; color: #991b1b; }
        .zone small { display: block; opacity: 0.7; margin-top: 0.25rem; }

        /* Stats Grid */
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 1rem;
        }
        .stat-card {
            display: flex;
            align-items: center;
            gap: 0.75rem;
            padding: 1rem;
            background: #f8fafc;
            border-radius: 12px;
        }
        .stat-icon { font-size: 1.5rem; }
        .stat-value {
            font-size: 1.25rem;
            font-weight: 800;
            color: #0f172a;
            display: block;
        }
        .stat-label {
            font-size: 0.7rem;
            color: #64748b;
            text-transform: uppercase;
            letter-spacing: 0.03em;
        }

        /* Verdict Section */
        .verdict-section {
            background: linear-gradient(135deg, #1e293b 0%, #334155 100%);
            color: #fff;
            border-radius: 24px;
            padding: 2.5rem;
            text-align: center;
            margin-bottom: 2rem;
        }
        .verdict-icon { font-size: 2.5rem; margin-bottom: 1rem; }
        .verdict-section h2 {
            font-size: 1.5rem;
            font-weight: 800;
            margin-bottom: 1rem;
        }
        .verdict-text {
            font-size: 1.1rem;
            color: #cbd5e1;
            margin-bottom: 1.5rem;
            line-height: 1.6;
        }
        .verdict-advice {
            background: rgba(255,255,255,0.1);
            padding: 1.25rem;
            border-radius: 12px;
            font-size: 0.95rem;
            line-height: 1.6;
        }
        .expert-insight {
            margin-top: 1.5rem;
            background: rgba(59, 130, 246, 0.15);
            border: 1px solid rgba(59, 130, 246, 0.3);
            padding: 1.25rem;
            border-radius: 12px;
            text-align: left;
        }
        .insight-header {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            margin-bottom: 0.75rem;
            font-size: 0.85rem;
        }
        .insight-icon { font-size: 1rem; }
        .expert-insight p {
            margin: 0;
            font-size: 0.95rem;
            color: #e2e8f0;
            line-height: 1.6;
        }

        /* Risk Section */
        .risk-section {
            background: #fff;
            border: 1px solid #e2e8f0;
            border-radius: 24px;
            padding: 2rem;
            margin-bottom: 2rem;
        }
        .risk-section h2 {
            font-size: 1.25rem;
            font-weight: 800;
            margin-bottom: 0.75rem;
        }
        .section-intro {
            color: #64748b;
            margin-bottom: 2rem;
            line-height: 1.6;
        }
        .risk-category { margin-bottom: 2rem; }
        .risk-category:last-child { margin-bottom: 0; }
        .risk-category h3 {
            font-size: 1rem;
            font-weight: 700;
            margin-bottom: 1rem;
            display: flex;
            align-items: center;
            gap: 0.75rem;
        }
        .category-badge {
            font-size: 0.65rem;
            padding: 0.35rem 0.75rem;
            border-radius: 99px;
            font-weight: 700;
            letter-spacing: 0.05em;
        }
        .category-badge.warning { background: #fefce8; color: #854d0e; }
        .category-badge.danger { background: #fef2f2; color: #991b1b; }
        
        .risk-cards {
            display: grid;
            gap: 1rem;
        }
        .risk-card {
            border: 1px solid #e2e8f0;
            border-radius: 16px;
            overflow: hidden;
        }
        .risk-card.critical {
            border-color: #fecaca;
            background: #fef2f2;
        }
        .risk-card-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 1rem 1.25rem;
            background: #f8fafc;
            border-bottom: 1px solid #e2e8f0;
        }
        .risk-card.critical .risk-card-header {
            background: #fee2e2;
            border-bottom-color: #fecaca;
        }
        .component-name {
            font-weight: 700;
            color: #0f172a;
        }
        .failure-rate {
            font-size: 0.8rem;
            font-weight: 600;
        }
        .failure-alert {
            font-size: 0.75rem;
            font-weight: 700;
            color: #dc2626;
        }
        .risk-card-body {
            padding: 1.25rem;
        }
        .mileage-indicator {
            display: flex;
            flex-wrap: wrap;
            gap: 0.5rem;
            align-items: center;
            margin-bottom: 1rem;
            font-size: 0.9rem;
        }
        .indicator-label { color: #64748b; }
        .indicator-value { font-weight: 700; color: #0f172a; }
        .indicator-distance { color: #22c55e; font-weight: 600; }
        .indicator-overdue { color: #ef4444; font-weight: 600; }
        .mileage-indicator.overdue .indicator-value { color: #ef4444; }
        .risk-card-body p {
            margin: 0.5rem 0;
            font-size: 0.9rem;
            color: #475569;
            line-height: 1.5;
        }
        .symptoms strong, .repair-cost strong, .implication { color: #0f172a; }
        .repair-cost { font-weight: 600; }

        .no-data-card {
            text-align: center;
            padding: 2rem;
            background: #f8fafc;
            border-radius: 16px;
        }
        .no-data-icon { font-size: 2.5rem; margin-bottom: 1rem; display: block; }
        .no-data-card h3 {
            font-size: 1.1rem;
            font-weight: 700;
            margin-bottom: 0.75rem;
        }
        .no-data-card p {
            color: #64748b;
            line-height: 1.6;
            max-width: 500px;
            margin: 0 auto;
        }

        /* Milestones Section */
        .milestones-section {
            background: #fff;
            border: 1px solid #e2e8f0;
            border-radius: 24px;
            padding: 2rem;
            margin-bottom: 2rem;
        }
        .milestones-section h2 {
            font-size: 1.25rem;
            font-weight: 800;
            margin-bottom: 1.5rem;
        }
        .milestones-timeline {
            position: relative;
            padding-left: 2rem;
            margin-bottom: 1.5rem;
        }
        .milestones-timeline::before {
            content: '';
            position: absolute;
            left: 8px;
            top: 0;
            bottom: 0;
            width: 2px;
            background: #e2e8f0;
        }
        .milestone-item {
            position: relative;
            padding-bottom: 1.5rem;
        }
        .milestone-item:last-child { padding-bottom: 0; }
        .milestone-marker {
            position: absolute;
            left: -2rem;
            top: 0;
            width: 18px;
            height: 18px;
            background: #3b82f6;
            border: 3px solid #fff;
            border-radius: 50%;
            box-shadow: 0 0 0 2px #3b82f6;
        }
        .milestone-content {
            background: #f8fafc;
            padding: 1rem 1.25rem;
            border-radius: 12px;
        }
        .milestone-mileage {
            font-size: 0.8rem;
            font-weight: 700;
            color: #3b82f6;
            margin-bottom: 0.25rem;
        }
        .milestone-description {
            font-weight: 600;
            color: #0f172a;
            margin-bottom: 0.25rem;
        }
        .milestone-cost {
            font-size: 0.85rem;
            color: #64748b;
        }
        .milestone-total {
            background: #eff6ff;
            padding: 1rem 1.25rem;
            border-radius: 12px;
            font-size: 0.95rem;
            color: #1e40af;
        }

        /* Decision Framework */
        .decision-framework {
            background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
            border-radius: 24px;
            padding: 2.5rem;
            margin-bottom: 2rem;
        }
        .decision-framework h2 {
            font-size: 1.35rem;
            font-weight: 800;
            margin-bottom: 0.75rem;
        }
        .framework-intro {
            color: #64748b;
            margin-bottom: 2rem;
            line-height: 1.6;
        }
        .framework-rules {
            display: flex;
            flex-direction: column;
            gap: 1rem;
        }
        .rule {
            display: flex;
            gap: 1rem;
            background: #fff;
            padding: 1.25rem;
            border-radius: 16px;
            align-items: flex-start;
        }
        .rule-icon {
            width: 40px;
            height: 40px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 800;
            font-size: 1.1rem;
            border-radius: 12px;
            flex-shrink: 0;
        }
        .rule-content h4 {
            font-weight: 700;
            margin-bottom: 0.25rem;
            color: #0f172a;
        }
        .rule-content p {
            margin: 0;
            color: #64748b;
            line-height: 1.5;
            font-size: 0.95rem;
        }

        /* CTA Section */
        .cta-section {
            background: #0f172a;
            border-radius: 24px;
            padding: 3rem 2rem;
            text-align: center;
            margin-bottom: 2rem;
        }
        .cta-section h2 {
            font-size: 1.75rem;
            font-weight: 800;
            color: #fff;
            margin-bottom: 1rem;
        }
        .cta-section > p, .cta-content > p {
            color: #94a3b8;
            line-height: 1.6;
            margin-bottom: 2rem;
            max-width: 550px;
            margin-left: auto;
            margin-right: auto;
        }
        .cta-button {
            display: inline-flex;
            align-items: center;
            gap: 0.75rem;
            background: #3b82f6;
            color: #fff;
            padding: 1.25rem 2.5rem;
            border-radius: 16px;
            font-size: 1.1rem;
            font-weight: 700;
            text-decoration: none;
            transition: all 0.3s;
            box-shadow: 0 10px 30px rgba(59, 130, 246, 0.3);
        }
        .cta-button:hover {
            background: #2563eb;
            transform: translateY(-2px);
            box-shadow: 0 15px 40px rgba(59, 130, 246, 0.4);
        }
        .cta-button .arrow {
            font-size: 1.25rem;
            transition: transform 0.3s;
        }
        .cta-button:hover .arrow { transform: translateX(4px); }
        .cta-subtext {
            font-size: 0.85rem !important;
            color: #64748b !important;
            margin-top: 1rem !important;
            margin-bottom: 0 !important;
        }

        /* FAQ Section */
        .faq-section {
            margin-bottom: 2rem;
        }
        .faq-section h2 {
            font-size: 1.5rem;
            font-weight: 800;
            margin-bottom: 1.5rem;
        }
        .faq-item {
            background: #f8fafc;
            padding: 1.5rem;
            border-radius: 16px;
            margin-bottom: 1rem;
        }
        .faq-item h3 {
            font-size: 1.05rem;
            font-weight: 700;
            color: #0f172a;
            margin-bottom: 0.75rem;
        }
        .faq-item p {
            color: #475569;
            line-height: 1.7;
            margin: 0;
        }

        /* Related Section */
        .related-section h3 {
            font-size: 1.1rem;
            font-weight: 700;
            color: #64748b;
            margin-bottom: 1rem;
        }
        .related-links {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 0.75rem;
        }
        .related-link {
            display: flex;
            align-items: center;
            gap: 0.75rem;
            padding: 1rem 1.25rem;
            background: #fff;
            border: 1px solid #e2e8f0;
            border-radius: 12px;
            text-decoration: none;
            color: #0f172a;
            font-weight: 600;
            transition: all 0.2s;
        }
        .related-link:hover {
            border-color: #3b82f6;
            background: #f8fafc;
        }

        /* Compare Section */
        .compare-section {
            background: #f0fdf4;
            padding: 1.5rem;
            border-radius: 16px;
            margin-top: 1.5rem;
            border: 1px solid #bbf7d0;
        }
        .compare-section h3 {
            font-size: 1rem;
            font-weight: 700;
            color: #166534;
            margin-bottom: 0.75rem;
        }
        .compare-intro {
            font-size: 0.85rem;
            color: #64748b;
            margin-bottom: 1rem;
        }
        .compare-links {
            display: flex;
            flex-wrap: wrap;
            gap: 0.5rem;
        }
        .compare-link {
            display: inline-block;
            padding: 0.5rem 1rem;
            background: #fff;
            border: 1px solid #bbf7d0;
            border-radius: 8px;
            text-decoration: none;
            color: #166534;
            font-weight: 600;
            font-size: 0.85rem;
            transition: all 0.2s;
        }
        .compare-link:hover {
            background: #dcfce7;
            border-color: #22c55e;
        }

        /* Responsive */
        @media (max-width: 768px) {
            .stats-grid { grid-template-columns: repeat(2, 1fr); }
            .stat-card { flex-direction: column; text-align: center; }
            .analysis-header { flex-direction: column; align-items: flex-start; }
            .meter-zones { font-size: 0.6rem; }
            .verdict-section { padding: 2rem 1.5rem; }
            .cta-section { padding: 2rem 1.5rem; }
            .decision-framework { padding: 1.5rem; }
            .rule { flex-direction: column; }
        }
        
        @media (max-width: 480px) {
            .stats-grid { grid-template-columns: 1fr; }
            .hero-title { font-size: 1.75rem; }
        }
    </style>
    `
)
