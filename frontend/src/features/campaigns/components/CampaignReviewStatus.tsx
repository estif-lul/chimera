import { ClipboardCheck } from 'lucide-react';

/**
 * Shows review status for a specific campaign's tasks.
 */
export default function CampaignReviewStatus({ campaignId }: { campaignId: string }) {
  return (
    <div className="card">
      <div className="card-header">
        <span className="card-title"><ClipboardCheck size={18} /> Review Status</span>
      </div>
      <p style={{ fontSize: 'var(--text-sm)' }}>Review status for campaign {campaignId} will appear here.</p>
    </div>
  );
}
