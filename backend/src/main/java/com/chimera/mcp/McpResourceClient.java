package com.chimera.mcp;

import java.util.Map;

/**
 * Client interface for consuming MCP Resources as the sole perception abstraction.
 * All agent perception flows must read external context through this contract.
 */
public interface McpResourceClient {

    /**
     * Fetch the latest state of an MCP Resource by type and URI.
     *
     * @param resourceType the MCP resource type identifier
     * @param resourceUri  the canonical resource URI
     * @return resource payload as a map of attributes
     */
    Map<String, Object> fetchResource(String resourceType, String resourceUri);

    /**
     * Subscribe to change notifications for an MCP Resource.
     *
     * @param resourceType the MCP resource type identifier
     * @param resourceUri  the canonical resource URI
     * @param callback     listener invoked when the resource changes
     */
    void subscribe(String resourceType, String resourceUri, McpResourceCallback callback);

    /**
     * Callback receiving MCP Resource change notifications.
     */
    @FunctionalInterface
    interface McpResourceCallback {
        void onResourceChanged(String resourceType, String resourceUri, Map<String, Object> payload);
    }
}
