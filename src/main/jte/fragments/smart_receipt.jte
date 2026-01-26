@import com.carmoneypit.engine.api.FinancialLineItem
@import java.util.List

@param List<FinancialLineItem> items

<div class="receipt-container">
    <div class="receipt-paper">
        <div class="receipt-header">
            <div class="receipt-logo">2026_ACTUARIAL_HUD</div>
            <div class="receipt-title">PREDICTIVE BILLING</div>
            <div class="receipt-date">TRANS_ID: ${java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase()}</div>
        </div>
        
        <div class="receipt-divider">--------------------------------</div>
        
        <div class="receipt-items">
            @for(FinancialLineItem item : items)
                <div class="receipt-item">
                    <div class="item-main">
                        <span class="item-label">${item.label().toUpperCase()}</span>
                        <span class="item-amount">$${String.format("%,.0f", item.amount())}</span>
                    </div>
                    <div class="item-sub">>> ${item.description()}</div>
                </div>
            @endfor
        </div>
        
        <div class="receipt-divider">--------------------------------</div>
        
        <div class="receipt-footer">
            <div class="footer-msg">ACTUARIAL RISK DETECTED</div>
            <div class="footer-warning">THIS IS NOT A QUOTE, IT IS A WARNING</div>
            <div class="barcode">|| |||| ||| ||||| || ||| ||</div>
        </div>
    </div>
</div>

<style>
    .receipt-container {
        display: flex;
        justify-content: center;
        padding: 1rem 0;
        filter: drop-shadow(0 10px 20px rgba(0,0,0,0.05));
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
        opacity: 0.2;
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
        font-size: 0.7rem;
        opacity: 0.5;
        margin-top: 0.2rem;
        line-height: 1.3;
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
