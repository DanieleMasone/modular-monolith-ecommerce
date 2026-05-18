package com.dmasone.identity.orders.application.events;

import com.dmasone.identity.sharedkernel.domain.DomainEvent;
import java.time.Instant;
import java.util.UUID;

/**
 * Integration event published after the orders module successfully places an
 * order. Payment listens to this fact instead of being called directly.
 */
public record OrderPlacedEvent(
        UUID eventId,
        Instant occurredAt,
        UUID orderId,
        Long productId,
        int quantity
) implements DomainEvent {

    public OrderPlacedEvent(UUID orderId, Long productId, int quantity) {
        this(UUID.randomUUID(), Instant.now(), orderId, productId, quantity);
    }
}
