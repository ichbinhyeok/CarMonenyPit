<header class="main-header">
    <div class="header-content">
        <a href="/" class="logo">
            <span class="logo-icon">PIT</span>
            <span class="logo-text">CarMoney<span>Pit</span></span>
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
        padding: 1.5rem 2rem;
        position: fixed;
        top: 0;
        left: 0;
        z-index: 100;
        background: rgba(252, 252, 253, 0.8);
        backdrop-filter: blur(12px);
        -webkit-backdrop-filter: blur(12px);
        border-bottom: 1px solid var(--border-subtle);
    }

    .header-content {
        max-width: 1400px;
        margin: 0 auto;
        display: flex;
        justify-content: space-between;
        align-items: center;
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
        .main-header { padding: 1rem; }
        .logo-text { font-size: 1.1rem; }
    }
</style>
