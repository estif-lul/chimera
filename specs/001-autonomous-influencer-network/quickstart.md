# Quickstart: Project Chimera Autonomous Influencer Network

## Purpose

This quickstart describes the intended local development flow for the first implementation slice after scaffolding is created. It assumes a monorepo with `backend/`, `frontend/`, and local infrastructure services.

## Prerequisites

- Java 25
- Maven 3.9+
- Node.js 22+
- Docker Desktop or compatible container runtime
- Access to local or test credentials for OIDC, PostgreSQL, Weaviate, Redis, and the required MCP servers

## Environment Variables

Backend environment variables:

- `CHIMERA_DB_URL`
- `CHIMERA_DB_USERNAME`
- `CHIMERA_DB_PASSWORD`
- `CHIMERA_WEAVIATE_URL`
- `CHIMERA_WEAVIATE_API_KEY`
- `CHIMERA_REDIS_URL`
- `CHIMERA_JWT_SIGNING_KEY`
- `CHIMERA_OIDC_ISSUER_URI`
- `CHIMERA_OIDC_CLIENT_ID`
- `CHIMERA_OIDC_CLIENT_SECRET`
- `CHIMERA_ALLOWED_ORIGINS`
- `CHIMERA_MCP_RESOURCE_GATEWAY_URL`
- `CHIMERA_MCP_IDEOGRAM_URL`
- `CHIMERA_MCP_MIDJOURNEY_URL`
- `CHIMERA_MCP_RUNWAY_URL`
- `CHIMERA_MCP_LUMA_URL`

Frontend environment variables:

- `VITE_API_BASE_URL`
- `VITE_OIDC_AUTHORITY`
- `VITE_OIDC_CLIENT_ID`
- `VITE_OIDC_REDIRECT_URI`

## Local Infrastructure

Use Docker Compose to start local dependencies first:

```bash
docker compose -f ops/docker-compose.yml up -d postgres weaviate redis
```

## Backend Development Flow

Install dependencies and run the Spring Boot API:

```bash
cd backend
mvn spring-boot:run
```

Run backend tests:

```bash
cd backend
mvn test
```

## Frontend Development Flow

Install dependencies and start the Vite development server:

```bash
cd frontend
npm install
npm run dev
```

Run frontend tests:

```bash
cd frontend
npm test
```

## Minimum Manual Validation

1. Sign in using a local account and confirm tenant-scoped route protection.
2. Exercise OIDC sign-in and verify the user lands in the correct tenant workspace.
3. Create a Chimera agent and confirm immutable persona content persists.
4. Verify the created agent stores and exposes its `SOUL.md`-derived persona fields.
5. Create a campaign, inspect the generated execution plan, and approve it.
6. Trigger a review-required item and confirm reviewer decisions create audit entries.
7. Open a wallet view and validate inbound and outbound transaction flows with budget enforcement.
8. Trigger a successful high-engagement interaction, confirm the Judge path enqueues memory summarization, and verify the write-back is visible from Weaviate-backed memory history.
9. Generate both Tier 1 and Tier 2 video tasks and confirm provider, tier, and cost metadata are captured in operational storage.

## Deployment Direction

Target runtime is Kubernetes in a hybrid AWS/GCP environment. The backend API, frontend web app, and future worker processes should be containerized separately so autoscaling policies can react to API load and orchestration queue depth independently. MCP gateway services and media-generation servers should be deployed or routed as separately managed dependencies so perception and generation providers can be swapped without changing core agent code.