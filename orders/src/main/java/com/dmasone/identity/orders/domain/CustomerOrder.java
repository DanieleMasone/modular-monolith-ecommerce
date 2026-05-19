package com.dmasone.identity.orders.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Order aggregate for the placement use case. The aggregate captures the
 * customer's purchase intent after catalog has enough information to reserve
 * stock for the requested product.
 */
public final class CustomerOrder {

    private final UUID id;
    private final Long productId;
    private final int quantity;
    private final OrderStatus status;
    private final Instant createdAt;
    private final String idempotencyKey;

    private CustomerOrder(
            UUID id,
            Long productId,
            int quantity,
            OrderStatus status,
            Instant createdAt,
            String idempotencyKey
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        if (productId == null) {
            throw new InvalidOrderException("Product id is required");
        }
        this.productId = productId;
        if (quantity <= 0) {
            throw new InvalidOrderException("Order quantity must be greater than zero");
        }
        this.quantity = quantity;
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.idempotencyKey = idempotencyKey;
    }

    public static CustomerOrder place(
            UUID id,
            Long productId,
            int quantity,
            Instant createdAt,
            String idempotencyKey
    ) {
        return new CustomerOrder(id, productId, quantity, OrderStatus.PLACED, createdAt, idempotencyKey);
    }

    public static CustomerOrder restore(
            UUID id,
            Long productId,
            int quantity,
            OrderStatus status,
            Instant createdAt,
            String idempotencyKey
    ) {
        return new CustomerOrder(id, productId, quantity, status, createdAt, idempotencyKey);
    }

    public UUID id() {
        return id;
    }

    public Long productId() {
        return productId;
    }

    public int quantity() {
        return quantity;
    }

    public OrderStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public String idempotencyKey() {
        return idempotencyKey;
    }
}
