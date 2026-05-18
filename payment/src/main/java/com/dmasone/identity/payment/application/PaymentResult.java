package com.dmasone.identity.payment.application;

import com.dmasone.identity.payment.domain.PaymentAttempt;
import com.dmasone.identity.payment.domain.PaymentStatus;
import java.time.Instant;
import java.util.UUID;

/**
 * Read model returned by payment application services and optional API
 * endpoints.
 */
public record PaymentResult(
        UUID id,
        UUID orderId,
        PaymentStatus status,
        Instant requestedAt
) {
    public static PaymentResult from(PaymentAttempt attempt) {
        return new PaymentResult(attempt.id(), attempt.orderId(), attempt.status(), attempt.requestedAt());
    }
}
