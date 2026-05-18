package com.dmasone.identity.catalog.domain;

import com.dmasone.identity.sharedkernel.domain.DomainException;

/**
 * Raised when a product exists but cannot satisfy the requested reservation.
 */
public final class InsufficientStockException extends DomainException {

    public InsufficientStockException(Long productId) {
        super("INSUFFICIENT_STOCK", "Insufficient stock for product " + productId);
    }
}
