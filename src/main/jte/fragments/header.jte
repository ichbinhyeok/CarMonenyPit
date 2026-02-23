<style>
    .main-header {
        width: 100%;
        position: fixed;
        top: 0;
        left: 0;
        z-index: 50;
        background: rgba(252, 252, 253, 0.9);
        backdrop-filter: blur(12px);
        -webkit-backdrop-filter: blur(12px);
        border-bottom: 1px solid var(--border-subtle);
        display: flex;
        flex-direction: column;
    }

    .header-content {
        width: 100%;
        max-width: 1400px;
        margin: 0 auto;
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 1rem 2rem;
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

    .header-nav {
        display: flex;
        align-items: center;
        gap: 1.5rem;
    }

    .header-nav a {
        text-decoration: none;
        font-size: 0.85rem;
        font-weight: 600;
        color: var(--text-secondary);
        transition: color 0.2s;
    }

    .header-nav a:hover {
        color: var(--text-main);
    }

    @media (max-width: 768px) {
        .header-content { padding: 0.75rem 1rem; }
        .logo-text { font-size: 1rem; }
        .logo-icon { padding: 2px 6px; font-size: 0.6rem; }
        .header-nav a { font-size: 0.75rem; }
        .header-nav { gap: 1rem; }
    }
</style>

<header class="main-header">
    <div class="header-content">
        <a href="/" class="logo">
            <span class="logo-icon">PIT</span>
            <span class="logo-text">AutoMoney<span>Pit</span></span>
        </a>
        <nav class="header-nav">
            <a href="/models">Models</a>
            <a href="/methodology">Methodology</a>
            <a href="/about">About</a>
        </nav>
    </div>
</header>
