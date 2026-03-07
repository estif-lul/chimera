import { Routes, Route, Navigate } from 'react-router-dom';
import { LoginPage } from '../features/auth/LoginPage';
import { AuthCallbackPage } from '../features/auth/AuthCallbackPage';
import { RequireAuth } from '../features/auth/RequireAuth';
import CampaignMonitorPage from '../features/campaigns/CampaignMonitorPage';
import CampaignCreatePage from '../features/campaigns/CampaignCreatePage';
import CampaignPlanPage from '../features/campaigns/CampaignPlanPage';
import ReviewQueuePage from '../features/review/ReviewQueuePage';
import ReviewDecisionPanel from '../features/review/ReviewDecisionPanel';
import AuditTimelinePage from '../features/audit/AuditTimelinePage';
import AgentProfilePage from '../features/agents/AgentProfilePage';
import WalletPage from '../features/wallets/WalletPage';

/**
 * Top-level route definitions with tenant-scoped auth guards.
 */
export function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/auth/callback" element={<AuthCallbackPage />} />

      {/* Protected routes */}
      <Route element={<RequireAuth />}>
        <Route path="/campaigns" element={<CampaignMonitorPage />} />
        <Route path="/campaigns/new" element={<CampaignCreatePage />} />
        <Route path="/campaigns/:campaignId/plan" element={<CampaignPlanPage />} />
        <Route path="/agents" element={<div>Agent list (placeholder)</div>} />
        <Route path="/agents/:agentId" element={<AgentProfilePage />} />
        <Route path="/agents/:agentId/wallet" element={<WalletPage />} />
        <Route path="/review" element={<ReviewQueuePage />} />
        <Route path="/review/:reviewItemId" element={<ReviewDecisionPanel />} />
        <Route path="/wallets/:agentId" element={<WalletPage />} />
        <Route path="/audit" element={<AuditTimelinePage />} />
        <Route path="/" element={<Navigate to="/campaigns" replace />} />
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
