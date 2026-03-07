import { useQuery } from '@tanstack/react-query';
import { apiClient } from './client';

/** Agent list / detail types */
export interface AgentView {
  id: string;
  displayName: string;
  personaSlug: string;
  status: string;
  mutableBiographySummary: string | null;
  soulDefinitionId: string;
}

export interface SoulDefinitionView {
  id: string;
  personaSlug: string;
  version: number;
  backstory: string;
  voiceTone: string;
  coreBeliefsAndValues: string;
  directives: string;
}

export interface MemoryWritebackView {
  id: string;
  chimeraAgentId: string;
  memoryType: string;
  storageBackend: string;
  content: string;
  sourceTaskId: string | null;
  engagementScore: number | null;
  createdAt: string;
}

export function useAgent(agentId: string) {
  return useQuery<AgentView>({
    queryKey: ['agents', agentId],
    queryFn: () => apiClient(`/api/v1/agents/${agentId}`),
    enabled: !!agentId,
  });
}

export function useAgentMemory(agentId: string) {
  return useQuery<MemoryWritebackView[]>({
    queryKey: ['agents', agentId, 'memory'],
    queryFn: () => apiClient(`/api/v1/agents/${agentId}/memory`),
    enabled: !!agentId,
  });
}
