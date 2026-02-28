import { expect, Page } from "@playwright/test";

export function extractSitemapPaths(xml: string): string[] {
  const paths: string[] = [];
  const locRegex = /<loc>(.*?)<\/loc>/g;
  for (const match of xml.matchAll(locRegex)) {
    const raw = match[1]?.trim();
    if (!raw) {
      continue;
    }
    try {
      const url = new URL(raw);
      paths.push(url.pathname);
    } catch {
      if (raw.startsWith("/")) {
        paths.push(raw);
      }
    }
  }
  return [...new Set(paths)];
}

export function firstPathMatching(paths: string[], regex: RegExp): string {
  const found = paths.find((path) => regex.test(path));
  if (!found) {
    throw new Error(`No sitemap path matched regex: ${regex}`);
  }
  return found;
}

export async function bodyText(page: Page): Promise<string> {
  const text = (await page.textContent("body")) ?? "";
  return text.replace(/\s+/g, " ").trim().toLowerCase();
}

export async function assertHasAbsoluteCanonical(page: Page): Promise<void> {
  const canonical = await page.getAttribute('link[rel="canonical"]', "href");
  expect(canonical, "canonical link must exist").toBeTruthy();
  expect(canonical!, "canonical must be absolute").toMatch(/^https?:\/\//);
}

export async function assertLdJsonParsable(page: Page): Promise<void> {
  const schemas = await page.$$eval(
    'script[type="application/ld+json"]',
    (nodes) => nodes.map((node) => node.textContent ?? "")
  );
  expect(schemas.length, "at least one JSON-LD block is expected").toBeGreaterThan(0);
  for (const schema of schemas) {
    expect(() => JSON.parse(schema), `invalid JSON-LD block: ${schema.slice(0, 120)}`).not.toThrow();
  }
}
