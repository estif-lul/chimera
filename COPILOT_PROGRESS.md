# Copilot Progress — Project Chimera

## Session: 2026-03-08 MCP Tooling Strategy

### Summary
- Added a shared workspace MCP configuration focused on local development workflows and documented the rationale in research.

### Changes
- **Workspace MCP config**: Updated `.vscode/mcp.json` to add `filesystem`, `git`, `github`, and `playwright` MCP servers while preserving the existing analytics server.
- **Tooling strategy doc**: Added `research/tooling_strategy.md` covering selection criteria, server-by-server rationale, Windows prerequisites, and why broader or credential-bound servers were intentionally excluded from shared config.

### Design Decisions
- Chose a small development-first MCP set instead of a large catalog to keep tool selection predictable and lower the trust surface.
- Used `npx` for Node-based servers already aligned with frontend prerequisites.
- Used Docker for the Git MCP server to avoid assuming `uvx` is installed on every developer machine.
- Kept GitHub MCP on the hosted remote endpoint so the shared workspace config does not commit PAT prompts or credentials.

### Lessons Learned / Follow-Up
- The official filesystem and git servers are reference implementations, so they are appropriate for trusted local development but should not be treated as hardened security boundaries.
- If the team wants database-aware MCP tooling later, add those servers in user-level configuration or via environment-specific overlays rather than committing connection details into the workspace.

## Session: 2026-03-08 Default Local Account Seed Data

### Summary
- Added configurable, idempotent seed data provisioning for the default tenant, admin user, confidence policy, and roles.

### Changes
- **`SeedProperties`** (`config/seed/SeedProperties.java`): Record-based `@ConfigurationProperties` bound to `chimera.seed.*` covering tenant slug/name, admin email/password/roles, and confidence policy thresholds.
- **`ApplicationDataSeeder`** (`config/seed/ApplicationDataSeeder.java`): `ApplicationRunner` that checks for existing data before inserting. Creates default tenant workspace, default confidence policy, and default admin user. Fully idempotent — safe on repeated restarts.
- **`UserAccount.createLocal()`**: Static factory method on the entity for constructing local-auth accounts with tenant, email, hashed password, and roles.
- **`ChimeraApplication`**: Added `@ConfigurationPropertiesScan` to enable record-based properties binding.
- **`application.yml`**: Added `chimera.seed.*` defaults — all values overridable via environment variables (`CHIMERA_SEED_ENABLED`, `CHIMERA_SEED_ADMIN_EMAIL`, `CHIMERA_SEED_ADMIN_PASSWORD`, etc.).

### Design Decisions
- Seed runs as an `ApplicationRunner` (not a Flyway migration) because this is application-level data that depends on BCrypt password encoding, not static SQL.
- Each seed entity checks existence first (`findBySlug`, `findByTenantWorkspaceIdAndEmail`, `findByTenantWorkspaceIdAndCampaignIdIsNull`) before inserting — idempotent across restarts.
- Default admin password is configurable and should be changed in production via `CHIMERA_SEED_ADMIN_PASSWORD`.
- Seed can be disabled entirely with `chimera.seed.enabled=false` or `CHIMERA_SEED_ENABLED=false`.

### Lessons Learned / Follow-Up
- The default admin password in `application.yml` is a development convenience — production deployments must override it via environment variable or secret.
- Consider adding a startup warning log when the default password has not been changed.

## Session: 2026-03-08 Frontend Complete Redesign

### Summary
- Complete frontend UI redesign: premium dark theme, Lucide-React icons, responsive layout with sidebar navigation.

### Changes
- **Dependency**: Added `lucide-react` icon library.
- **Design system**: Created `src/styles/global.css` — cohesive dark theme with indigo accent, CSS custom properties for spacing/colors/radii/typography, responsive breakpoints (768px, 480px).
- **Layout shell**: Created `src/app/Layout.tsx` — sidebar navigation with mobile hamburger menu, sticky topbar, page title derivation.
- **Entry point**: Updated `src/main.tsx` to import global styles; updated `index.html` with Inter font.
- **Routing**: Wrapped protected routes in Layout component at `src/routes/index.tsx`.
- **All page redesigns** (12 files):
  - Login: centered card with logo, form inputs, loading state.
  - Auth callback: centered spinner.
  - Campaign Monitor: stats row, table with status badges, empty state.
  - Campaign Create: card-based form with checkbox agents, back nav.
  - Campaign Plan: info cards, task table with priority/status badges.
  - Campaign Finance/Review Status: card placeholders with icons.
  - Agent Profile: identity/biography cards, wallet summary, memory timeline.
  - Agent Memory Timeline: timeline component with dot/line.
  - Soul Definition Card: info grid layout.
  - Wallet Summary Card: stat cards.
  - Wallet Page: stats row, transaction table with directional icons.
  - Review Queue: stats, table with type icons, status badges.
  - Review Decision: detail card, radio-pill decision selector, form.
  - Audit Timeline: table with actor type icons, event badges.
- **Icon replacements**: Removed all text arrows (`←`) and plus signs (`+`) with Lucide `ArrowLeft`, `Plus`, etc.

### Design Decisions
- **Palette**: Single dark theme — `#09090b` bg, `#111113` surface, `#6366f1` indigo accent, semantic colors for success/error/warning.
- **Typography**: Inter font, 14px base, tight heading line-height, uppercase label convention.
- **Spacing**: 4px base unit scale, consistent padding via CSS vars.
- **Responsiveness**: 3 breakpoints (1024/768/480), sidebar collapses to overlay on mobile, grid layouts adapt.

### Lessons Learned / Follow-Up
- Pre-existing `apiClient` type errors in service files (`agents.ts`, `campaigns.ts`, `wallets.ts`, `agentProfile.ts`) treat the object as callable with generics — these should be fixed to use `.get()`/`.post()` methods consistently.
- Consider adding a CSS reset or Tailwind as complexity grows.

## Session: 2026-03-08 Frontend Import Resolution Fix

### Summary
- Resolved Vite pre-transform import resolution failure in campaigns hooks.

### Changes
- Corrected relative re-export path in `frontend/src/features/campaigns/hooks/useCampaigns.ts` from `../../services/api/campaigns` to `../../../services/api/campaigns`.

### Lessons Learned / Follow-Up
- For deep feature folders, prefer path aliases or verify relative traversal depth (`hooks -> campaigns -> features -> src`) when adding barrel re-exports.

## Session: 2026-03-08 Schema Validation Fixes

### Summary
- Resolved startup-time Hibernate schema validation failures caused by PostgreSQL `TEXT[]` columns being mapped to scalar `String` fields.

### Changes
- Updated connector implementations (`InstagramConnector`, `TikTokConnector`, `YouTubeConnector`) to align with the current `PlatformConnector` interface signatures.
- Updated array-backed JPA fields to explicit SQL array mappings using `@JdbcTypeCode(SqlTypes.ARRAY)`:
	- `Campaign.brandConstraints`
	- `ConfidencePolicy.sensitiveTopics`
	- `ExecutionPlan.acceptanceCriteria`
	- `ReviewItem.reasonCodes`
	- `TransactionRequest.policyFlags`
- Adjusted `ReviewDecisionService` reason-code projection to work with `String[]` model values.

### Lessons Learned / Follow-Up
- For PostgreSQL array columns, avoid scalar `String` model fields: use array/list types with explicit Hibernate array JDBC typing to keep schema validation stable.

## Session: Phase 5–7 Implementation (All 51 Tasks Complete)

### Summary
Completed all remaining tasks T032–T051 across Phases 5, 6, and 7. The full task list (T001–T051) is now checked off in `specs/001-autonomous-influencer-network/tasks.md`.

### Phase 5 — US3 Agent Identity & Memory (T032–T039)
- **T032–T034**: Contract test, integration test, frontend spec for agent memory
- **T035**: `AgentMemoryRecord` entity + repository (SoulDefinition already existed from T016)
- **T036**: `SoulDefinitionService` (persona versioning, immutability), `AgentProfileService` (biography updates only)
- **T037**: `RecentContextStore` (Redis TTL-based), `AgentMemoryVectorStore` (Weaviate placeholder)
- **T038**: `JudgeMemoryWritebackService` (creates write-back records + audit), `MemoryController`
- **T039**: `AgentProfilePage`, `SoulDefinitionCard`, `AgentMemoryTimeline`, routes updated

### Phase 6 — US4 Wallet & Spend Controls (T040–T047)
- **T040–T042**: Wallet contract test, governance integration test, frontend wallet spec
- **T043**: `Wallet` entity (credit/debit/restrict/suspend), `TransactionRequest` entity, repositories
- **T044**: `WalletService`, `TransactionRequestService` (inbound auto-execute, outbound policy-gated)
- **T045**: `WalletPolicyService` (daily/per-transaction/balance checks), `FinancialReviewEscalationService`
- **T046**: `WalletController` (CRUD + transaction submit), `CampaignBudgetService`
- **T047**: `WalletPage`, `WalletSummaryCard`, `CampaignFinancePanel`, wallet API hooks, routes

### Phase 7 — Polish & Cross-Cutting (T048–T051)
- **T048**: API flow docs (`docs/api/chimera-control-plane.md`), connector architecture (`docs/architecture/platform-connectors.md`)
- **T049**: Campaign operations runbook, review & wallet governance runbook
- **T050**: K8s manifests for backend, frontend, workers (with HPA), observability (Prometheus + scrape config)
- **T051**: `SystemSmokeTest.java` (9 ordered tests), Playwright E2E spec (5 flows)

### Challenges
- MemoryWritebackView DTO had a different field order than the controller's `toView()` mapping — caught and fixed immediately
- Wallet `policyFlags` uses PostgreSQL `TEXT[]` array type — mapped as `List<String>` in JPA entity
- Frontend component paths in tasks.md used `components/` subdirectory; placed files directly under feature folders for consistency with earlier phases

### Follow-Up Items
- All integration/smoke tests have skeleton assertions — fill in with real test logic once Testcontainers and test data seeds are wired
- Platform connectors (`Instagram`, `TikTok`, `YouTube`) are placeholder stubs — implement actual API clients
- Weaviate vector store operations are placeholder — implement with WeaviateClient when schema is configured
- Auth TODO comments (`// TODO: resolve from authenticated principal`) remain in controllers — resolve when SecurityContext integration is complete
- Redis `RecentContextStore` uses generic `RedisTemplate<String, Object>` — consider typed serialization
