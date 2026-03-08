# skill_ingest_platform_signals

## Purpose

Normalize social-platform events into a Chimera perception payload that downstream relevance scoring and task-creation flows can consume without depending on provider-specific APIs.

This skill is the documentation-level package for the spec requirement that perception must enter the system through MCP Resources.

## Trigger Conditions

- A connector fetches mentions, replies, reactions, trend events, or other engagement signals.
- A polling or subscription flow receives an external event that may affect an active campaign or agent.
- The control plane needs a normalized signal envelope for scoring, audit, and deduplication.

## Dependencies and External Boundaries

- Upstream source: platform connector implementations.
- Perception boundary: MCP Resource descriptors and payloads.
- Downstream consumers: signal scoring, planner task creation, audit logging.

## Input Contract

### Required Fields

| Field | Type | Description |
| --- | --- | --- |
| `tenantWorkspaceId` | `uuid` | Tenant boundary for the signal. |
| `chimeraAgentId` | `uuid` | Agent that owns or is targeted by the signal. |
| `correlationId` | `uuid` | Idempotency and audit key for the ingestion attempt. |
| `platform` | `string` | Source platform, such as `instagram`, `tiktok`, or `youtube`. |
| `resourceType` | `string` | MCP Resource type identifier, such as `social.mention` or `social.trend`. |
| `resourceUri` | `string` | Canonical MCP Resource URI for the external event. |
| `occurredAt` | `datetime` | Timestamp when the external event occurred. |
| `payload` | `object` | Normalized event body from the connector. |

### Optional Fields

| Field | Type | Description |
| --- | --- | --- |
| `campaignId` | `uuid` | Campaign context if the signal maps directly to an active campaign. |
| `connectorAccountId` | `string` | Provider account or channel identifier used by the connector. |
| `signalType` | `string` | Semantic subtype such as `mention`, `reply`, `trend`, or `payment_event`. |
| `engagementMetrics` | `object` | Counts such as views, likes, replies, or reposts. |
| `sourceContentExternalId` | `string` | Remote content identifier that triggered the event. |

### Input Example

```json
{
  "tenantWorkspaceId": "15dc7d3d-c0fd-4d7f-8584-c0d7331a0319",
  "chimeraAgentId": "5c6f5dd3-2a8b-45cf-9d7b-2de6db4cf58b",
  "campaignId": "8f7954c7-96ec-48d2-b624-bb5f7a2c494c",
  "correlationId": "e5f58d3d-d53e-4d58-aad7-b3130fcff2fb",
  "platform": "instagram",
  "resourceType": "social.mention",
  "resourceUri": "mcp://instagram/mentions/178900000001",
  "signalType": "mention",
  "occurredAt": "2026-03-08T14:21:00Z",
  "sourceContentExternalId": "178900000001",
  "engagementMetrics": {
    "likes": 321,
    "replies": 17
  },
  "payload": {
    "authorHandle": "@stylewatch",
    "text": "Aster Nova should react to this recycled denim trend.",
    "language": "en"
  }
}
```

## Output Contract

### Required Fields

| Field | Type | Description |
| --- | --- | --- |
| `status` | `string` | One of `accepted`, `ignored`, `duplicate`, or `failed`. |
| `correlationId` | `uuid` | Echoed idempotency and audit key. |
| `signalEnvelope` | `object` | Normalized Chimera signal record for scoring and storage. |
| `nextAction` | `string` | One of `score_relevance`, `discard`, or `retry`. |
| `auditMetadata` | `object` | Metadata required for immutable traceability. |

### Signal Envelope Shape

| Field | Type | Description |
| --- | --- | --- |
| `tenantWorkspaceId` | `uuid` | Owning tenant. |
| `chimeraAgentId` | `uuid` | Owning or targeted agent. |
| `campaignId` | `uuid?` | Campaign context if known. |
| `platform` | `string` | Normalized source platform. |
| `resourceType` | `string` | MCP Resource type. |
| `resourceUri` | `string` | Canonical MCP Resource URI. |
| `contentPreview` | `string` | Short excerpt for operator and reviewer visibility. |
| `payload` | `object` | Original normalized body retained for downstream logic. |
| `occurredAt` | `datetime` | Event time from the source system. |

### Output Example

```json
{
  "status": "accepted",
  "correlationId": "e5f58d3d-d53e-4d58-aad7-b3130fcff2fb",
  "nextAction": "score_relevance",
  "signalEnvelope": {
    "tenantWorkspaceId": "15dc7d3d-c0fd-4d7f-8584-c0d7331a0319",
    "chimeraAgentId": "5c6f5dd3-2a8b-45cf-9d7b-2de6db4cf58b",
    "campaignId": "8f7954c7-96ec-48d2-b624-bb5f7a2c494c",
    "platform": "instagram",
    "resourceType": "social.mention",
    "resourceUri": "mcp://instagram/mentions/178900000001",
    "contentPreview": "Aster Nova should react to this recycled denim trend.",
    "payload": {
      "authorHandle": "@stylewatch",
      "text": "Aster Nova should react to this recycled denim trend.",
      "language": "en"
    },
    "occurredAt": "2026-03-08T14:21:00Z"
  },
  "auditMetadata": {
    "connector": "instagram",
    "ingestedAt": "2026-03-08T14:21:04Z",
    "idempotencyKey": "e5f58d3d-d53e-4d58-aad7-b3130fcff2fb"
  }
}
```

## Failure Modes

- Reject the input as `failed` when tenant, agent, or MCP Resource identifiers are missing.
- Return `duplicate` when the same `correlationId` was already accepted.
- Return `ignored` when the signal is valid but outside configured tenant or campaign scope.
- Return `retry` in `nextAction` when the connector payload is temporarily incomplete or downstream storage is unavailable.

## Observability and Audit Requirements

- Log ingestion outcome by `correlationId`.
- Preserve the original connector platform and normalized `resourceUri`.
- Emit immutable audit metadata for accepted and failed ingestions.

## Implementation Status

Draft contract only. Runtime logic, schemas, and tests are not implemented yet.
