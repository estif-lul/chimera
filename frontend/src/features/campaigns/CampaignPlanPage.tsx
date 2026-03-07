import { useParams } from 'react-router-dom';
import { useCampaignPlan, useApproveCampaign } from './hooks/useCampaigns';

/**
 * Displays the execution plan for review with an approve action.
 */
export default function CampaignPlanPage() {
  const { campaignId } = useParams<{ campaignId: string }>();
  const plan = useCampaignPlan(campaignId ?? '');
  const approve = useApproveCampaign();

  if (plan.isLoading) return <p>Loading plan...</p>;
  if (plan.isError) return <p role="alert">Failed to load plan: {plan.error.message}</p>;
  if (!plan.data) return <p>No plan found.</p>;

  const { data } = plan;

  return (
    <div>
      <h1>Execution Plan — v{data.planVersion}</h1>
      <p><strong>Status:</strong> {data.status}</p>
      <p>{data.summary}</p>

      <h2>Tasks ({data.tasks.length})</h2>
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
          {data.tasks.map((t) => (
            <tr key={t.id}>
              <td>{t.taskType}</td>
              <td>{t.priority}</td>
              <td>{t.status}</td>
              <td>{t.videoRenderTier ?? '—'}</td>
              <td>{t.stateVersion}</td>
            </tr>
          ))}
        </tbody>
      </table>

      {data.status === 'draft' && (
        <button
          onClick={() => approve.mutate(campaignId!)}
          disabled={approve.isPending}
        >
          {approve.isPending ? 'Approving...' : 'Approve Plan'}
        </button>
      )}
      {approve.isError && <p role="alert">Approve failed: {approve.error.message}</p>}
      {approve.isSuccess && <p>Plan approved and campaign activated.</p>}
    </div>
  );
}
