package com.dmasone.identity.payment.domain;

import java.util.Optional;
import java.util.UUID;

/**
 * Persistence port for payment attempts.
 */
public interface PaymentRepository {

    PaymentAttempt save(PaymentAttempt paymentAttempt);

    Optional<PaymentAttempt> findByOrderId(UUID orderId);
}
