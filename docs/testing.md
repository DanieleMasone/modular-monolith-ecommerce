# Testing

The test suite is split by risk and feedback speed.

## Unit Tests

Fast unit tests cover domain and use-case behavior:

- product stock reservation
- insufficient stock behavior
- invalid order placement
- order event publication
- payment listener delegation
- payment attempt creation and duplicate event handling
- REST mapper generation through MapStruct compilation

Unit tests use small hand-written fakes for application-service collaborators. That keeps the behavior explicit and avoids bytecode-agent warnings for simple interaction tests.

Run:

```bash
mvn test
```

## Architecture Tests

ArchUnit tests live in `ecommerce-app` and import the production packages. They enforce module boundaries and keep the shared kernel independent.

Rules include:

- domain packages must not depend on REST or infrastructure packages
- domain packages must not depend on Spring
- `orders` must not depend on `payment`
- `orders` and `payment` must not depend on catalog persistence
- payment must depend on the `OrderPlacedEvent` contract

## Integration and API Tests

`EcommerceApplicationIT` uses Testcontainers for PostgreSQL and Redis. It verifies:

- Flyway migration and seed data
- product read model endpoint
- OpenAPI document endpoint
- Redis-backed catalog query cache
- successful order placement
- stock reservation persistence
- `OrderPlacedEvent` triggering payment handling
- invalid quantity API response
- unknown product API response
- insufficient stock API response

Run the full suite:

```bash
mvn clean verify
```

Docker must be available for Testcontainers. The test class is marked with `disabledWithoutDocker = true` so local environments without Docker can still run the rest of the suite. A local `mvn clean verify` run without Docker is useful, but it is not a full integration-test signal.

## CI

The main CI workflow runs `mvn clean verify` on pushes to `master` and pull requests. GitHub-hosted Linux runners provide Docker, so Testcontainers integration tests run there.

The unified CI workflow also builds documentation on `master`. It generates aggregate JavaDoc, starts PostgreSQL and Redis with Docker Compose, runs the `generate-openapi` Maven profile, and publishes Markdown docs, JavaDoc, a static Swagger UI page, and OpenAPI JSON to GitHub Pages.
