# ADR 0002: Use Spring Events for Internal Communication

## Status

Accepted

## Context

Orders should trigger payment behavior, but the orders module should not depend on payment implementation details. Introducing Kafka or RabbitMQ would make the project look distributed without a real need.

## Decision

Orders publish `OrderPlacedEvent` through the shared `EventPublisher` port. `ecommerce-app` adapts that port to Spring's in-process event publisher. Payment listens with a transactional event listener after commit.

## Consequences

Benefits:

- order placement remains decoupled from payment
- event flow is testable without external infrastructure
- payment is not triggered for rolled-back orders

Trade-offs:

- events are in-process and not durable
- failures in payment handling need explicit retry or outbox support later
- event contracts still create a compile-time dependency from payment to orders
