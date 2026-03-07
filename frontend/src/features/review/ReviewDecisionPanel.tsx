import { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { apiClient } from '../../services/api/client';

interface ReviewItemView {
  id: string;
  queueStatus: string;
  confidenceScore: string | null;
  policyClassification: string | null;
  contentArtifactId: string | null;
  transactionRequestId: string | null;
}

/**
 * Decision workspace for a single review item: approve, reject, or edit.
 */
export default function ReviewDecisionPanel() {
  const { reviewItemId } = useParams<{ reviewItemId: string }>();
  const navigate = useNavigate();
  const qc = useQueryClient();

  const item = useQuery({
    queryKey: ['reviews', reviewItemId],
    queryFn: () => apiClient<ReviewItemView>(`/api/v1/reviews/${encodeURIComponent(reviewItemId!)}`),
    enabled: !!reviewItemId,
  });

  const [decisionType, setDecisionType] = useState('approve');
  const [rationale, setRationale] = useState('');
  const [editSummary, setEditSummary] = useState('');

  const submitDecision = useMutation({
    mutationFn: () =>
      apiClient(`/api/v1/reviews/${encodeURIComponent(reviewItemId!)}/decisions`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ decisionType, rationale, editSummary: editSummary || undefined }),
      }),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['reviews'] });
      navigate('/review');
    },
  });

  if (item.isLoading) return <p>Loading...</p>;
  if (item.isError) return <p role="alert">Error: {item.error.message}</p>;
  if (!item.data) return <p>Not found.</p>;

  return (
    <div>
      <h1>Review Decision</h1>
      <dl>
        <dt>Status</dt><dd>{item.data.queueStatus}</dd>
        <dt>Confidence</dt><dd>{item.data.confidenceScore ?? '—'}</dd>
        <dt>Classification</dt><dd>{item.data.policyClassification ?? '—'}</dd>
      </dl>

      <form onSubmit={(e) => { e.preventDefault(); submitDecision.mutate(); }}>
        <fieldset>
          <legend>Decision</legend>
          {(['approve', 'reject', 'edit'] as const).map((type) => (
            <label key={type}>
              <input
                type="radio"
                name="decisionType"
                value={type}
                checked={decisionType === type}
                onChange={() => setDecisionType(type)}
              />
              {type.charAt(0).toUpperCase() + type.slice(1)}
            </label>
          ))}
        </fieldset>

        <div>
          <label htmlFor="rationale">Rationale</label>
          <textarea
            id="rationale"
            value={rationale}
            onChange={(e) => setRationale(e.target.value)}
            required={decisionType !== 'approve'}
          />
        </div>

        {decisionType === 'edit' && (
          <div>
            <label htmlFor="editSummary">Edit Summary</label>
            <textarea id="editSummary" value={editSummary} onChange={(e) => setEditSummary(e.target.value)} />
          </div>
        )}

        <button type="submit" disabled={submitDecision.isPending}>
          {submitDecision.isPending ? 'Submitting...' : 'Submit Decision'}
        </button>
      </form>
      {submitDecision.isError && <p role="alert">Error: {submitDecision.error.message}</p>}
    </div>
  );
}
