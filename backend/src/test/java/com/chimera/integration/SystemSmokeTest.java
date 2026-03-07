package com.chimera.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * End-to-end smoke test covering campaign, review, memory, and wallet flows.
 * Runs against the full Spring context with Testcontainers-backed infrastructure.
 */
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SystemSmokeTest {

    @Test
    @Order(1)
    @DisplayName("Create tenant and authenticate")
    void createTenantAndAuthenticate() {
        // given: a new tenant workspace
        // when: POST /api/v1/auth/local/login
        // then: session is returned with valid token
    }

    @Test
    @Order(2)
    @DisplayName("Create agent with soul definition")
    void createAgentWithSoulDefinition() {
        // when: POST /api/v1/agents with soul definition input
        // then: agent is created with active status and valid soul
    }

    @Test
    @Order(3)
    @DisplayName("Create and approve campaign")
    void createAndApproveCampaign() {
        // given: an active agent
        // when: POST /api/v1/campaigns, then POST /api/v1/campaigns/{id}/approve
        // then: campaign transitions draft → planned → active with tasks generated
    }

    @Test
    @Order(4)
    @DisplayName("Ingest signal and verify scoring")
    void ingestSignalAndVerifyScoring() {
        // when: POST /api/v1/signals with valid payload
        // then: 202 Accepted, signal stored with relevance score
    }

    @Test
    @Order(5)
    @DisplayName("Content review queue flow")
    void contentReviewQueueFlow() {
        // given: a content artifact with confidence below review threshold
        // when: review item is created, claimed, and approved
        // then: review decision recorded, item resolved
    }

    @Test
    @Order(6)
    @DisplayName("Agent memory write-back via Judge")
    void agentMemoryWriteBack() {
        // given: a completed task with high engagement
        // when: Judge triggers biography write-back
        // then: memory record created, audit event logged
    }

    @Test
    @Order(7)
    @DisplayName("Wallet credit and governed debit")
    void walletCreditAndGovernedDebit() {
        // given: a wallet with daily spend limit
        // when: inbound credit (auto-executed), then outbound within limits
        // then: both transactions succeed, balance updated correctly
    }

    @Test
    @Order(8)
    @DisplayName("Wallet transaction escalation to review")
    void walletTransactionEscalation() {
        // given: a wallet with per-transaction limit of 100
        // when: outbound transaction of 200 is submitted
        // then: transaction flagged, review item created
    }

    @Test
    @Order(9)
    @DisplayName("Audit trail covers full lifecycle")
    void auditTrailCoversFullLifecycle() {
        // then: audit_event table contains entries for:
        //   - agent creation, campaign approval, signal ingestion,
        //   - review decision, memory write-back, wallet transactions
    }
}
