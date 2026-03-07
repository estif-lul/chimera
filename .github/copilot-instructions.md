# Project Chimera Copilot Instructions

## Project Context
- This is Project Chimera, an autonomous influencer system.
- Treat the repository as specification-driven. Prefer existing specs, contracts, and research documents over assumptions.
- Keep generated solutions aligned with the current platform direction: Java backend, Spring Boot services, and React frontend.

## Prime Directive
- NEVER generate code without checking specs/ first.
- Before proposing or writing code, inspect the relevant files under `specs/` and any linked research or contract documents.
- If the specs are missing, contradictory, or insufficient for the requested change, state that clearly and propose the smallest reasonable next step.

## Traceability
- Explain your plan first when writing code.
- When implementing behavior from a spec, make the relationship between the request, the spec, and the code explicit.
- Call out assumptions, tradeoffs, and gaps when the spec does not fully determine the implementation.

## Workflow / Communication
- Create or update a SPEC file when a change affects architecture, API contracts, or non-trivial behavior.
- Put SPEC files in a `specs/` folder with a short title, motivation, design, and acceptance criteria.
- When implementing features or fixes, include a brief "Lessons learned / Progress" note in `COPILOT_PROGRESS.md` (or update it) summarizing progress, what was challenging, and follow-up items.
- Ensure all app changes include a brief implementation plan and update `COPILOT_PROGRESS.md` for fixes.
- Prefer small, reviewable changes that preserve existing contracts unless the spec explicitly changes them.
- If a request conflicts with the specs, raise the conflict before changing code.

## Documentation Standards
- Always add Javadoc comments for public types, public methods, and any non-trivial members. Keep summaries concise and descriptive.
- Keep comments factual and maintainable. Document intent, invariants, and behavior rather than restating obvious syntax.
- Update nearby documentation when changing public behavior, domain rules, or operational expectations.

## Java-Specific Directives
- Strictly adhere to Java 21+ idioms.
- Utilize Records for immutable data transfer objects (DTOs) passing between Planner/Worker/Judge.
- Use JUnit 5 for all generated tests.
- Prefer immutable designs, explicit types, and clear domain modeling over framework-driven magic.
- Prefer `record`, `sealed`, and `enum` where they improve correctness and readability.
- Favor constructor injection, composition, and small focused classes.
- Avoid returning `null` when an empty collection, `Optional`, or a domain-specific result type is clearer.
- Keep concurrency explicit and safe. Use structured, well-bounded async patterns instead of ad hoc thread management.

## Java Best Practices
- Keep packages organized by bounded domain or feature, not by generic technical layer alone.
- Model domain invariants in types and constructors whenever possible.
- Prefer `List`, `Set`, and `Map` interfaces in APIs; choose concrete implementations deliberately.
- Validate inputs at system boundaries and fail fast with clear error messages.
- Keep methods short and cohesive; extract private helpers when branching or transformation logic becomes hard to follow.
- Avoid reflection-heavy or overly clever abstractions when straightforward code is clearer.
- Use meaningful names. Do not introduce abbreviations unless they are established domain terminology.
- Write tests for behavior, edge cases, and contract boundaries rather than implementation details.

## Spring Boot Best Practices
- Prefer stateless services and explicit dependency injection through constructors.
- Keep controllers thin. Put orchestration in application services and business rules in domain-focused components.
- Design APIs contract-first. Keep request and response models stable and aligned with the specs and OpenAPI documents.
- Use dedicated DTOs for external contracts and avoid exposing persistence entities directly through APIs.
- Validate incoming requests with Jakarta Bean Validation and translate failures into consistent API error responses.
- Keep persistence concerns isolated. Repositories should not contain business orchestration.
- Use transactions deliberately and keep transactional boundaries narrow and well understood.
- Prefer configuration properties classes over scattered `@Value` usage for structured configuration.
- Make observability standard: structured logging, health checks, and metrics for critical flows.
- Build secure-by-default endpoints: least privilege, explicit authentication and authorization, and no trust in client-supplied state.
- Favor integration tests for slices that cross HTTP, persistence, or messaging boundaries, and use JUnit 5 consistently.

## React Best Practices
- Prefer function components, hooks, and predictable unidirectional data flow.
- Keep components small and focused. Split presentation, state orchestration, and API interaction when complexity grows.
- Treat props as immutable inputs and avoid mutating objects passed through the component tree.
- Keep side effects explicit and localized. Use effects only for real synchronization work.
- Prefer controlled state transitions and avoid redundant derived state.
- Design components for accessibility: semantic HTML, keyboard support, labels, and visible focus states.
- Keep data fetching, caching, and error handling deliberate and user-visible.
- Avoid oversized components with intertwined rendering and business logic; extract custom hooks when behavior is reusable.
- Preserve stable API contracts between frontend and backend and reflect spec changes intentionally.
- Write tests for user-visible behavior and critical interaction flows rather than internal implementation details.

## Quality Bar
- Do not invent requirements that are not supported by the request or the specs.
- Prefer the simplest implementation that satisfies the spec, keeps behavior explicit, and remains easy to test.
- When adding or changing tests, cover the main success path, key edge cases, and failure behavior that matters to the contract.
- Flag uncertainty early rather than encoding assumptions silently into code.
