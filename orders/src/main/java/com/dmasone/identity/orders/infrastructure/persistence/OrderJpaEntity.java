package com.dmasone.identity.orders.infrastructure.persistence;

import com.dmasone.identity.orders.domain.CustomerOrder;
import com.dmasone.identity.orders.domain.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

/**
 * Database mapping for orders. The table name avoids the SQL keyword "order"
 * and keeps the persistence concern outside the domain package.
 */
@Entity(name = "CustomerOrderEntity")
@Table(name = "customer_orders")
public class OrderJpaEntity {

    @Id
    private UUID id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private OrderStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected OrderJpaEntity() {
    }

    private OrderJpaEntity(UUID id, Long productId, int quantity, OrderStatus status, Instant createdAt) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static OrderJpaEntity from(CustomerOrder order) {
        return new OrderJpaEntity(
                order.id(),
                order.productId(),
                order.quantity(),
                order.status(),
                order.createdAt()
        );
    }

    public CustomerOrder toDomain() {
        return CustomerOrder.restore(id, productId, quantity, status, createdAt);
    }
}
