@param String title = "404 - Page Not Found | Auto Money Pit"
@param String description = "The page you are looking for does not exist."

@template.layout(
    title = title,
    description = description,
    content = @`
        <div style="display: flex; flex-direction: column; align-items: center; justify-content: center; min-height: 60vh; text-align: center; animation: bentoEntrance 0.6s ease-out;">
            <div style="font-size: 8rem; font-weight: 800; background: linear-gradient(135deg, var(--futurist-blue), #6366f1); -webkit-background-clip: text; -webkit-text-fill-color: transparent; line-height: 1;">404</div>
            <h1 style="font-size: 2.5rem; margin-bottom: 1rem; color: var(--text-main);">Diagnostic Error: Page Missing</h1>
            <p style="font-size: 1.1rem; color: var(--text-secondary); max-width: 500px; margin-bottom: 2rem;">
                It seems the page you're searching for has been decommissioned or moved to a different sector.
            </p>
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
            " onmouseover="this.style.transform='translateY(-2px)'; this.style.boxShadow='0 8px 20px rgba(0, 102, 255, 0.3)';" 
               onmouseout="this.style.transform='translateY(0)'; this.style.boxShadow='0 4px 12px rgba(0, 102, 255, 0.2)';"
            >
                Return to Command Center
            </a>
        </div>
    `
)
