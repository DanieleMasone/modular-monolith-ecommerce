package com.dmasone.identity.orders.domain;

import com.dmasone.identity.sharedkernel.domain.DomainException;

/**
 * Raised when a client reuses an idempotency key for a different order
 * request. Replays are safe only when the business intent is identical.
 */
public final class IdempotencyKeyConflictException extends DomainException {

    public IdempotencyKeyConflictException(String idempotencyKey) {
        super(
                "IDEMPOTENCY_KEY_CONFLICT",
                "Idempotency key '" + idempotencyKey + "' was already used for a different order request"
        );
    }
}
