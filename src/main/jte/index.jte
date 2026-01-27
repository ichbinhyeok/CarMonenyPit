@import com.carmoneypit.engine.api.InputModels.VehicleType
@import com.carmoneypit.engine.api.InputModels.CarBrand

@template.layout(
    title = "Car Money Pit | Precision Actuarial Analysis",
    extraStyles = @`
        <style>
            /* ... (existing styles) ... */
            .form-grid {
                display: grid;
                grid-template-columns: 1fr 1fr 1fr 1fr auto;
                gap: 1.0rem;
                align-items: flex-end;
            }
            /* ... */
        </style>
    `,
    content = @`
        <!-- ... -->
            <article class="card form-card" id="form-container">
                <form hx-post="/analyze" hx-target="#form-container" hx-swap="outerHTML" hx-push-url="false" class="form-grid">
                    <div class="input-group">
                        <label>Manufacturer</label>
                        <select name="brand" required>
                            @for(CarBrand brand : CarBrand.values())
                                <option value="${brand}">${brand}</option>
                            @endfor
                        </select>
                    </div>

                    <div class="input-group">
                        <label>Vehicle Type</label>
                        <select name="vehicleType" required>
                            @for(VehicleType type : VehicleType.values())
                                <option value="${type}">${type}</option>
                            @endfor
                        </select>
                    </div>

                    <div class="input-group">
                        <label>Mileage</label>
                        <input type="number" name="mileage" placeholder="120000" required>
                    </div>

                    <div class="input-group">
                        <div style="display: flex; justify-content: space-between; align-items: center;">
                            <label>Repair Quote ($)</label>
                            <label style="display: flex; align-items: center; gap: 4px; font-size: 0.7rem; cursor: pointer; text-transform: none; color: var(--text-secondary); font-weight: 600;">
                                <input type="checkbox" id="noQuoteToggle" onchange="toggleQuoteInput(this)" style="width: auto; margin: 0; padding: 0;">
                                No Quote
                            </label>
                        </div>
                        <input type="number" id="repairQuoteInput" name="repairQuoteUsd" placeholder="1500" required>
                        <input type="hidden" id="isQuoteMissing" name="isQuoteMissing" value="false">
                    </div>

                    <button type="submit" class="btn-futuristic">
                        Analyze <span>→</span>
                    </button>
                </form>
                <!-- ... script ... -->
            </article>
        <!-- ... -->
    `
)

                <script>
                    function toggleQuoteInput(checkbox) {
                        const input = document.getElementById('repairQuoteInput');
                        const hidden = document.getElementById('isQuoteMissing');
                        if (checkbox.checked) {
                            input.value = '';
                            input.disabled = true;
                            input.required = false;
                            input.placeholder = "Auto-Estimating...";
                            hidden.value = "true";
                        } else {
                            input.disabled = false;
                            input.required = true;
                            input.placeholder = "1500";
                            hidden.value = "false";
                        }
                    }
                </script>
            </article>

            <!-- Bento Secondary Cards -->
            <div class="card info-card">
                <div>
                    <div class="info-title">Global Data Correlated</div>
                    <p class="info-body">Synchronizing with 18,204 actuarial maintenance data points across all segments.</p>
                </div>
            </div>

            <div class="card info-card" style="background: linear-gradient(135deg, #0066ff05, #6366f108);">
                <div>
                    <div class="info-title">Instant Decisioning</div>
                    <p class="info-body">Zero wait time. Actuarial logic computed at the edge for immediate conviction.</p>
                </div>
            </div>

            <div class="card info-card">
                <div>
                    <div class="info-title">Equity Protection</div>
                    <p class="info-body">We prioritize your long-term wealth over emotional vehicle attachment.</p>
                </div>
            </div>
        </div>
    `
)
