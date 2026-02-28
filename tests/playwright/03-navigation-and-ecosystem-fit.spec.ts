import { test, expect } from "@playwright/test";
import { assertHasAbsoluteCanonical, bodyText, extractSitemapPaths, firstPathMatching } from "./helpers/site-utils";

function unique<T>(arr: T[]): T[] {
  return [...new Set(arr)];
}

test.describe("Navigation depth and ecosystem-fit checks", () => {
  test("user journey: models -> brand -> model -> verdict -> lead capture", async ({ page }) => {
    await page.goto("/models");

    const brandPath = await page.evaluate(() => {
      const links = Array.from(document.querySelectorAll<HTMLAnchorElement>('a[href^="/models/"]'))
        .map((a) => a.getAttribute("href") || "")
        .filter((href) => /^\/models\/[a-z0-9-]+$/.test(href));
      return links[0] ?? "";
    });
    expect(brandPath).toBeTruthy();

    await page.goto(brandPath);
    const modelPath = await page.evaluate(() => {
      const links = Array.from(document.querySelectorAll<HTMLAnchorElement>('a[href^="/models/"]'))
        .map((a) => a.getAttribute("href") || "")
        .filter((href) => /^\/models\/[a-z0-9-]+\/[a-z0-9-]+$/.test(href));
      return links[0] ?? "";
    });
    expect(modelPath).toBeTruthy();

    await page.goto(modelPath);
    const verdictPath = await page.evaluate(() => {
      const links = Array.from(document.querySelectorAll<HTMLAnchorElement>('a[href^="/verdict/"]'))
        .map((a) => a.getAttribute("href") || "")
        .filter((href) => /^\/verdict\/[a-z0-9-]+\/[a-z0-9-]+\/(?!.*-miles$)[a-z0-9-]+$/.test(href));
      return links[0] ?? "";
    });
    expect(verdictPath).toBeTruthy();

    await page.goto(verdictPath);
    await expect(page.locator("h1")).toBeVisible();

    const leadHref = await page.evaluate(() => {
      const lead = document.querySelector<HTMLAnchorElement>('a[href^="/lead?"]');
      return lead?.getAttribute("href") ?? "";
    });
    expect(leadHref).toBeTruthy();

    await page.goto(leadHref);
    expect(page.url()).toContain("/lead-capture");
    await expect(page.locator("text=waitlist")).toBeVisible();
  });

  test("ecosystem baseline: sampled sitemap pages should have canonical + substantive body", async ({ page, request }) => {
    const xml = await (await request.get("/sitemap.xml")).text();
    const paths = extractSitemapPaths(xml);

    const samples = unique([
      firstPathMatching(paths, /^\/models\/[a-z0-9-]+$/),
      firstPathMatching(paths, /^\/models\/[a-z0-9-]+\/[a-z0-9-]+$/),
      firstPathMatching(paths, /^\/verdict\/[a-z0-9-]+\/[a-z0-9-]+\/[0-9]+-miles$/),
      firstPathMatching(paths, /^\/verdict\/[a-z0-9-]+\/[a-z0-9-]+\/(?!.*-miles$)[a-z0-9-]+$/),
      firstPathMatching(paths, /^\/should-i-fix\/[a-z0-9-]+$/),
      firstPathMatching(paths, /^\/fault\/[a-z0-9-]+$/)
    ]);

    const titleSet = new Set<string>();

    for (const path of samples) {
      const response = await request.get(path);
      expect(response.status(), `${path} should be reachable from sitemap`).toBe(200);

      await page.goto(path);
      await assertHasAbsoluteCanonical(page);
      await expect(page.locator("h1")).toBeVisible();

      const title = await page.title();
      expect(title.length, `${path} title should be descriptive`).toBeGreaterThan(20);
      titleSet.add(title);

      const text = await bodyText(page);
      expect(text.length, `${path} should not be thin`).toBeGreaterThan(1200);
      if (path.startsWith("/models/")) {
        expect(text).toContain("models");
      } else {
        expect(text).toContain("repair");
      }
    }

    expect(titleSet.size, "sampled pages should not collapse into duplicate titles").toBe(samples.length);
  });
});
