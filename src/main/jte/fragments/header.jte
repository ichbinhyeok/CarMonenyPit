<header class="main-header">
    <div class="live-ticker-bar">
        <div class="live-label">
            <span class="live-dot"></span>
            LIVE SYSTEM ACTIVITY
        </div>
        <div class="ticker-track">
            <div class="ticker-content">
                <span>⚡ Live Analysis: Ford F-150 (TX) - CAM_PHASER_DETECTED</span>
                <span>⚡ Live Analysis: Honda Accord (CA) - VCM_VIBRATION</span>
                <span>✓ Verdict: Jeep Wrangler (FL) - FINANCIAL_BLEED_CONFIRMED</span>
                <span>⚡ Live Analysis: Toyota Camry (NY) - TRANSMISSION_SLIP</span>
                <span>✓ Verdict: Chevrolet Silverado (OH) - ASSET_RETAINED</span>
                <span>⚡ Live Analysis: BMW 3-Series (GA) - WATER_PUMP_FAILURE</span>
                <!-- Duplicate for seamless loop -->
                <span>⚡ Live Analysis: Ford F-150 (TX) - CAM_PHASER_DETECTED</span>
                <span>⚡ Live Analysis: Honda Accord (CA) - VCM_VIBRATION</span>
                <span>✓ Verdict: Jeep Wrangler (FL) - FINANCIAL_BLEED_CONFIRMED</span>
                <span>⚡ Live Analysis: Toyota Camry (NY) - TRANSMISSION_SLIP</span>
                <span>✓ Verdict: Chevrolet Silverado (OH) - ASSET_RETAINED</span>
                <span>⚡ Live Analysis: BMW 3-Series (GA) - WATER_PUMP_FAILURE</span>
            </div>
        </div>
    </div>
    <div class="header-content">
        <a href="/" class="logo">
            <span class="logo-icon">PIT</span>
            <span class="logo-text">AutoMoney<span>Pit</span></span>
        </a>
        <nav class="header-nav">
            <span class="sys-status">
                <span class="status-dot"></span>
                SYSTEM_ACTIVE_v4.2
            </span>
        </nav>
    </div>
</header>

<style>
    .main-header {
        width: 100%;
        position: fixed;
        top: 0;
        left: 0;
        z-index: 100;
        background: rgba(252, 252, 253, 0.9);
        backdrop-filter: blur(12px);
        -webkit-backdrop-filter: blur(12px);
        border-bottom: 1px solid var(--border-subtle);
        display: flex;
        flex-direction: column;
    }

    /* Live Ticker Styles */
    .live-ticker-bar {
        background: #000;
        color: #fff;
        height: 32px;
        display: flex;
        align-items: center;
        overflow: hidden;
        font-size: 0.7rem;
        font-weight: 700;
        position: relative;
        transition: height 0.3s;
    }
    @media (max-width: 768px) {
        .live-ticker-bar { height: 24px; font-size: 0.65rem; }
    }

    .live-label {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0 1rem;
        height: 100%;
        background: #000;
        z-index: 10;
        box-shadow: 5px 0 10px rgba(0,0,0,0.5);
        white-space: nowrap;
    }
    @media (max-width: 768px) {
        .live-label span:not(.live-dot) { display: none; }
        .live-label { padding: 0 0.5rem; }
    }

    .live-dot {
        width: 6px;
        height: 6px;
        background: #10B981;
        border-radius: 50%;
        animation: pulse 2s infinite;
    }

    .ticker-track {
        flex: 1;
        overflow: hidden;
        display: flex;
        mask-image: linear-gradient(to right, transparent, black 20px, black 95%, transparent);
        -webkit-mask-image: linear-gradient(to right, transparent, black 20px, black 95%, transparent);
    }

    .ticker-content {
        display: flex;
        gap: 4rem;
        padding-left: 2rem;
        white-space: nowrap;
        animation: ticker 40s linear infinite;
    }

    .ticker-content span {
        opacity: 0.9;
    }

    @keyframes ticker {
        0% { transform: translateX(0); }
        100% { transform: translateX(-50%); }
    }

    @keyframes pulse {
        0% { box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.7); }
        70% { box-shadow: 0 0 0 4px rgba(16, 185, 129, 0); }
        100% { box-shadow: 0 0 0 0 rgba(16, 185, 129, 0); }
    }

    .header-content {
        width: 100%;
        max-width: 1400px;
        margin: 0 auto;
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 1rem 2rem; /* Reduced padding since we added the top bar */
    }

    .logo {
        text-decoration: none;
        display: flex;
        align-items: center;
        gap: 0.8rem;
    }

    .logo-icon {
        background: #000;
        color: #fff;
        padding: 4px 8px;
        border-radius: 8px;
        font-weight: 800;
        font-size: 0.7rem;
        letter-spacing: 0.05em;
    }

    .logo-text {
        font-size: 1.25rem;
        font-weight: 800;
        color: #000;
        letter-spacing: -0.02em;
    }

    .logo-text span {
        background: linear-gradient(90deg, #0066ff, #6366f1);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
    }

    .sys-status {
        font-size: 0.7rem;
        font-weight: 800;
        color: var(--text-secondary);
        display: flex;
        align-items: center;
        gap: 0.5rem;
        letter-spacing: 0.1em;
        opacity: 0.7;
    }

    .status-dot {
        width: 6px;
        height: 6px;
        background: #10b981;
        border-radius: 50%;
        box-shadow: 0 0 8px #10b981;
    }

    @media (max-width: 768px) {
        .header-content { padding: 0.5rem 1rem; }
        .logo-text { font-size: 1rem; }
        .logo-icon { padding: 2px 6px; font-size: 0.6rem; }
        .live-ticker-bar { display: flex; }
    }
</style>
