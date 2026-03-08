# MCP Tooling Strategy

## Purpose

This workspace now includes a small MCP server set aimed at improving day-to-day development on Project Chimera without hardcoding secrets into the repository.

The selection follows two constraints:

- It should help with the actual monorepo workflow used here: Spring Boot backend, React/Vite frontend, Playwright-based UI testing, and Git-based review cycles.
- Shared workspace configuration should stay safe to commit. Anything that requires credentials should either use VS Code-hosted OAuth or remain optional.

## Why These Servers

### 1. Filesystem MCP

Configured server: `@modelcontextprotocol/server-filesystem`

Why it belongs here:

- Chimera is a multi-folder monorepo with backend, frontend, docs, ops, research, and specs.
- Safe file-scoped read and write tooling is the most generally useful MCP capability for day-to-day implementation work.
- The server is explicitly constrained to `${workspaceFolder}`, which keeps access aligned to the repository root rather than the whole machine.

Configuration choice:

- Uses `npx` because Node.js is already a project prerequisite for the frontend.
- Passes `${workspaceFolder}` as the allowed directory, matching the server's recommended VS Code setup.

### 2. Git MCP

Configured server: official `mcp-server-git`

Why it belongs here:

- Local Git status, diffs, logs, branch inspection, and staging operations are part of normal development and review work.
- This repo already expects iterative implementation and review against specs and task lists, so version-control-aware tooling is directly useful.

Configuration choice:

- Uses Docker instead of `uvx` so the workspace does not assume a Python toolchain beyond the application runtime.
- Binds the workspace into the container at `/workspace`, which is the official VS Code pattern for the server.
- This does require Docker Desktop to be installed and running.

Tradeoff:

- The official Git MCP server is a reference implementation, not a hardened enterprise control plane. It is suitable for trusted local development, not for untrusted repositories.

### 3. GitHub MCP

Configured server: hosted GitHub MCP endpoint at `https://api.githubcopilot.com/mcp/`

Why it belongs here:

- Chimera has a GitHub-oriented workflow already visible in the repository conventions and agent setup.
- Repository, issue, pull request, and Actions context are useful when development work crosses from local code into collaboration and CI.

Configuration choice:

- Uses the hosted remote GitHub MCP server rather than a local Docker container.
- Avoids committing a PAT prompt or token handling into workspace config.
- In current VS Code builds with remote MCP support, OAuth-backed access is the cleanest shared setup.

Tradeoff:

- If your VS Code version, Copilot plan, or org policy does not permit remote GitHub MCP access, disable this server or replace it with a PAT-backed local or remote config in user-level `mcp.json` instead of the shared workspace file.

### 4. Playwright MCP

Configured server: `@playwright/mcp@latest`

Why it belongs here:

- The frontend already includes Playwright tests and browser-based validation is part of the development surface.
- This is the one MCP addition that directly helps verify UI behavior in addition to editing code.

Configuration choice:

- Uses `npx` because Node.js is already part of the frontend toolchain.
- Runs with `--isolated` and `--headless` to avoid polluting a persistent browser profile and to keep local automation predictable.

Tradeoff:

- Playwright MCP is useful for interactive browser workflows, but it is not a security boundary.
- For heavy automated testing or higher-throughput agent workflows, CLI-driven Playwright commands may still be more efficient than MCP.

## Why Other Servers Were Not Added

### Filesystem and Git were prioritized over broader tool catalogs

These two cover the core local development loop: inspect files, make edits, inspect diffs, and understand branch state.

### Database MCP servers were intentionally left out

Chimera uses PostgreSQL, MongoDB, Redis, and Weaviate, but shared workspace configuration should not guess local connection strings, credentials, or developer-specific ports. Those are better configured per-user once a concrete local environment exists.

### Terminal or shell MCP servers were intentionally left out

They are powerful, but they materially widen the execution surface. For a committed workspace config, filesystem and Git give better value-to-risk for now.

### Memory and reasoning servers were intentionally left out

They can be useful, but they are not as directly tied to the repo's concrete development workflow as file, git, GitHub, and browser tooling.

## Workspace Configuration

The shared workspace MCP file is:

- `.vscode/mcp.json`

Configured server summary:

- `filesystem`: local filesystem access restricted to `${workspaceFolder}`
- `git`: local git operations through the official Dockerized Git MCP server
- `github`: hosted GitHub MCP server for repository and collaboration context
- `playwright`: isolated headless browser automation for frontend validation
- `tenxfeedbackanalytics`: pre-existing analytics/proxy server retained as-is

## Windows Prerequisites

For this workspace configuration to start cleanly on Windows:

- Node.js must be installed and available on `PATH` for `npx`
- Docker Desktop must be installed and running for the Git MCP server
- VS Code should be recent enough to support workspace MCP configuration and remote MCP servers
- GitHub MCP remote access may depend on your Copilot entitlement and organization policy

## Recommended Usage Pattern

Use the servers for the jobs they fit best:

- `filesystem` for repo-local file inspection and edits
- `git` for working tree status, diffs, commit history, and branch awareness
- `github` for issues, pull requests, Actions, and remote repository context
- `playwright` for browser validation of frontend behavior and end-to-end flows

Keep credentials and environment-specific integrations in user-level MCP configuration when they are not safe or portable to commit into the repository.