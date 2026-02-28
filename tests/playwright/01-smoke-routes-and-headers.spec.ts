import { test, expect } from "@playwright/test";

test.describe("Smoke: routes and transport headers", () => {
  const staticPaths = ["/", "/about", "/contact", "/models", "/privacy", "/terms", "/robots.txt", "/sitemap.xml"];

  for (const path of staticPaths) {
    test(`GET ${path} should be healthy`, async ({ request }) => {
      const response = await request.get(path);
      expect(response.status(), `${path} should return 200`).toBe(200);
      const body = await response.text();
      expect(body.length, `${path} should return meaningful content`).toBeGreaterThan(80);
    });
  }

  test("lead endpoint should be tracked with no-store redirect", async ({ request }) => {
    const response = await request.get(
      "/lead?page_type=playwright&intent=SELL&verdict_state=TIME_BOMB&brand=toyota&model=camry",
      { maxRedirects: 0 }
    );
    expect(response.status()).toBe(302);

    const headers = response.headers();
    expect(headers["cache-control"]).toContain("no-store");
    expect(headers["pragma"]).toBe("no-cache");
    expect(headers["expires"]).toBe("0");
    expect(headers["location"] ?? "", "approvalPending=true should route internally").toContain("/lead-capture");
  });

  test("invalid report token should be redirect + noindex", async ({ request }) => {
    const response = await request.get("/report?token=invalid-playwright-token", { maxRedirects: 0 });
    expect(response.status()).toBe(302);
    const headers = response.headers();
    expect(headers["x-robots-tag"]).toContain("noindex");
    expect(headers["location"], "invalid report tokens should not render a public page").toBeTruthy();
  });

  test("robots and sitemap should look coherent", async ({ request }) => {
    const robots = await request.get("/robots.txt");
    const sitemap = await request.get("/sitemap.xml");

    expect(robots.status()).toBe(200);
    expect(sitemap.status()).toBe(200);

    const robotsBody = (await robots.text()).toLowerCase();
    const sitemapBody = await sitemap.text();

    expect(robotsBody).toContain("sitemap:");
    expect(robotsBody).toContain("disallow: /report?");
    expect(sitemapBody).toContain("<urlset");
    expect((sitemapBody.match(/<url>/g) ?? []).length, "sitemap should not be trivially small").toBeGreaterThan(100);
  });
});
