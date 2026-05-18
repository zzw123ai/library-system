import { defineConfig, devices } from '@playwright/test'

const FRONTEND_PORT = 5173
/** E2E 专用端口，避免与本地已启动的 8080 演示实例冲突 */
const BACKEND_PORT = 18080
const baseURL = `http://127.0.0.1:${FRONTEND_PORT}`

export default defineConfig({
  testDir: './e2e',
  timeout: 60_000,
  expect: { timeout: 10_000 },
  fullyParallel: false,
  retries: 0,
  reporter: [['list'], ['html', { open: 'never', outputFolder: 'playwright-report' }]],
  use: {
    baseURL,
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
    locale: 'zh-CN',
  },
  projects: [
    {
      name: 'chrome',
      use: { ...devices['Desktop Chrome'] },
    },
    {
      name: 'edge',
      use: { ...devices['Desktop Edge'], channel: 'msedge' },
    },
  ],
  webServer: [
    {
      command: `mvn -q spring-boot:run -Dspring-boot.run.arguments=--server.port=${BACKEND_PORT}`,
      cwd: '../Backend',
      url: `http://127.0.0.1:${BACKEND_PORT}/api/book/list`,
      reuseExistingServer: true,
      timeout: 180_000,
    },
    {
      command: 'npm run dev',
      url: baseURL,
      reuseExistingServer: true,
      timeout: 120_000,
      env: {
        VITE_API_TARGET: `http://127.0.0.1:${BACKEND_PORT}`,
      },
    },
  ],
})
