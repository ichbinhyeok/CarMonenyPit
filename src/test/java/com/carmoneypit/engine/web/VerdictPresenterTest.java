package com.carmoneypit.engine.web;

import com.carmoneypit.engine.api.InputModels.EngineInput;
import com.carmoneypit.engine.api.InputModels.VehicleType;
import com.carmoneypit.engine.api.OutputModels.VerdictState;
import com.carmoneypit.engine.config.PartnerRoutingConfig;
import com.carmoneypit.engine.core.ValuationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class VerdictPresenterTest {

    private final ValuationService valuationService = mock(ValuationService.class);

    @Test
    void waitlistModeShouldUseHonestSellCtaCopy() {
        PartnerRoutingConfig routingConfig = new PartnerRoutingConfig();
        routingConfig.setApprovalPending(true);
        VerdictPresenter presenter = new VerdictPresenter(new ObjectMapper(), valuationService, routingConfig);
        EngineInput input = new EngineInput("Camry", VehicleType.SEDAN, "TOYOTA", 2018, 120000, 4200, 9500, false,
                false);

        assertEquals("Join Sell Alert", presenter.getLeadLabel(VerdictState.TIME_BOMB, input, null));
        assertEquals(
                "We are not issuing live offers yet. Join the alert list and we will notify you when broken-car buyer access is live.",
                presenter.getLeadDescription(VerdictState.TIME_BOMB, input, null));
    }

    @Test
    void livePartnerModeShouldKeepDirectSellCtaCopy() {
        PartnerRoutingConfig routingConfig = new PartnerRoutingConfig();
        routingConfig.setApprovalPending(false);
        VerdictPresenter presenter = new VerdictPresenter(new ObjectMapper(), valuationService, routingConfig);
        EngineInput input = new EngineInput("Camry", VehicleType.SEDAN, "TOYOTA", 2018, 120000, 4200, 9500, false,
                false);

        assertEquals("Get Instant Cash Offer", presenter.getLeadLabel(VerdictState.TIME_BOMB, input, null));
    }
}
