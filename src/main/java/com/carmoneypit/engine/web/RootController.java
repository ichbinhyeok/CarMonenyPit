package com.carmoneypit.engine.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RootController {


    @GetMapping("/about")
    public String about() {
        return "pages/about";
    }

    @GetMapping("/methodology")
    public String methodology() {
        return "pages/methodology";
    }

    @GetMapping("/privacy")
    public String privacy() {
        return "pages/privacy";
    }

    @GetMapping("/terms")
    public String terms() {
        return "pages/terms";
    }

    @GetMapping("/contact")
    public String contact() {
        return "pages/contact";
    }

    @GetMapping(value = "/robots.txt", produces = "text/plain")
    @ResponseBody
    public String robots() {
        return """
                User-agent: *
                Allow: /
                Disallow: /verdict/share # Prevent indexing of temporary personal results
                Sitemap: https://automoneypit.com/sitemap.xml
                """;
    }

}

