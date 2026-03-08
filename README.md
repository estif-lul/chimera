# Project Chimera

🚀 Project Chimera is a spec-driven autonomous influencer platform for running AI agent campaigns with human governance, external platform integrations, persistent agent memory, and controlled agent-owned commerce.

The repository is organized as a full-stack monorepo with a Spring Boot backend, a React + Vite frontend, local development infrastructure, operational manifests, and detailed product and architecture specifications. The specs under `specs/` are the source of truth for platform behavior and contracts.

## What Chimera Does

Project Chimera is designed around four core product loops:

- Campaign orchestration: create campaigns, generate execution plans, approve plans, and monitor active work.
- Agent identity and memory: define stable personas, preserve continuity, and support controlled long-term learning.
- Review and governance: route sensitive or low-confidence actions into human review with immutable audit trails.
- Agentic commerce: let agents check balances, receive funds, and request governed outbound transactions.

## Architecture At A Glance

🧱 Current repository layout follows a contract-first control-plane architecture:

- `backend/`: Spring Boot API, domain services, persistence, security, migrations, and tests.
- `frontend/`: React 19 + Vite control-plane UI for campaigns, agents, reviews, and wallets.
- `specs/`: feature specs, technical direction, data model, OpenAPI contracts, and implementation plans.
- `docs/`: API and operational documentation.
- `ops/`: Docker Compose and Kubernetes manifests for local operations and deployment scaffolding.
- `skills/`: documentation for Chimera skill contracts.

Primary local dependencies:

- PostgreSQL for transactional control-plane data
- Redis for short-lived coordination and recent context
- Weaviate for vector-backed memory workflows

## Tech Stack

- Backend: Java 25, Maven, Spring Boot 3.4, Spring Security, Spring Data JPA, Flyway, Redis, Weaviate client
- Frontend: React 19, TypeScript, Vite, React Router, React Query, Zustand, Vitest, Playwright
- Local infrastructure: Docker Compose
- Contracts and documentation: OpenAPI, Markdown specs, operational docs

## Repository Map

```text
chimera/
├─ backend/        # Spring Boot control plane
├─ frontend/       # React control plane UI
├─ docs/           # API and operations docs
├─ ops/            # Compose and Kubernetes manifests
├─ research/       # architecture and tooling research
├─ scripts/        # repo utilities such as spec validation
├─ skills/         # skill package documentation
├─ specs/          # product, technical, and feature specifications
├─ test/           # top-level holding area for contract tests not in active Maven discovery
└─ README.md
```

## Quick Start

### Option 1: Full Stack With Docker Compose

This is the fastest way to bring up the local stack.

```powershell
docker compose up --build
```

Services exposed locally:

- Frontend: `http://localhost:5173`
- Backend API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- PostgreSQL: `localhost:5432`
- Redis: `localhost:6379`
- Weaviate: `http://localhost:8081`

The root compose file mounts the backend and frontend source trees directly for development-oriented workflows.

### Option 2: Run Services Manually

Start infrastructure first:

```powershell
docker compose up -d postgres redis weaviate
```

Run the backend:

```powershell
cd backend
mvn spring-boot:run
```

Run the frontend in a second terminal:

```powershell
cd frontend
npm install
npm run dev
```

## Local Configuration

The backend reads configuration from `backend/src/main/resources/application.yml` with environment-variable overrides.

Common backend variables:

- `CHIMERA_DB_URL`
- `CHIMERA_DB_USERNAME`
- `CHIMERA_DB_PASSWORD`
- `CHIMERA_REDIS_URL`
- `CHIMERA_WEAVIATE_URL`
- `CHIMERA_ALLOWED_ORIGINS`
- `CHIMERA_JWT_SIGNING_KEY`
- `CHIMERA_MCP_RESOURCE_GATEWAY_URL`

Common frontend variable:

- `VITE_API_BASE_URL`

### Default Seeded Local Account

For development, the backend seeds a default tenant and admin account unless seeding is disabled.

- Email: `admin@chimera.local`
- Password: `Ch1m3r@Admin!`

Change these defaults before using any shared or non-local environment.

## Development Commands

### Root-Level Convenience Commands

```powershell
make setup
make test
make lint
make docker-test
make spec-check
```

### Backend

```powershell
cd backend
mvn test
mvn checkstyle:check
```

### Frontend

```powershell
cd frontend
npm install
npm run dev
npm run build
npm run test
npm run test:e2e
```

## API Surface

The control-plane API is rooted at `/api/v1` and includes domains for:

- authentication
- agents and memory
- campaigns and execution plans
- reviews and decisions
- wallets and transactions
- external signal ingestion

Primary API documentation:

- Runtime docs: `/swagger-ui.html` and `/v3/api-docs`
- Repo doc: `docs/api/chimera-control-plane.md`
- Authoritative contract: `specs/001-autonomous-influencer-network/contracts/chimera-control-plane.openapi.yaml`

## Specs First

📚 This repository is intentionally specification-driven.

Start here when you need product or contract context:

- `specs/functional.md`: business roles, user stories, and success conditions
- `specs/technical.md`: architecture, runtime, and boundary definitions
- `specs/001-autonomous-influencer-network/spec.md`: detailed feature requirements and acceptance criteria
- `specs/001-autonomous-influencer-network/plan.md`: implementation planning artifacts
- `specs/001-autonomous-influencer-network/data-model.md`: entity and storage model

## Current State

Project Chimera is under active development. The repository already contains:

- a backend control plane with REST endpoints, persistence, validation, and security wiring
- a frontend dashboard shell and feature pages for campaigns, agents, reviews, audits, and wallets
- local infrastructure definitions for PostgreSQL, Redis, and Weaviate
- spec, contract, and operations documentation for future expansion into broader agent workflows and MCP-driven integrations

Some specification items describe target-state capabilities that are still being implemented. Treat the specs as the product contract and the codebase as the current implementation snapshot.

## Additional Documentation

- `docs/api/README.md`
- `docs/api/chimera-control-plane.md`
- `docs/operations/README.md`
- `runbooks/campaign-operations.md`
- `runbooks/review-and-wallet-governance.md`
- `research/architecture_strategy.md`
- `research/tooling_strategy.md`

## Contributing

When making changes:

- read the relevant files under `specs/` first
- keep API and behavior changes aligned with the documented contracts
- prefer small, reviewable changes
- update nearby documentation when public behavior changes
- use `COPILOT_PROGRESS.md` to record meaningful implementation progress

## License

No project license is declared in this repository yet.
