package com.dmasone.identity.orders.domain;

import com.dmasone.identity.sharedkernel.domain.DomainException;

/**
 * Raised when an order command violates placement rules before persistence or
 * downstream module interaction happens.
 */
public final class InvalidOrderException extends DomainException {

    public InvalidOrderException(String message) {
        super("INVALID_ORDER", message);
    }
}
