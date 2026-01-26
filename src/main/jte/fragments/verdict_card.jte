@import com.carmoneypit.engine.api.OutputModels.VerdictResult
@import com.carmoneypit.engine.api.OutputModels.VerdictState

@param VerdictResult result
@param String verdictTitle
@param String verdictExplanation
@param String verdictAction
@param String verdictCss

<article id="verdict-card" class="fade-in ${verdictCss}">
    <header>
        <hgroup>
            <h1>Verdict: <span>${verdictTitle}</span></h1>
            <p>Based on Regret Minimization Analysis</p>
        </hgroup>
    </header>
    
    <blockquote>
        "${verdictExplanation}"
    </blockquote>

    <div class="action-plan">
        <strong>Recommended Action:</strong>
        <p>${verdictAction}</p>
    </div>

    <footer>
        <small>Money Pit State: <strong>${result.visualizationHint().moneyPitState()}</strong></small>
        <!-- Debug Info (Hidden in prod, useful for now) -->
        <!-- RF: ${result.visualizationHint().rfScore()} | RM: ${result.visualizationHint().rmScore()} -->
    </footer>
</article>
