# AGENTS.md

Project-specific guidance for AI coding agents working on `modular-monolith-ecommerce`.

## Project Purpose

This repository is a portfolio-quality Java/Spring Boot backend project designed to showcase backend engineering skills to technical reviewers and recruiters.

The project should demonstrate:

- modular monolith architecture
- clear domain boundaries
- pragmatic event-driven design
- persistence discipline
- automated testing strategy
- documentation quality
- CI/CD maturity
- production-oriented engineering practices

Keep the implementation simple, credible, and realistic.

Do not turn this project into fake distributed architecture.

## Branch and CI

- The default branch is `master`.
- GitHub Actions workflows must trigger on `master`, not `main`.
- Main verification command:

```bash
mvn clean verify
```

- CI should:
    - run unit tests
    - run integration tests
    - run ArchUnit tests
    - generate documentation
    - generate OpenAPI JSON
    - generate aggregate JavaDoc
    - generate JaCoCo coverage reports
    - publish documentation to GitHub Pages

Published documentation URL:

```txt
https://danielemasone.github.io/modular-monolith-ecommerce/
```

## GitHub Pages Requirements

GitHub Pages is used only for static assets and generated documentation.

Pages should expose:

```txt
/
├── docs/
├── openapi/
├── javadoc/
└── coverage/
```

A static dashboard may display:

- architecture information
- OpenAPI metadata
- test results
- coverage metrics
- CI status
- generated documentation links

Do not assume GitHub Pages can host:

- Spring Boot applications
- PostgreSQL
- Redis
- backend services

## Architecture Rules

- Only `ecommerce-app` is executable.
- Do not add `@SpringBootApplication` classes to business modules.
- `shared-kernel` must remain tiny and independent.
- `orders` may depend on `catalog` application services.
- `payment` may depend on the `orders` event contract.
- `orders` must not depend on `payment`.
- Domain packages must not depend on REST or infrastructure packages.
- Cross-module communication should happen through application services or Spring events.
- Business modules must not depend on `ecommerce-app`.

Do not introduce:

- Kafka
- RabbitMQ
- Kubernetes
- microservices

## Module Ownership

### shared-kernel

Contains:

- `DomainEvent`
- `EventPublisher`
- shared exception abstractions

### catalog

Owns:

- product data
- stock reservation rules
- product read projections
- Redis-backed query cache

### orders

Owns:

- order placement
- idempotent retry handling
- order lookup
- `OrderPlacedEvent`

### payment

Owns:

- payment authorization simulation
- payment persistence
- payment status lifecycle

### ecommerce-app

Owns:

- application bootstrap
- runtime configuration
- REST exception handling
- Flyway migrations
- OpenAPI configuration

## Spring Boot 4 Notes

- Use Java 21.
- Use Spring Boot 4.x APIs and conventions.
- Use `spring-boot-starter-flyway`.
- Keep Hibernate `ddl-auto=validate`.

## Maven and Build Rules

- Root `pom.xml` must use:

```xml
<packaging>pom</packaging>
```

- Only `ecommerce-app` should be executable.

Do not commit generated artifacts:

- `target/`
- generated JavaDoc
- generated OpenAPI JSON
- coverage reports
- Maven site output

## OpenAPI Rules

OpenAPI runtime configuration lives in:

```txt
ecommerce-app/src/main/resources/openapi.yaml
```

Expected generated file:

```txt
ecommerce-app/target/generated-docs/openapi.json
```

## Testing Rules

Fast tests:

```bash
mvn test
```

Full verification:

```bash
mvn clean verify
```

Use:

- JUnit 5
- AssertJ
- Testcontainers
- ArchUnit

Integration tests should verify:

- PostgreSQL persistence
- Redis integration
- Flyway migrations
- REST API behavior
- event-driven flows

## Documentation Rules

All documentation must be written in English.

Recommended structure:

```txt
docs/
├── architecture.md
├── testing.md
├── trade-offs.md
├── business-flow.md
└── adr/
```

## JavaDoc Rules

Add JavaDoc to:

- public APIs
- application services
- architectural abstractions
- event contracts

Avoid useless JavaDoc.

## CI and GitHub Actions

CI should:

- cache Maven dependencies
- execute full verification
- generate docs
- generate coverage
- publish GitHub Pages artifacts

## Docker Rules

Provide local infrastructure through:

```txt
docker-compose.yml
```

Include:

- PostgreSQL
- Redis

## Error Handling

REST APIs should expose structured error responses.

Example:

```json
{
  "code": "INSUFFICIENT_STOCK",
  "message": "Insufficient stock for product 1"
}
```

## Avoid

Do not:

- expose repositories across module boundaries
- replace Flyway with Hibernate schema generation
- over-engineer the system
- add fake enterprise complexity

Prefer clarity and credibility over complexity.
