@import com.carmoneypit.engine.api.InputModels.CarBrand

@param String seoTitle
@param String seoDescription
@param Integer prefillYear
@param String prefillBrand
@param String prefillModel
@param Boolean isPseoPage = false
@param String pseoSlug = ""

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${seoTitle}</title>
    <meta name="description" content="${seoDescription}">
    <meta name="robots" content="index, follow">
    
    <!-- Open Graph -->
    <meta property="og:title" content="${seoTitle}">
    <meta property="og:description" content="${seoDescription}">
    <meta property="og:type" content="website">
    <meta property="og:url" content="https://automoneypit.com/should-i-fix/${pseoSlug}">
    
    <!-- Canonical -->
    <link rel="canonical" href="https://automoneypit.com/should-i-fix/${pseoSlug}">
    
    <link rel="icon" href="/favicon.png" type="image/png">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800;900&display=swap" rel="stylesheet">
    <script src="https://unpkg.com/htmx.org@1.9.12"></script>
    <link rel="stylesheet" href="/static/styles.css">
    
    <style>
        .pseo-hero {
            padding: 4rem 1rem 2rem;
            text-align: center;
            max-width: 800px;
            margin: 0 auto;
        }
        .pseo-badge {
            display: inline-flex;
            align-items: center;
            gap: 0.5rem;
            background: #eff6ff;
            color: #3b82f6;
            padding: 0.5rem 1rem;
            border-radius: 100px;
            font-size: 0.8rem;
            font-weight: 700;
            margin-bottom: 1.5rem;
        }
        .pseo-title {
            font-size: clamp(2rem, 5vw, 3rem);
            font-weight: 900;
            color: #0f172a;
            line-height: 1.1;
            margin-bottom: 1.5rem;
        }
        .pseo-subtitle {
            font-size: 1.1rem;
            color: #64748b;
            line-height: 1.6;
            max-width: 600px;
            margin: 0 auto 2.5rem;
        }
        .pseo-stats {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 1rem;
            max-width: 500px;
            margin: 0 auto 3rem;
        }
        .pseo-stat {
            background: #f8fafc;
            padding: 1rem;
            border-radius: 16px;
            text-align: center;
        }
        .pseo-stat-value {
            font-size: 1.5rem;
            font-weight: 800;
            color: #0f172a;
        }
        .pseo-stat-label {
            font-size: 0.75rem;
            color: #64748b;
            margin-top: 0.25rem;
        }
        .pseo-form-container {
            background: #fff;
            border-radius: 24px;
            padding: 2rem;
            box-shadow: 0 10px 40px rgba(0,0,0,0.08);
            border: 1px solid #e2e8f0;
            max-width: 500px;
            margin: 0 auto;
        }
        .pseo-form-title {
            font-size: 1.25rem;
            font-weight: 800;
            text-align: center;
            margin-bottom: 1.5rem;
        }
        .pseo-prefilled {
            background: #f0fdf4;
            border: 1px solid #bbf7d0;
            border-radius: 12px;
            padding: 1rem;
            margin-bottom: 1.5rem;
            display: flex;
            align-items: center;
            gap: 0.75rem;
        }
        .pseo-prefilled-icon {
            font-size: 1.25rem;
        }
        .pseo-prefilled-text {
            font-size: 0.9rem;
            color: #166534;
        }
        .pseo-prefilled-text strong {
            color: #14532d;
        }
        .faq-section {
            max-width: 700px;
            margin: 4rem auto;
            padding: 0 1rem;
        }
        .faq-title {
            font-size: 1.5rem;
            font-weight: 800;
            text-align: center;
            margin-bottom: 2rem;
        }
        .faq-item {
            background: #f8fafc;
            border-radius: 16px;
            padding: 1.5rem;
            margin-bottom: 1rem;
        }
        .faq-question {
            font-weight: 700;
            color: #0f172a;
            margin-bottom: 0.75rem;
        }
        .faq-answer {
            font-size: 0.95rem;
            color: #64748b;
            line-height: 1.6;
        }
    </style>
</head>
<body>
    <!-- Header for SEO -->
    <header style="padding: 1rem 2rem; border-bottom: 1px solid #e2e8f0;">
        <a href="/" style="font-weight: 800; color: #0f172a; text-decoration: none; font-size: 1.1rem;">
            🚗 AutoMoneyPit
        </a>
    </header>

    <main>
        <section class="pseo-hero">
            <div class="pseo-badge">
                <span>🔍</span> ${prefillYear} ${prefillBrand} ${prefillModel} Analysis
            </div>
            
            <h1 class="pseo-title">Should You Fix Your ${prefillYear} ${prefillBrand} ${prefillModel}?</h1>
            
            <p class="pseo-subtitle">
                Got a repair quote for your ${prefillModel}? We'll tell you if it makes financial sense 
                to fix it — or if you're better off selling and moving on.
            </p>
            
            <div class="pseo-stats">
                <div class="pseo-stat">
                    <div class="pseo-stat-value">30s</div>
                    <div class="pseo-stat-label">Analysis Time</div>
                </div>
                <div class="pseo-stat">
                    <div class="pseo-stat-value">Free</div>
                    <div class="pseo-stat-label">No Cost</div>
                </div>
                <div class="pseo-stat">
                    <div class="pseo-stat-value">1M+</div>
                    <div class="pseo-stat-label">Cars Analyzed</div>
                </div>
            </div>
        </section>

        <section class="pseo-form-container" id="form-container">
            <div class="pseo-form-title">Get Your Verdict</div>
            
            <div class="pseo-prefilled">
                <span class="pseo-prefilled-icon">✅</span>
                <div class="pseo-prefilled-text">
                    Vehicle pre-filled: <strong>${prefillYear} ${prefillBrand} ${prefillModel}</strong>
                </div>
            </div>
            
            <form hx-post="/analyze" hx-target="#form-container" hx-swap="outerHTML show:window:top" class="form-grid-modern">
                <input type="hidden" name="year" value="${prefillYear}">
                <input type="hidden" name="brand" value="${prefillBrand}">
                <input type="hidden" name="model" value="${prefillModel}">
                
                <div class="input-group">
                    <label>Mileage</label>
                    <select name="mileage" required>
                        <option value="">Select range...</option>
                        <option value="25000">Under 30,000 miles</option>
                        <option value="45000">30,000 – 60,000 miles</option>
                        <option value="75000">60,000 – 90,000 miles</option>
                        <option value="105000">90,000 – 120,000 miles</option>
                        <option value="135000">120,000 – 150,000 miles</option>
                        <option value="175000">150,000 – 200,000 miles</option>
                        <option value="225000">200,000+ miles</option>
                    </select>
                </div>
                
                <div class="input-group">
                    <label>Repair Quote ($)</label>
                    <input type="number" name="repairQuoteUsd" placeholder="e.g. 2500" required>
                    <input type="hidden" name="isQuoteMissing" value="false">
                </div>
                
                <div class="analyze-footer">
                    <button type="submit" class="btn-cinematic">
                        Get My Verdict <span>→</span>
                    </button>
                </div>
            </form>
        </section>

        <!-- FAQ Section for SEO -->
        <section class="faq-section">
            <h2 class="faq-title">Common Questions About ${prefillBrand} ${prefillModel}</h2>
            
            <div class="faq-item">
                <div class="faq-question">How much is my ${prefillYear} ${prefillBrand} ${prefillModel} worth?</div>
                <div class="faq-answer">
                    The value depends on mileage, condition, and your local market. Our calculator 
                    estimates this based on current market data and factors it into our fix-or-sell recommendation.
                </div>
            </div>
            
            <div class="faq-item">
                <div class="faq-question">What are common repairs for ${prefillBrand} ${prefillModel}?</div>
                <div class="faq-answer">
                    Common issues vary by model year. Our analysis considers known problem areas 
                    for your specific vehicle and factors in the likelihood of future repairs.
                </div>
            </div>
            
            <div class="faq-item">
                <div class="faq-question">Should I fix my car or buy a new one?</div>
                <div class="faq-answer">
                    It depends on the repair cost vs. your car's value and future reliability. 
                    If repairs exceed 50% of the car's value, selling often makes more sense. 
                    Our calculator does this math for you.
                </div>
            </div>
        </section>
    </main>

    <footer style="text-align: center; padding: 2rem; color: #64748b; font-size: 0.85rem; border-top: 1px solid #e2e8f0;">
        © 2026 AutoMoneyPit. All rights reserved. | <a href="/" style="color: #3b82f6;">Back to Calculator</a>
    </footer>
</body>
</html>
