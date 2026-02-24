package com.carmoneypit.engine.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.partner")
public class PartnerRoutingConfig {

    private boolean approvalPending = true;

    // Default Waitlist/Lead Capture Forms (Fallback)
    private String waitlistUrl = "/lead-capture";

    // Authorized Partner URLs
    private String sellPartnerUrl = "https://www.peddle.com/instant-offer?utm_source=automoneypit";
    private String warrantyPartnerUrl = "https://www.endurancewarranty.com/get-quote/?ref=automoneypit";
    private String repairPartnerUrl = "https://repairpal.com/estimator?utm_source=automoneypit";
    private String marketValuePartnerUrl = "https://www.kbb.com/?utm_source=automoneypit";

    public boolean isApprovalPending() {
        return approvalPending;
    }

    public void setApprovalPending(boolean approvalPending) {
        this.approvalPending = approvalPending;
    }

    public String getWaitlistUrl() {
        return waitlistUrl;
    }

    public void setWaitlistUrl(String waitlistUrl) {
        this.waitlistUrl = waitlistUrl;
    }

    public String getSellPartnerUrl() {
        return sellPartnerUrl;
    }

    public void setSellPartnerUrl(String sellPartnerUrl) {
        this.sellPartnerUrl = sellPartnerUrl;
    }

    public String getWarrantyPartnerUrl() {
        return warrantyPartnerUrl;
    }

    public void setWarrantyPartnerUrl(String warrantyPartnerUrl) {
        this.warrantyPartnerUrl = warrantyPartnerUrl;
    }

    public String getRepairPartnerUrl() {
        return repairPartnerUrl;
    }

    public void setRepairPartnerUrl(String repairPartnerUrl) {
        this.repairPartnerUrl = repairPartnerUrl;
    }

    public String getMarketValuePartnerUrl() {
        return marketValuePartnerUrl;
    }

    public void setMarketValuePartnerUrl(String marketValuePartnerUrl) {
        this.marketValuePartnerUrl = marketValuePartnerUrl;
    }
}
