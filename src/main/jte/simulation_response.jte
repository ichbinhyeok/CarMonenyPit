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

@template.fragments.verdict_card(result = result, verdictTitle = verdictTitle, verdictExplanation = verdictExplanation, verdictAction = verdictAction, verdictCss = verdictCss, leadLabel = leadLabel, leadDescription = leadDescription, leadUrl = leadUrl, isValueEstimated = isValueEstimated)

@template.fragments.sticky_bar(result = result, verdictTitle = verdictTitle, verdictExplanation = verdictExplanation, verdictAction = verdictAction, verdictCss = verdictCss, leadLabel = leadLabel, leadDescription = leadDescription, leadUrl = leadUrl, isValueEstimated = isValueEstimated, oob = true)
