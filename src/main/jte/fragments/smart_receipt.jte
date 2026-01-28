@import com.carmoneypit.engine.api.FinancialLineItem
@import com.carmoneypit.engine.api.ItemCategory
@import java.util.List
@import java.util.ArrayList

@param List<FinancialLineItem> items

!{
    List<FinancialLineItem> stayItems = new ArrayList<>();
    List<FinancialLineItem> moveItems = new ArrayList<>();
    double stayTotal = 0;
    double moveTotal = 0;
    
    for (FinancialLineItem item : items) {
        if (item.category() == ItemCategory.STAY) {
            stayItems.add(item);
            stayTotal += item.amount();
        } else {
            moveItems.add(item);
            moveTotal += item.amount();
        }
    }
}

<div class="receipt-container">
    <div class="receipt-paper">
        <div class="receipt-header">
            <div class="receipt-logo">2026_ACTUARIAL_HUD</div>
            <div class="receipt-title">DECISION AUDIT</div>
            <div class="receipt-date">CORRELATION_ID: ${java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase()}</div>
        </div>
        
        <!-- SECTION 1: STAY -->
        <div class="receipt-divider">STAY_PATH_LIABILITY // [A]</div>
        <div class="receipt-items">
            @for(FinancialLineItem item : stayItems)
                <div class="receipt-item">
                    <div class="item-main">
                        <span class="item-label">${item.label().toUpperCase()}</span>
                        <span class="item-amount">$${String.format("%,.0f", item.amount())}</span>
                    </div>
                    <div class="item-sub">>> ${item.description()}</div>
                </div>
            @endfor
            <div class="receipt-item total-row" style="border-top: 1px solid #000; padding-top: 0.5rem; margin-top: 0.5rem;">
                <div class="item-main" style="color: #d91b1b;">
                    <span class="item-label" style="font-weight: 900;">TOTAL RETENTION COST</span>
                    <span class="item-amount" style="font-weight: 900;">$${String.format("%,.0f", stayTotal)}</span>
                </div>
            </div>
        </div>

        <!-- SECTION 2: MOVE -->
        <div class="receipt-divider">EXIT_PATH_FRICTION // [B]</div>
        <div class="receipt-items">
            @for(FinancialLineItem item : moveItems)
                <div class="receipt-item">
                    <div class="item-main">
                        <span class="item-label">${item.label().toUpperCase()}</span>
                        <span class="item-amount">$${String.format("%,.0f", item.amount())}</span>
                    </div>
                    <div class="item-sub">>> ${item.description()}</div>
                </div>
            @endfor
            <div class="receipt-item total-row" style="border-top: 1px solid #000; padding-top: 0.5rem; margin-top: 0.5rem;">
                <div class="item-main" style="color: #1a1a1a;">
                    <span class="item-label" style="font-weight: 900;">TOTAL SWITCHING FRICTION</span>
                    <span class="item-amount" style="font-weight: 900;">$${String.format("%,.0f", moveTotal)}</span>
                </div>
            </div>
        </div>
        
        <div class="receipt-divider" style="opacity: 0.6;">--------------------------------</div>
        
        <div class="receipt-footer">
            <div class="footer-msg">SUMMARY VERDICT</div>
            <div class="footer-warning" style="font-size: 0.8rem; font-weight: 800; color: ${stayTotal > moveTotal ? "#d91b1b" : "#10b981"};">
                ${stayTotal > moveTotal ? "RETENTION_LIABILITY_EXCEEDED" : "RETENTION_JUSTIFIED"}
            </div>
            <div class="barcode">|| |||| ||| ||||| || ||| ||</div>
            <div style="margin-top: 1rem; font-size: 0.6rem; opacity: 0.5; text-align: center;">
                * All figures are actuarial estimates based on market data.
            </div>
        </div>
    </div>
</div>

<style>
    .receipt-container {
        display: flex;
        justify-content: center;
        padding: 2rem 0;
        filter: drop-shadow(0 20px 40px rgba(0,0,0,0.15));
    }

    .receipt-paper {
        background: #fdfdfb;
        width: 100%;
        max-width: 400px;
        padding: 2.5rem 1.5rem;
        font-family: 'Courier New', Courier, monospace;
        color: #1a1a1a;
        position: relative;
        border-radius: 2px;
        box-shadow: inset 0 0 50px rgba(0,0,0,0.02);
        border: 1px solid rgba(0,0,0,0.08); /* Added subtle border for contrast */
    }

    .receipt-paper::after {
        content: "";
        position: absolute;
        bottom: -10px;
        left: 0;
        width: 100%;
        height: 10px;
        background: radial-gradient(circle, transparent, transparent 50%, #fdfdfb 50%, #fdfdfb) 0 0 / 20px 20px;
        background-repeat: repeat-x;
    }

    .receipt-header {
        text-align: center;
        margin-bottom: 1.5rem;
    }

    .receipt-logo {
        font-size: 0.75rem;
        font-weight: 900;
        letter-spacing: 0.1em;
        margin-bottom: 0.25rem;
    }

    .receipt-title {
        font-size: 1.25rem;
        font-weight: 900;
        margin-bottom: 0.5rem;
    }

    .receipt-date {
        font-size: 0.7rem;
        opacity: 0.6;
    }

    .receipt-divider {
        text-align: center;
        letter-spacing: -2px;
        color: #9ca3af; /* clearer grey color instead of opacity */
        margin: 1rem 0;
        user-select: none;
    }

    .receipt-items {
        display: flex;
        flex-direction: column;
        gap: 1.25rem;
    }

    .receipt-item {
        display: flex;
        flex-direction: column;
    }

    .item-main {
        display: flex;
        justify-content: space-between;
        font-weight: 700;
        font-size: 0.95rem;
    }

    .item-amount {
        color: #d91b1b;
    }

    .item-sub {
        font-size: 0.8rem; /* Increased from 0.7rem (~11px -> ~13px) */
        color: #555555;    /* Solid dark grey, removed opacity */
        margin-top: 0.35rem;
        line-height: 1.4;
    }

    .receipt-footer {
        margin-top: 2rem;
        text-align: center;
    }

    .footer-msg {
        font-weight: 900;
        font-size: 0.8rem;
        margin-bottom: 0.25rem;
    }

    .footer-warning {
        font-size: 0.6rem;
        opacity: 0.7;
        margin-bottom: 1rem;
    }

    .barcode {
        font-size: 1.2rem;
        letter-spacing: 0.1em;
        opacity: 0.3;
    }
</style>
