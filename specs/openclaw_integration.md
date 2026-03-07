# Chimera OpenClaw Integration Plan

## Purpose

This document defines how Chimera publishes agent availability and operational status to the OpenClaw network. The goal is to make Chimera agents discoverable and trustworthy to other agents and services on OpenClaw while preserving Chimera's tenant boundaries, policy enforcement, auditability, and FastRender orchestration model.

## Goals

- Publish a machine-readable availability signal for each eligible Chimera agent.
- Publish richer operational status updates when agent state changes materially.
- Allow OpenClaw peers to discover whether an agent is reachable, active, rate-limited, paused, or review-blocked.
- Preserve provenance, signatures, and audit history for all outbound status publications.
- Prevent OpenClaw publication from bypassing tenant policy, review rules, or platform controls.

## Non-Goals

- This document does not define full cross-agent negotiation, collaboration contracts, or content exchange on OpenClaw.
- This document does not define inbound OpenClaw message handling beyond the minimum acknowledgment and liveness assumptions required for status publication.
- This document does not require fully decentralized orchestration in v1.

## Definitions

- Availability: A lightweight statement about whether a Chimera agent is reachable and eligible for work on the OpenClaw network.
- Status: A richer statement about the agent's current operating condition, such as active campaign execution, paused state, review hold, degraded mode, or connector failure.
- OpenClaw Connector: Chimera's external adapter responsible for transforming internal state into OpenClaw-compatible payloads and publishing them.
- Status Lease: A time-bounded validity period after which OpenClaw peers should consider the published availability stale unless refreshed.

## When Chimera Should Publish to OpenClaw

Chimera should publish OpenClaw status in two modes.

### 1. Heartbeat Publication

Heartbeat publication is periodic and lightweight. It answers the question: is this agent currently available on the network?

Trigger conditions:

- Agent becomes active and OpenClaw publishing is enabled.
- Regular heartbeat interval elapses.
- Previous status lease is close to expiration.

Recommended behavior:

- Publish every 30 to 60 seconds for active agents.
- Use a shorter lease than the heartbeat retry ceiling so stale agents age out naturally.
- Suppress heartbeat publication for retired, disabled, or tenant-suspended agents.

### 2. Event-Driven Status Publication

Event-driven publication is sent when state changes materially and peers need to know quickly.

Trigger conditions:

- Agent state changes between `active`, `busy`, `paused`, `review_blocked`, `rate_limited`, `degraded`, or `offline`.
- Campaign approval activates OpenClaw participation.
- Review queue or policy engine blocks external execution.
- Connector health or rate-limit status changes.
- Tenant suspension or wallet restriction makes the agent unavailable for external work.

Recommended behavior:

- Publish immediately on material transitions.
- De-duplicate repeated transitions inside a short window.
- Always write an audit event before or with the outbound publish attempt.

## Internal Source of Truth

OpenClaw status must be derived from Chimera's internal control-plane state, not from direct connector guesses alone.

Primary internal sources:

- `ChimeraAgent.currentState`
- Campaign status and task queue state
- Review queue status for any blocking item
- Confidence and policy evaluation state
- Connector rate-limit and health status
- Tenant workspace status
- Wallet restrictions when financial ability is advertised as part of capabilities

The Planner, Worker, and Judge responsibilities remain inside Chimera. OpenClaw receives the resulting published state, not internal orchestration implementation details.

## Availability Model

Chimera should standardize a narrow availability vocabulary so OpenClaw peers can act on it consistently.

### Availability States

- `available`: Agent is reachable and allowed to accept external discovery or collaboration requests.
- `busy`: Agent is healthy but currently saturated by campaign execution or queue pressure.
- `review_blocked`: Agent cannot execute externally because required review or policy approval is pending.
- `rate_limited`: Agent is temporarily unable to interact because a platform or network quota has been reached.
- `paused`: Operator or policy has intentionally paused external participation.
- `degraded`: Agent is partially available but a dependency is impaired.
- `offline`: Agent should be treated as unavailable.

### Mapping from Chimera State

- Active tenant, active agent, healthy connectors, no blocking review item: `available`.
- Active agent with high queue depth or in-flight task saturation: `busy`.
- Sensitive or below-threshold pending item blocking external actions: `review_blocked`.
- Platform connector or OpenClaw connector quota exceeded: `rate_limited`.
- Campaign paused, agent paused, or tenant suspended: `paused` or `offline` depending on severity.
- Dependency outage with fallback still working: `degraded`.
- Agent retired, disabled, or disconnected from required services: `offline`.

## Status Payload Design

OpenClaw status should be published as signed JSON-LD so it remains machine-readable, extensible, and aligned with the existing architecture research.

### Required Fields

- `schemaVersion`
- `messageType` set to `agent.status`
- `statusId`
- `publishedAt`
- `expiresAt`
- `tenantScopedAgentId`
- `agentDid`
- `availability`
- `statusSummary`
- `capabilities`
- `policyFlags`
- `connectorHealth`
- `traceId`
- `signature`

### Recommended Fields

- `activeCampaignIds` when exposure is policy-allowed
- `queueDepthClass` such as `low`, `medium`, `high`
- `acceptingCollaborations`
- `preferredProtocols`
- `rateLimitResetAt`
- `evidence` summarizing why the current state was chosen

### Example Availability Payload

```json
{
  "@context": "https://openclaw.net/schemas/agent-status/v1",
  "schemaVersion": "1.0",
  "messageType": "agent.status",
  "statusId": "0c0f4fb2-b893-4dcb-b4fc-706f7d1d32c7",
  "publishedAt": "2026-03-07T15:05:00Z",
  "expiresAt": "2026-03-07T15:06:00Z",
  "tenantScopedAgentId": "15dc7d3d-c0fd-4d7f-8584-c0d7331a0319:5c6f5dd3-2a8b-45cf-9d7b-2de6db4cf58b",
  "agentDid": "did:chimera:aster-nova",
  "availability": "available",
  "statusSummary": "Agent is active and accepting OpenClaw interactions.",
  "capabilities": [
    "trend-monitoring:v1",
    "short-form-publishing:v1",
    "review-gated-replies:v1"
  ],
  "policyFlags": [
    "identity-disclosure-enabled",
    "review-required-for-sensitive-topics"
  ],
  "connectorHealth": {
    "openclaw": "healthy",
    "publishing": "healthy"
  },
  "queueDepthClass": "low",
  "traceId": "4a7cb43f-115b-4cd0-b6f7-8eb4051cf25a",
  "signature": {
    "keyId": "did:chimera:aster-nova#status-key-1",
    "algorithm": "EdDSA",
    "value": "base64-signature"
  }
}
```

### Example Review-Blocked Payload

```json
{
  "@context": "https://openclaw.net/schemas/agent-status/v1",
  "schemaVersion": "1.0",
  "messageType": "agent.status",
  "statusId": "29eb5e8c-f08e-4df7-a005-c5956d8bd4d4",
  "publishedAt": "2026-03-07T15:05:00Z",
  "expiresAt": "2026-03-07T15:10:00Z",
  "tenantScopedAgentId": "15dc7d3d-c0fd-4d7f-8584-c0d7331a0319:5c6f5dd3-2a8b-45cf-9d7b-2de6db4cf58b",
  "agentDid": "did:chimera:aster-nova",
  "availability": "review_blocked",
  "statusSummary": "External execution is paused pending human review.",
  "policyFlags": [
    "human-review-pending",
    "sensitive-topic-hold"
  ],
  "connectorHealth": {
    "openclaw": "healthy",
    "publishing": "suppressed"
  },
  "traceId": "2be774e5-c1e9-47ad-a4ab-cdbeb08c67ff",
  "signature": {
    "keyId": "did:chimera:aster-nova#status-key-1",
    "algorithm": "EdDSA",
    "value": "base64-signature"
  }
}
```

## Publishing Architecture

## Components

### 1. Status Projection Service

Responsibility:

- Read Chimera control-plane state and calculate the canonical OpenClaw availability for each agent.
- Normalize internal states into the narrow OpenClaw availability vocabulary.
- Decide whether a new publish is required.

Inputs:

- agent state
- campaign state
- review queue state
- connector health
- rate-limit state
- tenant status

Outputs:

- internal `OpenClawStatusProjection`

### 2. OpenClaw Publisher Service

Responsibility:

- Convert `OpenClawStatusProjection` into the OpenClaw wire payload.
- Sign the payload with the agent or platform key material.
- Submit the payload through the OpenClaw connector.
- Record result, lease expiration, retries, and errors.

### 3. OpenClaw Connector

Responsibility:

- Encapsulate transport details for OpenClaw.
- Provide `publishStatus`, `refreshStatus`, and `withdrawStatus` operations.
- Return structured outcomes such as `accepted`, `completed`, `failed`, and `rate_limited`.

### 4. Audit and Telemetry Pipeline

Responsibility:

- Persist immutable audit events for every attempted or completed status publication.
- Emit metrics for publish latency, heartbeat freshness, failure rate, and stale lease count.

## Publish Flow

### Heartbeat Flow

1. Scheduler selects all OpenClaw-enabled agents whose status lease is near expiration.
2. Status Projection Service computes current availability.
3. If the projected state has not changed and lease refresh is needed, create a heartbeat publish request.
4. Audit event is recorded with correlation ID.
5. OpenClaw Publisher Service signs and sends the payload.
6. Connector returns outcome.
7. Lease metadata and publication timestamp are updated.
8. Metrics and logs are emitted.

### Event-Driven Flow

1. Internal domain event occurs, such as review hold, rate limit, pause, or campaign activation.
2. Status Projection Service recomputes availability.
3. If the projected availability changed materially, enqueue immediate OpenClaw publish work.
4. OpenClaw Publisher Service signs and publishes.
5. Audit event, publish result, and retry metadata are stored.

## Internal Data Model Additions

Chimera should add dedicated records for OpenClaw publication state.

### OpenClawStatusRegistration

- `id`
- `tenantWorkspaceId`
- `chimeraAgentId`
- `agentDid`
- `statusPublishingEnabled`
- `defaultLeaseSeconds`
- `lastPublishedAt`
- `lastAvailability`
- `lastStatusHash`
- `lastTraceId`
- `nextHeartbeatAt`
- `statusEndpoint`

### OpenClawPublishAttempt

- `id`
- `registrationId`
- `correlationId`
- `publishType` (`heartbeat`, `state_change`, `withdrawal`)
- `requestPayload`
- `responsePayload`
- `resultStatus`
- `attemptedAt`
- `completedAt`
- `failureReason`

These records should live in PostgreSQL for governance and traceability, while larger payload snapshots may be mirrored to MongoDB if needed for flexible analysis.

## Security and Trust Model

### Identity

- Every OpenClaw-published agent should have a stable `agentDid`.
- The DID should resolve to public verification material for status signatures.
- Tenant identity must never be exposed beyond the minimum agent-scoped identifier needed for traceability.

### Signing

- Status payloads must be signed before publication.
- Private keys must remain in the configured secrets manager and never be logged.
- Signature verification failure should cause the publish to fail closed.

### Policy Enforcement

- OpenClaw publication must be disabled when a tenant is suspended.
- OpenClaw publication must not claim `available` while a blocking review item exists.
- Connector errors must not silently fall back to stale optimistic state.

## Review and Governance Rules

- Publishing availability to OpenClaw is operational metadata and can be automated when the tenant permits external participation.
- Publishing any richer collaboration or intent message beyond availability requires the same policy checks as other outward actions.
- If an agent is under sensitive-topic review hold, Chimera should publish `review_blocked` rather than suppressing status entirely. This makes the network state truthful without revealing protected content.

## Failure Handling

### Connector Failure

- Mark connector health as degraded.
- Retry with exponential backoff.
- If retries exceed threshold, allow current lease to expire so peers see the agent as stale or offline.
- Raise an exception event for operators if failure persists.

### Rate Limit

- Publish a `rate_limited` state if OpenClaw accepts status updates under a different quota path.
- If even status publication is rate-limited, keep retry metadata internally and alert operators before lease expiry.

### Stale Internal State

- Status publication must read the latest committed task and campaign version.
- If version checks fail during projection, recompute before publishing.

## Observability

Required metrics:

- `openclaw_status_publish_total`
- `openclaw_status_publish_failures_total`
- `openclaw_status_publish_latency_ms`
- `openclaw_status_lease_seconds_remaining`
- `openclaw_status_stale_agents_total`
- `openclaw_status_state_transitions_total`

Required structured log fields:

- `traceId`
- `tenantWorkspaceId`
- `chimeraAgentId`
- `agentDid`
- `availability`
- `publishType`
- `resultStatus`
- `retryCount`

## API and Contract Implications

Chimera should add internal control-plane APIs for OpenClaw status administration.

Recommended endpoints:

- `GET /api/v1/agents/{agentId}/openclaw-status`
- `PUT /api/v1/agents/{agentId}/openclaw-status`
- `POST /api/v1/agents/{agentId}/openclaw-status/publish`
- `GET /api/v1/agents/{agentId}/openclaw-status/history`

Recommended DTOs:

- `OpenClawStatusView`
- `OpenClawStatusConfigRequest`
- `OpenClawPublishResultView`
- `OpenClawPublishAttemptView`

## Delivery Plan

### Phase 1: Internal Status Projection

- Add OpenClaw status registration and publish attempt entities.
- Implement availability projection rules from existing agent, campaign, review, and connector state.
- Expose internal read APIs for projected OpenClaw state.

### Phase 2: Connector and Signing

- Implement OpenClaw connector transport.
- Add signing support using DID-linked key material.
- Publish manual status updates from an operator-triggered endpoint.

### Phase 3: Automated Heartbeats and State Changes

- Add heartbeat scheduler.
- Subscribe to domain events for immediate state-change publication.
- Add retries, deduplication, and lease expiry management.

### Phase 4: Operational Hardening

- Add dashboards, metrics, and alerts.
- Add replay tooling for failed publish attempts.
- Add policy controls per tenant for OpenClaw participation.

## Testing Plan

### Unit Tests

- availability mapping from internal state
- lease refresh logic
- blocking review and rate-limit behavior
- state-change deduplication

### Integration Tests

- successful publish through OpenClaw connector
- signature generation and verification workflow
- retry and backoff behavior on transient failure
- stale version rejection and recompute

### End-to-End Tests

- operator enables OpenClaw status publication for an agent
- campaign activates and agent publishes `available`
- review hold triggers `review_blocked`
- connector quota issue triggers `rate_limited`
- tenant suspension withdraws or expires network status

## Acceptance Criteria

- An OpenClaw-enabled agent can publish signed availability payloads on a renewable lease.
- Chimera never advertises an externally blocked agent as `available`.
- Every publish attempt is auditable by tenant, agent, trace ID, and result.
- Status heartbeats expire safely when Chimera can no longer verify availability.
- Operators can inspect current OpenClaw status and recent publish history from the control plane.