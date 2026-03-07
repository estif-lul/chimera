# Tasks: Project Chimera Autonomous Influencer Network

**Input**: Design documents from `/specs/001-autonomous-influencer-network/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/, quickstart.md

**Tests**: Include backend contract and integration coverage plus frontend behavior coverage because the feature spec defines mandatory user scenarios and the implementation plan calls for JUnit 5, Spring integration tests, Vitest, React Testing Library, and Playwright coverage.

**Organization**: Tasks are grouped by user story to keep each increment independently implementable and testable.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Scaffold the monorepo application surfaces and local developer entry points.

- [ ] T001 Scaffold the Spring Boot backend module in backend/pom.xml
- [ ] T002 Scaffold the React Vite frontend and test toolchain in frontend/package.json, frontend/vite.config.ts, and frontend/tsconfig.json
- [ ] T003 [P] Create local infrastructure and environment templates in ops/docker-compose.yml, backend/src/main/resources/application.yml, and frontend/.env.example
- [ ] T004 [P] Seed API and operations documentation stubs in docs/api/README.md and docs/operations/README.md

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Build the platform capabilities that every user story depends on.

**⚠️ CRITICAL**: No user story work should start before this phase is complete.

- [ ] T005 Establish the baseline relational schema for tenants, auth, agents, campaigns, review, memory, wallets, audit, and video jobs in backend/src/main/resources/db/migration/V1__baseline_control_plane.sql
- [ ] T006 [P] Implement tenant-scoped security, local login, and OIDC session configuration in backend/src/main/java/com/chimera/config/SecurityConfig.java and backend/src/main/java/com/chimera/controller/AuthController.java
- [ ] T007 [P] Create request and response DTOs aligned to specs/001-autonomous-influencer-network/contracts/chimera-control-plane.openapi.yaml in backend/src/main/java/com/chimera/controller/dto/
- [ ] T008 [P] Configure PostgreSQL, Redis, and Weaviate adapters in backend/src/main/java/com/chimera/persistence/postgres/, backend/src/main/java/com/chimera/persistence/redis/, and backend/src/main/java/com/chimera/persistence/weaviate/
- [ ] T009 [P] Implement immutable audit logging, correlation IDs, and global API exception handling in backend/src/main/java/com/chimera/service/AuditService.java, backend/src/main/java/com/chimera/config/CorrelationIdFilter.java, and backend/src/main/java/com/chimera/controller/ApiExceptionHandler.java
- [ ] T010 [P] Define MCP resource and platform connector interfaces in backend/src/main/java/com/chimera/mcp/McpResourceClient.java and backend/src/main/java/com/chimera/persistence/external/connectors/PlatformConnector.java
- [ ] T011 [P] Create the frontend app shell, auth bootstrap, and tenant-scoped route guards in frontend/src/app/App.tsx, frontend/src/routes/index.tsx, and frontend/src/features/auth/
- [ ] T012 Implement the shared frontend API client, query configuration, and session state hooks in frontend/src/services/api/client.ts, frontend/src/app/queryClient.ts, and frontend/src/features/auth/useSession.ts

**Checkpoint**: Foundation ready. User story phases can now proceed in priority order or in parallel where dependencies allow.

---

## Phase 3: User Story 1 - Launch and Govern Agent Campaigns (Priority: P1) 🎯 MVP

**Goal**: Let operators create tenant-scoped agents and campaigns, review an execution plan, activate work, and monitor exceptions and progress.

**Independent Test**: Create a campaign for one or more tenant-owned agents, review and approve the generated plan, ingest an MCP-backed signal, and confirm the system produces trackable tasks, content outputs, publishing activity, and surfaced exceptions without stale state overwrites.

### Tests for User Story 1

- [ ] T013 [P] [US1] Add contract tests for agent, campaign, execution-plan, and signal-ingestion endpoints in backend/src/test/java/com/chimera/contract/CampaignContractTest.java and backend/src/test/java/com/chimera/contract/SignalContractTest.java
- [ ] T014 [P] [US1] Add an integration test for campaign approval, task generation, and optimistic state updates in backend/src/test/java/com/chimera/integration/CampaignExecutionFlowTest.java
- [ ] T015 [P] [US1] Add a frontend launch-and-monitor workflow test in frontend/tests/features/campaigns/campaign-launch-and-monitor.spec.tsx

### Implementation for User Story 1

- [ ] T016 [P] [US1] Create agent, campaign, execution-plan, task, external-signal, content-artifact, and video-render-job models in backend/src/main/java/com/chimera/domain/model/agents/, backend/src/main/java/com/chimera/domain/model/campaigns/, and backend/src/main/java/com/chimera/domain/model/media/
- [ ] T017 [US1] Implement campaign planning and signal relevance services in backend/src/main/java/com/chimera/service/campaign/CampaignPlanningService.java and backend/src/main/java/com/chimera/service/signals/SignalScoringService.java
- [ ] T018 [US1] Implement Planner and Worker task lifecycle handling with stale-write protection in backend/src/main/java/com/chimera/service/orchestration/TaskLifecycleService.java and backend/src/main/java/com/chimera/domain/model/shared/VersionedAggregate.java
- [ ] T019 [US1] Implement content generation, disclosure labeling, video tier selection, and MCP media orchestration in backend/src/main/java/com/chimera/service/orchestration/ContentGenerationService.java and backend/src/main/java/com/chimera/service/media/VideoRenderService.java
- [ ] T020 [US1] Implement three live publishing and engagement adapters behind the shared contract in backend/src/main/java/com/chimera/persistence/external/connectors/InstagramConnector.java, backend/src/main/java/com/chimera/persistence/external/connectors/TikTokConnector.java, and backend/src/main/java/com/chimera/persistence/external/connectors/YouTubeConnector.java
- [ ] T021 [US1] Implement agent, campaign, plan-approval, and signal controllers in backend/src/main/java/com/chimera/controller/AgentController.java, backend/src/main/java/com/chimera/controller/CampaignController.java, and backend/src/main/java/com/chimera/controller/SignalController.java
- [ ] T022 [P] [US1] Build campaign creation, plan review, and monitoring screens in frontend/src/features/campaigns/CampaignCreatePage.tsx, frontend/src/features/campaigns/CampaignPlanPage.tsx, and frontend/src/features/campaigns/CampaignMonitorPage.tsx
- [ ] T023 [US1] Wire campaign and fleet APIs into TanStack Query hooks in frontend/src/services/api/campaigns.ts, frontend/src/services/api/agents.ts, and frontend/src/features/campaigns/hooks/useCampaigns.ts

**Checkpoint**: User Story 1 should now support the MVP campaign lifecycle independently.

---

## Phase 4: User Story 2 - Review Sensitive or Low-Confidence Actions (Priority: P2)

**Goal**: Route sensitive or low-confidence outputs into a human review queue with approve, reject, and edit outcomes preserved in audit history.

**Independent Test**: Generate content and transaction candidates across confidence and policy classes, verify that eligible outputs auto-complete, and confirm that review-required items enter the queue with auditable approve, reject, and edit outcomes that feed back into operator views.

### Tests for User Story 2

- [ ] T024 [P] [US2] Add contract tests for review-queue and review-decision endpoints in backend/src/test/java/com/chimera/contract/ReviewContractTest.java
- [ ] T025 [P] [US2] Add an integration test for low-confidence routing, sensitive-topic override, and decision replay in backend/src/test/java/com/chimera/integration/ReviewWorkflowIntegrationTest.java
- [ ] T026 [P] [US2] Add a frontend reviewer-queue behavior test in frontend/tests/features/review/reviewer-queue.spec.tsx

### Implementation for User Story 2

- [ ] T027 [P] [US2] Create confidence-policy, review-item, and review-decision models in backend/src/main/java/com/chimera/domain/model/policy/ConfidencePolicy.java and backend/src/main/java/com/chimera/domain/model/review/
- [ ] T028 [US2] Implement confidence scoring, policy evaluation, and mandatory human-review routing in backend/src/main/java/com/chimera/service/policy/ConfidencePolicyService.java and backend/src/main/java/com/chimera/service/review/ReviewRoutingService.java
- [ ] T029 [US2] Implement reviewer queue, approve or reject or edit handling, and immutable decision capture in backend/src/main/java/com/chimera/controller/ReviewController.java and backend/src/main/java/com/chimera/service/review/ReviewDecisionService.java
- [ ] T030 [P] [US2] Build the reviewer queue and decision workspace UI in frontend/src/features/review/ReviewQueuePage.tsx and frontend/src/features/review/ReviewDecisionPanel.tsx
- [ ] T031 [US2] Reflect review outcomes in operator monitoring and audit views in frontend/src/features/campaigns/components/CampaignReviewStatus.tsx and frontend/src/features/audit/AuditTimelinePage.tsx

**Checkpoint**: User Story 2 should now provide an independently testable review and governance loop.

---

## Phase 5: User Story 3 - Preserve Agent Identity and Memory (Priority: P3)

**Goal**: Keep each agent grounded in an immutable `SOUL.md` persona while enabling recent-context retrieval and controlled long-term memory write-backs.

**Independent Test**: Create an agent from a `SOUL.md` definition, run multiple interactions, and confirm that future outputs retain tone and constraints while Judge-approved high-engagement interactions create traceable Weaviate write-backs without mutating immutable persona fields.

### Tests for User Story 3

- [ ] T032 [P] [US3] Add contract tests for agent creation and memory-writeback history endpoints in backend/src/test/java/com/chimera/contract/AgentMemoryContractTest.java
- [ ] T033 [P] [US3] Add an integration test for `SOUL.md` immutability, recent-context retention, and Judge-approved Weaviate write-backs in backend/src/test/java/com/chimera/integration/AgentMemoryWritebackIntegrationTest.java
- [ ] T034 [P] [US3] Add a frontend agent persona and memory continuity test in frontend/tests/features/agents/agent-memory-continuity.spec.tsx

### Implementation for User Story 3

- [ ] T035 [P] [US3] Create soul-definition and agent-memory models in backend/src/main/java/com/chimera/domain/model/agents/SoulDefinition.java and backend/src/main/java/com/chimera/domain/model/agents/AgentMemoryRecord.java
- [ ] T036 [US3] Implement the `SOUL.md` loader, persona versioning, and immutable persona enforcement in backend/src/main/java/com/chimera/service/agents/SoulDefinitionService.java and backend/src/main/java/com/chimera/service/agents/AgentProfileService.java
- [ ] T037 [US3] Implement Redis recent-context retrieval and Weaviate biography write-back adapters in backend/src/main/java/com/chimera/persistence/redis/RecentContextStore.java and backend/src/main/java/com/chimera/persistence/weaviate/AgentMemoryVectorStore.java
- [ ] T038 [US3] Implement the Judge-triggered summarization workflow and memory-writeback endpoint in backend/src/main/java/com/chimera/service/orchestration/JudgeMemoryWritebackService.java and backend/src/main/java/com/chimera/controller/MemoryController.java
- [ ] T039 [P] [US3] Build agent profile, SOUL definition, and memory timeline screens in frontend/src/features/agents/AgentProfilePage.tsx, frontend/src/features/agents/components/SoulDefinitionCard.tsx, and frontend/src/features/agents/components/AgentMemoryTimeline.tsx

**Checkpoint**: User Story 3 should now deliver identity continuity and controlled learning independently.

---

## Phase 6: User Story 4 - Operate Agent-Owned Commerce Safely (Priority: P4)

**Goal**: Let agents receive funds and request governed outbound spending while enforcing budget, anomaly, and review controls.

**Independent Test**: Assign a wallet to an agent, process inbound funding, submit an allowed outbound request, and confirm that over-limit or suspicious transactions are blocked or escalated with current balances and policy state visible to operators.

### Tests for User Story 4

- [ ] T040 [P] [US4] Add contract tests for wallet lookup and transaction-request endpoints in backend/src/test/java/com/chimera/contract/WalletContractTest.java
- [ ] T041 [P] [US4] Add an integration test for inbound payments, governed outbound transfers, and suspicious transaction escalation in backend/src/test/java/com/chimera/integration/WalletGovernanceIntegrationTest.java
- [ ] T042 [P] [US4] Add a frontend wallet-governance flow test in frontend/tests/features/wallets/wallet-governance.spec.tsx

### Implementation for User Story 4

- [ ] T043 [P] [US4] Create wallet and transaction-request models in backend/src/main/java/com/chimera/domain/model/wallet/Wallet.java and backend/src/main/java/com/chimera/domain/model/wallet/TransactionRequest.java
- [ ] T044 [US4] Implement wallet balance, inbound payment, and outbound transfer services in backend/src/main/java/com/chimera/service/wallet/WalletService.java and backend/src/main/java/com/chimera/service/wallet/TransactionRequestService.java
- [ ] T045 [US4] Implement daily-limit, per-action, and anomaly-based policy checks with review escalation in backend/src/main/java/com/chimera/service/wallet/WalletPolicyService.java and backend/src/main/java/com/chimera/service/review/FinancialReviewEscalationService.java
- [ ] T046 [US4] Implement wallet controllers and campaign budget enforcement hooks in backend/src/main/java/com/chimera/controller/WalletController.java and backend/src/main/java/com/chimera/service/campaign/CampaignBudgetService.java
- [ ] T047 [P] [US4] Build wallet, balance, and transaction views in frontend/src/features/wallets/WalletPage.tsx, frontend/src/features/agents/components/WalletSummaryCard.tsx, and frontend/src/features/campaigns/components/CampaignFinancePanel.tsx

**Checkpoint**: User Story 4 should now provide independently testable governed wallet operations.

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Harden cross-story concerns, operations readiness, and end-to-end validation.

- [ ] T048 [P] Document API flows and connector usage in docs/api/chimera-control-plane.md and docs/architecture/platform-connectors.md
- [ ] T049 [P] Add observability and governance runbooks in ops/runbooks/campaign-operations.md and ops/runbooks/review-and-wallet-governance.md
- [ ] T050 Harden Kubernetes deployment manifests and autoscaling telemetry in ops/k8s/backend/, ops/k8s/frontend/, ops/k8s/workers/, and ops/k8s/observability/
- [ ] T051 Run end-to-end smoke coverage for campaign, review, memory, and wallet flows in frontend/tests/e2e/autonomous-influencer-network.spec.ts and backend/src/test/java/com/chimera/integration/SystemSmokeTest.java

---

## Dependencies & Execution Order

### Phase Dependencies

- Setup (Phase 1) has no dependencies and can start immediately.
- Foundational (Phase 2) depends on Setup and blocks all user stories.
- User stories depend on Foundational completion.
- Polish (Phase 7) depends on the user stories selected for the release.

### User Story Dependencies

- US1 is the MVP and should be completed first.
- US2 depends on the shared audit, DTO, and task foundations from Phase 2 and reuses US1 task and artifact flows.
- US3 depends on the shared auth, persistence, and orchestration foundations from Phase 2 and can proceed after US1 establishes agent lifecycle surfaces.
- US4 depends on the shared wallet, audit, and campaign-budget foundations from Phase 2 and benefits from US2 review routing for suspicious transaction escalation.

### Recommended Delivery Order

- Complete Phase 1 and Phase 2.
- Deliver US1 as the first demoable slice.
- Deliver US2 and US3 next, in parallel if capacity allows.
- Deliver US4 after US2 review escalation is available.
- Finish with Phase 7 hardening and smoke validation.

---

## Parallel Execution Examples

### User Story 1

```text
T013 Campaign and signal contract tests
T014 Campaign execution integration test
T015 Frontend campaign workflow test

T016 Domain models for campaign execution
T022 Campaign UI screens
```

### User Story 2

```text
T024 Review contract test
T025 Review workflow integration test
T026 Frontend reviewer queue test

T027 Review domain models
T030 Reviewer queue UI
```

### User Story 3

```text
T032 Agent memory contract test
T033 Agent memory integration test
T034 Frontend memory continuity test

T035 Soul definition and memory models
T039 Agent profile and memory UI
```

### User Story 4

```text
T040 Wallet contract test
T041 Wallet governance integration test
T042 Frontend wallet governance test

T043 Wallet domain models
T047 Wallet and finance UI
```

---

## Implementation Strategy

### MVP First

1. Complete Phase 1: Setup.
2. Complete Phase 2: Foundational.
3. Complete Phase 3: User Story 1.
4. Validate the independent test for US1 before moving on.

### Incremental Delivery

1. Ship US1 as the first end-to-end campaign slice.
2. Add US2 to introduce human governance without destabilizing campaign execution.
3. Add US3 to preserve identity and controlled learning.
4. Add US4 to enable governed commerce after review escalation is in place.
5. Finish with cross-cutting hardening, observability, and smoke coverage.

### Parallel Team Strategy

1. One engineer can finish backend foundations while another completes frontend shell tasks in Phase 2.
2. After US1 is in place, US2 and US3 can be split across separate engineers with limited overlap.
3. US4 can begin once the review escalation path from US2 is available.
