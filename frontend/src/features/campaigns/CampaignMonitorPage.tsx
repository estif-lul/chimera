import { Link } from 'react-router-dom';
import { useCampaigns } from './hooks/useCampaigns';

/**
 * Campaign list with status indicators and links to plan and monitoring views.
 */
export default function CampaignMonitorPage() {
  const campaigns = useCampaigns();

  if (campaigns.isLoading) return <p>Loading campaigns...</p>;
  if (campaigns.isError) return <p role="alert">Error: {campaigns.error.message}</p>;

  return (
    <div>
      <h1>Campaign Monitor</h1>
      <Link to="/campaigns/new">+ New Campaign</Link>

      {campaigns.data?.length === 0 && <p>No campaigns yet.</p>}

      <table>
        <thead>
          <tr>
            <th>Name</th>
            <th>Status</th>
            <th>Goal</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {campaigns.data?.map((c) => (
            <tr key={c.id}>
              <td>{c.name}</td>
              <td>{c.status}</td>
              <td>{c.goalDescription}</td>
              <td>
                <Link to={`/campaigns/${c.id}/plan`}>View Plan</Link>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
