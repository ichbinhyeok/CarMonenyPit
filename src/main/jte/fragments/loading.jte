@import com.carmoneypit.engine.api.InputModels.VehicleType
@import com.carmoneypit.engine.api.InputModels.CarBrand

@param VehicleType vehicleType
@param CarBrand brand
@param long mileage
@param long repairQuoteUsd
@param long currentValueUsd
@param boolean isValueEstimated
@param boolean isQuoteEstimated = false
@param String modelName = "Other"

<div id="loading-screen" class="card form-card scan-stage" 
     hx-post="/analyze-final" 
     hx-trigger="load delay:2800ms" 
     hx-target="body" 
     hx-push-url="/analyze"
     hx-vals='{"vehicleType": "${vehicleType}", "brand": "${brand}", "model": "${modelName}", "mileage": ${mileage}, "repairQuoteUsd": ${repairQuoteUsd}, "currentValueUsd": ${currentValueUsd}, "isValueEstimated": ${isValueEstimated}, "isQuoteEstimated": ${isQuoteEstimated}}'>
    
    <div class="scan-visual">
        <div class="pulse-ring"></div>
        <div class="pulse-ring delay-1"></div>
        <div class="center-core"></div>
    </div>
    
    <h2 class="status-msg">Initiating Actuarial Scan</h2>
    
    <div class="log-output">
        <div class="log-line" style="animation-delay: 0.2s;"><span>></span> Correlating ${brand} ${modelName} asset tables...</div>
        <div class="log-line" style="animation-delay: 1.0s;"><span>></span> Processing ${mileage}mi depreciation nodes...</div>
        <div class="log-line" style="animation-delay: 1.8s;"><span>></span> Evaluating cascade failure probability...</div>
    </div>
</div>

<style>
    .scan-stage {
        text-align: center;
        width: 100%;
        max-width: 500px;
        margin: auto;
        padding: 4rem 2rem !important;
        background: #fff;
    }

    .scan-visual {
        position: relative;
        width: 100px;
        height: 100px;
        margin: 0 auto 3rem;
        display: flex;
        align-items: center;
        justify-content: center;
    }

    .pulse-ring {
        position: absolute;
        width: 100%;
        height: 100%;
        border: 2px solid var(--futurist-blue);
        border-radius: 50%;
        opacity: 0;
        animation: ringPulse 2s cubic-bezier(0.16, 1, 0.3, 1) infinite;
    }
    .delay-1 { animation-delay: 0.6s; }

    .center-core {
        width: 20px;
        height: 20px;
        background: var(--futurist-blue);
        border-radius: 50%;
        box-shadow: 0 0 30px var(--futurist-blue);
    }

    .status-msg {
        font-size: 1.4rem;
        font-weight: 800;
        letter-spacing: -0.02em;
        margin-bottom: 2rem;
    }

    .log-output {
        font-family: 'Plus Jakarta Sans', sans-serif;
        text-align: left;
        max-width: 320px;
        margin: 0 auto;
        display: flex;
        flex-direction: column;
        gap: 0.8rem;
    }

    .log-line {
        font-size: 0.85rem;
        font-weight: 600;
        color: var(--text-secondary);
        opacity: 0;
        transform: translateY(10px);
        animation: logEntry 0.4s ease-out forwards;
        display: flex;
        gap: 0.6rem;
    }
    .log-line span { color: var(--futurist-blue); }

    @keyframes ringPulse {
        0% { transform: scale(0.5); opacity: 0.8; }
        100% { transform: scale(2); opacity: 0; }
    }

    @keyframes logEntry {
        to { opacity: 1; transform: translateY(0); }
    }
</style>
