import { DollarSign } from 'lucide-react';

interface Props {
  campaignId: string;
}

/**
 * Finance panel placeholder showing campaign budget metrics.
 * Actual implementation will aggregate agent wallet spend per campaign.
 */
export default function CampaignFinancePanel({ campaignId }: Props) {
  return (
    <section className="card" aria-labelledby="finance-heading">
      <div className="card-header">
        <span className="card-title" id="finance-heading"><DollarSign size={18} /> Campaign Finance</span>
      </div>
      <p style={{ fontSize: 'var(--text-sm)' }}>Budget tracking for campaign {campaignId} will be displayed here.</p>
    </section>
  );
}
