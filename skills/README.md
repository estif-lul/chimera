# Chimera Skills Catalog

This directory holds capability packages for the Chimera Agent runtime. A skill is a narrowly scoped, reusable unit of work with a stable contract, such as signal ingestion, media generation, publishing, or wallet checks.

The current files are documentation-first stubs. They define the operating boundary and input/output contracts so implementation can be added later without re-deciding the package shape.

## Directory Layout

```text
skills/
  README.md
  skill_ingest_platform_signals/
    README.md
  skill_generate_media_artifact/
    README.md
```

## Naming Convention

- Prefix every package with `skill_`.
- Use a verb-object name that describes one concrete capability.
- Keep orchestration outside the skill. A skill should solve one job well and expose a predictable contract.

## Skill README Template

Each skill package README should include:

1. Purpose
2. Trigger Conditions
3. Dependencies and External Boundaries
4. Input Contract
5. Output Contract
6. Failure Modes
7. Observability and Audit Requirements
8. Implementation Status

## Current Critical Skills

### `skill_ingest_platform_signals`

- Purpose: convert platform events into MCP Resource-backed signals for planner and scoring flows.
- Spec alignment: FR-007, FR-007A, FR-008, FR-022, SC-009.

### `skill_generate_media_artifact`

- Purpose: generate image or video artifacts through MCP Tool providers while preserving provenance and cost metadata.
- Spec alignment: FR-009A, FR-010, FR-010A, FR-010B, FR-027, FR-028, SC-010.

## Contract Rules

- Every contract must carry `tenantWorkspaceId`, `chimeraAgentId`, and `correlationId` when the operation is agent-scoped.
- Skill outputs must be structured objects, not freeform text blobs.
- Provider-specific raw payloads can be retained in metadata, but the primary output must stay normalized.
- Skills that observe external systems must use MCP Resource semantics.
- Skills that create non-text media must use MCP Tool semantics.

## Next Implementation Step

When runtime work begins, each skill package can grow a manifest, executable module, test fixtures, and contract examples without changing the top-level catalog structure.
