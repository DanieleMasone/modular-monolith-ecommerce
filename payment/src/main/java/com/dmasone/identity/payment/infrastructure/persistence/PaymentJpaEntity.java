package com.dmasone.identity.payment.infrastructure.persistence;

import com.dmasone.identity.payment.domain.PaymentAttempt;
import com.dmasone.identity.payment.domain.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

/**
 * Persistence mapping for payment attempts. The unique order id supports
 * idempotent event handling.
 */
@Entity(name = "PaymentAttemptEntity")
@Table(name = "payment_attempts")
public class PaymentJpaEntity {

    @Id
    private UUID id;

    @Column(name = "order_id", nullable = false, unique = true)
    private UUID orderId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private PaymentStatus status;

    @Column(name = "requested_at", nullable = false)
    private Instant requestedAt;

    protected PaymentJpaEntity() {
    }

    private PaymentJpaEntity(
            UUID id,
            UUID orderId,
            Long productId,
            int quantity,
            PaymentStatus status,
            Instant requestedAt
    ) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.status = status;
        this.requestedAt = requestedAt;
    }

    public static PaymentJpaEntity from(PaymentAttempt attempt) {
        return new PaymentJpaEntity(
                attempt.id(),
                attempt.orderId(),
                attempt.productId(),
                attempt.quantity(),
                attempt.status(),
                attempt.requestedAt()
        );
    }

    public PaymentAttempt toDomain() {
        return PaymentAttempt.create(id, orderId, productId, quantity, status, requestedAt);
    }
}
