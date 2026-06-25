# User Guide

## Overview

This guide explains how to run, verify, and inspect the modular monolith locally. It is intentionally operational: the README explains what the project demonstrates, while this page covers day-to-day commands and troubleshooting.

## Requirements

- Java 21
- Maven 3.9+
- Docker with Docker Compose

On Windows, use Docker Desktop with the WSL 2 based engine and Linux containers. Run all commands from the repository root.

Check the local toolchain:

```bash
java -version
mvn -version
docker version
docker compose version
```

## Local Setup

Clone the repository, then run commands from the repository root. The project has one Maven wrapper at the root because all modules are built together.

Useful module map:

```txt
ecommerce-app      executable Spring Boot application
shared-kernel      small shared domain abstractions
catalog            products, stock, read model, Redis cache
orders             order placement, lookup, event publication
payment            payment event handling and persistence
coverage-report    aggregate JaCoCo report module
```

## Running Infrastructure

Start PostgreSQL and Redis:

```bash
docker compose up -d
```

Start and wait for healthy services:

```bash
docker compose up -d --wait
```

Stop infrastructure and remove development volumes:

```bash
docker compose down -v
```

The Docker Compose credentials are for local development only.

## Running the Application

Start infrastructure first, then run the application:

```bash
docker compose up -d
mvn -pl ecommerce-app -am spring-boot:run
```

The application starts on:

```txt
http://localhost:8080
```

Runtime API documentation:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Running Tests

Run the full verification suite:

```bash
mvn clean verify
```

With Docker available, this runs unit tests, architecture tests, and Testcontainers integration tests against PostgreSQL and Redis.

Run faster unit and architecture tests only:

```bash
mvn test
```

If Docker is not available, Testcontainers integration tests are skipped by the existing `disabledWithoutDocker = true` configuration. That is useful for quick local checks, but it is not a full integration-test signal.

## Generating OpenAPI

OpenAPI generation starts the application through Maven, reads the runtime API document, and writes a generated JSON file under `target`.

```bash
docker compose up -d --wait
mvn -pl ecommerce-app -am -Pgenerate-openapi -DskipTests verify
docker compose down -v
```

Generated output:

```txt
ecommerce-app/target/generated-docs/openapi.json
```

OpenAPI metadata, paths, Swagger UI path, and API grouping are configured in:

```txt
ecommerce-app/src/main/resources/openapi.yaml
```

## Generating JavaDoc

Generate aggregate JavaDoc:

```bash
mvn -DskipTests package javadoc:aggregate
```

Generated output:

```txt
target/reports/apidocs/index.html
```

## Generating Coverage Reports

Run verification:

```bash
mvn clean verify
```

Generated aggregate JaCoCo output:

```txt
coverage-report/target/site/jacoco-aggregate/index.html
```

## Generating Test Reports

After running tests, generate human-readable Surefire and Failsafe reports:

```bash
mvn surefire-report:report-only surefire-report:failsafe-report-only
```

Generated module report examples:

```txt
catalog/target/reports/surefire.html
ecommerce-app/target/reports/failsafe.html
```

## API Examples

List products:

```bash
curl http://localhost:8080/api/products
```

Fetch one product:

```bash
curl http://localhost:8080/api/products/1
```

Place an order:

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Idempotency-Key: checkout-001" \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"quantity":2}'
```

Fetch an order:

```bash
curl http://localhost:8080/api/orders/<order-id>
```

Fetch payment result:

```bash
curl http://localhost:8080/api/payments/<order-id>
```

## Idempotency Behavior

Order placement accepts an `Idempotency-Key` header.

- Repeating the same request with the same key returns the original order with `200 OK`.
- A repeated request with the same key does not reserve stock again.
- A repeated request with the same key does not create another payment attempt.
- Reusing the same key for a different product or quantity returns `409 Conflict`.

This keeps HTTP retries safe without adding distributed transaction machinery.

## Error Handling Examples

Example insufficient stock response:

```json
{
  "code": "INSUFFICIENT_STOCK",
  "message": "Insufficient stock for product 1"
}
```

Expected API behavior includes:

- invalid quantity returns a validation error
- unknown product returns a not-found response
- insufficient stock returns a domain error
- conflicting idempotency key reuse returns `409 Conflict`

Use the [OpenAPI UI](/modular-monolith-ecommerce/openapi/) for the published endpoint reference.

## Troubleshooting

### Docker Not Running

Symptoms:

- `docker version` cannot connect to the daemon
- Testcontainers integration tests are skipped
- OpenAPI generation cannot start PostgreSQL or Redis

Fix:

1. Start Docker Desktop.
2. Confirm it uses Linux containers.
3. Run `docker version` and `docker compose version`.
4. Re-run `mvn clean verify`.

### Testcontainers Issues

Testcontainers needs a reachable Docker daemon. On Windows, Docker Desktop should use the WSL 2 based engine. If integration tests are skipped, verify Docker first before debugging the test code.

Useful checks:

```bash
docker ps
docker info
```

### PostgreSQL Issues

If the application cannot connect to PostgreSQL:

1. Start infrastructure with `docker compose up -d --wait`.
2. Check container health with `docker compose ps`.
3. Restart local infrastructure with `docker compose down -v` and `docker compose up -d --wait`.

Flyway owns schema creation. Do not switch Hibernate to `ddl-auto=update`.

### Redis Issues

If product query caching fails locally:

1. Verify Redis is running with `docker compose ps`.
2. Restart infrastructure with `docker compose down -v` and `docker compose up -d --wait`.
3. Re-run the relevant application or integration test.

Redis is used only for read caching; PostgreSQL remains the source of truth.

### OpenAPI Generation Issues

OpenAPI generation requires the application to start successfully. If it fails:

1. Start infrastructure with `docker compose up -d --wait`.
2. Verify port `8080` is free.
3. Run `mvn -pl ecommerce-app -am -Pgenerate-openapi -DskipTests verify`.
4. Check `ecommerce-app/target/generated-docs/openapi.json`.

## Useful Links

- [Dashboard](/modular-monolith-ecommerce/)
- [Review Guide](/modular-monolith-ecommerce/docs/review-guide.html)
- [Architecture](/modular-monolith-ecommerce/docs/architecture.html)
- [API Guide](/modular-monolith-ecommerce/docs/api.html)
- [Testing](/modular-monolith-ecommerce/docs/testing.html)
- [CI and Pages](/modular-monolith-ecommerce/docs/ci-and-pages.html)
- [OpenAPI UI](/modular-monolith-ecommerce/openapi/)
- [JavaDoc](/modular-monolith-ecommerce/javadoc/)
- [Coverage Report](/modular-monolith-ecommerce/coverage/)
- [HTML Test Report](/modular-monolith-ecommerce/test-report/)
