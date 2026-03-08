import { useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { apiClient } from '../../services/api/client';
import {
  ArrowLeft,
  Loader2,
  AlertCircle,
  CheckCircle2,
  XCircle,
  Pencil,
  Send,
  ShieldCheck,
  BarChart3,
} from 'lucide-react';

interface ReviewItemView {
  id: string;
  queueStatus: string;
  confidenceScore: string | null;
  policyClassification: string | null;
  contentArtifactId: string | null;
  transactionRequestId: string | null;
}

const DECISION_OPTIONS = [
  { value: 'approve', label: 'Approve', icon: CheckCircle2 },
  { value: 'reject', label: 'Reject', icon: XCircle },
  { value: 'edit', label: 'Edit', icon: Pencil },
] as const;

/**
 * Decision workspace for a single review item: approve, reject, or edit.
 */
export default function ReviewDecisionPanel() {
  const { reviewItemId } = useParams<{ reviewItemId: string }>();
  const navigate = useNavigate();
  const qc = useQueryClient();

  const item = useQuery<ReviewItemView>({
    queryKey: ['reviews', reviewItemId],
    queryFn: () => apiClient.get(`/api/v1/reviews/${encodeURIComponent(reviewItemId!)}`),
    enabled: !!reviewItemId,
  });

  const [decisionType, setDecisionType] = useState('approve');
  const [rationale, setRationale] = useState('');
  const [editSummary, setEditSummary] = useState('');

  const submitDecision = useMutation({
    mutationFn: () =>
      apiClient.post(`/api/v1/reviews/${encodeURIComponent(reviewItemId!)}/decisions`, { decisionType, rationale, editSummary: editSummary || undefined }),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['reviews'] });
      navigate('/review');
    },
  });

  if (item.isLoading) {
    return (
      <div className="loading-state">
        <Loader2 className="spinner" />
        <span>Loading review item</span>
      </div>
    );
  }

  if (item.isError) {
    return (
      <div className="error-banner" role="alert">
        <AlertCircle size={16} />
        {item.error.message}
      </div>
    );
  }

  if (!item.data) {
    return (
      <div className="empty-state">
        <ShieldCheck size={48} />
        <p>Review item not found.</p>
      </div>
    );
  }

  return (
    <div>
      <Link to="/review" className="back-link">
        <ArrowLeft size={16} />
        Back to Queue
      </Link>

      <div className="page-header">
        <h1>Review Decision</h1>
      </div>

      <div className="card" style={{ marginBottom: 'var(--space-6)' }}>
        <div className="card-header">
          <span className="card-title"><ShieldCheck size={18} /> Item Details</span>
        </div>
        <div className="info-grid">
          <div className="info-item">
            <span className="info-label">Status</span>
            <span className="info-value">{item.data.queueStatus}</span>
          </div>
          <div className="info-item">
            <span className="info-label">Confidence</span>
            <span className="info-value" style={{ display: 'flex', alignItems: 'center', gap: 'var(--space-1)' }}>
              <BarChart3 size={14} style={{ color: 'var(--color-text-tertiary)' }} />
              {item.data.confidenceScore ?? '—'}
            </span>
          </div>
          <div className="info-item">
            <span className="info-label">Classification</span>
            <span className="info-value">{item.data.policyClassification ?? '—'}</span>
          </div>
        </div>
      </div>

      <div className="card" style={{ maxWidth: 640 }}>
        <div className="card-header">
          <span className="card-title">Decision</span>
        </div>
        <form onSubmit={(e) => { e.preventDefault(); submitDecision.mutate(); }} style={{ display: 'flex', flexDirection: 'column', gap: 'var(--space-5)' }}>
          <div className="radio-group">
            {DECISION_OPTIONS.map(({ value, label, icon: Icon }) => (
              <label key={value} className="radio-label">
                <input
                  type="radio"
                  name="decisionType"
                  value={value}
                  checked={decisionType === value}
                  onChange={() => setDecisionType(value)}
                />
                <Icon size={16} />
                {label}
              </label>
            ))}
          </div>

          <div className="form-group">
            <label htmlFor="rationale" className="form-label">Rationale</label>
            <textarea
              id="rationale"
              className="form-textarea"
              value={rationale}
              onChange={(e) => setRationale(e.target.value)}
              placeholder="Explain the reasoning behind this decision"
              required={decisionType !== 'approve'}
            />
          </div>

          {decisionType === 'edit' && (
            <div className="form-group">
              <label htmlFor="editSummary" className="form-label">Edit Summary</label>
              <textarea
                id="editSummary"
                className="form-textarea"
                value={editSummary}
                onChange={(e) => setEditSummary(e.target.value)}
                placeholder="Describe what was changed"
              />
            </div>
          )}

          <div style={{ display: 'flex', gap: 'var(--space-3)', paddingTop: 'var(--space-2)' }}>
            <button type="submit" className="btn btn-primary" disabled={submitDecision.isPending}>
              {submitDecision.isPending ? (
                <><Loader2 size={16} className="spinner" /> Submitting</>
              ) : (
                <><Send size={16} /> Submit Decision</>
              )}
            </button>
            <Link to="/review" className="btn btn-secondary">Cancel</Link>
          </div>
        </form>
      </div>

      {submitDecision.isError && (
        <div className="error-banner" role="alert" style={{ marginTop: 'var(--space-4)' }}>
          <AlertCircle size={16} />
          {submitDecision.error.message}
        </div>
      )}
    </div>
  );
}
