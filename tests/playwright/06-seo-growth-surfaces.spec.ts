import { test, expect } from "@playwright/test";
import { mkdirSync } from "node:fs";
import { bodyText } from "./helpers/site-utils";

const evidenceDirectory = "output/seo-browser-qa";

test.beforeAll(() => {
  mkdirSync(evidenceDirectory, { recursive: true });
});

test.describe("SEO growth surfaces", () => {
  test("repair-or-sell calculator works and tracks a useful decision", async ({ page }) => {
    const consoleErrors: string[] = [];
    page.on("console", (message) => {
      if (message.type() === "error") consoleErrors.push(message.text());
    });
    page.on("pageerror", (error) => consoleErrors.push(error.message));

    await page.goto("/tools/repair-or-sell-calculator?traffic=test");
    await expect(page.locator("h1")).toContainText("Repair the car or sell it?");
    await page.locator("#carValue").fill("8000");
    await page.locator("#repairQuote").fill("4200");
    await page.locator("#repairBacklog").fill("1200");
    await page.locator('button[type="submit"]').click();

    await expect(page.locator("#ratioResult")).toHaveText("53%");
    await expect(page.locator("#resultHeading")).toContainText("Pause");
    await expect(page.locator('link[rel="canonical"]')).toHaveAttribute(
      "href",
      "https://automoneypit.com/tools/repair-or-sell-calculator"
    );
    expect(consoleErrors).toEqual([]);

    await page.screenshot({
      path: `${evidenceDirectory}/repair-or-sell-calculator-desktop.png`,
      fullPage: true
    });
  });

  test("second-opinion guide is actionable on mobile", async ({ page }) => {
    await page.setViewportSize({ width: 390, height: 844 });
    await page.goto("/guides/car-repair-estimate-second-opinion?traffic=test");

    await expect(page.locator("h1")).toContainText("second opinion");
    await expect(page.locator("text=Diagnosis evidence")).toBeVisible();
    await expect(page.locator("text=Red flags that justify pausing")).toBeVisible();
    await expect(page.locator("#copyQuoteRequest")).toBeVisible();

    await page.screenshot({
      path: `${evidenceDirectory}/second-opinion-guide-mobile.png`,
      fullPage: true
    });
  });

  for (const path of [
    "/verdict/nissan/rogue/cvt-transmission",
    "/verdict/ford/fusion/15l20l-coolant-intrusion",
    "/verdict/ford/escape/coolant-intrusion",
    "/verdict/tesla/model-3/control-arms",
    "/verdict/ram/1500/exhaust-manifold-bolts"
  ]) {
    test(`${path} exposes diagnosis and quote support`, async ({ page }) => {
      await page.goto(`${path}?traffic=test`);
      await expect(page.getByRole("heading", { name: "What to verify before approving this repair" })).toBeVisible();
      await expect(page.getByRole("heading", { name: "Confirm the diagnosis", exact: true })).toBeVisible();
      await expect(page.getByRole("heading", { name: "Make the quote comparable", exact: true })).toBeVisible();
      await expect(page.getByRole("link", { name: "Compare the full cost", exact: true })).toBeVisible();

      const text = await bodyText(page);
      expect(text).not.toContain("sell immediately");
      expect(text).not.toContain("ticking time bomb");
      expect(text).not.toContain("??/span");
      expect(text).not.toContain("\ufffd");
    });
  }

  test("priority high-mileage page hands off to inspection and calculator", async ({ page }) => {
    await page.goto("/verdict/honda/odyssey/200000-miles?traffic=test");
    await expect(page.getByRole("heading", { name: "What to check before you keep, repair, or sell" })).toBeVisible();
    await expect(page.getByRole("heading", { name: "Inspection checklist", exact: true })).toBeVisible();
    await expect(page.getByRole("link", { name: "Compare keep vs replace cost", exact: true })).toBeVisible();
  });
});
