# ADR 0001: Use Modular Monolith

## Status

Accepted

## Context

The project is a portfolio backend intended to show senior engineering judgment. A microservice architecture would add deployment, networking, data consistency, and observability complexity before the domain requires it.

## Decision

Use a modular monolith: one Spring Boot application composed from Maven modules with explicit package boundaries and ArchUnit tests.

## Consequences

Benefits:

- simple local and CI execution
- clear ownership without distributed-system overhead
- refactorable boundaries while the domain evolves
- easier end-to-end testing

Trade-offs:

- module boundaries rely on code structure and tests, not process isolation
- all modules share one runtime and one database
- future extraction requires deliberate contracts and migration planning
