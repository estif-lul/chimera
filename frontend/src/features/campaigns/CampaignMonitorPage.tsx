import { Link } from 'react-router-dom';
import { useCampaigns, type CampaignView } from './hooks/useCampaigns';
import {
  Loader2,
  AlertCircle,
  Inbox,
  Plus,
  ArrowRight,
  Target,
  TrendingUp,
  Clock,
} from 'lucide-react';

function statusBadge(status: string) {
  const s = status.toLowerCase();
  if (s === 'active' || s === 'running') return 'badge badge-success';
  if (s === 'draft' || s === 'pending') return 'badge badge-warning';
  if (s === 'failed' || s === 'cancelled') return 'badge badge-error';
  return 'badge badge-default';
}

/**
 * Campaign list with status indicators and links to plan and monitoring views.
 */
export default function CampaignMonitorPage() {
  const campaigns = useCampaigns();

  if (campaigns.isLoading) {
    return (
      <div className="loading-state">
        <Loader2 className="spinner" />
        <span>Loading campaigns</span>
      </div>
    );
  }

  if (campaigns.isError) {
    return (
      <div className="error-banner" role="alert">
        <AlertCircle size={16} />
        {campaigns.error.message}
      </div>
    );
  }

  const data = campaigns.data ?? [];

  return (
    <div>
      <div className="page-header">
        <div>
          <h1>Campaigns</h1>
          <p className="page-subtitle">{data.length} campaign{data.length !== 1 ? 's' : ''} total</p>
        </div>
        <Link to="/campaigns/new" className="btn btn-primary">
          <Plus size={16} />
          New Campaign
        </Link>
      </div>

      {data.length > 0 && (
        <div className="stats-row" style={{ marginBottom: 'var(--space-6)' }}>
          <div className="stat-card">
            <span className="stat-label"><Target size={14} /> Total</span>
            <span className="stat-value">{data.length}</span>
          </div>
          <div className="stat-card">
            <span className="stat-label"><TrendingUp size={14} /> Active</span>
            <span className="stat-value">
              {data.filter((c: CampaignView) => c.status.toLowerCase() === 'active' || c.status.toLowerCase() === 'running').length}
            </span>
          </div>
          <div className="stat-card">
            <span className="stat-label"><Clock size={14} /> Draft</span>
            <span className="stat-value">
              {data.filter((c: CampaignView) => c.status.toLowerCase() === 'draft' || c.status.toLowerCase() === 'pending').length}
            </span>
          </div>
        </div>
      )}

      {data.length === 0 ? (
        <div className="empty-state">
          <Inbox size={48} />
          <p>No campaigns yet. Create your first campaign to get started.</p>
          <Link to="/campaigns/new" className="btn btn-primary">
            <Plus size={16} />
            Create Campaign
          </Link>
        </div>
      ) : (
        <div className="table-container">
          <table>
            <thead>
              <tr>
                <th>Name</th>
                <th>Status</th>
                <th>Goal</th>
                <th>Budget</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {data.map((c: CampaignView) => (
                <tr key={c.id}>
                  <td style={{ fontWeight: 500, color: 'var(--color-text-primary)' }}>{c.name}</td>
                  <td><span className={statusBadge(c.status)}>{c.status}</span></td>
                  <td style={{ maxWidth: 280, overflow: 'hidden', textOverflow: 'ellipsis' }}>{c.goalDescription}</td>
                  <td>{c.activeBudgetRemaining ?? '—'}</td>
                  <td>
                    <Link to={`/campaigns/${c.id}/plan`} className="table-action" style={{ display: 'inline-flex', alignItems: 'center', gap: 'var(--space-1)' }}>
                      Plan <ArrowRight size={14} />
                    </Link>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
