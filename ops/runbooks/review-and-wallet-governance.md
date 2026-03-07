# Review & Wallet Governance Runbook

## Review Queue Operations

### Monitoring the Queue
- `GET /api/v1/reviews` lists all pending review items
- Items are created automatically when content or transactions fail policy checks

### Processing Reviews
1. Claim a review item (status: `pending` → `claimed`)
2. Inspect the linked content artifact or transaction request
3. Submit decision: `approve`, `reject`, or `edit`
4. An immutable `review_decision` record is created for audit trail

### Escalation Triggers
- **Content confidence** below `reviewThreshold` in the tenant's `confidence_policy`
- **Transaction policy violations** (daily limit, per-transaction limit, insufficient balance)
- **Anomaly detection** flags on transaction counterparties

## Wallet Governance

### Daily Spend Monitoring
```sql
SELECT w.id, w.chimera_agent_id, w.available_balance, w.daily_spend_limit,
       COALESCE(SUM(t.amount) FILTER (WHERE t.direction = 'outbound'
           AND t.status IN ('approved','executed')
           AND t.created_at >= CURRENT_DATE), 0) AS spent_today
FROM wallet w
LEFT JOIN transaction_request t ON t.wallet_id = w.id
GROUP BY w.id;
```

### Investigating Flagged Transactions
1. Check `policy_flags` on the `transaction_request` record
2. Review the corresponding `review_item` if escalated
3. Inspect `audit_event` entries for the transaction

### Emergency Wallet Suspension
1. Update wallet status: `active` → `suspended`
2. All pending outbound transactions will be blocked
3. Inbound payments continue to accumulate

### Policy Configuration
- `daily_spend_limit` and `per_transaction_limit` are set per wallet
- `confidence_policy` thresholds control auto-approve vs review routing
- Changes to limits take effect immediately on next transaction

## Audit Trail

- All actions produce immutable `audit_event` records
- Events include: `actor_type`, `event_type`, `resource_type`, `resource_id`, JSONB `event_payload`
- Correlation IDs link HTTP requests to audit events for end-to-end tracing
- View audit timeline at `/audit` in the frontend
