# API Guide

The public REST API is intentionally small. It is designed to demonstrate the module flow rather than expose generic CRUD screens.

## Endpoints

| Method | Path | Purpose |
| --- | --- | --- |
| `GET` | `/api/products` | Lists the catalog read model. Results are cached through Redis when infrastructure is available. |
| `GET` | `/api/products/{id}` | Reads one catalog projection by product id. |
| `POST` | `/api/orders` | Places an order, reserves stock in catalog, persists the order, and publishes `OrderPlacedEvent`. |
| `GET` | `/api/orders/{id}` | Reads a persisted order by id. |
| `GET` | `/api/payments/{orderId}` | Reads the payment attempt created by the order placed event listener. |

## Order Placement

Request:

```json
{
  "productId": 1,
  "quantity": 2
}
```

Successful responses return `201 Created` with a `Location` header pointing to `/api/orders/{id}`.

Important failure cases:

| Code | HTTP status | Meaning |
| --- | --- | --- |
| `VALIDATION_ERROR` | `400` | The request body failed bean validation, for example quantity is below one. |
| `PRODUCT_NOT_FOUND` | `404` | Catalog does not contain the requested product, either on product lookup or order placement. |
| `INSUFFICIENT_STOCK` | `409` | Catalog owns stock and rejected the reservation. |
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
