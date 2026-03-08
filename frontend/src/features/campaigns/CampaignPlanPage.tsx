import { useParams, Link } from 'react-router-dom';
import { useCampaignPlan, useApproveCampaign, type TaskSummary } from './hooks/useCampaigns';
import {
  ArrowLeft,
  Loader2,
  AlertCircle,
  CheckCircle2,
  FileText,
  ListChecks,
  CircleDot,
} from 'lucide-react';

function priorityBadge(priority: string) {
  const p = priority.toLowerCase();
  if (p === 'high' || p === 'critical') return 'badge badge-error';
  if (p === 'medium') return 'badge badge-warning';
  return 'badge badge-default';
}

function taskStatusBadge(status: string) {
  const s = status.toLowerCase();
  if (s === 'completed' || s === 'done') return 'badge badge-success';
  if (s === 'in_progress' || s === 'running') return 'badge badge-accent';
  if (s === 'failed') return 'badge badge-error';
  return 'badge badge-default';
}

/**
 * Displays the execution plan for review with an approve action.
 */
export default function CampaignPlanPage() {
  const { campaignId } = useParams<{ campaignId: string }>();
  const plan = useCampaignPlan(campaignId ?? '');
  const approve = useApproveCampaign();

  if (plan.isLoading) {
    return (
      <div className="loading-state">
        <Loader2 className="spinner" />
        <span>Loading plan</span>
      </div>
    );
  }

  if (plan.isError) {
    return (
      <div className="error-banner" role="alert">
        <AlertCircle size={16} />
        Failed to load plan: {plan.error.message}
      </div>
    );
  }

  if (!plan.data) {
    return (
      <div className="empty-state">
        <FileText size={48} />
        <p>No execution plan found for this campaign.</p>
      </div>
    );
  }

  const { data } = plan;

  return (
    <div>
      <Link to="/campaigns" className="back-link">
        <ArrowLeft size={16} />
        Back to Campaigns
      </Link>

      <div className="page-header">
        <div>
          <h1>Execution Plan</h1>
          <p className="page-subtitle">Version {data.planVersion}</p>
        </div>
        {data.status === 'draft' && (
          <button
            className="btn btn-success"
            onClick={() => approve.mutate(campaignId!)}
            disabled={approve.isPending}
          >
            {approve.isPending ? (
              <><Loader2 size={16} className="spinner" /> Approving</>
            ) : (
              <><CheckCircle2 size={16} /> Approve Plan</>
            )}
          </button>
        )}
      </div>

      {approve.isError && (
        <div className="error-banner" role="alert" style={{ marginBottom: 'var(--space-4)' }}>
          <AlertCircle size={16} />
          Approve failed: {approve.error.message}
        </div>
      )}
      {approve.isSuccess && (
        <div className="success-banner" style={{ marginBottom: 'var(--space-4)' }}>
          <CheckCircle2 size={16} />
          Plan approved and campaign activated.
        </div>
      )}

      <div className="stats-row" style={{ marginBottom: 'var(--space-6)' }}>
        <div className="stat-card">
          <span className="stat-label"><CircleDot size={14} /> Status</span>
          <span className="stat-value" style={{ fontSize: 'var(--text-lg)' }}>{data.status}</span>
        </div>
        <div className="stat-card">
          <span className="stat-label"><ListChecks size={14} /> Tasks</span>
          <span className="stat-value">{data.tasks.length}</span>
        </div>
      </div>

      <div className="card" style={{ marginBottom: 'var(--space-6)' }}>
        <div className="card-header">
          <span className="card-title"><FileText size={18} /> Summary</span>
        </div>
        <p style={{ fontSize: 'var(--text-sm)', lineHeight: 'var(--leading-relaxed)' }}>{data.summary}</p>
      </div>

      <div className="table-container">
        <table>
          <thead>
            <tr>
              <th>Type</th>
              <th>Priority</th>
              <th>Status</th>
              <th>Video Tier</th>
              <th>Version</th>
            </tr>
          </thead>
          <tbody>
            {data.tasks.map((t: TaskSummary) => (
              <tr key={t.id}>
                <td style={{ fontWeight: 500, color: 'var(--color-text-primary)' }}>{t.taskType}</td>
                <td><span className={priorityBadge(t.priority)}>{t.priority}</span></td>
                <td><span className={taskStatusBadge(t.status)}>{t.status}</span></td>
                <td>{t.videoRenderTier ?? '—'}</td>
                <td>{t.stateVersion}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
