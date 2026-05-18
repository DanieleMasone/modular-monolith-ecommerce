package com.dmasone.identity.payment.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Payment module record of an authorization attempt for an order. The module
 * owns the attempt history and treats the order event as an input fact.
 */
public final class PaymentAttempt {

    private final UUID id;
    private final UUID orderId;
    private final Long productId;
    private final int quantity;
    private final PaymentStatus status;
    private final Instant requestedAt;

    private PaymentAttempt(
            UUID id,
            UUID orderId,
            Long productId,
            int quantity,
            PaymentStatus status,
            Instant requestedAt
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
        this.productId = Objects.requireNonNull(productId, "productId must not be null");
        this.quantity = quantity;
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.requestedAt = Objects.requireNonNull(requestedAt, "requestedAt must not be null");
    }

    public static PaymentAttempt create(
            UUID id,
            UUID orderId,
            Long productId,
            int quantity,
            PaymentStatus status,
            Instant requestedAt
    ) {
        return new PaymentAttempt(id, orderId, productId, quantity, status, requestedAt);
    }

    public UUID id() {
        return id;
    }

    public UUID orderId() {
        return orderId;
    }

    public Long productId() {
        return productId;
    }

    public int quantity() {
        return quantity;
    }

    public PaymentStatus status() {
        return status;
    }

    public Instant requestedAt() {
        return requestedAt;
    }
}
