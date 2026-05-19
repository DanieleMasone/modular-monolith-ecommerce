# Architecture Decision Records

| ID | Decision | Status | Summary |
| --- | --- | --- | --- |
| [0001](0001-use-modular-monolith.md) | Use Modular Monolith | Accepted | Keep one deployable application while preserving clear module ownership and testable boundaries. |
| [0002](0002-use-spring-events-for-internal-communication.md) | Use Spring Events for Internal Communication | Accepted | Use in-process events for module communication instead of introducing a broker before operational complexity is justified. |
| [0003](0003-use-cqrs-light.md) | Use CQRS Light | Accepted | Separate catalog reads and writes pragmatically without adding a second database or distributed read model. |
| [0004](0004-use-flyway-and-postgresql.md) | Use Flyway and PostgreSQL | Accepted | Let Flyway own schema evolution and run Hibernate with validation rather than schema generation. |
| [0005](0005-use-generated-openapi-and-mapstruct.md) | Use Generated OpenAPI and MapStruct | Accepted | Generate API reference and boundary mappers to keep documentation and DTO mapping explicit and repeatable. |
| [0006](0006-use-idempotency-keys-for-order-placement.md) | Use Idempotency Keys for Order Placement | Accepted | Make HTTP retries safe for order placement without introducing distributed infrastructure. |
