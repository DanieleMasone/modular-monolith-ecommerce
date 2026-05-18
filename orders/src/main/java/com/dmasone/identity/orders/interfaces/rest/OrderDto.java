package com.dmasone.identity.orders.interfaces.rest;

import com.dmasone.identity.orders.domain.OrderStatus;
import java.time.Instant;
import java.util.UUID;

/**
 * HTTP representation of an order. The application layer keeps its own
 * response model so API evolution does not leak into use-case services.
 */
public record OrderDto(
        UUID id,
        Long productId,
        int quantity,
        OrderStatus status,
        Instant createdAt
) {
}
