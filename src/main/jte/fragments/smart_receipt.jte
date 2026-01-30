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
            <div class="receipt-title">Cost Breakdown</div>
            <div class="receipt-subtitle">Click any item to see how we calculated it</div>
        </div>
        
        <!-- SECTION 1: IF YOU FIX IT -->
        <div class="receipt-section-label">If You Fix It</div>
        <div class="receipt-items">
            @for(FinancialLineItem item : stayItems)
                <div class="receipt-item expandable" onclick="this.classList.toggle('expanded')">
                    <div class="item-main">
                        <span class="item-label">${item.label()}</span>
                        <div style="display: flex; align-items: center; gap: 0.5rem;">
                            <span class="item-amount" style="${item.amount() < 0 ? "color: #10b981;" : ""}">$${String.format("%,.0f", Math.round(item.amount() / 100.0) * 100.0)}</span>
                            <span class="expand-icon">▼</span>
                        </div>
                    </div>
                    <div class="item-explanation">${item.description()}</div>
                </div>
            @endfor
            <div class="receipt-item total-row">
                <div class="item-main">
                    <span class="item-label" style="font-weight: 800;">Total Cost to Keep</span>
                    <span class="item-amount" style="font-weight: 800; color: #dc2626;">$${String.format("%,.0f", Math.round(stayTotal / 100.0) * 100.0)}</span>
                </div>
            </div>
        </div>

        <div class="section-divider"></div>

        <!-- SECTION 2: IF YOU SELL IT -->
        <div class="receipt-section-label">If You Sell It</div>
        <div class="receipt-items">
            @for(FinancialLineItem item : moveItems)
                <div class="receipt-item expandable" onclick="this.classList.toggle('expanded')">
                    <div class="item-main">
                        <span class="item-label">${item.label()}</span>
                        <div style="display: flex; align-items: center; gap: 0.5rem;">
                            <span class="item-amount" style="${item.amount() < 0 ? "color: #10b981;" : ""}">$${String.format("%,.0f", Math.round(item.amount() / 100.0) * 100.0)}</span>
                            <span class="expand-icon">▼</span>
                        </div>
                    </div>
                    <div class="item-explanation">${item.description()}</div>
                </div>
            @endfor
            <div class="receipt-item total-row">
                <div class="item-main">
                    <span class="item-label" style="font-weight: 800;">Total Cost to Switch</span>
                    <span class="item-amount" style="font-weight: 800; color: #6366f1;">$${String.format("%,.0f", Math.round(moveTotal / 100.0) * 100.0)}</span>
                </div>
            </div>
        </div>
        
        <div class="section-divider"></div>
        
        <!-- BOTTOM LINE -->
        <div class="receipt-conclusion">
            @if(stayTotal > moveTotal)
                <div class="conclusion-badge" style="background: #fef2f2; color: #dc2626;">
                    Fixing costs $${String.format("%,.0f", stayTotal - moveTotal)} more
                </div>
                <p class="conclusion-text">Based on these numbers, selling looks like the smarter financial move.</p>
            @else
                <div class="conclusion-badge" style="background: #f0fdf4; color: #16a34a;">
                    Selling costs $${String.format("%,.0f", moveTotal - stayTotal)} more
                </div>
                <p class="conclusion-text">Based on these numbers, fixing your car makes more sense.</p>
            @endif
        </div>
        
        <div class="receipt-disclaimer">
            * All figures are estimates based on market data and historical patterns.
        </div>
    </div>
</div>

<style>
    .receipt-container {
        display: flex;
        justify-content: center;
        padding: 1rem 0;
    }

    .receipt-paper {
        background: #fff;
        width: 100%;
        max-width: 420px;
        padding: 2rem;
        border-radius: 24px;
        border: 1px solid #e2e8f0;
        box-shadow: 0 10px 40px rgba(0,0,0,0.06);
    }

    @media (max-width: 480px) {
        .receipt-paper { padding: 1.5rem 1rem; }
    }

    .receipt-header {
        text-align: center;
        margin-bottom: 1.5rem;
    }

    .receipt-title {
        font-size: 1.25rem;
        font-weight: 800;
        color: #0f172a;
    }

    .receipt-subtitle {
        font-size: 0.8rem;
        color: #64748b;
        margin-top: 0.25rem;
    }

    .receipt-section-label {
        font-size: 0.7rem;
        font-weight: 800;
        text-transform: uppercase;
        letter-spacing: 0.1em;
        color: #94a3b8;
        margin-bottom: 0.75rem;
        padding-left: 0.25rem;
    }

    .receipt-items {
        display: flex;
        flex-direction: column;
        gap: 0.5rem;
    }

    .receipt-item {
        padding: 0.75rem 1rem;
        background: #f8fafc;
        border-radius: 12px;
        transition: all 0.2s ease;
    }

    .receipt-item.expandable {
        cursor: pointer;
    }

    .receipt-item.expandable:hover {
        background: #f1f5f9;
    }

    .receipt-item.total-row {
        background: transparent;
        border-top: 2px solid #e2e8f0;
        border-radius: 0;
        margin-top: 0.5rem;
        padding-top: 1rem;
    }

    .item-main {
        display: flex;
        justify-content: space-between;
        align-items: center;
        font-weight: 600;
        font-size: 0.95rem;
        color: #1e293b;
    }

    .item-amount {
        color: #dc2626;
        font-weight: 700;
    }

    .expand-icon {
        font-size: 0.6rem;
        color: #94a3b8;
        transition: transform 0.2s;
    }

    .receipt-item.expanded .expand-icon {
        transform: rotate(180deg);
    }

    .item-explanation {
        font-size: 0.8rem;
        color: #64748b;
        line-height: 1.5;
        margin-top: 0;
        max-height: 0;
        overflow: hidden;
        transition: all 0.2s ease;
    }

    .receipt-item.expanded .item-explanation {
        margin-top: 0.75rem;
        max-height: 100px;
        padding-top: 0.75rem;
        border-top: 1px dashed #e2e8f0;
    }

    .section-divider {
        height: 1px;
        background: #e2e8f0;
        margin: 1.5rem 0;
    }

    .receipt-conclusion {
        text-align: center;
        padding: 1rem;
    }

    .conclusion-badge {
        display: inline-block;
        padding: 0.5rem 1rem;
        border-radius: 100px;
        font-weight: 800;
        font-size: 0.9rem;
        margin-bottom: 0.75rem;
    }

    .conclusion-text {
        font-size: 0.85rem;
        color: #64748b;
        margin: 0;
    }

    .receipt-disclaimer {
        font-size: 0.7rem;
        color: #94a3b8;
        text-align: center;
        margin-top: 1.5rem;
    }
</style>

