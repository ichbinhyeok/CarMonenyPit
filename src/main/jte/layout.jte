@import gg.jte.Content

@param String title = "Car Money Pit | Precision Actuarial Analysis"
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
    <div class="mesh-container"></div>
    
    @template.fragments.header()

    <main style="flex: 1; display: flex; align-items: center; justify-content: center; padding: 2rem;">
        ${content}
    </main>

    @template.fragments.footer()
</body>
</html>
