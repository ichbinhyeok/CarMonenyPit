import { test, expect } from "@playwright/test";
import { assertHasAbsoluteCanonical, assertLdJsonParsable, bodyText, extractSitemapPaths, firstPathMatching } from "./helpers/site-utils";

const bannedClaims = [
  "based on nada/kbb data",
  "actual repair databases",
  "get 2-3 offers today",
  "take the best offer this week",
  "peddle, carmax, and local dealers",
  "signal freshness",
  "freshness to google"
];

test.describe("Content trust and quality", () => {
  test("homepage should expose decision context, not just CTA", async ({ page }) => {
    await page.goto("/");
    await expect(page.locator("h1")).toBeVisible();

    const text = await bodyText(page);
    expect(text).toContain("fix it or");
    expect(text).toContain("sell it");
    expect(text).toContain("example analyses");
    await expect(page.locator("text=Affiliate Disclosure")).toBeVisible();
    await expect(page.locator("text=Disclaimer")).toBeVisible();
  });

  test("sample should-i-fix page should be content-rich and policy-safer", async ({ page, request }) => {
    const sitemapXml = await (await request.get("/sitemap.xml")).text();
    const paths = extractSitemapPaths(sitemapXml);
    const shouldFixPath = firstPathMatching(paths, /^\/should-i-fix\/[a-z0-9-]+$/);

    await page.goto(shouldFixPath);
    await expect(page.locator("h1")).toBeVisible();
    await expect(page.locator("text=Frequently Asked Questions")).toBeVisible();
    await expect(page.locator("text=Factors We Consider")).toBeVisible();

    const text = await bodyText(page);
    expect(text.length, "content should be deep enough to help users").toBeGreaterThan(1800);
    expect(text).toContain("market");
    expect(text).toContain("repair");
    expect(text).toContain("value");

    for (const phrase of bannedClaims) {
      expect(text, `banned phrase detected: ${phrase}`).not.toContain(phrase);
    }

    await assertHasAbsoluteCanonical(page);
    await assertLdJsonParsable(page);
  });

  test("sample verdict page should present evidence + method + disclosure", async ({ page, request }) => {
    const sitemapXml = await (await request.get("/sitemap.xml")).text();
    const paths = extractSitemapPaths(sitemapXml);
    const verdictPath = firstPathMatching(paths, /^\/verdict\/[a-z0-9-]+\/[a-z0-9-]+\/(?!.*-miles$)[a-z0-9-]+$/);

    await page.goto(verdictPath);
    await expect(page.locator("h1")).toBeVisible();
    await expect(page.locator("text=Vehicle Analysis Summary")).toBeVisible();
    await expect(page.locator("text=Should You Repair or Sell?")).toBeVisible();
    await expect(page.locator("text=Frequently Asked Questions")).toBeVisible();

    const text = await bodyText(page);
    expect(
      text.includes("affiliate disclosure") || text.includes("disclaimer"),
      "verdict pages should expose trust/disclosure context"
    ).toBeTruthy();
    expect(
      text.includes("for informational purposes only") || text.includes("for informational and entertainment purposes only"),
      "verdict pages should clearly scope advisory limitations"
    ).toBeTruthy();
    expect(text).toContain("cost");
    expect(text).toContain("market");
    expect(/\$\d/.test(text), "verdict pages should include concrete numerical context").toBeTruthy();

    for (const phrase of bannedClaims) {
      expect(text, `banned phrase detected: ${phrase}`).not.toContain(phrase);
    }

    await assertHasAbsoluteCanonical(page);
    await assertLdJsonParsable(page);
  });
});
