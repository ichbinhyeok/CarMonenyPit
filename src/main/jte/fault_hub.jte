@import com.carmoneypit.engine.web.FaultHubViewModel
@import com.carmoneypit.engine.web.FaultHubViewModel.AffectedModel
@import com.carmoneypit.engine.web.FaultHubViewModel.ReferenceSource
@import com.carmoneypit.engine.web.FaultHubViewModel.FaqItem
@import java.util.List

@param FaultHubViewModel hub
@param String canonicalUrl
@param List<java.util.Map<String, String>> breadcrumbs

@template.layout(
    title = hub.displayName() + ": Cost, Symptoms & Fix-or-Sell Guide | AutoMoneyPit",
    canonical = canonicalUrl,
    content = @`
        <%-- FAQ JSON-LD Schema --%>
        <script type="application/ld+json">
        {
            "@context": "https://schema.org",
            "@type": "FAQPage",
            "mainEntity": [
                @for(int i = 0; i < hub.faqItems().size(); i++)
                    ${i > 0 ? "," : ""}
                    {
                        "@type": "Question",
                        "name": "${hub.faqItems().get(i).question()}",
                        "acceptedAnswer": {
                            "@type": "Answer",
                            "text": "${hub.faqItems().get(i).answer()}"
                        }
                    }
                @endfor
            ]
        }
        </script>


        <style>
            .hub-page { max-width: 800px; margin: 0 auto; padding: 2rem 1rem; }
            .hub-breadcrumbs { display: flex; gap: 0.5rem; font-size: 0.85rem; color: var(--text-secondary, #94a3b8); margin-bottom: 2rem; flex-wrap: wrap; }
            .hub-breadcrumbs a { color: var(--primary-color, #3b82f6); text-decoration: none; }
            .hub-breadcrumbs a:hover { text-decoration: underline; }
            .hub-page h1 { font-size: 1.85rem; font-weight: 800; line-height: 1.25; margin-bottom: 1.5rem; }

            /* Quick Answer */
            .quick-answer {
                background: linear-gradient(135deg, rgba(59,130,246,0.1), rgba(139,92,246,0.08));
                border: 1px solid var(--border-color, #334155);
                border-radius: 12px;
                padding: 1.25rem 1.5rem;
                margin-bottom: 2rem;
                font-size: 1rem;
                line-height: 1.6;
            }
            .quick-answer .disclaimer {
                font-size: 0.8rem;
                color: var(--text-secondary, #94a3b8);
                margin-top: 0.5rem;
                font-style: italic;
            }

            /* Sections */
            .hub-section { margin-bottom: 2.5rem; }
            .hub-section h2 { font-size: 1.3rem; font-weight: 700; margin-bottom: 1rem; padding-bottom: 0.5rem; border-bottom: 1px solid var(--border-color, #334155); }
            .hub-section p, .hub-section li { font-size: 0.95rem; line-height: 1.7; color: var(--text-secondary, #cbd5e1); }
            .hub-section ul { padding-left: 1.25rem; }
            .hub-section li { margin-bottom: 0.4rem; }

            /* Models Table */
            .models-table-wrap { overflow-x: auto; margin: 1rem 0; }
            .models-table { width: 100%; border-collapse: collapse; font-size: 0.85rem; }
            .models-table th { background: var(--card-bg, #1e293b); padding: 0.75rem 0.5rem; text-align: left; font-weight: 600; border-bottom: 2px solid var(--border-color, #334155); white-space: nowrap; }
            .models-table td { padding: 0.6rem 0.5rem; border-bottom: 1px solid var(--border-color, #334155); }
            .models-table tr:hover td { background: rgba(59,130,246,0.05); }

            /* Disclosure */
            .disclosure-box {
                background: var(--card-bg, #1e293b);
                border: 1px solid var(--border-color, #334155);
                border-radius: 8px;
                padding: 1rem 1.25rem;
                font-size: 0.85rem;
                color: var(--text-secondary, #94a3b8);
                line-height: 1.6;
            }

            /* CTA */
            .hub-cta {
                display: inline-block;
                background: linear-gradient(135deg, #3b82f6, #8b5cf6);
                color: #fff;
                padding: 0.75rem 1.5rem;
                border-radius: 8px;
                text-decoration: none;
                font-weight: 600;
                font-size: 0.95rem;
                transition: opacity 0.2s;
            }
            .hub-cta:hover { opacity: 0.9; }

            /* FAQ */
            .faq-item { margin-bottom: 1.25rem; }
            .faq-item h3 { font-size: 1rem; font-weight: 600; margin-bottom: 0.4rem; }
            .faq-item p { margin: 0; }

            /* References */
            .ref-list { list-style: none; padding: 0; }
            .ref-list li { margin-bottom: 0.75rem; font-size: 0.85rem; }
            .ref-list a { color: var(--primary-color, #3b82f6); text-decoration: none; }
            .ref-list a:hover { text-decoration: underline; }
            .ref-note { color: var(--text-secondary, #94a3b8); display: block; margin-top: 0.15rem; }
        </style>

        <div class="hub-page">
            <%-- 1. Breadcrumbs --%>
            <nav class="hub-breadcrumbs" aria-label="Breadcrumb">
                @for(int i = 0; i < breadcrumbs.size(); i++)
                    @if(i > 0)<span>/</span>@endif
                    @if(i < breadcrumbs.size() - 1)
                        <a href="${breadcrumbs.get(i).get("url")}">${breadcrumbs.get(i).get("label")}</a>
                    @else
                        <span>${breadcrumbs.get(i).get("label")}</span>
                    @endif
                @endfor
            </nav>

            <%-- 2. H1 --%>
            <h1>${hub.displayName()}: Cost, Symptoms & What to Do</h1>

            <%-- 3. Quick Answer --%>
            <div class="quick-answer">
                ${hub.quickAnswer()}
                <div class="disclaimer">Estimates based on our dataset of ${hub.modelCount()} models. Actual costs vary by trim, condition, and location.</div>
            </div>

            <%-- 4. What it usually means --%>
            <section class="hub-section">
                <h2>What It Usually Means</h2>
                @if(!hub.affectedModels().isEmpty())
                    <p>Common symptoms reported across affected models include:
                        <strong>${hub.affectedModels().get(0).symptoms()}</strong>.
                        @if(hub.affectedModels().size() > 1)
                            Other variations include:
                            <em>${hub.affectedModels().get(Math.min(1, hub.affectedModels().size()-1)).symptoms()}</em>.
                        @endif
                    </p>
                @endif
            </section>

            <%-- 5. Is it safe to drive? --%>
            <section class="hub-section">
                <h2>Is It Safe to Drive?</h2>
                <div class="disclosure-box">
                    ⚠️ <strong>Caution:</strong> The answer depends on the specific symptoms and severity.
                    Minor symptoms (noise, slight vibration) may allow short-distance driving to a mechanic.
                    Severe symptoms (loss of power, overheating, stalling) require immediate attention — do not drive.
                    Always consult a qualified mechanic for a diagnosis specific to your vehicle.
                </div>
            </section>

            <%-- 6. Checklist --%>
            <section class="hub-section">
                <h2>What to Check Next</h2>
                <ul>
                    <li>Get a diagnostic scan to identify any related fault codes</li>
                    <li>Check fluid levels and condition (color, smell, consistency)</li>
                    <li>Get at least 2–3 repair estimates from independent shops</li>
                    <li>Verify recall and TSB status for your specific VIN</li>
                    <li>Compare estimated repair cost against your vehicle's market value</li>
                    <li>Use our calculator to see a repair-vs-sell analysis for your situation</li>
                </ul>
            </section>

            <%-- 7. Affected Models Table --%>
            <section class="hub-section">
                <h2>Models Affected in Our Dataset</h2>
                <div class="models-table-wrap">
                    <table class="models-table">
                        <thead>
                            <tr>
                                <th>Brand</th>
                                <th>Model</th>
                                <th>Est. Repair Cost</th>
                                <th>Avg. Failure Mileage</th>
                                <th>Reported Rate</th>
                            </tr>
                        </thead>
                        <tbody>
                            @for(AffectedModel m : hub.affectedModels())
                                <tr>
                                    <td>${m.brand()}</td>
                                    <td>${m.model()}</td>
                                    <td>${m.repairCost() > 0 ? "$" + String.format("%,.0f", m.repairCost()) : "Varies"}</td>
                                    <td>${m.avgFailureMileage() > 0 ? String.format("%,d", m.avgFailureMileage()) + " mi" : "Unknown"}</td>
                                    <td>${m.occurrenceRate() > 0 ? String.format("%.0f%%", m.occurrenceRate() * 100) : "Unknown"}</td>
                                </tr>
                            @endfor
                        </tbody>
                    </table>
                </div>
                <p style="font-size:0.8rem; color: var(--text-secondary, #94a3b8); margin-top: 0.5rem;">
                    Data from our internal dataset. Not all trim levels or model years are equally affected.
                </p>
            </section>

            <%-- 8. Repair vs Sell Framework --%>
            <section class="hub-section">
                <h2>Repair vs. Sell: How to Decide</h2>
                <p>Our <a href="/" style="color: var(--primary-color, #3b82f6);">Decision Engine</a> weighs the
                    "Regret of Fixing" against the "Regret of Moving" using your specific vehicle details. Key factors include:</p>
                <ul>
                    <li><strong>Repair cost relative to vehicle value</strong> — many owners use high repair-to-value ratios as a caution signal, then confirm with full condition and maintenance context</li>
                    <li><strong>Vehicle age and remaining useful life</strong> — newer cars with one major fix often remain cost-effective</li>
                    <li><strong>Other pending maintenance</strong> — cascading failures can shift the equation</li>
                </ul>
                <p style="font-size: 0.85rem; color: var(--text-secondary);"><em>This is decision support only, not financial advice.</em></p>
                <div style="margin-top: 1rem;">
                    <a href="/lead?page_type=fault_hub&intent=analyze&verdict_state=UNKNOWN&detail=${hub.slug()}&placement=inline" class="hub-cta" id="hub-cta-inline">
                        Run Your Fix-or-Sell Analysis →
                    </a>
                </div>
            </section>

            <%-- 9. FAQ --%>
            @if(!hub.faqItems().isEmpty())
                <section class="hub-section">
                    <h2>Frequently Asked Questions</h2>
                    @for(FaqItem faq : hub.faqItems())
                        <div class="faq-item">
                            <h3>${faq.question()}</h3>
                            <p>${faq.answer()}</p>
                        </div>
                    @endfor
                </section>
            @endif

            <%-- 10. References --%>
            @if(!hub.references().isEmpty())
                <section class="hub-section">
                    <h2>References & Further Reading</h2>
                    <ul class="ref-list">
                        @for(ReferenceSource ref : hub.references())
                            <li>
                                <a href="${ref.url()}" target="_blank" rel="noopener noreferrer">${ref.name()}</a>
                                @if(!ref.note().isEmpty())
                                    <span class="ref-note">${ref.note()}</span>
                                @endif
                            </li>
                        @endfor
                    </ul>
                    <p style="font-size: 0.8rem; color: var(--text-secondary);">Links provided for reference. We do not control external content.</p>
                </section>
            @endif

            <%-- Bottom CTA --%>
            <div style="text-align: center; margin-top: 2rem; padding-top: 2rem; border-top: 1px solid var(--border-color, #334155);">
                <a href="/lead?page_type=fault_hub&intent=analyze&verdict_state=UNKNOWN&detail=${hub.slug()}&placement=bottom" class="hub-cta" id="hub-cta-bottom">
                    Compare Repair vs. Sell for Your Car →
                </a>
            </div>
        </div>
    `
)
