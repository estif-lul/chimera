import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { apiClient } from './client';

/** Campaign API types */
export interface CampaignView {
  id: string;
  tenantWorkspaceId: string;
  name: string;
  status: string;
  goalDescription: string;
  targetAudience: string;
  currentPlanId: string | null;
  activeBudgetRemaining: string | null;
}

export interface CreateCampaignRequest {
  name: string;
  goalDescription: string;
  targetAudience: string;
  agentIds: string[];
  brandConstraints?: string[];
  riskProfile?: string;
  confidencePolicyId?: string;
  budgetPolicyId?: string;
}

export interface ExecutionPlanView {
  id: string;
  campaignId: string;
  planVersion: number;
  status: string;
  summary: string;
  acceptanceCriteria: string[];
  tasks: TaskSummary[];
}

export interface TaskSummary {
  id: string;
  taskType: string;
  priority: string;
  videoRenderTier: string | null;
  status: string;
  stateVersion: number;
}

const CAMPAIGNS_KEY = ['campaigns'] as const;

export function useCampaigns() {
  return useQuery({
    queryKey: CAMPAIGNS_KEY,
    queryFn: () => apiClient<CampaignView[]>('/api/v1/campaigns'),
  });
}

export function useCampaignPlan(campaignId: string) {
  return useQuery({
    queryKey: ['campaigns', campaignId, 'plan'],
    queryFn: () => apiClient<ExecutionPlanView>(`/api/v1/campaigns/${encodeURIComponent(campaignId)}/plan`),
    enabled: !!campaignId,
  });
}

export function useCreateCampaign() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (data: CreateCampaignRequest) =>
      apiClient<CampaignView>('/api/v1/campaigns', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data),
      }),
    onSuccess: () => qc.invalidateQueries({ queryKey: CAMPAIGNS_KEY }),
  });
}

export function useApproveCampaign() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (campaignId: string) =>
      apiClient<void>(`/api/v1/campaigns/${encodeURIComponent(campaignId)}/approve`, { method: 'POST' }),
    onSuccess: () => qc.invalidateQueries({ queryKey: CAMPAIGNS_KEY }),
  });
}
