import { useState } from 'react';
import { useCreateCampaign } from './hooks/useCampaigns';
import { useAgents, type AgentView } from '../../services/api/agents';
import { Link } from 'react-router-dom';
import {
  ArrowLeft,
  Loader2,
  AlertCircle,
  CheckCircle2,
  Rocket,
  Users,
} from 'lucide-react';

/**
 * Campaign creation form. Lets operator pick agents, name, goals, and submit.
 */
export default function CampaignCreatePage() {
  const agents = useAgents();
  const createCampaign = useCreateCampaign();

  const [name, setName] = useState('');
  const [goal, setGoal] = useState('');
  const [audience, setAudience] = useState('');
  const [selectedAgents, setSelectedAgents] = useState<string[]>([]);

  function toggleAgent(id: string) {
    setSelectedAgents((prev) =>
      prev.includes(id) ? prev.filter((a) => a !== id) : [...prev, id],
    );
  }

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    createCampaign.mutate({
      name,
      goalDescription: goal,
      targetAudience: audience,
      agentIds: selectedAgents,
    });
  }

  return (
    <div>
      <Link to="/campaigns" className="back-link">
        <ArrowLeft size={16} />
        Back to Campaigns
      </Link>

      <div className="page-header">
        <h1>Create Campaign</h1>
      </div>

      <div className="card" style={{ maxWidth: 640 }}>
        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 'var(--space-5)' }}>
          <div className="form-group">
            <label htmlFor="name" className="form-label">Campaign Name</label>
            <input
              id="name"
              className="form-input"
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="e.g. Q1 Brand Awareness Push"
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="goal" className="form-label">Goal Description</label>
            <textarea
              id="goal"
              className="form-textarea"
              value={goal}
              onChange={(e) => setGoal(e.target.value)}
              placeholder="Describe what this campaign should achieve"
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="audience" className="form-label">Target Audience</label>
            <input
              id="audience"
              className="form-input"
              value={audience}
              onChange={(e) => setAudience(e.target.value)}
              placeholder="e.g. Tech-savvy millennials, 25-35"
              required
            />
          </div>

          <div className="fieldset">
            <div className="card-header" style={{ marginBottom: 'var(--space-3)' }}>
              <span className="fieldset-legend" style={{ display: 'flex', alignItems: 'center', gap: 'var(--space-2)' }}>
                <Users size={16} /> Assign Agents
              </span>
            </div>
            {agents.isLoading && (
              <div style={{ display: 'flex', alignItems: 'center', gap: 'var(--space-2)', color: 'var(--color-text-tertiary)', fontSize: 'var(--text-sm)' }}>
                <Loader2 size={14} className="spinner" /> Loading agents
              </div>
            )}
            <div className="checkbox-group">
              {agents.data?.map((agent: AgentView) => (
                <label key={agent.id} className="checkbox-label">
                  <input
                    type="checkbox"
                    checked={selectedAgents.includes(agent.id)}
                    onChange={() => toggleAgent(agent.id)}
                  />
                  <span>
                    <span style={{ color: 'var(--color-text-primary)', fontWeight: 500 }}>{agent.displayName}</span>
                    <span style={{ color: 'var(--color-text-tertiary)', marginLeft: 'var(--space-2)' }}>{agent.personaSlug}</span>
                  </span>
                </label>
              ))}
            </div>
          </div>

          <div style={{ display: 'flex', gap: 'var(--space-3)', paddingTop: 'var(--space-2)' }}>
            <button type="submit" className="btn btn-primary" disabled={createCampaign.isPending}>
              {createCampaign.isPending ? (
                <><Loader2 size={16} className="spinner" /> Creating</>
              ) : (
                <><Rocket size={16} /> Create Campaign</>
              )}
            </button>
            <Link to="/campaigns" className="btn btn-secondary">Cancel</Link>
          </div>
        </form>
      </div>

      {createCampaign.isError && (
        <div className="error-banner" role="alert" style={{ marginTop: 'var(--space-4)' }}>
          <AlertCircle size={16} />
          {createCampaign.error.message}
        </div>
      )}
      {createCampaign.isSuccess && (
        <div className="success-banner" style={{ marginTop: 'var(--space-4)' }}>
          <CheckCircle2 size={16} />
          Campaign created successfully.
        </div>
      )}
    </div>
  );
}
