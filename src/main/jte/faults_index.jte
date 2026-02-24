@import com.carmoneypit.engine.web.FaultHubViewModel
@import java.util.List

@param List<FaultHubViewModel> hubs
@param String canonicalUrl
@param List<java.util.Map<String, String>> breadcrumbs

@template.layout(
    title = "Common Car Faults: Repair Costs & Fix-or-Sell Guides | AutoMoneyPit",
    description = "Expert guides on the most common car faults — CVT transmission, timing chain, oil consumption & more. Compare repair costs and decide whether to fix or sell.",
    canonical = canonicalUrl,
    content = @`
        <style>
            .fault-directory { max-width: 1000px; margin: 0 auto; padding: 2rem 1rem; }
            .fault-directory h1 { font-size: 2rem; font-weight: 800; text-align: center; margin-bottom: 0.5rem; }
            .fault-directory .subtitle { text-align: center; color: var(--text-secondary, #94a3b8); margin-bottom: 3rem; font-size: 1.05rem; }
            .fault-breadcrumbs { display: flex; gap: 0.5rem; font-size: 0.85rem; color: var(--text-secondary, #94a3b8); margin-bottom: 2rem; flex-wrap: wrap; }
            .fault-breadcrumbs a { color: var(--primary-color, #3b82f6); text-decoration: none; }
            .fault-breadcrumbs a:hover { text-decoration: underline; }
            .fault-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 1.5rem; }
            .fault-card {
                background: var(--card-bg, #1e293b);
                border: 1px solid var(--border-color, #334155);
                border-radius: 12px;
                padding: 1.5rem;
                transition: transform 0.2s ease, box-shadow 0.2s ease;
                text-decoration: none;
                color: inherit;
                display: block;
            }
            .fault-card:hover { transform: translateY(-3px); box-shadow: 0 8px 25px rgba(0,0,0,0.3); }
            .fault-card h2 { font-size: 1.15rem; font-weight: 700; margin: 0 0 0.75rem 0; color: var(--text-primary, #f1f5f9); }
            .fault-card .card-meta { display: flex; gap: 1rem; margin-bottom: 0.75rem; font-size: 0.85rem; color: var(--text-secondary, #94a3b8); }
            .fault-card .card-meta span { display: flex; align-items: center; gap: 0.25rem; }
            .fault-card .card-desc { font-size: 0.9rem; color: var(--text-secondary, #94a3b8); line-height: 1.5; }
            .fault-card .card-arrow { display: flex; justify-content: flex-end; margin-top: 1rem; color: var(--primary-color, #3b82f6); font-weight: 600; font-size: 0.9rem; }
        </style>

        <div class="fault-directory">
            <nav class="fault-breadcrumbs" aria-label="Breadcrumb">
                @for(int i = 0; i < breadcrumbs.size(); i++)
                    @if(i > 0)<span>/</span>@endif
                    @if(i < breadcrumbs.size() - 1)
                        <a href="${breadcrumbs.get(i).get("url")}">${breadcrumbs.get(i).get("label")}</a>
                    @else
                        <span>${breadcrumbs.get(i).get("label")}</span>
                    @endif
                @endfor
            </nav>

            <h1>Common Car Faults</h1>
            <p class="subtitle">Data-driven guides to help you decide: fix it or sell it?</p>

            <div class="fault-grid">
                @for(FaultHubViewModel hub : hubs)
                    <a href="/fault/${hub.slug()}" class="fault-card" id="fault-card-${hub.slug()}">
                        <h2>${hub.displayName()}</h2>
                        <div class="card-meta">
                            <span>📊 ${hub.modelCount()} models affected</span>
                        </div>
                        <p class="card-desc">${hub.quickAnswer().length() > 120 ? hub.quickAnswer().substring(0, 120) + "..." : hub.quickAnswer()}</p>
                        <div class="card-arrow">View Guide →</div>
                    </a>
                @endfor
            </div>
        </div>
    `
)
