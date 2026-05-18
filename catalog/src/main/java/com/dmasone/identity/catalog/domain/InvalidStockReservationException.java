package com.dmasone.identity.catalog.domain;

import com.dmasone.identity.sharedkernel.domain.DomainException;

/**
 * Raised when catalog stock is asked to reserve a non-positive quantity.
 */
public final class InvalidStockReservationException extends DomainException {

    public InvalidStockReservationException(int quantity) {
        super("INVALID_QUANTITY", "Quantity must be greater than zero: " + quantity);
    }
}
