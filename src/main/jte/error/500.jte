@param String title = "500 - System Failure | Auto Money Pit"
@param String description = "An internal server error occurred."

@template.layout(
    title = title,
    description = description,
    content = @`
        <div style="display: flex; flex-direction: column; align-items: center; justify-content: center; min-height: 60vh; text-align: center; animation: bentoEntrance 0.6s ease-out;">
            <div style="font-size: 8rem; font-weight: 800; background: linear-gradient(135deg, var(--red-alert), #ef4444); -webkit-background-clip: text; -webkit-text-fill-color: transparent; line-height: 1;">500</div>
            <h1 style="font-size: 2.5rem; margin-bottom: 1rem; color: var(--text-main);">Critical System Fault</h1>
            <p style="font-size: 1.1rem; color: var(--text-secondary); max-width: 500px; margin-bottom: 2rem;">
                Our engines encountered an unexpected turbulence. The engineering team has been notified of this anomaly.
            </p>
            <div style="display: flex; gap: 1rem;">
                <a href="/" style="
                    display: inline-block;
                    padding: 1rem 2rem;
                    background: var(--futurist-blue);
                    color: white;
                    text-decoration: none;
                    border-radius: 12px;
                    font-weight: 600;
                    transition: transform 0.2s, box-shadow 0.2s;
                    box-shadow: 0 4px 12px rgba(0, 102, 255, 0.2);
                " onmouseover="this.style.transform='translateY(-2px)';" 
                   onmouseout="this.style.transform='translateY(0)';"
                >
                    Reboot to Home
                </a>
                <button onclick="window.location.reload()" style="
                    display: inline-block;
                    padding: 1rem 2rem;
                    background: white;
                    color: var(--text-main);
                    border: 1px solid var(--border-subtle);
                    text-decoration: none;
                    border-radius: 12px;
                    font-weight: 600;
                    cursor: pointer;
                    transition: background 0.2s;
                " onmouseover="this.style.background='var(--futurist-bg)';"
                   onmouseout="this.style.background='white';"
                >
                    Retry Analysis
                </button>
            </div>
        </div>
    `
)
