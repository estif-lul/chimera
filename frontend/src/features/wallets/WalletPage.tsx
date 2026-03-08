import { useParams, Link } from 'react-router-dom';
import { useWallet, useWalletTransactions, type TransactionRequestView } from '../../services/api/wallets';
import {
  ArrowLeft,
  Loader2,
  AlertCircle,
  Wallet,
  ArrowUpRight,
  ArrowDownLeft,
  Banknote,
  Shield,
  CreditCard,
  Inbox,
} from 'lucide-react';

function txStatusBadge(status: string) {
  const s = status.toLowerCase();
  if (s === 'completed' || s === 'settled') return 'badge badge-success';
  if (s === 'pending') return 'badge badge-warning';
  if (s === 'failed' || s === 'rejected') return 'badge badge-error';
  return 'badge badge-default';
}

/**
 * Wallet overview page showing balance, limits, and transaction history.
 */
export default function WalletPage() {
  const { agentId } = useParams<{ agentId: string }>();
  const { data: wallet, isLoading, error } = useWallet(agentId ?? '');
  const { data: transactions } = useWalletTransactions(wallet?.id ?? '');

  if (isLoading) {
    return (
      <div className="loading-state">
        <Loader2 className="spinner" />
        <span>Loading wallet</span>
      </div>
    );
  }

  if (error || !wallet) {
    return (
      <div className="error-banner" role="alert">
        <AlertCircle size={16} />
        Wallet not found.
      </div>
    );
  }

  return (
    <div>
      <Link to="/agents" className="back-link">
        <ArrowLeft size={16} />
        Back to Agents
      </Link>

      <div className="page-header">
        <div>
          <h1>Wallet</h1>
          <p className="page-subtitle">Agent wallet overview</p>
        </div>
        <span className={`badge ${wallet.status.toLowerCase() === 'active' ? 'badge-success' : 'badge-default'}`}>
          {wallet.status}
        </span>
      </div>

      <div className="stats-row" style={{ marginBottom: 'var(--space-6)' }}>
        <div className="stat-card">
          <span className="stat-label"><Banknote size={14} /> Balance</span>
          <span className="stat-value">{wallet.availableBalance}</span>
        </div>
        <div className="stat-card">
          <span className="stat-label"><Shield size={14} /> Daily Limit</span>
          <span className="stat-value" style={{ fontSize: 'var(--text-lg)' }}>{wallet.dailySpendLimit ?? '\u2014'}</span>
        </div>
        <div className="stat-card">
          <span className="stat-label"><CreditCard size={14} /> Per-Tx Limit</span>
          <span className="stat-value" style={{ fontSize: 'var(--text-lg)' }}>{wallet.perTransactionLimit ?? '\u2014'}</span>
        </div>
      </div>

      <div className="card">
        <div className="card-header">
          <span className="card-title"><Wallet size={18} /> Transactions</span>
          <span className="badge badge-default">{transactions?.length ?? 0}</span>
        </div>

        {transactions && transactions.length > 0 ? (
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>Direction</th>
                  <th>Amount</th>
                  <th>Asset</th>
                  <th>Status</th>
                  <th>Flags</th>
                </tr>
              </thead>
              <tbody>
                {transactions.map((tx: TransactionRequestView) => (
                  <tr key={tx.id}>
                    <td>
                      <span style={{ display: 'inline-flex', alignItems: 'center', gap: 'var(--space-1)' }}>
                        {tx.direction.toLowerCase() === 'outbound' ? (
                          <ArrowUpRight size={14} style={{ color: 'var(--color-error)' }} />
                        ) : (
                          <ArrowDownLeft size={14} style={{ color: 'var(--color-success)' }} />
                        )}
                        {tx.direction}
                      </span>
                    </td>
                    <td style={{ fontWeight: 500, color: 'var(--color-text-primary)' }}>{tx.amount}</td>
                    <td>{tx.assetCode}</td>
                    <td><span className={txStatusBadge(tx.status)}>{tx.status}</span></td>
                    <td>{tx.policyFlags.join(', ') || '\u2014'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <div className="empty-state" style={{ padding: 'var(--space-8)' }}>
            <Inbox size={36} />
            <p>No transactions yet.</p>
          </div>
        )}
      </div>
    </div>
  );
}
