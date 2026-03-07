# Phase 0 Research: Project Chimera Autonomous Influencer Network

## Decision 1: Use a monorepo with one Spring Boot backend and one React Vite frontend

**Decision**: Implement the control plane as a single repository containing a Spring Boot 4.x Maven backend, a React JS + Vite frontend, and shared operational documentation.

**Rationale**: The repository currently contains only planning assets. A monorepo keeps contract-first API work, frontend feature development, and ops definitions aligned while the product is still in its first implementation phase. It also respects the constitution's simplicity rule by avoiding premature service sprawl.

**Alternatives considered**:
- Multiple backend services from day one: rejected because FastRender responsibilities can be modeled as internal orchestration services until scale pressure proves the need for runtime decomposition.
- Separate frontend and backend repositories: rejected because the product is contract-heavy and still evolving quickly.

## Decision 2: Express FastRender as orchestration modules before distributed swarm workers

**Decision**: Model Planner, Worker, and Judge as explicit backend services and task state machines inside the Spring Boot application for v1, with asynchronous boundaries backed by Redis and persistence version checks.

**Rationale**: The spec requires FastRender-style orchestration and stale-update protection, but the repo has no code yet. Starting with internal orchestration keeps implementation tractable while preserving the domain concepts needed to evolve toward autoscaled worker processes later.

**Alternatives considered**:
- Fully distributed worker fleet immediately: rejected because it adds infrastructure complexity before the core domain and review flows are proven.
- Single monolithic agent service without role boundaries: rejected because it obscures Planner, Worker, and Judge responsibilities and makes later swarm evolution harder.

## Decision 3: Use PostgreSQL, Weaviate, and Redis for the first production-shaped slice

**Decision**: Store transactional control-plane data and high-velocity video metadata in PostgreSQL, mutable long-term agent memory in Weaviate, and short-lived coordination state and queues in Redis.

**Rationale**: Campaigns, review records, wallets, audit trails, budget enforcement, and video render lifecycle metadata all need strong relational guarantees and efficient joins. Weaviate is a better fit for semantic long-term biography memory because the learning requirement explicitly calls for updating a mutable memory collection that supports retrieval by meaning rather than rigid schema. Redis remains well suited for ephemeral context, queue depth, and orchestration handoff.

**Alternatives considered**:
- PostgreSQL only: rejected because semantic retrieval and long-term biography search are materially better served by a vector-native memory store.
- PostgreSQL plus MongoDB: rejected because the updated requirements explicitly call for Weaviate-based mutable memories and the operational metadata still benefits more from relational consistency than a document store.
- Storing video job metadata in Weaviate or MongoDB: rejected because budget, campaign, audit, and state-transition queries require predictable transactional semantics and indexed relational access paths.

## Decision 4: Drive the backend contract with OpenAPI and explicit DTO boundaries

**Decision**: Define the human-facing control plane through OpenAPI and implement Spring Boot controllers using request and response DTOs only, never persistence entities.

**Rationale**: This is a direct constitution requirement and is necessary because the React UI must integrate against stable contracts rather than internal persistence details.

**Alternatives considered**:
- Code-first controllers with generated docs afterward: rejected because it weakens contract discipline and makes frontend integration less predictable.
- Exposing entities directly: rejected because it couples persistence to public API behavior.

## Decision 5: Support both local auth and OIDC from v1 with tenant-scoped RBAC

**Decision**: Implement local email/password authentication and OIDC SSO in the first release, enforcing tenant-scoped role-based access for operators, reviewers, and technical administrators.

**Rationale**: The clarified specification requires both models from day one. This supports enterprise tenants and self-managed customers without introducing separate access control models.

**Alternatives considered**:
- Local auth only: rejected because enterprise customers need SSO.
- OIDC only: rejected because smaller tenants may need built-in account management.

## Decision 6: Keep frontend state local-first and feature-oriented

**Decision**: Organize the React application by feature and rely on React primitives plus server-state tooling before introducing a global store.

**Rationale**: The constitution explicitly requires feature organization and local-first state decisions. The main views are naturally separated into auth, campaigns, agents, review, wallets, signals, and audit.

**Alternatives considered**:
- Global Redux-style store from the start: rejected because it adds ceremony before cross-feature coordination is proven.
- Page-oriented UI structure: rejected because it makes shared behavior across campaign, review, and audit flows harder to encapsulate.

## Decision 7: Define a standardized connector boundary and implement at least three live connectors in v1

**Decision**: Create a platform connector contract that abstracts publishing, replies, engagement ingestion, rate limits, and delivery outcomes, then implement at least three external platform adapters behind it.

**Rationale**: The spec explicitly requires three or more live platforms in the first release. A connector contract keeps the agent core platform-agnostic and localizes volatility from external APIs.

**Alternatives considered**:
- Direct platform calls from orchestration services: rejected because it violates the spec and weakens maintainability.
- Simulated connector in place of live integrations: rejected because the clarified specification requires live multi-platform support in v1.

## Decision 10: Make MCP Resources the sole perception abstraction

**Decision**: Require all agent perception flows to consume external context exclusively through MCP Resources.

**Rationale**: This keeps provider choice decoupled from agent logic and allows the system to swap upstream sources such as news, trend, or search providers without rewriting prompts, reasoning logic, or orchestration code.

**Alternatives considered**:
- Direct API integrations inside Planner or Worker logic: rejected because they couple cognition to vendors and violate the abstraction requirement.
- Mixed MCP and non-MCP ingestion: rejected because it creates inconsistent behavior and undermines the portability goal.

## Decision 11: Use MCP Tools for image and video generation while keeping text native to the core LLM

**Decision**: Keep caption, script, and reply text generation inside the Cognitive Core, but require image and video generation to flow through specialized MCP Tools.

**Rationale**: Text generation is already a native capability of the core models, while media generation requires provider-specific orchestration that should remain replaceable behind tool contracts. This aligns with the requirement to support `mcp-server-ideogram` or `mcp-server-midjourney` for images and `mcp-server-runway` or `mcp-server-luma` for video.

**Alternatives considered**:
- Single provider lock-in for all media: rejected because the requirements call for pluggable MCP tool choices.
- Generating media directly inside backend services without MCP tools: rejected because it breaks the abstraction boundary and makes provider swaps harder.

## Decision 12: Implement a two-tier video rendering strategy

**Decision**: Introduce two explicit rendering tiers, with Tier 1 using living-portrait image-to-video generation for daily content and Tier 2 using full text-to-video generation for hero content.

**Rationale**: Video costs vary dramatically by generation mode. A tiered policy allows the platform to preserve content quality for high-value moments while keeping routine production economically viable. Planner ownership of tier selection ensures the decision can incorporate priority and remaining budget before execution starts.

**Alternatives considered**:
- Text-to-video for all content: rejected because API cost would scale poorly for daily volume.
- Image-to-video only: rejected because flagship campaign moments require higher creative flexibility and quality.

## Decision 13: Treat `SOUL.md` as immutable persona DNA

**Decision**: Standardize persona instantiation on a `SOUL.md` file that is immutable at runtime and versioned only through explicit operator workflows.

**Rationale**: Separating immutable identity from mutable biography prevents learning loops from drifting the core persona while still allowing the system to adapt through controlled long-term memory updates.

**Alternatives considered**:
- Storing persona as freeform mutable database fields only: rejected because it weakens governance and traceability.
- Allowing automated systems to rewrite persona definitions directly: rejected because it creates brand-drift risk and undermines hard directives.

## Decision 8: Deploy as containerized workloads on Kubernetes in a hybrid AWS/GCP environment

**Decision**: Package the backend, frontend, and future worker workloads as containers deployed to Kubernetes, with autoscaling for burst workloads and queue-based activity spikes.

**Rationale**: The user explicitly requires a hybrid cloud environment using Kubernetes with autoscaling for viral or high-activity events. This matches the swarm evolution path and allows separate scaling of API and worker responsibilities.

**Alternatives considered**:
- Single VM deployment: rejected because it does not satisfy the autoscaling requirement.
- Serverless-only model: rejected because long-running orchestration and connector workloads need more predictable runtime control.

## Decision 9: Use OpenTelemetry-compatible observability with immutable audit events

**Decision**: Standardize on structured JSON logs, trace correlation IDs, metrics, health checks, and immutable audit events for privileged, review, and financial actions.

**Rationale**: The constitution requires observability and supportability, and the spec requires auditable sensitive operations. OpenTelemetry-compatible instrumentation keeps deployment-provider choice flexible across AWS and GCP.

**Alternatives considered**:
- Logs-only observability: rejected because queue depth, latency, and traceability are first-class product risks.
- Best-effort auditing: rejected because governance and finance flows depend on non-repudiable event trails.