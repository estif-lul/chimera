# Chimera Control Plane API

Refer to the [OpenAPI contract](../../specs/001-autonomous-influencer-network/contracts/chimera-control-plane.openapi.yaml) for endpoint definitions.

## Base URL

| Environment | URL |
|---|---|
| Local | `http://localhost:8080/api/v1` |

## Authentication

All endpoints require a valid session obtained via local login or OIDC callback. Tenant scope is derived from the authenticated principal.
