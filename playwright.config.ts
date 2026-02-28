import { defineConfig, devices } from "@playwright/test";

const useManagedServer = process.env.PW_USE_WEBSERVER === "1";
const managedPort = process.env.PW_SERVER_PORT ?? "8091";
const managedBaseUrl = `http://127.0.0.1:${managedPort}`;
const baseURL = process.env.PLAYWRIGHT_BASE_URL ?? (useManagedServer ? managedBaseUrl : "http://127.0.0.1:8080");

export default defineConfig({
  testDir: "./tests/playwright",
  timeout: 60_000,
  expect: {
    timeout: 10_000
  },
  fullyParallel: false,
  retries: process.env.CI ? 2 : 0,
  reporter: [
    ["list"],
    ["html", { open: "never", outputFolder: "playwright-report" }]
  ],
  use: {
    baseURL,
    trace: "retain-on-failure",
    screenshot: "only-on-failure",
    video: "retain-on-failure",
    actionTimeout: 15_000,
    navigationTimeout: 30_000
  },
  webServer: useManagedServer
    ? {
        command:
          process.platform === "win32"
            ? `cmd /c "set JAVA_HOME=C:\\Program Files\\Microsoft\\jdk-21.0.10.7-hotspot&& set PATH=C:\\Program Files\\Microsoft\\jdk-21.0.10.7-hotspot\\bin;%PATH%&& java -jar build\\libs\\app.jar --server.port=${managedPort}"`
            : `java -jar build/libs/app.jar --server.port=${managedPort}`,
        url: managedBaseUrl,
        timeout: 240_000,
        reuseExistingServer: false
      }
    : undefined,
  projects: [
    {
      name: "chromium",
      use: {
        ...devices["Desktop Chrome"]
      }
    }
  ]
});
