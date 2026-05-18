package com.dmasone.identity.orders.application;

import com.dmasone.identity.orders.domain.CustomerOrder;
import com.dmasone.identity.orders.domain.OrderStatus;
import java.time.Instant;
import java.util.UUID;

/**
 * API-facing order projection returned by order commands and queries.
 */
public record OrderResponse(
        UUID id,
        Long productId,
        int quantity,
        OrderStatus status,
        Instant createdAt
) {
    public static OrderResponse from(CustomerOrder order) {
        return new OrderResponse(
                order.id(),
                order.productId(),
                order.quantity(),
                order.status(),
                order.createdAt()
        );
    }
}
