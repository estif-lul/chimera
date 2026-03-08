# Chimera Skills Foundation

## Motivation

Project Chimera needs a stable way to describe reusable agent capabilities before implementation details are committed to code. A skill package defines a narrow, testable unit of capability with a clear input contract, output contract, operating constraints, and integration boundary.

This document establishes the initial documentation contract for skill packages so the future agent runtime can invoke skills consistently without coupling orchestration logic to provider-specific behavior.

## Design

### Skill Package Rules

- Skills live under `skills/` at the repository root.
- Each skill has its own directory named with the `skill_` prefix.
- Each skill directory must contain a `README.md` that defines purpose, trigger conditions, dependencies, input contract, output contract, failure modes, and implementation status.
- Skill contracts must preserve Chimera tenant scoping and audit correlation identifiers.
- Perception-oriented skills must consume upstream context through MCP Resources rather than direct provider-specific APIs.
- Media-generation skills must orchestrate image and video providers through MCP Tool boundaries rather than embedding provider-specific logic into the core agent runtime.

### Initial Critical Skills

The first documented skill packages are:

- `skill_ingest_platform_signals`: Normalize connector-originated social signals into MCP Resource-backed perception inputs for downstream relevance scoring.
- `skill_generate_media_artifact`: Produce image or video artifacts through MCP Tool orchestration while preserving render-tier, provenance, and cost metadata.

### Non-Goals

- This change does not implement runtime invocation logic.
- This change does not define a final machine-readable manifest format.
- This change does not replace connector or OpenAPI contracts already defined under `specs/001-autonomous-influencer-network/`.

## Acceptance Criteria

- A root `skills/` directory exists in the repository.
- A top-level skills catalog README explains naming and package conventions.
- At least two critical skill packages exist as directories with draft `README.md` files.
- Each drafted skill README defines explicit input and output contracts.
- The drafted contracts align with the current Chimera spec boundaries for MCP Resources, MCP Tools, tenant scoping, and auditability.
