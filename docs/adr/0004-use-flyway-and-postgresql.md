# ADR 0004: Use Flyway and PostgreSQL

## Status

Accepted

## Context

The project should demonstrate production persistence discipline. Letting Hibernate update schemas automatically hides important database changes and makes CI less representative of production.

## Decision

Use PostgreSQL as the primary relational database and Flyway for all schema changes. Hibernate validates the schema with `ddl-auto=validate`.

## Consequences

Benefits:

- database changes are reviewed as versioned artifacts
- CI catches entity/schema drift
- local development matches production-style behavior

Trade-offs:

- every schema change requires a migration
- tests need PostgreSQL, provided by Testcontainers
- developers must understand both JPA mappings and SQL migrations
