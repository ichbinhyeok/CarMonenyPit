@import com.carmoneypit.engine.api.FinancialLineItem
@import java.util.List

@param List<FinancialLineItem> items

<div class="futurist-receipt">
    <div class="receipt-header">
        <div class="col-lbl">Factor Mapping</div>
        <div class="col-lbl" style="text-align: right;">Impact Delta</div>
    </div>
    
    <div class="receipt-body">
        @for(FinancialLineItem item : items)
            <div class="receipt-item ${item.isNegative() ? "neg" : "pos"}">
                <div class="item-meta">
                    <span class="item-title">${item.label()}</span>
                    <span class="item-details">${item.description()}</span>
                </div>
                <div class="item-val">
                    ${item.isNegative() ? "-" : "+"}${item.amount()}
                </div>
            </div>
        @endfor
    </div>
    
    <div class="receipt-footer">
        Statistical confidence based on 2026 actuarial curves.
    </div>
</div>

<style>
    .futurist-receipt {
        background: #fff;
        border-radius: 20px;
        overflow: hidden;
    }

    .receipt-header {
        display: flex;
        justify-content: space-between;
        padding: 0.75rem 0;
        border-bottom: 1px solid #f1f5f9;
        margin-bottom: 1rem;
    }

    .col-lbl {
        font-size: 0.65rem;
        font-weight: 800;
        text-transform: uppercase;
        letter-spacing: 0.1em;
        color: var(--text-secondary);
        opacity: 0.6;
    }

    .receipt-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 1rem 0;
        border-bottom: 1px solid #f8fafc;
        transition: transform 0.2s;
    }
    .receipt-item:last-child { border-bottom: none; }

    .item-meta { display: flex; flex-direction: column; gap: 0.2rem; }
    .item-title { font-size: 0.95rem; font-weight: 700; color: #000; }
    .item-details { font-size: 0.8rem; color: var(--text-secondary); font-weight: 500; }

    .item-val {
        font-size: 1.1rem;
        font-weight: 800;
        letter-spacing: -0.03em;
    }

    .neg .item-val { color: #f43f5e; }
    .pos .item-val { color: #10b981; }
    
    .receipt-footer {
        margin-top: 1.5rem;
        font-size: 0.7rem;
        font-weight: 600;
        color: var(--text-secondary);
        opacity: 0.5;
        font-style: italic;
    }
</style>
