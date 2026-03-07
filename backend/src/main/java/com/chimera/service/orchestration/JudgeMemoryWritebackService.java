package com.chimera.service.orchestration;

import com.chimera.domain.model.agents.AgentMemoryRecord;
import com.chimera.domain.repository.AgentMemoryRecordRepository;
import com.chimera.persistence.weaviate.AgentMemoryVectorStore;
import com.chimera.service.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Judge-triggered summarization workflow creating biography write-backs
 * from Judge-approved high-engagement interactions.
 */
@Service
public class JudgeMemoryWritebackService {

    private static final Logger log = LoggerFactory.getLogger(JudgeMemoryWritebackService.class);

    private final AgentMemoryRecordRepository memoryRepository;
    private final AgentMemoryVectorStore vectorStore;
    private final AuditService auditService;

    public JudgeMemoryWritebackService(AgentMemoryRecordRepository memoryRepository,
                                       AgentMemoryVectorStore vectorStore,
                                       AuditService auditService) {
        this.memoryRepository = memoryRepository;
        this.vectorStore = vectorStore;
        this.auditService = auditService;
    }

    /**
     * Create a biography write-back from a Judge-approved interaction.
     */
    @Transactional
    public AgentMemoryRecord writeBack(UUID agentId, UUID sourceTaskId,
                                       String content, BigDecimal engagementScore) {
        AgentMemoryRecord record = new AgentMemoryRecord(
                agentId, "biography_writeback", content, "weaviate", sourceTaskId);
        record.setEngagementScore(engagementScore);

        String embeddingRef = vectorStore.writeBack(agentId, content, "biography_writeback",
                Map.of("sourceTaskId", sourceTaskId.toString(), "engagementScore", engagementScore.toPlainString()));
        record.setEmbeddingReference(embeddingRef);

        record = memoryRepository.save(record);

        auditService.record(null, "system", "judge-writeback",
                "memory.biography_writeback", "agent_memory_record", record.getId().toString(),
                Map.of("agentId", agentId.toString(), "engagementScore", engagementScore.toPlainString()), null);

        log.info("Biography write-back created: agent={} record={}", agentId, record.getId());
        return record;
    }

    @Transactional(readOnly = true)
    public List<AgentMemoryRecord> getWritebackHistory(UUID agentId) {
        return memoryRepository.findByChimeraAgentIdAndMemoryType(agentId, "biography_writeback");
    }
}
