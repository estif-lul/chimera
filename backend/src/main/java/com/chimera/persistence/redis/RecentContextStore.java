package com.chimera.persistence.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

/**
 * Redis-backed store for agent recent-context retrieval.
 * Entries expire according to configured retention.
 */
@Component
public class RecentContextStore {

    private static final Logger log = LoggerFactory.getLogger(RecentContextStore.class);
    private static final String KEY_PREFIX = "agent:context:";

    private final RedisTemplate<String, Object> redisTemplate;

    public RecentContextStore(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Store a recent context entry with TTL-based expiration.
     */
    public void store(UUID agentId, String content, Duration ttl) {
        String key = KEY_PREFIX + agentId;
        redisTemplate.opsForList().rightPush(key, content);
        redisTemplate.expire(key, ttl);
        log.debug("Stored recent context for agent {}", agentId);
    }

    /**
     * Retrieve the most recent context entries for an agent.
     */
    public List<Object> retrieve(UUID agentId, int maxEntries) {
        String key = KEY_PREFIX + agentId;
        Long size = redisTemplate.opsForList().size(key);
        if (size == null || size == 0) {
            return List.of();
        }
        long start = Math.max(0, size - maxEntries);
        return redisTemplate.opsForList().range(key, start, -1);
    }
}
