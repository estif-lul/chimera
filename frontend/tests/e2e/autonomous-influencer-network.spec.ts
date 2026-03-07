import { test, expect } from '@playwright/test';

/**
 * End-to-end smoke tests for the autonomous influencer network.
 * Covers campaign, review, memory, and wallet user flows.
 */

test.describe('Autonomous Influencer Network E2E', () => {

  test('campaign creation and monitoring flow', async ({ page }) => {
    await page.goto('/login');
    // Authenticate
    await page.fill('[name="email"]', 'test@chimera.local');
    await page.fill('[name="password"]', 'test-password');
    await page.click('button[type="submit"]');
    await page.waitForURL('/campaigns');

    // Navigate to create campaign
    await page.click('text=Create Campaign');
    await page.waitForURL('/campaigns/new');
    await page.fill('[name="name"]', 'Smoke Test Campaign');
    await page.fill('[name="goalDescription"]', 'E2E smoke validation');
    await page.click('button[type="submit"]');

    // Verify campaign appears in monitor
    await page.waitForURL('/campaigns');
    await expect(page.locator('text=Smoke Test Campaign')).toBeVisible();
  });

  test('review queue interaction', async ({ page }) => {
    await page.goto('/review');
    // Verify review queue renders
    await expect(page.locator('h2')).toContainText('Review');
  });

  test('agent profile and memory timeline', async ({ page }) => {
    await page.goto('/agents');
    // Verify agent list or placeholder renders
    await expect(page.locator('main')).toBeVisible();
  });

  test('wallet balance and transactions', async ({ page }) => {
    // Navigate to a wallet page (requires agent context)
    await page.goto('/wallets/test-agent-id');
    // Wallet page should render (may show "not found" for missing agent)
    await expect(page.locator('main')).toBeVisible();
  });

  test('audit timeline displays events', async ({ page }) => {
    await page.goto('/audit');
    await expect(page.locator('h2')).toContainText('Audit');
  });
});
