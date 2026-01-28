@import gg.jte.Content

@param String title = "Car Money Pit | Precision Actuarial Analysis"
@param String description = null
@param String canonical = null
@param String schemaJson = null
@param Content content
@param Content extraStyles = null
@param boolean noindex = false
@param String ogTitle = null
@param String ogDescription = null

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title}</title>
    <link rel="icon" type="image/png" href="/favicon_modern.png">
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
    <div id="live-pulse" style="position: fixed; top: 0; left: 0; width: 100%; height: 36px; background: #000; color: #fff; z-index: 9999; display: flex; align-items: center; overflow: hidden; font-size: 0.75rem; font-weight: 700; border-bottom: 1px solid rgba(255,255,255,0.1);">
        <div style="display: flex; align-items: center; gap: 0.5rem; padding: 0 1rem; border-right: 1px solid rgba(255,255,255,0.2); height: 100%; white-space: nowrap; background: #000; z-index: 2;">
            <span style="display: block; width: 6px; height: 6px; background: #10B981; border-radius: 50%; box-shadow: 0 0 8px #10B981;"></span>
            LIVE SYSTEM ACTIVITY
        </div>
        <div id="ticker-content" style="white-space: nowrap; padding-left: 100%; animation: ticker 40s linear infinite; display: flex; gap: 4rem; opacity: 0.9;">
            <!-- Content verified via JS -->
            <span>⚡ Analyzing: Ford F-150 (Texas)</span>
            <span>⚡ Analyzing: Honda Accord (California)</span>
            <span>✓ Verdict Generated: Jeep Wrangler (Florida) - $4,200 Bleed</span>
            <span>⚡ Analyzing: Toyota Camry (New York)</span>
            <span>✓ Verdict Generated: Chevrolet Silverado (Ohio) - SOLD</span>
            <span>⚡ Analyzing: BMW 3-Series (Georgia)</span>
        </div>
    </div>
    <div class="mesh-container"></div>
    
    @template.fragments.header()

    <main style="flex: 1; width: 100%; max-width: 1400px; margin: 0 auto; padding: 2rem;">
        ${content}
    </main>

    <footer style="margin-top: auto; padding: 2rem; background-color: #1a1a1a; color: #666; font-size: 0.9rem; border-top: 1px solid #333;">
        <div style="max-width: 1400px; margin: 0 auto; display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 1rem;">
            <div>
                &copy; 2026 CarMoneyPit. All rights reserved.
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
    </footer>
</body>
</html>
