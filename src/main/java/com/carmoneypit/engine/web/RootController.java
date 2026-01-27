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

    @GetMapping(value = "/robots.txt", produces = "text/plain")
    @ResponseBody
    public String robots() {
        return """
                User-agent: *
                Allow: /
                Disallow: /verdict/share # Prevent indexing of temporary personal results
                Sitemap: https://carmoneypit.com/sitemap.xml
                """;
    }

    @GetMapping(value = "/sitemap.xml", produces = "application/xml")
    @ResponseBody
    public String sitemap() {
        // Simple static sitemap for now. Will be expanded dynamically in Phase 2.
        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
                    <url>
                        <loc>https://carmoneypit.com/</loc>
                        <changefreq>daily</changefreq>
                        <priority>1.0</priority>
                    </url>
                    <url>
                        <loc>https://carmoneypit.com/about</loc>
                        <changefreq>monthly</changefreq>
                        <priority>0.5</priority>
                    </url>
                    <url>
                        <loc>https://carmoneypit.com/methodology</loc>
                        <changefreq>monthly</changefreq>
                        <priority>0.8</priority>
                    </url>
                </urlset>
                """;
    }
}
