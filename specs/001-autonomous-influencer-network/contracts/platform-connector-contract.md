# Platform Connector Contract

## Purpose

This contract defines the boundary between the Chimera control plane and external social-platform connectors. Connectors encapsulate platform-specific API behavior so orchestration services can remain platform-agnostic. Perception inputs reaching the control plane must be exposed as MCP Resources, while publishing and engagement delivery remain connector responsibilities.

## Required Capabilities

Each connector implementation must expose the following operations:

- `publishContent`: Publish text and optional media for a tenant-owned agent.
- `replyToInteraction`: Send a reply associated with a prior interaction or content item.
- `fetchSignals`: Retrieve mentions, replies, reactions, or other engagement signals.
- `fetchPublishingStatus`: Resolve final publication state, remote identifiers, and delivery errors.
- `validateRateLimit`: Return current posting and interaction allowance for the connector account.
- `emitMcpResources`: Normalize fetched signals into MCP Resource descriptors consumable by agent perception flows.

## Request Shape

```json
{
  "tenantWorkspaceId": "uuid",
  "chimeraAgentId": "uuid",
  "platform": "string",
  "operation": "publishContent",
  "correlationId": "uuid",
  "payload": {
    "text": "string",
    "media": [
      {
        "url": "string",
        "mediaType": "image|video"
      }
    ],
    "replyToExternalId": "string",
    "disclosureMode": "automated|assisted",
    "sourceMcpResource": {
      "resourceType": "string",
      "resourceUri": "string"
    },
    "metadata": {
      "campaignId": "uuid",
      "taskId": "uuid"
    }
  }
}
```

## Response Shape

```json
{
  "correlationId": "uuid",
  "platform": "string",
  "operation": "publishContent",
  "status": "accepted|completed|failed|rate_limited",
  "externalResourceId": "string",
  "publishedAt": "2026-03-07T12:00:00Z",
  "rateLimit": {
    "remaining": 42,
    "resetAt": "2026-03-07T12:15:00Z"
  },
  "errors": []
}
```

## Behavioral Rules

- Connector operations must be idempotent by `correlationId`.
- Connectors must never bypass review or policy decisions determined by the control plane.
- Connector responses must preserve remote identifiers needed for audit and replay.
- Rate-limit or policy-related failures must return structured errors rather than opaque text.
- Secrets and platform credentials must be injected from environment or secret-management systems only.
- Signal ingestion from connectors must be transformed into MCP Resource records before Planner or Worker services consume it.

## Versioning

- Contract version starts at `v1`.
- Breaking changes require a new contract version and adapter compatibility plan.
- Connector implementations for the first release must cover at least three live platforms while honoring the same contract.