# Implementation Plan: Project Chimera Autonomous Influencer Network

**Branch**: `[001-autonomous-influencer-network]` | **Date**: 2026-03-07 | **Spec**: [spec.md](C:/Users/est_lul/Documents/Projects/chimera/specs/001-autonomous-influencer-network/spec.md)
**Input**: Feature specification from `/specs/001-autonomous-influencer-network/spec.md`

## Summary

Build the first production-shaped slice of Project Chimera as a multi-tenant control plane for autonomous influencer agents. The implementation uses a Spring Boot 4.x + Java 25 Maven backend and a React JS + Vite frontend, with FastRender-inspired Planner, Worker, and Judge responsibilities expressed first as internal orchestration services. The v1 scope includes campaign planning and approval, human-in-the-loop review, persistent agent identity anchored by immutable `SOUL.md` definitions, controlled long-term learning written to Weaviate, governed wallet operations, MCP Resource-based perception, MCP Tool-orchestrated media generation, tiered video rendering, and the observability and audit controls required by the constitution.

## Technical Context

**Language/Version**: Java 25 for backend, TypeScript on React JS for frontend  
**Primary Dependencies**: Spring Boot 4.x, Spring Web, Spring Security, Spring Data JPA, Spring Validation, Springdoc OpenAPI, OAuth2 Client, PostgreSQL driver, Weaviate client, Redis client, MCP client SDKs, React, Vite, React Router, TanStack Query, React Hook Form, Zod, Vitest, React Testing Library  
**Storage**: PostgreSQL for transactional control-plane data and high-velocity video metadata, Weaviate for mutable long-term memory collections and semantic retrieval, Redis for short-lived context, queues, and coordination state  
**Testing**: JUnit 5, Spring Boot Test, MockMvc, Testcontainers, Vitest, React Testing Library, Playwright for critical UI flows  
**Target Platform**: Linux containers in a hybrid AWS/GCP Kubernetes environment with autoscaling worker workloads  
**Project Type**: Web application with backend API, frontend SPA, asynchronous orchestration services, and external connector integrations  
**Performance Goals**: Support at least 1,000 concurrent agents, keep non-HITL high-priority interaction decisions under 10 seconds, preserve responsive operator dashboards under burst activity  
**Constraints**: Contract-first APIs with OpenAPI, explicit DTOs only, tenant-scoped RBAC for local and OIDC auth, mandatory audit trail for privileged and sensitive actions, three or more social platform connectors in v1, MCP Resources as the only perception interface, MCP Tool orchestration for non-text media generation, autoscaling-friendly stateless application services  
**Scale/Scope**: Single monorepo containing backend, frontend, ops docs, and feature specs for a multi-tenant autonomous influencer platform with operator, reviewer, and technical admin roles

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### Pre-Research Gate Review

- Contract-first full-stack design: PASS. Plan centers on OpenAPI-first backend contracts, explicit DTOs, and React UI integration through generated or hand-maintained API client modules.
- Secure by default: PASS. Design includes tenant-scoped RBAC, local plus OIDC auth, server-authoritative authorization, externalized secrets, immutable `SOUL.md` persona rules, and immutable audit events for sensitive operations.
- Testable boundaries: PASS. Plan includes backend unit and integration tests, frontend behavior tests, and end-to-end checks for key operator and reviewer flows.
- Observable and supportable systems: PASS. Plan includes structured logs, health checks, metrics, correlation IDs, queue-depth visibility, failure surfacing, and traceable memory write-back and video-render telemetry.
- Simplicity, separation, and evolvability: PASS. Backend responsibilities remain layered across controller, service, domain, and persistence; frontend remains feature-organized with local state first and API access behind service modules.

### Post-Design Gate Review

- Contract-first full-stack design: PASS. Contracts are captured in `contracts/chimera-control-plane.openapi.yaml`, with connector boundary definitions documented separately.
- Secure by default: PASS. Data model includes tenant ownership, role enforcement, policy state, immutable persona definitions, audit records, and transaction controls; quickstart isolates secrets to environment variables.
- Testable boundaries: PASS. Quickstart and project structure define unit, integration, contract, and UI test entry points.
- Observable and supportable systems: PASS. Research and data model include telemetry, auditability, retry boundaries, MCP abstraction boundaries, and autoscaling-aware deployment assumptions.
- Simplicity, separation, and evolvability: PASS. Chosen structure preserves a single backend and frontend application with adapter boundaries for external platforms and future swarm decomposition.

## Project Structure

### Documentation (this feature)

```text
specs/001-autonomous-influencer-network/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   ├── chimera-control-plane.openapi.yaml
│   └── platform-connector-contract.md
└── tasks.md
```

### Source Code (repository root)

```text
backend/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/chimera/
│   │   │   ├── config/
│   │   │   ├── mcp/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── domain/
│   │   │   │   ├── model/
│   │   │   │   ├── repository/
│   │   │   │   └── policy/
│   │   │   └── persistence/
│   │   │       ├── postgres/
│   │   │       ├── redis/
│   │   │       ├── weaviate/
│   │   │       └── external/
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/migration/
│   └── test/
│       ├── java/com/chimera/
│       │   ├── contract/
│       │   ├── integration/
│       │   └── unit/
│       └── resources/
└── Dockerfile

frontend/
├── package.json
├── vite.config.ts
├── src/
│   ├── app/
│   ├── features/
│   │   ├── auth/
│   │   ├── campaigns/
│   │   ├── agents/
│   │   ├── review/
│   │   ├── wallets/
│   │   ├── signals/
│   │   └── audit/
│   ├── components/
│   ├── hooks/
│   ├── services/
│   ├── routes/
│   ├── styles/
│   └── types/
├── tests/
│   ├── components/
│   ├── features/
│   └── e2e/
└── Dockerfile

ops/
├── docker-compose.yml
├── k8s/
│   ├── backend/
│   ├── frontend/
│   ├── workers/
│   └── observability/
└── runbooks/

docs/
├── architecture/
├── api/
└── operations/
```

**Structure Decision**: Use a monorepo with one Spring Boot backend and one React frontend, plus supporting ops and docs directories. This satisfies the constitution by keeping the codebase simple while allowing clear boundaries between controller, service, domain, persistence, MCP adapters, frontend features, and external platform adapters.

## Design Updates

### Persona and Learning

- `SOUL.md` is the immutable persona source of truth loaded at agent instantiation time.
- Judge-approved high-engagement interactions enqueue a background summarization workflow that writes to Weaviate as mutable biography memory.
- Mutable memory retrieval augments prompts, but the immutable `SOUL.md` content remains the authoritative guardrail.

### Perception and Generation Boundaries

- All perception flows enter the system as MCP Resources, insulating the agent core from provider-specific APIs.
- The Agent Core generates text natively and orchestrates specialized MCP Tools for images and video.
- Media generation adapters must support `mcp-server-ideogram` and `mcp-server-midjourney` for images, plus `mcp-server-runway` and `mcp-server-luma` for video.

### Video Storage Strategy

- Store video job metadata in PostgreSQL rather than a document store because budget enforcement, audit joins, campaign linkage, and render-status transitions require transactional consistency and indexed relational queries.
- Store large generated media assets in object storage, referenced from PostgreSQL metadata rows.
- Use Weaviate only for semantic memory and biography retrieval, not for operational video job state.

### Video Rendering Policy

- Tier 1 uses static-image-plus-motion-brush image-to-video for routine daily content where cost efficiency is the primary objective.
- Tier 2 uses full text-to-video generation for hero content tied to major campaign milestones.
- Planner tier selection must consider task priority, available campaign budget, and any operator-imposed media policy overrides.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

No constitution violations require exceptions in this plan.
