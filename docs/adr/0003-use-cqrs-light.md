# ADR 0003: Use CQRS Light

## Status

Accepted

## Context

The catalog module needs a clean read path and cacheable projections, but a separate read database would be unnecessary complexity for this showcase.

## Decision

Use CQRS light in catalog:

- command service owns stock reservation
- query service returns immutable `ProductView` projections
- both paths use the same PostgreSQL database
- Redis caches read operations through Spring Cache

## Consequences

Benefits:

- clearer read/write responsibilities
- cacheable query model
- no fake distributed consistency story

Trade-offs:

- reads and writes still share one database schema
- cache invalidation must be handled by command operations
- projections remain simple and local to catalog
