@template.layout(content = @`
    <div class="static-page-container" style="max-width: 800px; margin: 0 auto; padding: 4rem 1rem;">
        <h1 style="font-size: 2.5rem; font-weight: 800; margin-bottom: 2rem;">The Market Liquidation Approach.</h1>
        
        <p style="font-size: 1.2rem; line-height: 1.8; margin-bottom: 2rem;">
            <strong>"Regret Score" isn't a guess. It's a calculation of liability.</strong>
        </p>

        <h2 style="font-size: 1.8rem; font-weight: 700; margin-top: 3rem; margin-bottom: 1rem;">The Core Equation: RF vs. RM</h2>
        <p style="margin-bottom: 1.5rem;">
            Our proprietary algorithm evaluates two mutually exclusive future paths for your asset strategy:
        </p>

        <div style="background: #f4f4f4; padding: 2rem; border-radius: 8px; margin-bottom: 2rem;">
            <h3 style="font-size: 1.5rem; font-weight: 700; margin-bottom: 1rem; color: #d91b1b;">1. Regret of Fixing (RF)</h3>
            <p style="margin-bottom: 1rem;">The total financial liability incurred by deciding to keep the current asset.</p>
            <code style="display: block; background: #222; color: #fff; padding: 1rem; border-radius: 4px;">RF = Repair Cost + (P(Failure) * Avg. Major Repair Cost) + Continued Depreciation</code>
        </div>

        <div style="background: #f4f4f4; padding: 2rem; border-radius: 8px; margin-bottom: 2rem;">
            <h3 style="font-size: 1.5rem; font-weight: 700; margin-bottom: 1rem; color: #2ecc71;">2. Regret of Moving (RM)</h3>
            <p style="margin-bottom: 1rem;">The "friction costs" lost during the transition to a replacement asset.</p>
            <code style="display: block; background: #222; color: #fff; padding: 1rem; border-radius: 4px;">RM = Sales Tax + Dealer Fees + Registration + Immediate Depreciation</code>
        </div>

        <h2 style="font-size: 1.8rem; font-weight: 700; margin-top: 3rem; margin-bottom: 1rem;">Data Sources & Calibration</h2>
        <p style="margin-bottom: 1.5rem;">
            To ensure accuracy, we do not rely on user-reported "reliability surveys," which are often biased. Instead, we use:
        </p>
        <ul style="list-style-type: disc; padding-left: 2rem; margin-bottom: 2rem;">
            <li style="margin-bottom: 0.5rem;"><strong>Parts & Labor Databases:</strong> Current pricing for OEM and aftermarket components.</li>
            <li style="margin-bottom: 0.5rem;"><strong>NHTSA Recalls:</strong> Federal safety recall data impacting vehicle value.</li>
            <li style="margin-bottom: 0.5rem;"><strong>Auction Data:</strong> Wholesale depreciation trends for specific model years.</li>
        </ul>
    </div>
`)
