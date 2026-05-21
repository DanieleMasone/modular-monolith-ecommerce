# AGENTS.md

Project-specific guidance for AI coding agents working on `modular-monolith-ecommerce`.

## Project Purpose

This repository is a portfolio-quality Java/Spring Boot backend project designed to showcase senior backend engineering skills to technical reviewers and recruiters. Keep the implementation simple, credible, and realistic. Do not turn this project into fake distributed architecture.

The project should demonstrate modular monolith architecture, clear domain boundaries, pragmatic event-driven design, persistence discipline, automated testing strategy, documentation quality, CI/CD maturity, and production-minded engineering practices. Prefer clarity over overengineering.

## Branch and CI

- The default branch is `master`.
- GitHub Actions workflows must trigger on `master`, not `main`.
- Keep CI and GitHub Pages deployment in `.github/workflows/ci.yml` unless there is a strong reason to split them.
- Main verification command: `mvn clean verify`.
- CI should run unit tests, integration tests, ArchUnit tests, OpenAPI generation, aggregate JavaDoc, JaCoCo coverage, HTML test reports, and GitHub Pages publication.
- Published documentation URL: `https://danielemasone.github.io/modular-monolith-ecommerce/`.
- Prefer current action major versions that run on Node.js 24; do not reintroduce deprecated Node.js 20 action versions.

## GitHub Pages Requirements

GitHub Pages is used only for static assets and generated documentation. The Pages root is the static portfolio dashboard. Pages should expose:

```txt
/
|-- index.html
|-- docs/
|-- openapi/
|-- javadoc/
|-- coverage/
`-- test-report/
```

Do not publish a separate `/dashboard/` alias unless the user explicitly asks for one. Do not assume GitHub Pages can host Spring Boot applications, PostgreSQL, Redis, backend services, or background workers.

## Architecture Rules

- Only `ecommerce-app` is executable.
- Do not add `@SpringBootApplication` classes to business modules.
- `shared-kernel` must remain tiny and independent.
- `orders` may depend on `catalog` application services.
- `payment` may depend on the `orders` event contract.
- `orders` must not depend on `payment`.
- Domain packages must not depend on REST or infrastructure packages.
- Domain packages should not depend on Spring.
- Do not expose repositories across module boundaries.
- Cross-module communication should happen through application services or Spring events.
- Business modules must not depend on `ecommerce-app`; bootstrap wiring belongs in `ecommerce-app` only.
- Do not introduce microservices, Kafka, RabbitMQ, Kubernetes, distributed transactions, fake cloud-native complexity, or broad dependencies without a clear reason.

## Module Ownership

- `shared-kernel`: `DomainEvent`, `EventPublisher`, shared domain exception base.
- `catalog`: product data, stock rules, read projection, Redis-backed query cache.
- `orders`: order placement, idempotent retry handling, order lookup, `OrderPlacedEvent` publication.
- `payment`: event listener for `OrderPlacedEvent`, simulated payment authorization, payment persistence.
- `ecommerce-app`: application bootstrap, runtime config, REST error handling, Flyway migrations, OpenAPI config.
- `coverage-report`: build-only aggregate JaCoCo reporting; do not add runtime code here.

## Spring Boot 4 Notes

- Use Java 21 and Spring Boot 4.x APIs and conventions.
- Use `org.springframework.boot.persistence.autoconfigure.EntityScan`.
- Use `org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc`.
- Use `spring-boot-starter-flyway`; `flyway-core` alone does not enable Boot 4 Flyway autoconfiguration.
- Keep Hibernate `ddl-auto=validate`.

## Maven Generation

- Root `pom.xml` must use `<packaging>pom</packaging>`.
- Only `ecommerce-app` should be executable.
- MapStruct implementations are generated during compilation under `target/generated-sources/annotations`.
- OpenAPI runtime configuration lives in `ecommerce-app/src/main/resources/openapi.yaml`.
- Expected OpenAPI output: `ecommerce-app/target/generated-docs/openapi.json`.
- JaCoCo aggregate output is generated under `coverage-report/target/site/jacoco-aggregate`.
- HTML test reports are generated under module `target/reports` directories.
- Do not commit generated OpenAPI, JavaDoc, coverage, test report, Maven site, MapStruct implementation, `pages/`, `_site/`, or `target/` artifacts.
- Generated content should live only under build output folders such as `target/` or temporary CI staging directories that are explicitly ignored.

## Tests

- Fast tests: `mvn test`.
- Full suite: `mvn clean verify`.
- Testcontainers integration tests require Docker. They are marked with `disabledWithoutDocker = true`, so local environments without Docker can still run the rest of the suite.
- Treat `mvn clean verify` as a full local verification only when Docker Desktop is reachable and Testcontainers starts PostgreSQL and Redis.
- On Windows, assume Docker Desktop should use the WSL 2 based engine with Linux containers for this Java/PostgreSQL/Redis project.
- Generate local HTML test reports with `mvn surefire-report:report-only surefire-report:failsafe-report-only` after tests have run.
- For the CI documentation path, verify Docker Compose plus OpenAPI generation with `docker compose up -d --wait` followed by `mvn -pl ecommerce-app -am -Pgenerate-openapi -DskipTests verify`, then shut infrastructure down with `docker compose down -v`.
- Every implemented feature should have meaningful tests. Add or update API tests for externally exposed behavior, architecture rules for boundary-sensitive changes, and integration tests when persistence, Flyway, Redis, or events are involved.

## Documentation

- Keep README and files under `docs/` in English.
- Keep `docs/` versioned. It is source documentation for the Pages build, not generated output.
- Add ADRs for meaningful architectural decisions.
- Document externally visible API behavior through OpenAPI annotations or configuration when relevant.
- Update documentation when a feature changes business flow, module responsibility, CI behavior, or a deliberate trade-off.
- Generated documentation should remain Maven/CI driven, not manually edited artifacts.
- Use the root Maven wrapper only. Do not add module-local Maven wrappers, module-local `.gitignore`, or module-local `.gitattributes` files.

## Avoid

- Do not reintroduce module-local `application.yaml` files unless there is a strong reason.
- Do not expose Spring Data repositories across module boundaries.
- Do not replace Flyway with Hibernate schema generation.
- Do not add broad dependencies without a clear architectural reason.
- Prefer clarity and credibility over complexity.
