import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { apiClient } from './client';

export interface WalletView {
  id: string;
  chimeraAgentId: string;
  status: string;
  availableBalance: string;
  dailySpendLimit: string | null;
  perTransactionLimit: string | null;
}

export interface TransactionRequestView {
  id: string;
  walletId: string;
  direction: string;
  amount: string;
  assetCode: string;
  status: string;
  policyFlags: string[];
}

export interface TransactionRequestInput {
  direction: string;
  amount: string;
  assetCode: string;
  counterparty?: string;
  rationale?: string;
}

export function useWallet(agentId: string) {
  return useQuery<WalletView>({
    queryKey: ['wallets', 'agent', agentId],
    queryFn: () => apiClient(`/api/v1/wallets/agent/${agentId}`),
    enabled: !!agentId,
  });
}

export function useWalletTransactions(walletId: string) {
  return useQuery<TransactionRequestView[]>({
    queryKey: ['wallets', walletId, 'transactions'],
    queryFn: () => apiClient(`/api/v1/wallets/${walletId}/transactions`),
    enabled: !!walletId,
  });
}

export function useSubmitTransaction(walletId: string) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (input: TransactionRequestInput) =>
      apiClient(`/api/v1/wallets/${walletId}/transactions`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(input),
      }),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['wallets', walletId, 'transactions'] });
      qc.invalidateQueries({ queryKey: ['wallets'] });
    },
  });
}
