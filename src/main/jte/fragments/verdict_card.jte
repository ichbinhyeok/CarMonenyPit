@import com.carmoneypit.engine.api.OutputModels.VerdictResult
@import com.carmoneypit.engine.api.OutputModels.VerdictState

@param VerdictResult result

<article id="verdict-card" class="fade-in">
    <header>
        <hgroup>
            <h1>Verdict: 
                @if(result.verdictState() == VerdictState.STABLE)
                    <span style="color: #2ed573">STABLE (Green)</span>
                @elseif(result.verdictState() == VerdictState.BORDERLINE)
                    <span style="color: #ffa502">BORDERLINE (Yellow)</span>
                @else
                    <span style="color: #ff4757">TIME BOMB (Red)</span>
                @endif
            </h1>
            <p>Based on Regret Minimization Analysis</p>
        </hgroup>
    </header>
    
    <blockquote>
        "${result.narrativeContext()}"
    </blockquote>

    <footer>
        <small>Money Pit State: <strong>${result.visualizationHint().moneyPitState()}</strong></small>
        <!-- Debug Info (Hidden in prod, useful for now) -->
        <!-- RF: ${result.visualizationHint().rfScore()} | RM: ${result.visualizationHint().rmScore()} -->
    </footer>
</article>
