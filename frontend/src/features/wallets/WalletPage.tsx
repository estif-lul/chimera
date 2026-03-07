import { useParams, Link } from 'react-router-dom';
import { useWallet, useWalletTransactions, type TransactionRequestView } from '../../services/api/wallets';

/**
 * Wallet overview page showing balance, limits, and transaction history.
 */
export default function WalletPage() {
  const { agentId } = useParams<{ agentId: string }>();
  const { data: wallet, isLoading, error } = useWallet(agentId ?? '');
  const { data: transactions } = useWalletTransactions(wallet?.id ?? '');

  if (isLoading) return <p>Loading wallet…</p>;
  if (error || !wallet) return <p role="alert">Wallet not found.</p>;

  return (
    <main>
      <nav><Link to="/agents">← Agents</Link></nav>
      <h2>Wallet — {wallet.status}</h2>
      <dl>
        <dt>Balance</dt><dd>{wallet.availableBalance}</dd>
        <dt>Daily Spend Limit</dt><dd>{wallet.dailySpendLimit ?? '—'}</dd>
        <dt>Per-Transaction Limit</dt><dd>{wallet.perTransactionLimit ?? '—'}</dd>
      </dl>

      <h3>Transactions</h3>
      {transactions && transactions.length > 0 ? (
        <table>
          <thead>
            <tr>
              <th>Direction</th><th>Amount</th><th>Asset</th><th>Status</th><th>Flags</th>
            </tr>
          </thead>
          <tbody>
            {transactions.map((tx: TransactionRequestView) => (
              <tr key={tx.id}>
                <td>{tx.direction}</td>
                <td>{tx.amount}</td>
                <td>{tx.assetCode}</td>
                <td>{tx.status}</td>
                <td>{tx.policyFlags.join(', ') || '—'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : (
        <p>No transactions yet.</p>
      )}
    </main>
  );
}
