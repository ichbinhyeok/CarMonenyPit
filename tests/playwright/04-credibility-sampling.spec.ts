import { test, expect } from "@playwright/test";
import { bodyText, extractSitemapPaths } from "./helpers/site-utils";

const bannedClaims = [
  "based on nada/kbb data",
  "actual repair databases",
  "team of data analysts",
  "team of automotive experts",
  "signal freshness",
  "freshness to google",
  "get 2-3 offers today",
  "take the best offer this week"
];

function pickPaths(paths: string[], regex: RegExp, count: number): string[] {
  const matched = paths.filter((path) => regex.test(path));
  return [...new Set(matched)].slice(0, count);
}

test.describe("Credibility sampling across sitemap", () => {
  test("sampled should-i-fix and verdict pages should maintain trust signals", async ({ page, request }) => {
    const sitemapXml = await (await request.get("/sitemap.xml")).text();
    const paths = extractSitemapPaths(sitemapXml);

    const shouldFixSamples = pickPaths(paths, /^\/should-i-fix\/[a-z0-9-]+$/, 3);
    const faultVerdictSamples = pickPaths(paths, /^\/verdict\/[a-z0-9-]+\/[a-z0-9-]+\/(?!.*-miles$)[a-z0-9-]+$/, 3);
    const mileageVerdictSamples = pickPaths(paths, /^\/verdict\/[a-z0-9-]+\/[a-z0-9-]+\/[0-9]+-miles$/, 2);

    expect(shouldFixSamples.length, "need should-i-fix pages in sitemap").toBeGreaterThan(0);
    expect(faultVerdictSamples.length, "need fault verdict pages in sitemap").toBeGreaterThan(0);
    expect(mileageVerdictSamples.length, "need mileage verdict pages in sitemap").toBeGreaterThan(0);

    for (const path of [...shouldFixSamples, ...faultVerdictSamples, ...mileageVerdictSamples]) {
      const response = await request.get(path);
      expect(response.status(), `${path} should return 200`).toBe(200);

      await page.goto(path);
      await expect(page.locator("h1")).toBeVisible();

      const text = await bodyText(page);
      expect(text.length, `${path} should have substantive content`).toBeGreaterThan(1300);
      expect(text.includes("repair") || text.includes("fix"), `${path} should discuss repair decision context`).toBeTruthy();
      expect(text.includes("value") || text.includes("market"), `${path} should discuss valuation context`).toBeTruthy();
      expect(/\$\d/.test(text), `${path} should include numeric/cost context`).toBeTruthy();

      if (path.startsWith("/verdict/")) {
        expect(
          text.includes("vehicle analysis summary") || text.includes("analysis summary"),
          `${path} should expose analysis framing`
        ).toBeTruthy();
      }

      for (const phrase of bannedClaims) {
        expect(text, `${path}: banned phrase detected -> ${phrase}`).not.toContain(phrase);
      }
    }
  });
});
