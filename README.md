# Modular Monolith E-commerce

A showcase backend project demonstrating a modular monolith architecture for an e-commerce system.

The goal of this repository is not to build fake microservices, but to show clear domain boundaries, internal event-driven communication, persistence isolation, and an evolutionary architecture that can grow without becoming a distributed mess too early.

## Architecture

This project is structured as a Maven multi-module Spring Boot application.

```txt
modular-monolith-ecommerce
├── ecommerce-app      # Bootstrapping module
├── shared-kernel      # Shared domain abstractions and internal event publisher
├── catalog            # Product catalog domain
├── orders             # Order management domain
└── payment            # Payment domain
```

Why Modular Monolith?

Microservices are not a default choice.
A modular monolith is often the better starting point when the business domains are still evolving and operational complexity must stay under control.

This project demonstrates:

Clear domain boundaries
Package-level modularization
Internal communication through Spring events
JPA/Hibernate persistence
Flyway database migrations
CQRS-light read/write separation
Redis-backed caching
Production-oriented configuration
Tech Stack
Java 21
Spring Boot 4
Spring Web MVC
Spring Data JPA
Hibernate
PostgreSQL
Flyway
Redis
Maven multi-module
Docker Compose
Modules
catalog

Owns product data and stock availability.

Responsibilities:

Product write model
Product read model
Cached product queries
Product persistence
orders

Owns order placement.

Responsibilities:

Order creation
Order lifecycle
Publishing OrderPlacedEvent
payment

Owns payment processing logic.

Responsibilities:

Reacting to order events
Simulating payment request handling
shared-kernel

Contains small shared abstractions used across modules.

Responsibilities:

DomainEvent
Internal event publishing abstraction
ecommerce-app

Application bootstrap module.

Responsibilities:

Spring Boot entrypoint
Runtime configuration
Flyway migrations
REST API exposure
Internal Communication

Modules communicate through Spring application events.

Example:

orders module
publishes OrderPlacedEvent
↓
payment module
listens with @EventListener

This keeps modules decoupled without introducing network calls, queues, or premature microservice complexity.

CQRS Light

The catalog module separates:

Write model: JPA entity Product
Read model: projection ProductView
Query service: ProductQueryService

This is not full CQRS with separate databases.
It is a pragmatic read/write separation useful in real-world monoliths.

Database Migrations

Flyway is used for schema versioning.

Migration files are located in:

ecommerce-app/src/main/resources/db/migration

Initial migration:

V1__init_schema.sql
Running Locally
Requirements
Java 21+
Maven 3.9+
Docker
Docker Compose
Start infrastructure
docker compose up -d
Build
mvn clean install
Run

From IntelliJ, run:

EcommerceApplication

Or from terminal:

mvn -pl ecommerce-app spring-boot:run
Test API
Place order
curl -X POST http://localhost:8080/api/orders \
-H "Content-Type: application/json" \
-d '{"productId":1,"quantity":2}'

Expected response:

<generated-order-uuid>
Design Principles

This repository follows a few strict rules:

Domain modules should not expose persistence details as public API.
Modules communicate through events, not direct procedural chains.
Read and write paths can evolve independently.
Database changes are versioned through Flyway.
Redis caching is applied only where it makes sense.
The architecture should remain simple until complexity is justified.
What This Project Demonstrates

This project is designed as a portfolio repository for backend engineering roles.

It shows that the author understands:

Modular architecture
Domain separation
Spring Boot beyond CRUD
Persistence boundaries
Event-driven design inside a monolith
Pragmatic CQRS
Database migration discipline
Caching strategy
Avoiding unnecessary microservices
Future Improvements

Possible extensions:

Add ArchUnit tests to enforce module boundaries
Add Testcontainers integration tests
Add OpenAPI documentation
Add idempotency for order placement
Add transactional outbox pattern
Add module-specific package visibility rules
Add observability with Micrometer and Prometheus

---

# 17. Cosa aggiungere per renderlo davvero “senior”

Aggiungi dopo la prima versione:

```txt
tests/
├── ArchUnit boundary tests
├── Testcontainers PostgreSQL tests
├── Application event integration tests
```