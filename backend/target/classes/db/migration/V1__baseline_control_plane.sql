-- V1 Baseline Control Plane Schema
-- Covers: tenants, auth, agents, campaigns, review, memory, wallets, audit, video jobs

-- ============================================================
-- Tenant Workspace
-- ============================================================
CREATE TABLE tenant_workspace (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    slug            VARCHAR(128) NOT NULL UNIQUE,
    display_name    VARCHAR(255) NOT NULL,
    status          VARCHAR(32)  NOT NULL DEFAULT 'active'
                        CHECK (status IN ('active', 'suspended', 'archived')),
    default_confidence_policy_id UUID,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- ============================================================
-- User Account
-- ============================================================
CREATE TABLE user_account (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_workspace_id UUID         NOT NULL REFERENCES tenant_workspace(id),
    email               VARCHAR(320) NOT NULL,
    password_hash       VARCHAR(256),
    auth_provider_type  VARCHAR(16)  NOT NULL DEFAULT 'local'
                            CHECK (auth_provider_type IN ('local', 'oidc')),
    provider_subject    VARCHAR(512),
    role_set            VARCHAR(128) NOT NULL DEFAULT 'operator',
    status              VARCHAR(16)  NOT NULL DEFAULT 'invited'
                            CHECK (status IN ('invited', 'active', 'disabled')),
    last_login_at       TIMESTAMPTZ,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (tenant_workspace_id, email)
);

-- ============================================================
-- Confidence Policy
-- ============================================================
CREATE TABLE confidence_policy (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_workspace_id     UUID        NOT NULL REFERENCES tenant_workspace(id),
    campaign_id             UUID,
    auto_approve_threshold  NUMERIC(4,3) NOT NULL DEFAULT 0.900
                                CHECK (auto_approve_threshold >= 0 AND auto_approve_threshold <= 1),
    review_threshold        NUMERIC(4,3) NOT NULL DEFAULT 0.600
                                CHECK (review_threshold >= 0 AND review_threshold <= 1),
    sensitive_topics        TEXT[]       NOT NULL DEFAULT '{}',
    created_at              TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CHECK (review_threshold <= auto_approve_threshold)
);

ALTER TABLE tenant_workspace
    ADD CONSTRAINT fk_default_confidence_policy
    FOREIGN KEY (default_confidence_policy_id)
    REFERENCES confidence_policy(id);

-- ============================================================
-- Soul Definition
-- ============================================================
CREATE TABLE soul_definition (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_workspace_id UUID         NOT NULL REFERENCES tenant_workspace(id),
    persona_slug        VARCHAR(128) NOT NULL,
    version             INT          NOT NULL DEFAULT 1,
    backstory           TEXT         NOT NULL,
    voice_tone          TEXT         NOT NULL,
    core_beliefs_and_values TEXT     NOT NULL,
    directives          TEXT         NOT NULL,
    source_path         VARCHAR(512),
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (tenant_workspace_id, persona_slug, version)
);

-- ============================================================
-- Chimera Agent
-- ============================================================
CREATE TABLE chimera_agent (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_workspace_id     UUID         NOT NULL REFERENCES tenant_workspace(id),
    display_name            VARCHAR(255) NOT NULL,
    persona_slug            VARCHAR(128) NOT NULL,
    soul_definition_id      UUID         NOT NULL REFERENCES soul_definition(id),
    soul_document_version   INT          NOT NULL DEFAULT 1,
    status                  VARCHAR(32)  NOT NULL DEFAULT 'draft'
                                CHECK (status IN ('draft', 'active', 'paused', 'retired')),
    mutable_biography_summary TEXT,
    visual_reference_id     VARCHAR(512),
    current_state           JSONB,
    created_at              TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- ============================================================
-- Wallet
-- ============================================================
CREATE TABLE wallet (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_workspace_id     UUID         NOT NULL REFERENCES tenant_workspace(id),
    chimera_agent_id        UUID         NOT NULL REFERENCES chimera_agent(id),
    provider_type           VARCHAR(64)  NOT NULL,
    wallet_address          VARCHAR(256) NOT NULL,
    status                  VARCHAR(32)  NOT NULL DEFAULT 'active'
                                CHECK (status IN ('active', 'restricted', 'suspended')),
    available_balance       NUMERIC(20,8) NOT NULL DEFAULT 0,
    daily_spend_limit       NUMERIC(20,8),
    per_transaction_limit   NUMERIC(20,8),
    updated_at              TIMESTAMPTZ  NOT NULL DEFAULT now(),
    UNIQUE (provider_type, wallet_address)
);

-- ============================================================
-- Campaign
-- ============================================================
CREATE TABLE campaign (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_workspace_id     UUID         NOT NULL REFERENCES tenant_workspace(id),
    name                    VARCHAR(255) NOT NULL,
    goal_description        TEXT         NOT NULL,
    target_audience         TEXT,
    brand_constraints       TEXT[],
    risk_profile            VARCHAR(64),
    budget_policy_id        UUID,
    confidence_policy_id    UUID         REFERENCES confidence_policy(id),
    status                  VARCHAR(32)  NOT NULL DEFAULT 'draft'
                                CHECK (status IN ('draft', 'planned', 'approved', 'active',
                                                  'paused', 'completed', 'cancelled')),
    created_by_user_id      UUID         REFERENCES user_account(id),
    created_at              TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ  NOT NULL DEFAULT now()
);

ALTER TABLE confidence_policy
    ADD CONSTRAINT fk_confidence_policy_campaign
    FOREIGN KEY (campaign_id)
    REFERENCES campaign(id);

-- ============================================================
-- Campaign Agent Assignment
-- ============================================================
CREATE TABLE campaign_agent_assignment (
    campaign_id      UUID NOT NULL REFERENCES campaign(id),
    chimera_agent_id UUID NOT NULL REFERENCES chimera_agent(id),
    assignment_role  VARCHAR(64) NOT NULL DEFAULT 'member',
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (campaign_id, chimera_agent_id)
);

-- ============================================================
-- Execution Plan
-- ============================================================
CREATE TABLE execution_plan (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    campaign_id         UUID        NOT NULL REFERENCES campaign(id),
    plan_version        INT         NOT NULL DEFAULT 1,
    summary             TEXT,
    acceptance_criteria TEXT[],
    generated_by        VARCHAR(128),
    status              VARCHAR(32) NOT NULL DEFAULT 'draft'
                            CHECK (status IN ('draft', 'under_review', 'approved', 'superseded')),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ============================================================
-- Task
-- ============================================================
CREATE TABLE task (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_workspace_id UUID        NOT NULL REFERENCES tenant_workspace(id),
    campaign_id         UUID        NOT NULL REFERENCES campaign(id),
    chimera_agent_id    UUID        NOT NULL REFERENCES chimera_agent(id),
    execution_plan_id   UUID        NOT NULL REFERENCES execution_plan(id),
    task_type           VARCHAR(64) NOT NULL,
    priority            VARCHAR(16) NOT NULL DEFAULT 'normal',
    video_render_tier   VARCHAR(32)
                            CHECK (video_render_tier IS NULL
                                   OR video_render_tier IN ('tier_1_living_portrait', 'tier_2_hero_video')),
    status              VARCHAR(32) NOT NULL DEFAULT 'pending'
                            CHECK (status IN ('pending', 'in_progress', 'awaiting_review',
                                              'approved', 'rejected', 'executed', 'failed', 'cancelled')),
    planner_context     JSONB,
    worker_output       JSONB,
    judge_decision_summary TEXT,
    state_version       INT         NOT NULL DEFAULT 1,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ============================================================
-- External Signal
-- ============================================================
CREATE TABLE external_signal (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_workspace_id UUID        NOT NULL REFERENCES tenant_workspace(id),
    campaign_id         UUID        REFERENCES campaign(id),
    chimera_agent_id    UUID        REFERENCES chimera_agent(id),
    mcp_resource_type   VARCHAR(128) NOT NULL,
    mcp_resource_uri    VARCHAR(1024) NOT NULL,
    source_platform     VARCHAR(64) NOT NULL,
    signal_type         VARCHAR(64) NOT NULL,
    payload_summary     JSONB,
    relevance_score     NUMERIC(4,3)
                            CHECK (relevance_score IS NULL
                                   OR (relevance_score >= 0 AND relevance_score <= 1)),
    received_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    processed_at        TIMESTAMPTZ
);

-- ============================================================
-- Content Artifact
-- ============================================================
CREATE TABLE content_artifact (
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    task_id               UUID         NOT NULL REFERENCES task(id),
    chimera_agent_id      UUID         NOT NULL REFERENCES chimera_agent(id),
    campaign_id           UUID         NOT NULL REFERENCES campaign(id),
    artifact_type         VARCHAR(16)  NOT NULL
                              CHECK (artifact_type IN ('text', 'image', 'video', 'mixed')),
    content_location      VARCHAR(1024),
    preview_text          TEXT,
    generation_provider   VARCHAR(128),
    generation_mode       VARCHAR(32)  NOT NULL
                              CHECK (generation_mode IN ('native_text', 'mcp_image_tool', 'mcp_video_tool')),
    confidence_score      NUMERIC(4,3)
                              CHECK (confidence_score >= 0 AND confidence_score <= 1),
    policy_classification VARCHAR(64),
    disclosure_mode       VARCHAR(32),
    created_at            TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- ============================================================
-- Video Render Job
-- ============================================================
CREATE TABLE video_render_job (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    task_id                 UUID         NOT NULL REFERENCES task(id),
    content_artifact_id     UUID         REFERENCES content_artifact(id),
    chimera_agent_id        UUID         NOT NULL REFERENCES chimera_agent(id),
    campaign_id             UUID         NOT NULL REFERENCES campaign(id),
    render_tier             VARCHAR(32)  NOT NULL
                                CHECK (render_tier IN ('tier_1_living_portrait', 'tier_2_hero_video')),
    provider                VARCHAR(64)  NOT NULL
                                CHECK (provider IN ('mcp-server-runway', 'mcp-server-luma')),
    source_prompt           TEXT,
    source_image_asset_id   VARCHAR(512),
    status                  VARCHAR(32)  NOT NULL DEFAULT 'queued'
                                CHECK (status IN ('queued', 'submitted', 'rendering',
                                                  'completed', 'failed', 'cancelled')),
    cost_amount             NUMERIC(20,8),
    cost_currency           VARCHAR(8),
    requested_at            TIMESTAMPTZ  NOT NULL DEFAULT now(),
    completed_at            TIMESTAMPTZ,
    asset_uri               VARCHAR(1024),
    provider_job_id         VARCHAR(256),
    metadata_json           JSONB
);

-- ============================================================
-- Agent Memory Record (metadata row; content in Redis/Weaviate)
-- ============================================================
CREATE TABLE agent_memory_record (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    chimera_agent_id        UUID        NOT NULL REFERENCES chimera_agent(id),
    memory_type             VARCHAR(32) NOT NULL
                                CHECK (memory_type IN ('recent_context', 'long_term_summary',
                                                       'policy_note', 'biography_writeback')),
    content                 TEXT,
    embedding_reference     VARCHAR(512),
    storage_backend         VARCHAR(16) NOT NULL
                                CHECK (storage_backend IN ('redis', 'weaviate')),
    source_task_id          UUID        REFERENCES task(id),
    source_interaction_id   UUID,
    engagement_score        NUMERIC(6,4),
    retention_until         TIMESTAMPTZ,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ============================================================
-- Review Item
-- ============================================================
CREATE TABLE review_item (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_workspace_id     UUID        NOT NULL REFERENCES tenant_workspace(id),
    task_id                 UUID        NOT NULL REFERENCES task(id),
    content_artifact_id     UUID        REFERENCES content_artifact(id),
    transaction_request_id  UUID,
    queue_status            VARCHAR(16) NOT NULL DEFAULT 'pending'
                                CHECK (queue_status IN ('pending', 'claimed', 'approved',
                                                        'rejected', 'edited', 'expired')),
    reason_codes            TEXT[],
    confidence_score        NUMERIC(4,3)
                                CHECK (confidence_score >= 0 AND confidence_score <= 1),
    policy_classification   VARCHAR(64),
    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    resolved_at             TIMESTAMPTZ,
    CHECK (content_artifact_id IS NOT NULL OR transaction_request_id IS NOT NULL)
);

-- ============================================================
-- Review Decision
-- ============================================================
CREATE TABLE review_decision (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    review_item_id      UUID        NOT NULL REFERENCES review_item(id),
    reviewed_by_user_id UUID        NOT NULL REFERENCES user_account(id),
    decision_type       VARCHAR(16) NOT NULL
                            CHECK (decision_type IN ('approve', 'reject', 'edit')),
    rationale           TEXT,
    edit_summary        TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ============================================================
-- Transaction Request
-- ============================================================
CREATE TABLE transaction_request (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    wallet_id       UUID        NOT NULL REFERENCES wallet(id),
    task_id         UUID        REFERENCES task(id),
    direction       VARCHAR(16) NOT NULL CHECK (direction IN ('inbound', 'outbound')),
    amount          NUMERIC(20,8) NOT NULL,
    asset_code      VARCHAR(16) NOT NULL,
    counterparty    VARCHAR(256),
    status          VARCHAR(16) NOT NULL DEFAULT 'pending'
                        CHECK (status IN ('pending', 'approved', 'rejected', 'executed', 'failed')),
    policy_flags    TEXT[],
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    executed_at     TIMESTAMPTZ
);

-- back-reference from review_item to transaction_request
ALTER TABLE review_item
    ADD CONSTRAINT fk_review_item_transaction
    FOREIGN KEY (transaction_request_id)
    REFERENCES transaction_request(id);

-- ============================================================
-- Audit Event
-- ============================================================
CREATE TABLE audit_event (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_workspace_id UUID        NOT NULL REFERENCES tenant_workspace(id),
    actor_type          VARCHAR(16) NOT NULL
                            CHECK (actor_type IN ('user', 'system', 'connector')),
    actor_id            VARCHAR(256) NOT NULL,
    event_type          VARCHAR(128) NOT NULL,
    resource_type       VARCHAR(128) NOT NULL,
    resource_id         VARCHAR(256) NOT NULL,
    event_payload       JSONB,
    correlation_id      UUID,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Indexes for common query patterns
CREATE INDEX idx_user_account_tenant ON user_account(tenant_workspace_id);
CREATE INDEX idx_chimera_agent_tenant ON chimera_agent(tenant_workspace_id);
CREATE INDEX idx_campaign_tenant ON campaign(tenant_workspace_id);
CREATE INDEX idx_task_campaign ON task(campaign_id);
CREATE INDEX idx_task_agent ON task(chimera_agent_id);
CREATE INDEX idx_task_status ON task(status);
CREATE INDEX idx_external_signal_tenant ON external_signal(tenant_workspace_id);
CREATE INDEX idx_content_artifact_task ON content_artifact(task_id);
CREATE INDEX idx_video_render_job_task ON video_render_job(task_id);
CREATE INDEX idx_review_item_tenant ON review_item(tenant_workspace_id);
CREATE INDEX idx_review_item_status ON review_item(queue_status);
CREATE INDEX idx_wallet_agent ON wallet(chimera_agent_id);
CREATE INDEX idx_transaction_request_wallet ON transaction_request(wallet_id);
CREATE INDEX idx_audit_event_tenant ON audit_event(tenant_workspace_id);
CREATE INDEX idx_audit_event_correlation ON audit_event(correlation_id);
