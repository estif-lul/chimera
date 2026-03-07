/**
 * Shows review status for a specific campaign's tasks.
 */
export default function CampaignReviewStatus({ campaignId }: { campaignId: string }) {
  // This would query task-level review items for the campaign.
  // For now, renders a summary placeholder.
  return (
    <div>
      <h3>Review Status</h3>
      <p>Review status for campaign {campaignId} will appear here.</p>
    </div>
  );
}
