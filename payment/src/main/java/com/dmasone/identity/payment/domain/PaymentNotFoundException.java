package com.dmasone.identity.payment.domain;

import com.dmasone.identity.sharedkernel.domain.DomainException;
import java.util.UUID;

/**
 * Raised when payment state has not yet been recorded for an order.
 */
public final class PaymentNotFoundException extends DomainException {

    public PaymentNotFoundException(UUID orderId) {
        super("PAYMENT_NOT_FOUND", "Payment for order " + orderId + " was not found");
    }
}
