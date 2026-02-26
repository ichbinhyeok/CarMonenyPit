package com.carmoneypit.engine.web;

import com.carmoneypit.engine.service.FaultHubService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Controller
public class FaultController {

    private final FaultHubService faultHubService;
    private final String baseUrl;

    public FaultController(FaultHubService faultHubService,
            @org.springframework.beans.factory.annotation.Value("${app.baseUrl:https://automoneypit.com}") String baseUrl) {
        this.faultHubService = faultHubService;
        this.baseUrl = baseUrl;
    }

    @GetMapping("/faults")
    public String faultsIndex(Model model) {
        List<FaultHubViewModel> hubs = faultHubService.getAllHubSummaries();
        model.addAttribute("hubs", hubs);
        model.addAttribute("canonicalUrl", baseUrl + "/faults");
        model.addAttribute("breadcrumbs", List.of(
                Map.of("label", "Home", "url", "/"),
                Map.of("label", "Common Faults", "url", "/faults")));
        return "faults_index";
    }

    @GetMapping("/fault/{faultSlug}")
    public String faultHub(@PathVariable String faultSlug, Model model) {
        // Strict slug validation — only allowed slugs render
        FaultHubViewModel hub = faultHubService.getHub(faultSlug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        model.addAttribute("hub", hub);
        model.addAttribute("canonicalUrl", baseUrl + "/fault/" + faultSlug);
        model.addAttribute("breadcrumbs", List.of(
                Map.of("label", "Home", "url", "/"),
                Map.of("label", "Common Faults", "url", "/faults"),
                Map.of("label", hub.displayName(), "url", "/fault/" + faultSlug)));
        return "fault_hub";
    }
}
