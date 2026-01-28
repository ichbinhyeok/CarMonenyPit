@import gg.jte.Content

@param String title = "Auto Money Pit | Precision Actuarial Analysis"
@param String description = null
@param String canonical = null
@param String schemaJson = null
@param Content content
@param Content extraStyles = null
@param boolean noindex = false
@param String ogTitle = null
@param String ogDescription = null
@param String ogImage = null

<!DOCTYPE html>
<html lang="en">
<head>
    <!-- Google tag (gtag.js) -->
    <script async src="https://www.googletagmanager.com/gtag/js?id=G-1NQYSFWZ7C"></script>
    <script>
      window.dataLayer = window.dataLayer || [];
      function gtag(){dataLayer.push(arguments);}
      gtag('js', new Date());

      gtag('config', 'G-1NQYSFWZ7C');
    </script>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title}</title>
    <link rel="apple-touch-icon" sizes="180x180" href="/favicon.png?v=2026.1">
    <link rel="icon" type="image/png" sizes="32x32" href="/favicon.png?v=2026.1">
    <link rel="icon" type="image/x-icon" href="/favicon.ico?v=2026.1">
    <link rel="shortcut icon" href="/favicon.ico?v=2026.1">
    @if(description != null)
    <meta name="description" content="${description}">
    @endif
    @if(canonical != null)
    <link rel="canonical" href="${canonical}">
    @endif
    @if(schemaJson != null)
    <script type="application/ld+json">
        $unsafe{schemaJson}
    </script>
    @endif
    @if(noindex)
    <meta name="robots" content="noindex">
    @endif
    
    @if(ogTitle != null)
    <meta property="og:title" content="${ogTitle}">
    @endif
    @if(ogDescription != null)
    <meta property="og:description" content="${ogDescription}">
    @endif
    @if(ogImage != null)
    <meta property="og:image" content="${ogImage}">
    <meta name="twitter:card" content="summary_large_image">
    <meta name="twitter:image" content="${ogImage}">
    @endif
    <meta property="og:type" content="website">
    <!-- 2026 Futurist Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    <script src="https://unpkg.com/htmx.org@1.9.10"></script>
    
    <style>
        :root {
            --futurist-blue: #0066ff;
            --futurist-bg: #fcfcfd;
            --futurist-card: rgba(255, 255, 255, 0.85);
            --text-main: #0f172a;
            --text-secondary: #64748b;
            --accent-glow: rgba(0, 102, 255, 0.08);
            --border-subtle: rgba(0, 0, 0, 0.04);
            
            --red-alert: #ef4444;
            --green-safe: #10b981;
            --orange-warn: #f59e0b;
        }

        * { box-sizing: border-box; margin: 0; padding: 0; }

        body {
            font-family: 'Plus Jakarta Sans', -apple-system, sans-serif;
            background: var(--futurist-bg);
            color: var(--text-main);
            min-height: 100vh;
            display: flex;
            flex-direction: column;
            position: relative;
            overflow-x: hidden;
            padding-top: 5rem; /* Space for the fixed header */
        }

        /* Sophisticated Mesh Background */
        .mesh-container {
            position: fixed;
            top: 0; left: 0; width: 100vw; height: 100vh;
            z-index: -1;
            background: 
                radial-gradient(circle at 15% 15%, rgba(0, 102, 255, 0.04) 0%, transparent 40%),
                radial-gradient(circle at 85% 85%, rgba(99, 102, 241, 0.06) 0%, transparent 40%),
                radial-gradient(circle at 50% 50%, rgba(244, 114, 182, 0.02) 0%, transparent 50%);
        }

        @keyframes bentoEntrance {
            from { opacity: 0; transform: translateY(30px) scale(0.98); }
            to { opacity: 1; transform: translateY(0) scale(1); }
        }
    </style>
    @if(extraStyles != null)
        ${extraStyles}
    @endif
</head>
<body>
    <!-- Live Pulse Ticker (Social Proof) -->

    <div class="mesh-container"></div>
    
    @template.fragments.header()

    <main style="flex: 1; width: 100%; max-width: 1400px; margin: 0 auto; padding: 2rem;">
        ${content}
    </main>

    <footer style="margin-top: auto; padding: 3rem 2rem; background-color: #1a1a1a; color: #666; font-size: 0.9rem; border-top: 1px solid #333;">
        <div style="max-width: 1400px; margin: 0 auto;">
            <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem; margin-bottom: 2rem;">
                <div style="font-weight: 700; color: #888;">
                    &copy; 2026 AutoMoneyPit. All rights reserved.
                </div>
                <div style="display: flex; gap: 1.5rem; flex-wrap: wrap;">
                    <a href="/about" style="color: #888; text-decoration: none; transition: color 0.2s;">About Us</a>
                    <a href="/models" style="color: #888; text-decoration: none; transition: color 0.2s;">Models</a>
                    <a href="/about#expertise" style="color: #888; text-decoration: none; transition: color 0.2s;">Methodology</a>
                    <a href="/contact" style="color: #888; text-decoration: none; transition: color 0.2s;">Contact</a>
                    <a href="/privacy" style="color: #888; text-decoration: none; transition: color 0.2s;">Privacy</a>
                    <a href="/terms" style="color: #888; text-decoration: none; transition: color 0.2s;">Terms</a>
                </div>
            </div>
            
            <div style="border-top: 1px solid #2a2a2a; padding-top: 1.5rem; font-size: 0.75rem; line-height: 1.6; color: #555; max-width: 800px;">
                <strong>Affiliate Disclosure:</strong> AutoMoneyPit is a participant in various affiliate partnership programs. We may earn a commission when you click through links on our site and complete a lead form, request a quote, or make a purchase. This partnership allows us to maintain our proprietary actuarial engine and provide high-precision financial diagnostics free of charge to all vehicle owners. Our verdicts remain data-driven and objective, independent of our affiliate relationships.
                <br><br>
                <strong>Disclaimer:</strong> This tool provides estimates based on market data for informational and entertainment purposes only. We are not financial advisors or certified mechanics. Always consult a professional before making financial decisions.
            </div>
        </div>
    </footer>
</body>
</html>
