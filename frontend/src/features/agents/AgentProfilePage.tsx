import { useParams, Link } from 'react-router-dom';
import { useAgent } from '../../services/api/agentProfile';
import AgentMemoryTimeline from './AgentMemoryTimeline';
import WalletSummaryCard from './WalletSummaryCard';
import {
  ArrowLeft,
  Loader2,
  AlertCircle,
  User,
  BookOpen,
} from 'lucide-react';

/**
 * Agent profile page showing persona identity, wallet, and memory timeline.
 */
export default function AgentProfilePage() {
  const { agentId } = useParams<{ agentId: string }>();
  const { data: agent, isLoading, error } = useAgent(agentId ?? '');

  if (isLoading) {
    return (
      <div className="loading-state">
        <Loader2 className="spinner" />
        <span>Loading agent</span>
      </div>
    );
  }

  if (error || !agent) {
    return (
      <div className="error-banner" role="alert">
        <AlertCircle size={16} />
        Agent not found.
      </div>
    );
  }

  return (
    <div>
      <Link to="/agents" className="back-link">
        <ArrowLeft size={16} />
        Back to Agents
      </Link>

      <div className="page-header">
        <div>
          <h1>{agent.displayName}</h1>
          <p className="page-subtitle">{agent.personaSlug}</p>
        </div>
        <span className={`badge ${agent.status.toLowerCase() === 'active' ? 'badge-success' : 'badge-default'}`}>
          {agent.status}
        </span>
      </div>

      <div className="info-grid" style={{ marginBottom: 'var(--space-6)' }}>
        <div className="card">
          <div className="card-header">
            <span className="card-title"><User size={18} /> Identity</span>
          </div>
          <div className="info-grid">
            <div className="info-item">
              <span className="info-label">Persona</span>
              <span className="info-value">{agent.personaSlug}</span>
            </div>
            <div className="info-item">
              <span className="info-label">Status</span>
              <span className="info-value">{agent.status}</span>
            </div>
          </div>
        </div>
        <div className="card">
          <div className="card-header">
            <span className="card-title"><BookOpen size={18} /> Biography</span>
          </div>
          <p style={{ fontSize: 'var(--text-sm)', lineHeight: 'var(--leading-relaxed)' }}>
            {agent.mutableBiographySummary ?? 'No biography recorded yet.'}
          </p>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr', gap: 'var(--space-6)' }}>
        <WalletSummaryCard agentId={agent.id} />
        <AgentMemoryTimeline agentId={agent.id} />
      </div>
    </div>
  );
}
