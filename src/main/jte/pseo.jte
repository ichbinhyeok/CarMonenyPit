@import com.carmoneypit.engine.service.CarDataService.MajorFaults
@param String seoTitle
@param String seoDescription
@param Integer prefillYear
@param String prefillBrand
@param String prefillModel
@param Boolean isPseoPage = false
@param String pseoSlug = ""
@param String canonicalUrl = null
@param MajorFaults majorFaults = null

!{var faqSchema = """
{
  "@context": "https://schema.org",
  "@type": "FAQPage",
  "mainEntity": [
    {
      "@type": "Question",
      "name": "How much is my %d %s %s worth?",
      "acceptedAnswer": {
        "@type": "Answer",
        "text": "The value of a %d %s varies significantly based on mileage and condition. Depreciation varies significantly by model popularity, condition, and mileage. Use our calculator or check KBB/NADA for a current estimate specific to your trim and condition."
      }
    },
    {
      "@type": "Question",
      "name": "Should I fix my %d %s %s or sell it?",
      "acceptedAnswer": {
        "@type": "Answer",
        "text": "Whether to fix or sell depends on how the repair cost compares to your vehicle's current value, along with factors like condition, mileage, and local market. Our calculator helps you weigh these factors for your specific situation."
      }
    },
    {
      "@type": "Question",
      "name": "What are common repairs for the %s %s?",
      "acceptedAnswer": {
        "@type": "Answer",
        "text": "Common repairs vary by mileage. Typical issues for this model may include suspension components, brake system wear, and depending on mileage, potential transmission or engine-related maintenance."
      }
    }
  ]
}
""".formatted(prefillYear, prefillBrand, prefillModel, prefillYear, prefillModel, 2026 - prefillYear, prefillYear, prefillBrand, prefillModel, prefillBrand, prefillModel);}
@template.layout(
    title = seoTitle,
    description = seoDescription,
    canonical = canonicalUrl,
    ogTitle = seoTitle,
    ogDescription = seoDescription,
    schemaJson = faqSchema,
    extraStyles = @`
        <style>
            .pseo-hero {
                padding: 3rem 1rem 2rem;
                text-align: center;
                max-width: 800px;
                margin: 0 auto;
            }
            .pseo-badge {
                display: inline-flex;
                align-items: center;
                gap: 0.5rem;
                background: #eff6ff;
                color: #3b82f6;
                padding: 0.5rem 1rem;
                border-radius: 100px;
                font-size: 0.8rem;
                font-weight: 700;
                margin-bottom: 1.5rem;
            }
            .pseo-title {
                font-size: clamp(2rem, 5vw, 3rem);
                font-weight: 900;
                color: #0f172a;
                line-height: 1.1;
                margin-bottom: 1.5rem;
            }
            .pseo-subtitle {
                font-size: 1.1rem;
                color: #64748b;
                line-height: 1.6;
                max-width: 600px;
                margin: 0 auto 2.5rem;
            }
            .pseo-stats {
                display: grid;
                grid-template-columns: repeat(3, 1fr);
                gap: 1rem;
                max-width: 500px;
                margin: 0 auto 3rem;
            }
            .pseo-stat {
                background: #f8fafc;
                padding: 1rem;
                border-radius: 16px;
                text-align: center;
            }
            .pseo-stat-value {
                font-size: 1.5rem;
                font-weight: 800;
                color: #0f172a;
            }
            .pseo-stat-label {
                font-size: 0.75rem;
                color: #64748b;
                margin-top: 0.25rem;
            }
            .pseo-form-container {
                background: #fff;
                border-radius: 24px;
                padding: 2rem;
                box-shadow: 0 10px 40px rgba(0,0,0,0.08);
                border: 1px solid #e2e8f0;
                max-width: 500px;
                margin: 0 auto;
            }
            .pseo-form-title {
                font-size: 1.25rem;
                font-weight: 800;
                text-align: center;
                margin-bottom: 1.5rem;
            }
            .pseo-prefilled {
                background: #f0fdf4;
                border: 1px solid #bbf7d0;
                border-radius: 12px;
                padding: 1rem;
                margin-bottom: 1.5rem;
                display: flex;
                align-items: center;
                gap: 0.75rem;
            }
            .pseo-prefilled-icon {
                font-size: 1.25rem;
            }
            .pseo-prefilled-text {
                font-size: 0.9rem;
                color: #166534;
            }
            .pseo-prefilled-text strong {
                color: #14532d;
            }
            .pseo-input-group {
                margin-bottom: 1.5rem;
            }
            .pseo-input-group label {
                display: block;
                font-size: 0.75rem;
                font-weight: 700;
                text-transform: uppercase;
                letter-spacing: 0.05em;
                color: #64748b;
                margin-bottom: 0.5rem;
            }
            .pseo-input-group select, .pseo-input-group input {
                width: 100%;
                padding: 1rem;
                border: 1px solid #e2e8f0;
                border-radius: 12px;
                font-size: 1rem;
                font-weight: 600;
                font-family: inherit;
                box-sizing: border-box;
            }
            .pseo-input-group select:focus, .pseo-input-group input:focus {
                outline: none;
                border-color: #3b82f6;
                box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
            }
            .pseo-btn {
                width: 100%;
                padding: 1.25rem;
                background: #000;
                color: #fff;
                border: none;
                border-radius: 16px;
                font-size: 1.1rem;
                font-weight: 800;
                cursor: pointer;
                transition: all 0.3s;
                display: flex;
                justify-content: center;
                align-items: center;
                gap: 0.5rem;
            }
            .pseo-btn:hover {
                background: #1a1a1a;
                transform: translateY(-2px);
                box-shadow: 0 10px 30px rgba(0,0,0,0.15);
            }
            .content-section {
                max-width: 700px;
                margin: 4rem auto;
                padding: 0 1rem;
            }
            .content-section h2 {
                font-size: 1.5rem;
                font-weight: 800;
                margin-bottom: 1.5rem;
                color: #0f172a;
            }
            .content-section h3 {
                font-size: 1.25rem;
                font-weight: 700;
                margin: 2rem 0 1rem;
                color: #1e293b;
            }
            .content-section p {
                font-size: 1rem;
                color: #475569;
                line-height: 1.7;
                margin-bottom: 1rem;
            }
            .info-box {
                background: #f8fafc;
                border-left: 4px solid #3b82f6;
                padding: 1.5rem;
                border-radius: 0 12px 12px 0;
                margin: 2rem 0;
            }
            .info-box-title {
                font-weight: 700;
                color: #1e40af;
                margin-bottom: 0.5rem;
            }
            .fault-warning-box {
                background: #fff1f2;
                border: 1px solid #fecdd3;
                border-radius: 12px;
                padding: 1.5rem;
                margin: 1.5rem 0;
            }
            .fault-item {
                border-bottom: 1px solid #e2e8f0;
                padding-bottom: 1rem;
                margin-bottom: 1rem;
            }
            .fault-item:last-child {
                border-bottom: none;
                padding-bottom: 0;
                margin-bottom: 0;
            }
            .fault-name {
                font-weight: 700;
                color: #991b1b;
                display: flex;
                justify-content: space-between;
                align-items: center;
            }
            .fault-cost {
                background: #fff;
                padding: 0.25rem 0.5rem;
                border-radius: 6px;
                font-size: 0.85rem;
                color: #ef4444;
                border: 1px solid #fecdd3;
            }
            .faq-section {
                max-width: 700px;
                margin: 4rem auto;
                padding: 0 1rem;
            }
            .faq-title {
                font-size: 1.5rem;
                font-weight: 800;
                text-align: center;
                margin-bottom: 2rem;
            }
            .faq-item {
                background: #f8fafc;
                border-radius: 16px;
                padding: 1.5rem;
                margin-bottom: 1rem;
            }
            .faq-question {
                font-weight: 700;
                color: #0f172a;
                margin-bottom: 0.75rem;
            }
            .faq-answer {
                font-size: 0.95rem;
                color: #64748b;
                line-height: 1.6;
            }
            .related-links {
                max-width: 700px;
                margin: 3rem auto;
                padding: 0 1rem;
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
                margin-bottom: 0.75rem;
                transition: all 0.2s;
            }
            .related-link:hover {
                border-color: #3b82f6;
                background: #f8fafc;
            }
        </style>
    `,
    content = @`
        <section class="pseo-hero">
            <div class="pseo-badge">
                <span>🔍</span> ${prefillYear} ${prefillBrand} ${prefillModel} Analysis
            </div>
            
            <h1 class="pseo-title">Should You Fix Your ${prefillYear} ${prefillBrand} ${prefillModel}?</h1>
            
            <p class="pseo-subtitle">
                Got a repair quote for your ${prefillModel}? We'll tell you if it makes financial sense 
                to fix it — or if you're better off selling and moving on.
            </p>
            
            <div class="pseo-stats">
                <div class="pseo-stat">
                    <div class="pseo-stat-value">30s</div>
                    <div class="pseo-stat-label">Analysis Time</div>
                </div>
                <div class="pseo-stat">
                    <div class="pseo-stat-value">Free</div>
                    <div class="pseo-stat-label">No Cost</div>
                </div>
                <div class="pseo-stat">
                    <div class="pseo-stat-value">Data</div>
                    <div class="pseo-stat-label">Driven Results</div>
                </div>
            </div>
        </section>

        <section class="pseo-form-container" id="form-container">
            <div class="pseo-form-title">Get Your Verdict</div>
            
            <div class="pseo-prefilled">
                <span class="pseo-prefilled-icon">✅</span>
                <div class="pseo-prefilled-text">
                    Vehicle pre-filled: <strong>${prefillYear} ${prefillBrand} ${prefillModel}</strong>
                </div>
            </div>
            
            <form hx-post="/analyze" hx-target="#form-container" hx-swap="outerHTML show:#form-container:top" hx-indicator="#pseo-loading" hx-disabled-elt="#pseo-submit-btn">
                <input type="hidden" name="year" value="${prefillYear}">
                <input type="hidden" name="brand" value="${prefillBrand}">
                <input type="hidden" name="model" value="${prefillModel}">
                
                <div class="pseo-input-group">
                    <label>Mileage</label>
                    <select name="mileage" required>
                        <option value="">Select range...</option>
                        <option value="25000">Under 30,000 miles</option>
                        <option value="45000">30,000 – 60,000 miles</option>
                        <option value="75000">60,000 – 90,000 miles</option>
                        <option value="105000">90,000 – 120,000 miles</option>
                        <option value="135000">120,000 – 150,000 miles</option>
                        <option value="175000">150,000 – 200,000 miles</option>
                        <option value="225000">200,000+ miles</option>
                    </select>
                </div>
                
                <div class="pseo-input-group">
                    <label>Repair Quote ($)</label>
                    <input type="number" name="repairQuoteUsd" placeholder="e.g. 2500" required>
                    <input type="hidden" name="isQuoteMissing" value="false">
                </div>
                
                <!-- Loading State -->
                <div id="pseo-loading" class="pseo-loading-overlay">
                    <div class="pseo-spinner"></div>
                    <span>Analyzing your vehicle...</span>
                </div>
                
                <button type="submit" id="pseo-submit-btn" class="pseo-btn">
                    <span class="btn-text">Get My Verdict</span>
                    <span class="btn-arrow">→</span>
                </button>
            </form>
            
            <style>
                .pseo-loading-overlay {
                    display: none;
                    flex-direction: column;
                    align-items: center;
                    gap: 1rem;
                    padding: 2rem;
                    background: #f8fafc;
                    border-radius: 16px;
                    margin-bottom: 1rem;
                }
                .htmx-request .pseo-loading-overlay,
                .htmx-request.pseo-loading-overlay {
                    display: flex !important;
                }
                .pseo-spinner {
                    width: 40px;
                    height: 40px;
                    border: 3px solid #e2e8f0;
                    border-top-color: #3b82f6;
                    border-radius: 50%;
                    animation: spin 0.8s linear infinite;
                }
                @keyframes spin {
                    to { transform: rotate(360deg); }
                }
                .pseo-btn:disabled {
                    opacity: 0.6;
                    cursor: not-allowed;
                    transform: none !important;
                }
            </style>
        </section>

        <!-- Unique Content Section -->
        <section class="content-section">
            <h2>Understanding ${prefillYear} ${prefillBrand} ${prefillModel} Repair Costs</h2>
            
            <p>
                The ${prefillYear} ${prefillBrand} ${prefillModel} is generally a reliable vehicle, but as it ages, specific mechanical patterns emerge. 
                Instead of guessing, we analyze documented failure patterns and repair cost data for this model.
            </p>

            @if(majorFaults != null && !majorFaults.faults().isEmpty())
                <div class="tldr-summary" style="background: #eef2ff; border-left: 4px solid #4f46e5; padding: 1.5rem; border-radius: 8px; margin-bottom: 2rem;">
                    <h3 style="font-size: 1.1rem; font-weight: 800; color: #312e81; margin-top: 0; margin-bottom: 0.5rem; display: flex; align-items: center; gap: 0.5rem;">
                        <span style="font-size: 1.25rem;">⚡</span> Vehicle Analysis Summary
                    </h3>
                    <p style="font-size: 1rem; color: #3730a3; line-height: 1.6; margin: 0;">
                        The <strong>${prefillYear} ${prefillBrand} ${prefillModel}</strong> has documented failure patterns, most notably the <strong>${majorFaults.faults().get(0).component()}</strong> averaging around <strong>$${String.format("%,.0f", majorFaults.faults().get(0).repairCost())}</strong> to fix. If your repair quote approaches this threshold, our algorithm highly recommends comparing it against the vehicle's remaining lifespan and market value to avoid sunk-cost traps.
                    </p>
                </div>

                <div class="fault-warning-box">
                    <h3 style="color: #991b1b; margin-top: 0; font-size: 1.1rem;">⚠️ Known High-Cost Failures for ${prefillModel}</h3>
                    <p style="font-size: 0.9rem; color: #7f1d1d; margin-bottom: 1rem;">
                        Our data indicates these specific issues have a high occurrence rate for this model generation:
                    </p>
                    
                    @for(var fault : majorFaults.faults())
                        <div class="fault-item">
                            <div class="fault-name">
                                ${fault.component()}
                                <span class="fault-cost">Avg: ~$${(int)fault.repairCost()}</span>
                            </div>
                            <p style="font-size: 0.9rem; margin: 0.5rem 0 0 0; color: #4b5563;">
                                <strong>Symptoms:</strong> ${fault.symptoms()}
                            </p>
                            @if(fault.avgFailureMileage() > 0)
                                <p style="font-size: 0.85rem; margin: 0.25rem 0 0 0; color: #64748b;">
                                    Typically occurs around ${fault.avgFailureMileage()} miles
                                </p>
                            @endif
                        </div>
                    @endfor
                </div>
                
                <p>
                    If you are facing one of the repairs listed above (especially <strong>${majorFaults.faults().get(0).component()}</strong>), 
                    caution is advised. These are often "gateway repairs" that signal the vehicle is reaching the end of its economic life.
                </p>
            @else
                <p>
                    A ${prefillYear} model is now ${2026 - prefillYear} years old. At this age, common maintenance items 
                    like timing belts, water pumps, and suspension components may need attention.
                </p>
            @endif
            
            <div class="info-box">
                <div class="info-box-title">📊 Repair vs. Value Comparison</div>
                <p style="margin: 0; color: #475569;">
                    A common decision framework is to compare the repair cost against your vehicle's 
                    current market value. When repair costs represent a large share of the car's worth, 
                    selling may be the more practical option. Our calculator weighs this alongside 
                    other factors specific to your ${prefillModel}.
                </p>
            </div>
            
            <h2>Factors We Consider</h2>
            
            <p>
                Our analysis takes into account multiple factors beyond just the repair cost:
            </p>
            
            <ul style="color: #475569; line-height: 1.8; padding-left: 1.25rem;">
                <li><strong>Current market value</strong> of your ${prefillYear} ${prefillBrand} ${prefillModel}</li>
                <li><strong>Mileage-based depreciation</strong> curves for this model</li>
                <li><strong>Historical reliability data</strong> for ${prefillBrand} vehicles</li>
                <li><strong>Repair-to-value ratio</strong> at your specific mileage point</li>
                <li><strong>Cost of switching</strong> to a replacement vehicle (taxes, fees, etc.)</li>
            </ul>
        </section>

        <!-- FAQ Section for SEO -->
        <section class="faq-section">
            <h2 class="faq-title">Frequently Asked Questions</h2>
            
            <div class="faq-item">
                <div class="faq-question">How much is my ${prefillYear} ${prefillBrand} ${prefillModel} worth?</div>
                <div class="faq-answer">
                    The value of a ${prefillYear} ${prefillModel} varies significantly based on mileage, condition, 
                    and your local market. For a ${2026 - prefillYear}-year-old vehicle, expect depreciation to have 
                    reduced the value by 50-70% from the original MSRP. Our calculator uses current market 
                    data to estimate your specific vehicle's value.
                </div>
            </div>
            
            <div class="faq-item">
                <div class="faq-question">What are common repairs for the ${prefillBrand} ${prefillModel}?</div>
                <div class="faq-answer">
                    @if(majorFaults != null && !majorFaults.faults().isEmpty())
                        For the ${prefillYear} ${prefillModel}, owners frequently report issues with the 
                        <strong>${majorFaults.faults().get(0).component()}</strong> (approx. $${(int)majorFaults.faults().get(0).repairCost()}) 
                        and 
                        @if(majorFaults.faults().size() > 1)
                            <strong>${majorFaults.faults().get(1).component()}</strong>.
                        @else
                            suspension components.
                        @endif
                        These repairs can be costly relative to the vehicle's value.
                    @else
                        Common repairs vary by model year and mileage. For vehicles in the ${prefillYear} range, 
                        typical issues may include suspension components, brake system wear, and depending on 
                        mileage, potential transmission or engine-related maintenance. Enter your details above 
                        for a personalized assessment.
                    @endif
                </div>
            </div>
            
            <div class="faq-item">
                <div class="faq-question">Should I fix my ${prefillModel} or buy a new car?</div>
                <div class="faq-answer">
                    This depends on the repair cost relative to your car's value and your financial situation. 
                    If repairs exceed 50% of the car's value, or if you're facing multiple major repairs, 
                    selling often makes more financial sense. Our calculator analyzes your specific situation 
                    to provide a data-backed recommendation.
                </div>
            </div>
            
            <div class="faq-item">
                <div class="faq-question">Is repairing a high-mileage ${prefillModel} worth it?</div>
                <div class="faq-answer">
                    High-mileage vehicles (over 100,000 miles) require more careful consideration. While the 
                    ${prefillBrand} ${prefillModel} has a reputation for longevity, each repair decision should 
                    factor in remaining useful life. Our tool helps you make this calculation objectively.
                </div>
            </div>
        </section>
        
        <!-- Related Links Section -->
        <section class="related-links">
            <h3 style="font-size: 1.1rem; font-weight: 700; margin-bottom: 1rem; color: #64748b;">Related Searches</h3>
            
            <a href="/verdict/${prefillBrand.toLowerCase().replaceAll("[^a-z0-9]", "")}/${prefillModel.toLowerCase().replaceAll("[^a-z0-9]", "")}/100000-miles" class="related-link">
                <span>📍</span> ${prefillBrand} ${prefillModel} at 100K miles - worth keeping?
            </a>
            <a href="/models/${prefillBrand.toLowerCase().replaceAll("[^a-z0-9]", "")}" class="related-link">
                <span>📊</span> Browse all ${prefillBrand} models
            </a>
            <a href="/" class="related-link">
                <span>🔧</span> Analyze a different vehicle
            </a>
            <a href="/models" class="related-link">
                <span>🚗</span> View all supported brands
            </a>
        </section>
    `
)
