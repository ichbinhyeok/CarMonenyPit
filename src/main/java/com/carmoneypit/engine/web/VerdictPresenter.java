package com.carmoneypit.engine.web;

import com.carmoneypit.engine.api.OutputModels.VerdictState;
import org.springframework.stereotype.Service;

@Service
public class VerdictPresenter {

    public String getVerdictTitle(VerdictState state) {
        switch (state) {
            case TIME_BOMB:
                return "TERMINATE";
            case STABLE:
                return "SUSTAIN";
            case BORDERLINE:
                return "PROBATION";
            default:
                return "UNKNOWN";
        }
    }

    public String getLawyerExplanation(VerdictState state) {
        switch (state) {
            case TIME_BOMB:
                return "This vehicle has become a liability rather than an asset. The math proves that holding onto it is actively destroying your wealth. Every dollar spent on repair today is a dollar lost forever.";
            case STABLE:
                return "While painful, this repair is the most strictly economical choice compared to the high cost of buying a replacement vehicle in today's market.";
            case BORDERLINE:
            default:
                return "You are in the danger zone where emotional attachment masks financial risk. The numbers represent a toss-up, which effectively means 'No'.";
        }
    }

    public String getActionPlan(VerdictState state) {
        switch (state) {
            case TIME_BOMB:
                return "Do not repair. Sell immediately as 'mechanic special' or trade it in.";
            case STABLE:
                return "Authorize the repair, but demand a 12-month warranty on the work. Keep the vehicle for at least 6 more months to amortize this cost.";
            case BORDERLINE:
            default:
                return "Only proceed if you can negotiate the repair bill down by 15% or more. If not, this is your signal to exit.";
        }
    }

    public String getCssClass(VerdictState state) {
        switch (state) {
            case TIME_BOMB:
                return "verdict-terminate";
            case STABLE:
                return "verdict-sustain";
            case BORDERLINE:
                return "verdict-probation";
            default:
                return "verdict-unknown";
        }
    }
}
