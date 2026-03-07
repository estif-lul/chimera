package com.chimera.domain.model.shared;

/**
 * Marker interface for entities that use optimistic concurrency via a version field.
 * Implementations must increment the state version on every state mutation
 * to prevent stale writes.
 */
public interface VersionedAggregate {

    /**
     * Returns the current state version used for optimistic concurrency control.
     */
    int getStateVersion();
}
