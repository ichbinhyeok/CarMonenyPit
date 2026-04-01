@import com.carmoneypit.engine.service.CarDataService.CarModel
@import com.carmoneypit.engine.service.CarDataService.MajorFaults
@import com.carmoneypit.engine.service.CarDataService.ModelReliability
@import com.carmoneypit.engine.service.CarDataService.ModelMarket
@import com.carmoneypit.engine.web.PSeoController.ModelHubLink
@param String title
@param String metaDescription
@param String canonicalUrl
@param java.util.List<String> breadcrumbs
@param CarModel car
@param java.util.List<ModelHubLink> faultLinks
@param MajorFaults majorFaults = null
@param ModelReliability reliability = null
@param ModelMarket market = null
@param int representativeYear
@param String shouldFixUrl
@param String datasetVersion = "2026-02-24"

!{
    String bestYearsText = (reliability != null && reliability.bestYears() != null && !reliability.bestYears().isEmpty())
        ? reliability.bestYears().stream().map(String::valueOf).collect(java.util.stream.Collectors.joining(", "))
        : null;
    String worstYearsText = (reliability != null && reliability.worstYears() != null && !reliability.worstYears().isEmpty())
        ? reliability.worstYears().stream().map(String::valueOf).collect(java.util.stream.Collectors.joining(", "))
        : null;
    var topFault = (majorFaults != null && majorFaults.faults() != null && !majorFaults.faults().isEmpty())
        ? majorFaults.faults().stream().max(java.util.Comparator.comparingDouble(f -> f.repairCost())).orElse(majorFaults.faults().get(0))
        : null;
}

@template.layout(
    title = title,
    description = metaDescription,
    canonical = canonicalUrl,
    content = @`
    <div class="static-page-container" style="max-width: 920px; margin: 0 auto; padding: 4rem 1rem;">
        <div style="margin-bottom: 2rem; color: #666; font-size: 0.9rem;">
            <a href="/" style="color: #666; text-decoration: none;">Home</a>
            @for(String crumb : breadcrumbs)
                <span> / </span> <span>${crumb}</span>
            @endfor
        </div>

        <div style="display: inline-flex; align-items: center; gap: 0.5rem; background: #eef2ff; color: #4338ca; padding: 0.45rem 0.9rem; border-radius: 999px; font-size: 0.78rem; font-weight: 800; text-transform: uppercase; letter-spacing: 0.06em; margin-bottom: 1.25rem;">
            Model decision hub | ${car.startYear()}-${car.endYear()} generation
        </div>

        <h1 style="font-size: clamp(2.2rem, 5vw, 3.2rem); font-weight: 900; letter-spacing: -0.03em; margin-bottom: 1rem; color: #0f172a;">
            ${car.brand()} ${car.model()} Problems, Repair Costs, and Fix-or-Sell Guidance
        </h1>

        <p style="font-size: 1.08rem; color: #475569; line-height: 1.75; max-width: 760px; margin-bottom: 2rem;">
            Use this page when you need the fast version: the typical value of this generation, the most expensive common failure, and the year-specific page to open before you approve a painful repair quote.
        </p>

        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); gap: 1rem; margin-bottom: 2rem;">
            <div style="background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 16px; padding: 1rem;">
                <div style="font-size: 0.72rem; font-weight: 800; letter-spacing: 0.06em; text-transform: uppercase; color: #64748b; margin-bottom: 0.35rem;">Representative Year</div>
                <div style="font-size: 1.35rem; font-weight: 900; color: #0f172a;">${representativeYear}</div>
            </div>
            <div style="background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 16px; padding: 1rem;">
                <div style="font-size: 0.72rem; font-weight: 800; letter-spacing: 0.06em; text-transform: uppercase; color: #64748b; margin-bottom: 0.35rem;">Typical Value</div>
                <div style="font-size: 1.35rem; font-weight: 900; color: #0f172a;">${market != null ? "$" + String.format("%,d", market.jan2026AvgPrice()) : "Data"}</div>
            </div>
            <div style="background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 16px; padding: 1rem;">
                <div style="font-size: 0.72rem; font-weight: 800; letter-spacing: 0.06em; text-transform: uppercase; color: #64748b; margin-bottom: 0.35rem;">Expected Lifespan</div>
                <div style="font-size: 1.35rem; font-weight: 900; color: #0f172a;">${reliability != null ? String.format("%,d", reliability.lifespanMiles()) + " mi" : "Data"}</div>
            </div>
            <div style="background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 16px; padding: 1rem;">
                <div style="font-size: 0.72rem; font-weight: 800; letter-spacing: 0.06em; text-transform: uppercase; color: #64748b; margin-bottom: 0.35rem;">Top Failure</div>
                <div style="font-size: 1.05rem; font-weight: 900; color: #0f172a;">${topFault != null ? topFault.component() : "Model guide"}</div>
                @if(topFault != null)
                    <div style="font-size: 0.82rem; color: #b91c1c; font-weight: 700; margin-top: 0.3rem;">~$${String.format("%,.0f", topFault.repairCost())}</div>
                @endif
            </div>
        </div>

        <div style="background: linear-gradient(135deg, #0f172a, #1d4ed8); color: #fff; border-radius: 24px; padding: 2rem; margin-bottom: 2.5rem;">
            <div style="font-size: 0.76rem; font-weight: 800; letter-spacing: 0.08em; text-transform: uppercase; opacity: 0.82; margin-bottom: 0.8rem;">Start Here</div>
            <h2 style="font-size: 1.6rem; font-weight: 900; margin: 0 0 0.85rem;">Open the ${representativeYear} ${car.brand()} ${car.model()} decision page</h2>
            <p style="font-size: 1rem; line-height: 1.7; color: rgba(255,255,255,0.92); margin: 0 0 1.25rem;">
                This is the fastest route if you already have a quote. It compares repair cost, market value, expected lifespan, and known failure risk before you decide to fix or sell.
            </p>
            <a href="${shouldFixUrl}" style="display: inline-block; text-decoration: none; background: #fff; color: #1d4ed8; padding: 1rem 1.25rem; border-radius: 14px; font-weight: 900;">Go To ${representativeYear} Fix-or-Sell Page -></a>
        </div>

        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: 1rem; margin-bottom: 2.5rem;">
            @if(bestYearsText != null)
            <div style="background: #f0fdf4; border: 1px solid #bbf7d0; border-radius: 16px; padding: 1rem;">
                <div style="font-size: 0.72rem; font-weight: 800; letter-spacing: 0.06em; text-transform: uppercase; color: #166534; margin-bottom: 0.35rem;">Best Years In Our Data</div>
                <div style="font-size: 0.98rem; font-weight: 800; color: #14532d;">${bestYearsText}</div>
            </div>
            @endif
            @if(worstYearsText != null)
            <div style="background: #fff7ed; border: 1px solid #fdba74; border-radius: 16px; padding: 1rem;">
                <div style="font-size: 0.72rem; font-weight: 800; letter-spacing: 0.06em; text-transform: uppercase; color: #9a3412; margin-bottom: 0.35rem;">Years To Check More Carefully</div>
                <div style="font-size: 0.98rem; font-weight: 800; color: #7c2d12;">${worstYearsText}</div>
            </div>
            @endif
            @if(reliability != null && reliability.commonTroubleSpots() != null && !reliability.commonTroubleSpots().isEmpty())
            <div style="background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 16px; padding: 1rem;">
                <div style="font-size: 0.72rem; font-weight: 800; letter-spacing: 0.06em; text-transform: uppercase; color: #64748b; margin-bottom: 0.35rem;">Common Trouble Spots</div>
                <div style="font-size: 0.98rem; font-weight: 700; color: #0f172a;">${String.join(", ", reliability.commonTroubleSpots())}</div>
            </div>
            @endif
        </div>

        <h2 style="font-size: 1.5rem; font-weight: 900; margin-bottom: 1rem; color: #0f172a;">Most Relevant Repair Guides</h2>
        <p style="font-size: 1rem; color: #64748b; line-height: 1.7; margin-bottom: 1.5rem; max-width: 720px;">
            These are the problem pages most likely to matter before you approve a large repair on this model.
        </p>

        <ul style="list-style: none; padding: 0; margin: 0 0 2rem;">
            @for(ModelHubLink item : faultLinks)
                <li style="margin-bottom: 1rem; padding: 1rem 1.1rem; border: 1px solid #e5e7eb; border-radius: 14px; transition: transform 0.2s, border-color 0.2s, box-shadow 0.2s;">
                    <a href="${item.url()}" style="text-decoration: none; color: #111; font-weight: 700; font-size: 1.05rem; display: block;">
                        ${item.label()} <span style="float: right; color: #d91b1b;">-></span>
                    </a>
                    <div style="font-size: 0.88rem; color: #64748b; margin-top: 0.45rem; line-height: 1.5;">
                        Open the fault-specific cost and decision page for this generation.
                    </div>
                </li>
            @endfor
        </ul>

        <div style="font-size: 0.78rem; color: #64748b; border-top: 1px solid #e2e8f0; padding-top: 1rem;">
            Dataset: ${datasetVersion} | This hub summarizes internal market, reliability, and failure data for the ${car.startYear()}-${car.endYear()} ${car.brand()} ${car.model()} generation.
        </div>
    </div>
`)
