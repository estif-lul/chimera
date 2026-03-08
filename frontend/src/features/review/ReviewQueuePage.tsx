import { useQuery } from '@tanstack/react-query';
import { apiClient } from '../../services/api/client';
import { Link } from 'react-router-dom';
import {
  Loader2,
  AlertCircle,
  Inbox,
  ArrowRight,
  FileText,
  CreditCard,
  ShieldCheck,
  BarChart3,
} from 'lucide-react';

interface ReviewItemView {
  id: string;
  tenantWorkspaceId: string;
  taskId: string;
  contentArtifactId: string | null;
  transactionRequestId: string | null;
  queueStatus: string;
  confidenceScore: string | null;
  policyClassification: string | null;
}

function useReviewQueue() {
  return useQuery<ReviewItemView[]>({
    queryKey: ['reviews'],
    queryFn: () => apiClient.get('/api/v1/reviews'),
  });
}

function queueStatusBadge(status: string) {
  const s = status.toLowerCase();
  if (s === 'pending' || s === 'queued') return 'badge badge-warning';
  if (s === 'approved' || s === 'completed') return 'badge badge-success';
  if (s === 'rejected') return 'badge badge-error';
  return 'badge badge-default';
}

/**
 * Reviewer queue page showing pending items with links to decision workspace.
 */
export default function ReviewQueuePage() {
  const queue = useReviewQueue();

  if (queue.isLoading) {
    return (
      <div className="loading-state">
        <Loader2 className="spinner" />
        <span>Loading review queue</span>
      </div>
    );
  }

  if (queue.isError) {
    return (
      <div className="error-banner" role="alert">
        <AlertCircle size={16} />
        {queue.error.message}
      </div>
    );
  }

  const data = queue.data ?? [];

  return (
    <div>
      <div className="page-header">
        <div>
          <h1>Review Queue</h1>
          <p className="page-subtitle">{data.length} item{data.length !== 1 ? 's' : ''} pending review</p>
        </div>
      </div>

      {data.length > 0 && (
        <div className="stats-row" style={{ marginBottom: 'var(--space-6)' }}>
          <div className="stat-card">
            <span className="stat-label"><ShieldCheck size={14} /> Total</span>
            <span className="stat-value">{data.length}</span>
          </div>
          <div className="stat-card">
            <span className="stat-label"><FileText size={14} /> Content</span>
            <span className="stat-value">{data.filter((i) => i.contentArtifactId).length}</span>
          </div>
          <div className="stat-card">
            <span className="stat-label"><CreditCard size={14} /> Transactions</span>
            <span className="stat-value">{data.filter((i) => i.transactionRequestId).length}</span>
          </div>
        </div>
      )}

      {data.length === 0 ? (
        <div className="empty-state">
          <Inbox size={48} />
          <p>No items pending review. The queue is clear.</p>
        </div>
      ) : (
        <div className="table-container">
          <table>
            <thead>
              <tr>
                <th>Status</th>
                <th>Confidence</th>
                <th>Classification</th>
                <th>Type</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {data.map((item) => (
                <tr key={item.id}>
                  <td><span className={queueStatusBadge(item.queueStatus)}>{item.queueStatus}</span></td>
                  <td>
                    {item.confidenceScore != null ? (
                      <span style={{ display: 'inline-flex', alignItems: 'center', gap: 'var(--space-1)' }}>
                        <BarChart3 size={14} style={{ color: 'var(--color-text-tertiary)' }} />
                        {item.confidenceScore}
                      </span>
                    ) : '—'}
                  </td>
                  <td>{item.policyClassification ?? '—'}</td>
                  <td>
                    <span style={{ display: 'inline-flex', alignItems: 'center', gap: 'var(--space-1)' }}>
                      {item.contentArtifactId ? <FileText size={14} /> : <CreditCard size={14} />}
                      {item.contentArtifactId ? 'Content' : 'Transaction'}
                    </span>
                  </td>
                  <td>
                    <Link to={`/review/${item.id}`} className="table-action" style={{ display: 'inline-flex', alignItems: 'center', gap: 'var(--space-1)' }}>
                      Review <ArrowRight size={14} />
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
