import { useAgentMemory, type MemoryWritebackView } from '../../services/api/agentProfile';

interface Props {
  agentId: string;
}

/**
 * Timeline of biography write-back records from Judge-approved interactions.
 */
export default function AgentMemoryTimeline({ agentId }: Props) {
  const { data: memories, isLoading, error } = useAgentMemory(agentId);

  if (isLoading) return <p>Loading memory timeline…</p>;
  if (error) return <p role="alert">Failed to load memory timeline.</p>;
  if (!memories || memories.length === 0) return <p>No memory write-backs recorded yet.</p>;

  return (
    <section aria-label="Agent memory timeline">
      <h3>Memory Write-backs</h3>
      <ol>
        {memories.map((m: MemoryWritebackView) => (
          <li key={m.id}>
            <time dateTime={m.createdAt}>{new Date(m.createdAt).toLocaleString()}</time>
            <span> — {m.memoryType} ({m.storageBackend})</span>
            {m.engagementScore != null && <span> score: {m.engagementScore}</span>}
            <p>{m.content}</p>
          </li>
        ))}
      </ol>
    </section>
  );
}
