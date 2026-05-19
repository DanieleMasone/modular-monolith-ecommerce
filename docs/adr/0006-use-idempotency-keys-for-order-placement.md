# ADR 0006: Use Idempotency Keys for Order Placement

## Status

Accepted

## Context

Order placement changes stock and triggers payment handling. Clients may retry a request when a network timeout or connection interruption happens after the server has already accepted the order. Without idempotency, a retry could reserve stock twice and publish a second order placed event.

## Decision

`POST /api/orders` accepts an optional `Idempotency-Key` header. The orders module stores the key with the order aggregate and enforces uniqueness at the database level.

When the same key is reused with the same product and quantity, the service returns the original order and does not reserve stock or publish another `OrderPlacedEvent`.

When the same key is reused with a different product or quantity, the service rejects the request with `IDEMPOTENCY_KEY_CONFLICT`.

## Consequences

- HTTP retries become safe for the order placement workflow.
- The feature stays inside the modular monolith and does not require a distributed cache or message broker.
- The orders module owns idempotency because it owns the placement use case.
- The database unique index provides a durable boundary for duplicate keys.
- A future high-concurrency implementation could add explicit handling for unique constraint races, but the current design is sufficient for this portfolio scope.
