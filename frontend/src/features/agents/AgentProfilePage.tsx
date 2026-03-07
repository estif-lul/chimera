import { useParams, Link } from 'react-router-dom';
import { useAgent } from '../../services/api/agentProfile';
import AgentMemoryTimeline from './AgentMemoryTimeline';

/**
 * Agent profile page showing persona identity and memory timeline.
 */
export default function AgentProfilePage() {
  const { agentId } = useParams<{ agentId: string }>();
  const { data: agent, isLoading, error } = useAgent(agentId ?? '');

  if (isLoading) return <p>Loading agent…</p>;
  if (error || !agent) return <p role="alert">Agent not found.</p>;

  return (
    <main>
      <nav><Link to="/agents">← Agents</Link></nav>
      <h2>{agent.displayName}</h2>
      <dl>
        <dt>Persona</dt><dd>{agent.personaSlug}</dd>
        <dt>Status</dt><dd>{agent.status}</dd>
        <dt>Biography</dt><dd>{agent.mutableBiographySummary ?? '—'}</dd>
      </dl>

      <AgentMemoryTimeline agentId={agent.id} />
    </main>
  );
}
