# Chimera Constitution

## Core Principles

### I. Contract-First Full-Stack Design
All externally consumed backend behavior must be expressed as stable HTTP or event contracts before implementation. Spring Boot controllers must expose explicit request and response DTOs, input validation, and predictable error payloads. React UI work must be driven by those published contracts rather than implicit backend behavior. Breaking API changes require versioning or an approved migration plan.

### II. Secure by Default
Security is mandatory for both application layers. Spring Boot services must enforce authentication, authorization, input validation, output encoding where relevant, and secret externalization. React must never embed secrets, trust raw HTML by default, or bypass server-side authorization assumptions. High-risk actions such as publishing, approvals, and administrative operations must leave an audit trail.

### III. Testable Boundaries Are Non-Negotiable
Every feature must include automated tests at the boundary it changes. Backend logic requires unit tests for service behavior and integration tests for controller, persistence, and security flows. React features require component or behavior tests for user-visible state changes and critical form flows. A feature is incomplete if the happy path is implemented without failure-path coverage.

### IV. Observable and Supportable Systems
The platform must be diagnosable in production. Spring Boot services must emit structured logs, health checks, and metrics for core operations, errors, and latency-sensitive paths. React must surface actionable error states to users and record client-side failures through the approved telemetry path. Request tracing and correlation identifiers should be preserved across UI, API, and asynchronous processing whenever feasible.

### V. Simplicity, Separation, and Evolvability
The system must remain easy to change. Business rules belong in backend services, not controllers or React components. React components should stay presentation-focused, with stateful data access isolated in dedicated hooks, service modules, or feature containers. New frameworks, abstractions, or infrastructure may be introduced only when they reduce repeated complexity rather than speculate on future needs.

## Technology and Architecture Requirements

The backend standard is Spring Boot 4.x on Java 25 with Gradle or Maven, using layered modules for controller, service, domain, and persistence concerns. Public APIs must be documented with OpenAPI, use explicit DTOs, and avoid leaking persistence entities directly.

The frontend standard is React JS with a modern build tool such as Vite. UI code must be organized by feature, use reusable components for shared interaction patterns, and keep network access behind dedicated API client modules. State management should start with React primitives and only expand to a global store when cross-feature coordination clearly requires it.

Persistence and integration choices must favor clear ownership boundaries. Transactional business data belongs in a relational store by default. Flexible or high-volume metadata stores may be introduced when justified by workload, but their schema, retention, and consistency expectations must be documented. External service calls must use timeouts, retries where safe, and explicit failure handling.

Security requirements apply end-to-end: JWT or session-based authentication must be enforced consistently, role or permission checks must be server-authoritative, CORS must be explicitly configured, CSRF protections must be considered for the chosen auth model, and all secrets must come from environment or secret-management systems. Sensitive events must be auditable.

Performance requirements must be considered during design, not after release. Backend endpoints should support pagination for list resources, avoid N+1 query patterns, and define caching only where correctness is preserved. React views must avoid unnecessary full-page reloads, handle loading and empty states explicitly, and keep initial page load reasonable by splitting large routes or heavy features when needed.

## Development Workflow and Quality Gates

Each feature must start with a short specification that identifies user value, backend contract changes, UI states, security implications, and test scope. Implementation should proceed backend contract first, then service logic, then UI integration, unless a spike is explicitly approved.

Before merge, the following gates must pass: backend tests, frontend tests, linting or static analysis, and any required integration checks for modified contracts or persistence flows. Code review must verify API shape, validation, security posture, observability, and regression risk, not just style.

Definition of done for a full-stack change includes: documented API or event contract changes, validated request and response models, user-visible loading and error states, audit handling for privileged actions, and automated coverage for the changed paths. Manual verification must confirm that the React UI works against the Spring Boot API in a realistic local or test environment.

## Governance

This constitution governs planning, implementation, and review decisions for Chimera. If another document conflicts with these rules, this constitution takes precedence unless it has been formally amended.

Amendments require three things: the proposed rule change, the reason the current rule is insufficient, and any migration guidance for code or process already in place. Version updates follow semantic intent: major for principle changes, minor for new mandatory sections or requirements, and patch for wording clarifications.

All pull requests and reviews must explicitly evaluate compliance with these principles. Exceptions are allowed only when documented with scope, rationale, risks, and a follow-up plan to remove or contain the exception.

**Version**: 1.0.0 | **Ratified**: 2026-03-07 | **Last Amended**: 2026-03-07
