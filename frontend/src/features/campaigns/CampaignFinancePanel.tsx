interface Props {
  campaignId: string;
}

/**
 * Finance panel placeholder showing campaign budget metrics.
 * Actual implementation will aggregate agent wallet spend per campaign.
 */
export default function CampaignFinancePanel({ campaignId }: Props) {
  return (
    <section aria-labelledby="finance-heading">
      <h4 id="finance-heading">Campaign Finance</h4>
      <p>Budget tracking for campaign {campaignId} will be displayed here.</p>
    </section>
  );
}
