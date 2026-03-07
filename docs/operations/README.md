# Operations

## Local Development

```bash
# Start infrastructure
docker compose -f ops/docker-compose.yml up -d

# Start backend
cd backend && mvn spring-boot:run

# Start frontend
cd frontend && npm install && npm run dev
```

## Deployment

Target runtime: Kubernetes on hybrid AWS/GCP. See `ops/k8s/` for manifests.
