import { useQuery } from '@tanstack/react-query';
import { apiClient } from '../../services/api/client';
import { Link } from 'react-router-dom';

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
  return useQuery({
    queryKey: ['reviews'],
    queryFn: () => apiClient<ReviewItemView[]>('/api/v1/reviews'),
  });
}

/**
 * Reviewer queue page showing pending items with links to decision workspace.
 */
export default function ReviewQueuePage() {
  const queue = useReviewQueue();

  if (queue.isLoading) return <p>Loading review queue...</p>;
  if (queue.isError) return <p role="alert">Error: {queue.error.message}</p>;

  return (
    <div>
      <h1>Review Queue</h1>
      {queue.data?.length === 0 && <p>No items pending review.</p>}

      <table>
        <thead>
          <tr>
            <th>Status</th>
            <th>Confidence</th>
            <th>Classification</th>
            <th>Type</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {queue.data?.map((item) => (
            <tr key={item.id}>
              <td>{item.queueStatus}</td>
              <td>{item.confidenceScore ?? '—'}</td>
              <td>{item.policyClassification ?? '—'}</td>
              <td>{item.contentArtifactId ? 'Content' : 'Transaction'}</td>
              <td>
                <Link to={`/review/${item.id}`}>Review</Link>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
