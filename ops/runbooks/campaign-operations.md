# Campaign Operations Runbook

## Health Checks

- **Backend**: `GET /actuator/health` — checks DB, Redis, Weaviate connectivity
- **Frontend**: Verify Vite dev server or static build is serving at configured port

## Campaign Lifecycle

### Starting a Campaign
1. Ensure agents are in `active` status
2. Create campaign via `POST /api/v1/campaigns`
3. Review the generated execution plan at `GET /api/v1/campaigns/{id}/plan`
4. Approve via `POST /api/v1/campaigns/{id}/approve`
5. Monitor task execution through the Campaign Monitor page

### Pausing a Campaign
- Use the campaign status transition: `active` → `paused`
- All in-flight tasks will complete; no new tasks will be dispatched

### Investigating Stuck Tasks
1. Check task status in the `task` table: `SELECT * FROM task WHERE campaign_id = ? AND status != 'completed'`
2. Review `worker_output` and `judge_decision_summary` JSONB fields for error details
3. Check `audit_event` table for correlation IDs: `SELECT * FROM audit_event WHERE resource_id = ?`
4. Examine application logs filtered by correlation ID (MDC `correlationId`)

## Signal Ingestion

- Signals are ingested asynchronously via `POST /api/v1/signals` (returns 202)
- Relevance scoring is heuristic-based in `SignalScoringService`
- Low-relevance signals (score < 0.3) can be periodically pruned

## Observability

### Metrics
- Spring Boot Actuator: `/actuator/prometheus`
- Key metrics: `http_server_requests`, `jvm_memory_used`, `db_pool_active_connections`

### Structured Logging
- All requests carry a `correlationId` via `CorrelationIdFilter`
- Log format: JSON with `timestamp`, `level`, `correlationId`, `message`

### Alerts
- Campaign stuck: no task transitions for > 30 minutes on an active campaign
- Signal backlog: pending signals count exceeds 1000
- Error rate: HTTP 5xx rate exceeds 1% over 5 minutes
