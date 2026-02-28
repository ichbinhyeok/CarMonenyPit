import { test, expect } from "@playwright/test";
import { extractSitemapPaths, firstPathMatching } from "./helpers/site-utils";

test.describe("Beta persona journeys", () => {
  test("search-landing user can move from should-i-fix to lead capture with clear handoff", async ({ page, request }) => {
    const sitemapXml = await (await request.get("/sitemap.xml")).text();
    const paths = extractSitemapPaths(sitemapXml);
    const shouldFixPath = firstPathMatching(paths, /^\/should-i-fix\/[a-z0-9-]+$/);
    const verdictPath = firstPathMatching(paths, /^\/verdict\/[a-z0-9-]+\/[a-z0-9-]+\/(?!.*-miles$)[a-z0-9-]+$/);

    await page.goto(shouldFixPath);
    await expect(page.locator("h1")).toBeVisible();

    let leadHref = await page.evaluate(() => {
      const cta = document.querySelector<HTMLAnchorElement>("#inlineCta");
      if (cta?.getAttribute("href")) {
        return cta.getAttribute("href") ?? "";
      }
      const fallback = document.querySelector<HTMLAnchorElement>('a[href^="/lead?"]');
      return fallback?.getAttribute("href") ?? "";
    });

    if (!leadHref) {
      await page.goto(verdictPath);
      await expect(page.locator("h1")).toBeVisible();
      leadHref = await page.evaluate(() => {
        const cta = document.querySelector<HTMLAnchorElement>('a[href^="/lead?"]');
        return cta?.getAttribute("href") ?? "";
      });
    }

    expect(leadHref, "lead handoff CTA should exist on search entry pages").toMatch(/^\/lead\?/);

    await page.goto(leadHref);

    // Pending approval flows to waitlist; approved flow may redirect externally.
    if (page.url().includes("/lead-capture")) {
      await expect(page.locator("h2")).toBeVisible();
      await expect(page.locator('form[action="/waitlist/submit"]')).toBeVisible();
    } else {
      expect(page.url(), "lead CTA should either route to waitlist or partner URL").toMatch(/^https?:\/\//);
    }
  });

  test("waitlist submission flow handles invalid and valid email states", async ({ request }) => {
    const invalid = await request.post("/waitlist/submit", {
      form: {
        email: "invalid-email-format",
        verdict: "TIME_BOMB",
        brand: "toyota",
        source: "playwright"
      },
      maxRedirects: 0
    });
    expect(invalid.status()).toBe(302);
    const invalidLocation = invalid.headers()["location"] ?? "";
    expect(invalidLocation).toContain("status=invalid_email");

    const valid = await request.post("/waitlist/submit", {
      form: {
        email: `playwright-${Date.now()}@example.com`,
        verdict: "TIME_BOMB",
        brand: "toyota",
        source: "playwright"
      },
      maxRedirects: 0
    });
    expect(valid.status()).toBe(302);
    const validLocation = valid.headers()["location"] ?? "";
    expect(validLocation).toContain("status=success");
  });

  test("mobile user can consume homepage and verdict without horizontal overflow", async ({ browser, request }) => {
    const context = await browser.newContext({
      viewport: { width: 390, height: 844 }
    });
    const page = await context.newPage();

    await page.goto("/");
    await expect(page.locator("h1")).toBeVisible();
    const homeNoOverflow = await page.evaluate(() => document.documentElement.scrollWidth <= window.innerWidth + 2);
    expect(homeNoOverflow, "homepage should not overflow horizontally on mobile").toBeTruthy();

    const sitemapXml = await (await request.get("/sitemap.xml")).text();
    const paths = extractSitemapPaths(sitemapXml);
    const verdictPath = firstPathMatching(paths, /^\/verdict\/[a-z0-9-]+\/[a-z0-9-]+\/(?!.*-miles$)[a-z0-9-]+$/);

    await page.goto(verdictPath);
    await expect(page.locator("h1")).toBeVisible();
    const verdictNoOverflow = await page.evaluate(() => document.documentElement.scrollWidth <= window.innerWidth + 2);
    expect(verdictNoOverflow, "verdict page should not overflow horizontally on mobile").toBeTruthy();

    await context.close();
  });
});
