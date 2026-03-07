import { useWallet, type WalletView } from '../../services/api/wallets';

interface Props {
  agentId: string;
}

/**
 * Compact wallet summary card for agent detail views.
 */
export default function WalletSummaryCard({ agentId }: Props) {
  const { data: wallet, isLoading } = useWallet(agentId);

  if (isLoading) return <p>Loading wallet…</p>;
  if (!wallet) return <p>No wallet configured.</p>;

  return (
    <section aria-labelledby="wallet-summary-heading">
      <h4 id="wallet-summary-heading">Wallet ({wallet.status})</h4>
      <dl>
        <dt>Balance</dt><dd>{wallet.availableBalance}</dd>
        <dt>Daily Limit</dt><dd>{wallet.dailySpendLimit ?? '—'}</dd>
      </dl>
    </section>
  );
}
