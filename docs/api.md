# API Guide

The public REST API is intentionally small. It is designed to demonstrate the module flow rather than expose generic CRUD screens.

## Endpoints

| Method | Path | Purpose |
| --- | --- | --- |
| `GET` | `/api/products` | Lists the catalog read model. Results are cached through Redis when infrastructure is available. |
| `GET` | `/api/products/{id}` | Reads one catalog projection by product id. |
| `POST` | `/api/orders` | Places an order, reserves stock in catalog, persists the order, and publishes `OrderPlacedEvent`. Supports `Idempotency-Key` for safe retries. |
| `GET` | `/api/orders/{id}` | Reads a persisted order by id. |
| `GET` | `/api/payments/{orderId}` | Reads the payment attempt created by the order placed event listener. |

## Order Placement

Optional header:

```http
Idempotency-Key: checkout-001
```

Request:

```json
{
  "productId": 1,
  "quantity": 2
}
```

New successful placements return `201 Created` with a `Location` header pointing to `/api/orders/{id}`. Retrying the same request with the same `Idempotency-Key` returns `200 OK` and the original order body. The retry does not reserve stock again and does not publish a second payment-triggering event.

The same key must not be reused for a different product or quantity. That returns `409 Conflict` with `IDEMPOTENCY_KEY_CONFLICT`.

Important failure cases:

| Code | HTTP status | Meaning |
| --- | --- | --- |
| `VALIDATION_ERROR` | `400` | The request body failed bean validation, for example quantity is below one. |
| `PRODUCT_NOT_FOUND` | `404` | Catalog does not contain the requested product, either on product lookup or order placement. |
| `INSUFFICIENT_STOCK` | `409` | Catalog owns stock and rejected the reservation. |
| `IDEMPOTENCY_KEY_CONFLICT` | `409` | The idempotency key was already used for a different order request. |
| `ORDER_NOT_FOUND` | `404` | The requested order id does not exist. |
| `PAYMENT_NOT_FOUND` | `404` | No payment attempt exists for the order id. |

Example error:

```json
{
  "code": "INSUFFICIENT_STOCK",
  "message": "Insufficient stock for product 1"
}
```

## Generated Reference

At runtime the application exposes:

- Swagger UI: `/swagger-ui.html`
- OpenAPI JSON: `/v3/api-docs`

The CI documentation build exports the same OpenAPI document and publishes:

- Static Swagger UI: `/openapi/`
- OpenAPI JSON: `/openapi/openapi.json`

OpenAPI metadata and path matching are configured in `ecommerce-app/src/main/resources/openapi.yaml`.

The application bootstrap also contributes the shared `ApiError` schema to generated error responses. This keeps module controllers independent from `ecommerce-app` while still making the published OpenAPI reference useful to API readers.
