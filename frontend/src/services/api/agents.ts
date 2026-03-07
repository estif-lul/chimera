import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { apiClient } from './client';

/** Agent API types */
export interface AgentView {
  id: string;
  tenantWorkspaceId: string;
  displayName: string;
  personaSlug: string;
  status: string;
  soulDefinitionVersion: string;
  mutableBiographySummary: string | null;
  visualReferenceId: string | null;
}

export interface CreateAgentRequest {
  displayName: string;
  personaSlug: string;
  soulDefinition: {
    backstory: string;
    voiceTone: string[];
    coreBeliefsAndValues: string[];
    directives: string[];
  };
  visualReferenceId?: string;
}

const AGENTS_KEY = ['agents'] as const;

export function useAgents() {
  return useQuery({
    queryKey: AGENTS_KEY,
    queryFn: () => apiClient<AgentView[]>('/api/v1/agents'),
  });
}

export function useAgent(agentId: string) {
  return useQuery({
    queryKey: ['agents', agentId],
    queryFn: () => apiClient<AgentView>(`/api/v1/agents/${encodeURIComponent(agentId)}`),
    enabled: !!agentId,
  });
}

export function useCreateAgent() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (data: CreateAgentRequest) =>
      apiClient<AgentView>('/api/v1/agents', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data),
      }),
    onSuccess: () => qc.invalidateQueries({ queryKey: AGENTS_KEY }),
  });
}
