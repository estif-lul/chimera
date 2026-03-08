import { useAgentMemory, type MemoryWritebackView } from '../../services/api/agentProfile';
import { Loader2, AlertCircle, Brain, Database, BarChart3 } from 'lucide-react';

interface Props {
  agentId: string;
}

/**
 * Timeline of biography write-back records from Judge-approved interactions.
 */
export default function AgentMemoryTimeline({ agentId }: Props) {
  const { data: memories, isLoading, error } = useAgentMemory(agentId);

  if (isLoading) {
    return (
      <div className="card">
        <div style={{ display: 'flex', alignItems: 'center', gap: 'var(--space-2)', color: 'var(--color-text-tertiary)', fontSize: 'var(--text-sm)', padding: 'var(--space-4)' }}>
          <Loader2 size={14} className="spinner" /> Loading memory timeline
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="error-banner" role="alert">
        <AlertCircle size={16} />
        Failed to load memory timeline.
      </div>
    );
  }

  return (
    <section className="card" aria-label="Agent memory timeline">
      <div className="card-header">
        <span className="card-title"><Brain size={18} /> Memory Write-backs</span>
        <span className="badge badge-default">{memories?.length ?? 0}</span>
      </div>

      {!memories || memories.length === 0 ? (
        <p style={{ fontSize: 'var(--text-sm)', color: 'var(--color-text-tertiary)' }}>No memory write-backs recorded yet.</p>
      ) : (
        <div className="timeline">
          {memories.map((m: MemoryWritebackView) => (
            <div key={m.id} className="timeline-item">
              <div className="timeline-dot" />
              <time className="timeline-time" dateTime={m.createdAt}>
                {new Date(m.createdAt).toLocaleString()}
              </time>
              <div className="timeline-content">
                <p style={{ fontSize: 'var(--text-sm)', color: 'var(--color-text-primary)', marginBottom: 'var(--space-2)' }}>
                  {m.content}
                </p>
                <div className="timeline-meta">
                  <Database size={12} />
                  <span>{m.memoryType} / {m.storageBackend}</span>
                  {m.engagementScore != null && (
                    <>
                      <BarChart3 size={12} />
                      <span>Score: {m.engagementScore}</span>
                    </>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </section>
  );
}
