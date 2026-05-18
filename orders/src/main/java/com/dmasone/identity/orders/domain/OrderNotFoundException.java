package com.dmasone.identity.orders.domain;

import com.dmasone.identity.sharedkernel.domain.DomainException;
import java.util.UUID;

/**
 * Raised when an API caller asks for an order id that the orders module does
 * not own.
 */
public final class OrderNotFoundException extends DomainException {

    public OrderNotFoundException(UUID orderId) {
        super("ORDER_NOT_FOUND", "Order " + orderId + " was not found");
    }
}
