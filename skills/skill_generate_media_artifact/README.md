# skill_generate_media_artifact

## Purpose

Generate a non-text campaign artifact through MCP Tool orchestration while preserving render-tier choice, provider provenance, asset location, and cost attribution.

This skill covers image and video generation only. Caption, script, and reply text remain native to the core LLM and are intentionally out of scope.

## Trigger Conditions

- A planned task requires image generation for a post, thumbnail, or creative variant.
- A planned task requires video generation using Tier 1 living-portrait or Tier 2 full text-to-video rendering.
- The planner or worker has approved budget and policy context for a media task.

## Dependencies and External Boundaries

- Upstream source: planner or worker orchestration.
- Media boundary: MCP Tool providers such as `mcp-server-ideogram`, `mcp-server-midjourney`, `mcp-server-runway`, or `mcp-server-luma`.
- Downstream consumers: content artifact persistence, review workflow, connector publishing flow, audit logging.

## Input Contract

### Required Fields

| Field | Type | Description |
| --- | --- | --- |
| `tenantWorkspaceId` | `uuid` | Tenant boundary for the task. |
| `chimeraAgentId` | `uuid` | Agent requesting the artifact. |
| `campaignId` | `uuid` | Campaign context for cost and audit joins. |
| `taskId` | `uuid` | Task that owns the generation request. |
| `correlationId` | `uuid` | Idempotency and provenance key. |
| `assetType` | `string` | One of `image` or `video`. |
| `providerTool` | `string` | MCP Tool name selected for execution. |
| `promptPackage` | `object` | Structured prompt bundle for the provider. |

### Conditional Fields

| Field | Type | Description |
| --- | --- | --- |
| `renderTier` | `string` | Required for `video`; one of `tier1` or `tier2`. |
| `sourceImageUri` | `string` | Required for Tier 1 living-portrait generation. |
| `styleReferenceIds` | `string[]` | Optional style or character reference pointers. |
| `budgetGuardrails` | `object` | Optional budget constraints checked before execution. |
| `disclosureMode` | `string` | Content disclosure expectation such as `automated` or `assisted`. |

### Prompt Package Shape

| Field | Type | Description |
| --- | --- | --- |
| `brief` | `string` | Creative brief anchored to persona and campaign goals. |
| `visualGoal` | `string` | Intended visual outcome. |
| `personaConstraints` | `string[]` | Immutable persona or brand rules that the provider output must honor. |
| `format` | `string` | Output format such as `portrait_image`, `story_video`, or `feed_video`. |

### Input Example

```json
{
  "tenantWorkspaceId": "15dc7d3d-c0fd-4d7f-8584-c0d7331a0319",
  "chimeraAgentId": "5c6f5dd3-2a8b-45cf-9d7b-2de6db4cf58b",
  "campaignId": "8f7954c7-96ec-48d2-b624-bb5f7a2c494c",
  "taskId": "6ad44c5c-4a0b-4e53-8c47-9c0dcfa89302",
  "correlationId": "866b2430-cf63-4496-b0fb-cd40a07d3af0",
  "assetType": "video",
  "renderTier": "tier1",
  "providerTool": "mcp-server-runway",
  "sourceImageUri": "s3://chimera/assets/agent-aster-keyframe.png",
  "styleReferenceIds": [
    "ref_01HXYZ",
    "char_aster_v3"
  ],
  "budgetGuardrails": {
    "maxCostUsd": "12.00"
  },
  "disclosureMode": "automated",
  "promptPackage": {
    "brief": "Create a short eco-fashion reaction clip about recycled denim trends.",
    "visualGoal": "Confident selfie-style vertical clip with energetic delivery.",
    "personaConstraints": [
      "keep Aster Nova visually consistent",
      "avoid luxury-brand logos",
      "do not imply human status"
    ],
    "format": "story_video"
  }
}
```

## Output Contract

### Required Fields

| Field | Type | Description |
| --- | --- | --- |
| `status` | `string` | One of `accepted`, `completed`, `failed`, or `review_required`. |
| `correlationId` | `uuid` | Echoed idempotency key. |
| `assetUri` | `string` | Stored artifact location when generation succeeds. |
| `providerJobId` | `string` | Provider-side job identifier for tracking. |
| `contentArtifact` | `object` | Normalized Chimera artifact summary. |
| `provenance` | `object` | Provider, tier, input references, and cost metadata. |

### Content Artifact Shape

| Field | Type | Description |
| --- | --- | --- |
| `artifactType` | `string` | `image` or `video`. |
| `previewText` | `string` | Human-readable summary for operators and review flows. |
| `confidenceScore` | `number?` | Optional quality signal when available. |
| `policyClassification` | `string?` | Optional policy classification for downstream routing. |
| `disclosureMode` | `string` | Disclosure mode retained on the artifact. |

### Provenance Shape

| Field | Type | Description |
| --- | --- | --- |
| `providerTool` | `string` | Executed MCP Tool. |
| `renderTier` | `string?` | Tier used for video generation. |
| `sourceImageUri` | `string?` | Source image for Tier 1 video, if used. |
| `styleReferenceIds` | `string[]` | Persona continuity references. |
| `estimatedCostUsd` | `string?` | Cost reported or inferred for the generation. |
| `metadata` | `object` | Provider-specific payload retained for audit. |

### Output Example

```json
{
  "status": "completed",
  "correlationId": "866b2430-cf63-4496-b0fb-cd40a07d3af0",
  "assetUri": "s3://chimera/generated/video/aster-denim-story.mp4",
  "providerJobId": "runway-job-9012",
  "contentArtifact": {
    "artifactType": "video",
    "previewText": "Vertical story clip of Aster Nova reacting to recycled denim trend coverage.",
    "confidenceScore": 0.93,
    "policyClassification": "standard",
    "disclosureMode": "automated"
  },
  "provenance": {
    "providerTool": "mcp-server-runway",
    "renderTier": "tier1",
    "sourceImageUri": "s3://chimera/assets/agent-aster-keyframe.png",
    "styleReferenceIds": [
      "ref_01HXYZ",
      "char_aster_v3"
    ],
    "estimatedCostUsd": "8.40",
    "metadata": {
      "durationSeconds": 12,
      "aspectRatio": "9:16"
    }
  }
}
```

## Failure Modes

- Return `failed` when required task, tenant, or provider identifiers are missing.
- Return `failed` when a video request omits `renderTier`.
- Return `failed` when Tier 1 video generation omits `sourceImageUri`.
- Return `review_required` when the output is generated but policy checks or continuity checks require human review before publish.

## Observability and Audit Requirements

- Record `correlationId`, `providerTool`, `providerJobId`, `campaignId`, and `taskId` for every invocation.
- Preserve render tier, style references, and cost metadata for audit and budget analysis.
- Store provider-specific response metadata without making downstream consumers depend on it.

## Implementation Status

Draft contract only. Runtime invocation, provider adapters, persistence mapping, and tests are not implemented yet.
