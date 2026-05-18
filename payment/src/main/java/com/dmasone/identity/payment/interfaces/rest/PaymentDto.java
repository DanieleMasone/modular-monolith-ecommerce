package com.dmasone.identity.payment.interfaces.rest;

import com.dmasone.identity.payment.domain.PaymentStatus;
import java.time.Instant;
import java.util.UUID;

/**
 * HTTP representation of a payment attempt result.
 */
public record PaymentDto(
        UUID id,
        UUID orderId,
        PaymentStatus status,
        Instant requestedAt
) {
}
