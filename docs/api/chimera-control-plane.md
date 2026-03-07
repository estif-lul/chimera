# Chimera Control Plane API

## Overview

The Chimera Control Plane exposes a RESTful JSON API under `/api/v1/`. All endpoints require a valid session (local JWT or OIDC token) except for the authentication and health endpoints.

## Authentication

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/v1/auth/local/login` | Log in with email/password |
| `GET`  | `/api/v1/auth/session` | Get current session info |
| `GET`  | `/oauth2/authorization/{provider}` | Start OIDC flow |

## Agents

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/v1/agents` | Create agent + soul definition |
| `GET`  | `/api/v1/agents` | List all agents |
| `GET`  | `/api/v1/agents/{agentId}` | Get agent detail |
| `GET`  | `/api/v1/agents/{agentId}/memory` | List memory write-backs |

## Campaigns

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/v1/campaigns` | Create campaign with agent assignments |
| `GET`  | `/api/v1/campaigns` | List campaigns |
| `GET`  | `/api/v1/campaigns/{id}/plan` | Get execution plan + tasks |
| `POST` | `/api/v1/campaigns/{id}/approve` | Approve plan and activate campaign |

## Signals

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/v1/signals` | Ingest external signal (returns 202) |

## Review

| Method | Path | Description |
|--------|------|-------------|
| `GET`  | `/api/v1/reviews` | List pending review items |
| `GET`  | `/api/v1/reviews/{id}` | Get review item detail |
| `POST` | `/api/v1/reviews/{id}/decisions` | Submit review decision |

## Wallets

| Method | Path | Description |
|--------|------|-------------|
| `GET`  | `/api/v1/wallets/agent/{agentId}` | Get wallet by agent |
| `GET`  | `/api/v1/wallets/{walletId}` | Get wallet by ID |
| `GET`  | `/api/v1/wallets/{walletId}/transactions` | List transactions |
| `POST` | `/api/v1/wallets/{walletId}/transactions` | Submit transaction request |

## Error Responses

All error responses use RFC 9457 Problem Detail format:

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation failure",
  "instance": "/api/v1/campaigns"
}
```

## OpenAPI Specification

The full OpenAPI 3.1 specification is available at:
- Runtime: `/v3/api-docs` (JSON) and `/swagger-ui.html`
- Source: `specs/001-autonomous-influencer-network/contracts/chimera-control-plane.openapi.yaml`
