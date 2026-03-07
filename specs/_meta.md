# Project Chimera Meta Spec

## Vision

Project Chimera is an autonomous influencer network that allows operators to manage a fleet of digital agents capable of trend discovery, content generation, audience engagement, human-reviewed publishing, and governed financial activity. The platform is intended to support both first-party influencer operations and multi-tenant platform customers.

## Product Goals

- Provide a control plane where operators can create campaigns, assign agents, review execution plans, and manage exceptions.
- Preserve long-lived digital personas with stable voice, memory continuity, and auditable operating boundaries.
- Route risky or low-confidence actions through human review without blocking safe autonomous flows.
- Support at least three live social platform connectors in the first release.
- Support governed wallet operations, including inbound payments, balance checks, and outbound transfers.

## Strategic Scope

- Multi-tenant SaaS control plane for operators, reviewers, and technical administrators.
- FastRender-inspired orchestration with Planner, Worker, and Judge responsibilities.
- Contract-first Spring Boot backend with explicit DTOs and OpenAPI.
- React JS + Vite frontend organized by feature.
- Hybrid cloud deployment on Kubernetes across AWS and GCP, with autoscaling for burst activity.

## Non-Negotiable Constraints

- Tenant isolation must be enforced across authentication, authorization, persistence, memory, review queues, and financial data.
- Sensitive-topic actions must always require human review regardless of confidence score.
- Public APIs must use explicit request and response DTOs and must not leak persistence entities.
- Audit trails are mandatory for privileged, financial, and review-driven actions.
- The first release must support both local login and OIDC SSO.

## Technical Standards

- Backend: Spring Boot 4.x on Java 25 using Maven.
- Backend architecture: layered modules for controller, service, domain, and persistence.
- Frontend: React JS with Vite.
- Frontend architecture: feature-organized UI, reusable shared components, dedicated API client modules, React primitives first for state.
- Storage: transactional data in PostgreSQL; flexible metadata and memory in MongoDB; short-lived coordination and queues in Redis.

## Scale and Performance Targets

- Support at least 1,000 concurrently active agents.
- Keep non-HITL high-priority interaction decisions under 10 seconds.
- Preserve operator visibility into fleet state, queue depth, and financial health during burst events.

## Canonical Detailed Specs

- Feature scope and acceptance criteria: `specs/001-autonomous-influencer-network/spec.md`
- Planning and design artifacts: `specs/001-autonomous-influencer-network/plan.md`
- Detailed data model: `specs/001-autonomous-influencer-network/data-model.md`
- Control-plane API contract: `specs/001-autonomous-influencer-network/contracts/chimera-control-plane.openapi.yaml`
