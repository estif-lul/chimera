package com.chimera.mcp;

import java.util.Map;

/**
 * Client interface for orchestrating MCP Tools (image and video generation).
 * Text generation remains native to the core LLM; media generation flows
 * through specialized MCP Tool servers.
 */
public interface McpToolClient {

    /**
     * Invoke an MCP Tool by name with the given parameters.
     *
     * @param toolName   e.g. "mcp-server-ideogram", "mcp-server-runway"
     * @param parameters tool-specific input parameters
     * @return tool execution result
     */
    McpToolResult invoke(String toolName, Map<String, Object> parameters);

    /**
     * Result returned by an MCP Tool invocation.
     */
    record McpToolResult(
            String status,
            String assetUri,
            String providerJobId,
            Map<String, Object> metadata
    ) {}
}
