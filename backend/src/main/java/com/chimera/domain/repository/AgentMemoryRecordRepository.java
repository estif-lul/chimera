package com.chimera.domain.repository;

import com.chimera.domain.model.agents.AgentMemoryRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for agent memory record metadata.
 */
public interface AgentMemoryRecordRepository extends JpaRepository<AgentMemoryRecord, UUID> {

    List<AgentMemoryRecord> findByChimeraAgentIdOrderByCreatedAtDesc(UUID agentId);

    List<AgentMemoryRecord> findByChimeraAgentIdAndMemoryType(UUID agentId, String memoryType);
}
