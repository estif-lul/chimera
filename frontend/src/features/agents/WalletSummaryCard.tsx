import { useWallet } from '../../services/api/wallets';
import { Wallet, Loader2 } from 'lucide-react';

interface Props {
  agentId: string;
}

/**
 * Compact wallet summary card for agent detail views.
 */
export default function WalletSummaryCard({ agentId }: Props) {
  const { data: wallet, isLoading } = useWallet(agentId);

  if (isLoading) {
    return (
      <div className="card">
        <div style={{ display: 'flex', alignItems: 'center', gap: 'var(--space-2)', color: 'var(--color-text-tertiary)', fontSize: 'var(--text-sm)' }}>
          <Loader2 size={14} className="spinner" /> Loading wallet
        </div>
      </div>
    );
  }

  if (!wallet) {
    return (
      <div className="card">
        <div className="card-header">
          <span className="card-title"><Wallet size={18} /> Wallet</span>
        </div>
        <p style={{ fontSize: 'var(--text-sm)', color: 'var(--color-text-tertiary)' }}>No wallet configured.</p>
      </div>
    );
  }

  return (
    <section className="card" aria-labelledby="wallet-summary-heading">
      <div className="card-header">
        <span className="card-title" id="wallet-summary-heading"><Wallet size={18} /> Wallet</span>
        <span className={`badge ${wallet.status.toLowerCase() === 'active' ? 'badge-success' : 'badge-default'}`}>
          {wallet.status}
        </span>
      </div>
      <div className="stats-row">
        <div className="stat-card">
          <span className="stat-label">Balance</span>
          <span className="stat-value">{wallet.availableBalance}</span>
        </div>
        <div className="stat-card">
          <span className="stat-label">Daily Limit</span>
          <span className="stat-value" style={{ fontSize: 'var(--text-lg)' }}>{wallet.dailySpendLimit ?? '—'}</span>
        </div>
      </div>
    </section>
  );
}
