# Review Guide

This page helps reviewers inspect the repository quickly and intentionally. It gives different paths for recruiters, hiring managers, engineering managers, and hands-on engineers depending on how much time they have.

## Suggested Review Paths

| Reviewer | Time | Start Here | What to Check |
| --- | --- | --- | --- |
| Recruiter / HR | 2-5 minutes | [Dashboard](/modular-monolith-ecommerce/) and [README](https://github.com/DanieleMasone/modular-monolith-ecommerce#readme) | Project purpose, tech stack, CI badge, documentation links, and GitHub Pages site. |
| Engineering Manager | 5-10 minutes | [Architecture](/modular-monolith-ecommerce/docs/architecture.html), [Business Flow](/modular-monolith-ecommerce/docs/business-flow.html), and [Trade-offs](/modular-monolith-ecommerce/docs/trade-offs.html) | Whether the design is pragmatic, documented, testable, and easy to explain. |
| Senior Engineer / Architect | 15-30 minutes | Source modules, [ADR index](/modular-monolith-ecommerce/docs/adr/), [Coverage Report](/modular-monolith-ecommerce/coverage/), and [Test Report](/modular-monolith-ecommerce/test-report/) | Module boundaries, event flow, persistence rules, tests, and generated documentation. |
| Hands-on Local Review | 30-45 minutes | Clone the repository and run the local commands below | Build behavior, integration tests, API behavior, and local developer experience. |

## Key Links

- [Dashboard](/modular-monolith-ecommerce/)
- [Architecture](/modular-monolith-ecommerce/docs/architecture.html)
- [Business Flow](/modular-monolith-ecommerce/docs/business-flow.html)
- [Trade-offs](/modular-monolith-ecommerce/docs/trade-offs.html)
- [Architecture Decision Records](/modular-monolith-ecommerce/docs/adr/)
- [OpenAPI UI](/modular-monolith-ecommerce/openapi/)
- [OpenAPI JSON](/modular-monolith-ecommerce/openapi/openapi.json)
- [JavaDoc](/modular-monolith-ecommerce/javadoc/)
- [Coverage Report](/modular-monolith-ecommerce/coverage/)
- [HTML Test Report](/modular-monolith-ecommerce/test-report/)

## Recruiter / HR Review

Start with the dashboard and README. The important signal is not that this is a large system, but that it is organized like a serious backend project: clear purpose, visible CI, public documentation, generated API reference, and a small but realistic e-commerce domain.

Look for:

- Java 21 and Spring Boot 4.x backend stack
- Modular monolith architecture rather than a toy single-package CRUD app
- GitHub Actions CI badge on the README
- Public GitHub Pages documentation
- Generated API, JavaDoc, coverage, and test reports

## Engineering Manager Review

Read the architecture, business flow, trade-offs, and ADR index. This path shows how the project balances engineering discipline with scope control.

Look for:

- Clear module ownership across catalog, orders, payment, shared-kernel, and ecommerce-app
- Order placement flow from API call to stock reservation to payment event handling
- Explicit trade-offs explaining why the project avoids microservices and distributed messaging
- Testing strategy across unit, API, integration, and architecture tests
- CI that publishes useful reviewer-facing artifacts instead of only running tests

## Senior Engineer / Architect Review

Inspect the source modules and tests. The strongest architecture signals are enforceable boundaries, narrow shared abstractions, internal Spring eventing, Flyway-managed persistence, and Testcontainers-backed integration tests.

Focus areas:

- `shared-kernel`: intentionally small shared abstractions such as domain events and exception base types.
- `catalog`: product ownership, stock reservation rules, read projections, and Redis-backed query caching.
- `orders`: order placement, idempotency-key handling, order lookup, and `OrderPlacedEvent` publication.
- `payment`: listener for `OrderPlacedEvent`, simulated authorization, and persisted payment attempts.
- `ecommerce-app`: Spring Boot bootstrap, REST error handling, Flyway migrations, OpenAPI configuration, and runtime wiring.
- `coverage-report`: build-only aggregate JaCoCo reporting module.

Files and areas worth checking:

- ArchUnit rules under `ecommerce-app/src/test/java`
- integration tests under `ecommerce-app/src/test/java`
- Flyway migrations under `ecommerce-app/src/main/resources/db/migration`
- OpenAPI configuration in `ecommerce-app/src/main/resources/openapi.yaml`
- package-level JavaDoc in module source packages

## What To Look For In Tests

The test suite is meant to prove behavior and architecture, not only line coverage.

- Unit tests cover stock reservation, insufficient stock, order placement validation, idempotent retry handling, and payment behavior.
- API and integration tests exercise order placement, product queries, OpenAPI exposure, persistence, Flyway migrations, Redis caching, and payment event handling.
- Testcontainers starts PostgreSQL and Redis for realistic integration coverage.
- ArchUnit validates module boundaries and forbidden dependencies.
- The coverage report gives a quick health check, while the HTML test report shows executed test classes and integration-test status.

## Hands-on Local Review

Requirements: Java 21, Maven 3.9+, and Docker with Docker Compose.

Start infrastructure:

```bash
docker compose up -d
```

Run the full build and test suite:

```bash
mvn clean verify
```

Run the application:

```bash
mvn -pl ecommerce-app -am spring-boot:run
```

Useful API checks:

```bash
curl http://localhost:8080/api/products
curl http://localhost:8080/api/products/1
curl http://localhost:8080/api/orders/<order-id>
curl http://localhost:8080/api/payments/<order-id>
```

Place an order:

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Idempotency-Key: checkout-001" \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"quantity":2}'
```

The README contains the full local workflow and API examples.

## What This Project Is Not

This is a portfolio-quality, production-minded modular monolith. It is not a full production commerce platform.

It intentionally does not include:

- microservices
- Kafka or RabbitMQ
- Kubernetes
- distributed transactions
- external cloud deployment
- full production authentication and authorization

Those omissions are deliberate scope choices. The project focuses on clear backend boundaries, persistence discipline, testability, documentation, and CI/CD maturity without adding distributed-system complexity that the domain does not need.

## Final Recommendation

For a quick review, check the dashboard and trade-offs first. For a technical review, inspect module boundaries and tests next. For a hands-on review, run `mvn clean verify` with Docker available and then exercise the API locally.
