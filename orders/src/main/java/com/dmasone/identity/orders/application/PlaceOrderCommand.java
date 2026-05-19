package com.dmasone.identity.orders.application;

import com.dmasone.identity.orders.domain.InvalidOrderException;

/**
 * Command object for the order placement use case. REST request models are
 * translated into this command so validation and use-case orchestration stay
 * independent from HTTP details. The optional idempotency key represents a
 * client retry boundary rather than a business identifier.
 */
public record PlaceOrderCommand(Long productId, int quantity, String idempotencyKey) {

    private static final int MAX_IDEMPOTENCY_KEY_LENGTH = 128;

    public PlaceOrderCommand {
        idempotencyKey = normalize(idempotencyKey);
        if (idempotencyKey != null && idempotencyKey.length() > MAX_IDEMPOTENCY_KEY_LENGTH) {
            throw new InvalidOrderException("Idempotency key must be at most 128 characters");
        }
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
