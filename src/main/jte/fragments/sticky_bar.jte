@import com.carmoneypit.engine.api.OutputModels.VerdictResult

@param VerdictResult result
@param String verdictTitle
@param String verdictExplanation
@param String verdictAction
@param String verdictCss
@param String leadLabel
@param String leadDescription
@param String leadUrl
@param boolean isValueEstimated = false
@param boolean oob = false

@if(oob)
    <div id="sticky-verdict-bar" class="sticky-bar ${verdictCss}" hx-swap-oob="true">
        <div class="sticky-content">
            <div class="sticky-left">
                <div class="status-dot"></div>
                <div class="sticky-text">
                    <span class="sticky-title">${verdictTitle}</span>
                    <span class="sticky-action-text">${verdictAction}</span>
                </div>
            </div>
            <a href="${leadUrl}" class="sticky-cta">
                ${leadLabel} <span>→</span>
            </a>
        </div>
    </div>
@else
    <div id="sticky-verdict-bar" class="sticky-bar ${verdictCss}">
        <div class="sticky-content">
            <div class="sticky-left">
                <div class="status-dot"></div>
                <div class="sticky-text">
                    <span class="sticky-title">${verdictTitle}</span>
                    <span class="sticky-action-text">${verdictAction}</span>
                </div>
            </div>
            <a href="${leadUrl}" class="sticky-cta">
                ${leadLabel} <span>→</span>
            </a>
        </div>
    </div>
@endif
