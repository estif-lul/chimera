# Data Model: Project Chimera Autonomous Influencer Network

## TenantWorkspace

- Purpose: Security and billing boundary for all customer-owned resources.
- Key fields:
  - `id`
  - `slug`
  - `displayName`
  - `status` (`active`, `suspended`, `archived`)
  - `defaultConfidencePolicyId`
  - `createdAt`
  - `updatedAt`
- Relationships:
  - Owns many `UserAccount`, `ChimeraAgent`, `Campaign`, `ReviewItem`, `Wallet`, and `AuditEvent` records.
- Validation rules:
  - `slug` must be unique.
  - Suspended or archived tenants cannot activate campaigns or publish content.

## UserAccount

- Purpose: Authenticated human principal for operators, reviewers, and technical administrators.
- Key fields:
  - `id`
  - `tenantWorkspaceId`
  - `email`
  - `authProviderType` (`local`, `oidc`)
  - `providerSubject`
  - `roleSet`
  - `status` (`invited`, `active`, `disabled`)
  - `lastLoginAt`
- Relationships:
  - Belongs to one `TenantWorkspace`.
  - Creates many `AuditEvent` and `ReviewDecision` records.
- Validation rules:
  - Email must be unique within tenant scope.
  - Local accounts require password credential material stored outside plaintext.
  - OIDC accounts require stable provider subject binding.

## ConfidencePolicy

- Purpose: Tenant- or campaign-scoped decision thresholds for automated approval and human review.
- Key fields:
  - `id`
  - `tenantWorkspaceId`
  - `campaignId` nullable
  - `autoApproveThreshold`
  - `reviewThreshold`
  - `sensitiveTopics`
  - `createdAt`
  - `updatedAt`
- Relationships:
  - Belongs to one `TenantWorkspace`.
  - Optionally overrides one `Campaign`.
- Validation rules:
  - `0.0 <= reviewThreshold <= autoApproveThreshold <= 1.0`
  - Sensitive topics list cannot be empty when policy is active.

## ChimeraAgent

- Purpose: Persistent digital influencer identity with governance, memory, and wallet references.
- Key fields:
  - `id`
  - `tenantWorkspaceId`
  - `displayName`
  - `personaSlug`
  - `soulDocumentVersion`
  - `soulDocumentLocation`
  - `status` (`draft`, `active`, `paused`, `retired`)
  - `immutablePersona`
  - `mutableBiographySummary`
  - `visualReferenceId`
  - `walletId`
  - `currentState`
  - `createdAt`
  - `updatedAt`
- Relationships:
  - Belongs to one `TenantWorkspace`.
  - Participates in many `Campaign` records.
  - Owns many `AgentMemoryRecord`, `Task`, `ContentArtifact`, and `WalletTransaction` records.
- Validation rules:
  - Immutable persona fields and the referenced `SOUL.md` version cannot be edited by automated workflows.
  - Active agents require an associated confidence policy and wallet record.

## SoulDefinition

- Purpose: Immutable persona blueprint loaded from `SOUL.md` during agent creation and versioned for explicit operator changes.
- Key fields:
  - `id`
  - `tenantWorkspaceId`
  - `personaSlug`
  - `version`
  - `backstory`
  - `voiceTone`
  - `coreBeliefsAndValues`
  - `directives`
  - `sourcePath`
  - `createdAt`
- Relationships:
  - Belongs to one `TenantWorkspace`.
  - May be referenced by many `ChimeraAgent` records over time.
- Validation rules:
  - `personaSlug` plus `version` must be unique within tenant scope.
  - Backstory, voice and tone, core beliefs and values, and directives are all required.
  - Runtime automation may reference a `SoulDefinition` but cannot mutate it.

## AgentMemoryRecord

- Purpose: Stores recent context and long-term summaries for agent continuity.
- Key fields:
  - `id`
  - `chimeraAgentId`
  - `memoryType` (`recent_context`, `long_term_summary`, `policy_note`, `biography_writeback`)
  - `content`
  - `embeddingReference`
  - `storageBackend` (`redis`, `weaviate`)
  - `sourceTaskId`
  - `sourceInteractionId` nullable
  - `engagementScore` nullable
  - `retentionUntil`
  - `createdAt`
- Relationships:
  - Belongs to one `ChimeraAgent`.
- Validation rules:
  - `policy_note` entries may reference governance but cannot mutate immutable persona.
  - `recent_context` entries should expire according to configured retention.
  - `long_term_summary` and `biography_writeback` entries must be persisted to Weaviate.
  - `biography_writeback` entries require provenance to a Judge-approved successful high-engagement interaction.

## Campaign

- Purpose: Operator-defined initiative that drives planning, execution, review, and measurement.
- Key fields:
  - `id`
  - `tenantWorkspaceId`
  - `name`
  - `goalDescription`
  - `targetAudience`
  - `brandConstraints`
  - `riskProfile`
  - `budgetPolicyId`
  - `confidencePolicyId`
  - `status` (`draft`, `planned`, `approved`, `active`, `paused`, `completed`, `cancelled`)
  - `createdByUserId`
  - `createdAt`
  - `updatedAt`
- Relationships:
  - Belongs to one `TenantWorkspace`.
  - Has many `CampaignAgentAssignment`, `ExecutionPlan`, `Task`, `ExternalSignal`, and `AuditEvent` records.
- Validation rules:
  - Campaign cannot move to `approved` without at least one assigned agent and one generated `ExecutionPlan`.
  - Paused campaigns cannot accept new execution commits.

## CampaignAgentAssignment

- Purpose: Join entity linking agents to campaigns.
- Key fields:
  - `campaignId`
  - `chimeraAgentId`
  - `assignmentRole`
  - `createdAt`
- Validation rules:
  - Agent and campaign must belong to the same tenant.

## ExecutionPlan

- Purpose: Reviewable decomposition of a campaign into taskable work.
- Key fields:
  - `id`
  - `campaignId`
  - `planVersion`
  - `summary`
  - `acceptanceCriteria`
  - `generatedBy`
  - `status` (`draft`, `under_review`, `approved`, `superseded`)
  - `createdAt`
- Relationships:
  - Belongs to one `Campaign`.
  - Has many `Task` records.
- Validation rules:
  - Only one approved plan may be active per campaign.
  - Superseded plans are immutable.

## Task

- Purpose: Atomic unit of orchestrated work handled by Planner, Worker, and Judge responsibilities.
- Key fields:
  - `id`
  - `tenantWorkspaceId`
  - `campaignId`
  - `chimeraAgentId`
  - `executionPlanId`
  - `taskType`
  - `priority`
  - `videoRenderTier` nullable (`tier_1_living_portrait`, `tier_2_hero_video`)
  - `status` (`pending`, `in_progress`, `awaiting_review`, `approved`, `rejected`, `executed`, `failed`, `cancelled`)
  - `plannerContext`
  - `workerOutput`
  - `judgeDecisionSummary`
  - `stateVersion`
  - `createdAt`
  - `updatedAt`
- Relationships:
  - Belongs to one `Campaign`, one `ChimeraAgent`, and one `ExecutionPlan`.
  - May produce one `ContentArtifact` or one `TransactionRequest`.
  - May create one or more `ReviewItem` and `AuditEvent` records.
- Validation rules:
  - State updates must increment `stateVersion` for optimistic concurrency.
  - Tasks tied to paused campaigns cannot transition to `executed`.
  - Video tasks must declare a render tier before media generation begins.
- State transitions:
  - `pending -> in_progress -> awaiting_review -> approved -> executed`
  - `pending -> in_progress -> awaiting_review -> rejected`
  - `pending -> cancelled`
  - `in_progress -> failed`

## ExternalSignal

- Purpose: Incoming trend, mention, or market input that may generate new work.
- Key fields:
  - `id`
  - `tenantWorkspaceId`
  - `campaignId` nullable
  - `chimeraAgentId` nullable
  - `mcpResourceType`
  - `mcpResourceUri`
  - `sourcePlatform`
  - `signalType`
  - `payloadSummary`
  - `relevanceScore`
  - `receivedAt`
  - `processedAt`
- Validation rules:
  - `relevanceScore` must be between `0.0` and `1.0`.
  - Raw payload retention must follow redaction and data-minimization policy.
  - Perception records must originate from an MCP Resource rather than direct provider-specific ingestion.

## ContentArtifact

- Purpose: Generated text or media draft connected to review and execution.
- Key fields:
  - `id`
  - `taskId`
  - `chimeraAgentId`
  - `campaignId`
  - `artifactType` (`text`, `image`, `video`, `mixed`)
  - `contentLocation`
  - `previewText`
  - `generationProvider`
  - `generationMode` (`native_text`, `mcp_image_tool`, `mcp_video_tool`)
  - `confidenceScore`
  - `policyClassification`
  - `disclosureMode`
  - `createdAt`
- Validation rules:
  - Confidence score must be between `0.0` and `1.0`.
  - Sensitive classifications always require a linked review item before execution.
  - Image artifacts must record `mcp-server-ideogram` or `mcp-server-midjourney` when generated through MCP image tools.
  - Video artifacts must record `mcp-server-runway` or `mcp-server-luma` when generated through MCP video tools.

## VideoRenderJob

- Purpose: Tracks high-velocity operational metadata for video generation jobs and ties cost, provenance, and status to campaign governance.
- Key fields:
  - `id`
  - `taskId`
  - `contentArtifactId`
  - `chimeraAgentId`
  - `campaignId`
  - `renderTier` (`tier_1_living_portrait`, `tier_2_hero_video`)
  - `provider` (`mcp-server-runway`, `mcp-server-luma`)
  - `sourcePrompt`
  - `sourceImageAssetId` nullable
  - `status` (`queued`, `submitted`, `rendering`, `completed`, `failed`, `cancelled`)
  - `costAmount`
  - `costCurrency`
  - `requestedAt`
  - `completedAt` nullable
  - `assetUri`
  - `providerJobId`
  - `metadataJson`
- Relationships:
  - Belongs to one `Task`, one `ContentArtifact`, one `ChimeraAgent`, and one `Campaign`.
- Validation rules:
  - Stored in PostgreSQL to preserve transactional joins with budgets, audit events, and campaign state.
  - Tier 1 jobs require a `sourceImageAssetId`.
  - Tier 2 jobs require a non-empty `sourcePrompt`.
  - Status transitions must be append-audited for cost and failure analysis.

## ReviewItem

- Purpose: Queueable review work unit for human moderation.
- Key fields:
  - `id`
  - `tenantWorkspaceId`
  - `taskId`
  - `contentArtifactId` nullable
  - `transactionRequestId` nullable
  - `queueStatus` (`pending`, `claimed`, `approved`, `rejected`, `edited`, `expired`)
  - `reasonCodes`
  - `confidenceScore`
  - `policyClassification`
  - `createdAt`
  - `resolvedAt`
- Validation rules:
  - At least one of `contentArtifactId` or `transactionRequestId` must be present.
  - `policyClassification` marked sensitive cannot bypass `pending` or `claimed` states.

## ReviewDecision

- Purpose: Immutable record of a human review action.
- Key fields:
  - `id`
  - `reviewItemId`
  - `reviewedByUserId`
  - `decisionType` (`approve`, `reject`, `edit`)
  - `rationale`
  - `editSummary` nullable
  - `createdAt`
- Relationships:
  - Belongs to one `ReviewItem` and one `UserAccount`.
- Validation rules:
  - `rationale` is required for rejection and edit decisions.

## Wallet

- Purpose: Financial identity and policy boundary for an agent.
- Key fields:
  - `id`
  - `tenantWorkspaceId`
  - `chimeraAgentId`
  - `providerType`
  - `walletAddress`
  - `status` (`active`, `restricted`, `suspended`)
  - `availableBalance`
  - `dailySpendLimit`
  - `perTransactionLimit`
  - `updatedAt`
- Validation rules:
  - Wallet address must be unique within provider scope.
  - Restricted or suspended wallets cannot execute outbound transfers.

## TransactionRequest

- Purpose: Proposed inbound or outbound financial action under governance.
- Key fields:
  - `id`
  - `walletId`
  - `taskId` nullable
  - `direction` (`inbound`, `outbound`)
  - `amount`
  - `assetCode`
  - `counterparty`
  - `status` (`pending`, `approved`, `rejected`, `executed`, `failed`)
  - `policyFlags`
  - `createdAt`
  - `executedAt` nullable
- Validation rules:
  - Outbound requests require balance and limit checks before approval.
  - Suspicious or flagged requests require review regardless of amount.

## AuditEvent

- Purpose: Immutable log entry for privileged or sensitive actions.
- Key fields:
  - `id`
  - `tenantWorkspaceId`
  - `actorType` (`user`, `system`, `connector`)
  - `actorId`
  - `eventType`
  - `resourceType`
  - `resourceId`
  - `eventPayload`
  - `correlationId`
  - `createdAt`
- Validation rules:
  - Audit events are append-only.
  - Sensitive payload fields must be redacted before persistence.