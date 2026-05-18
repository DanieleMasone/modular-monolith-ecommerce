package com.dmasone.identity.payment.infrastructure.persistence;

import com.dmasone.identity.payment.domain.PaymentAttempt;
import com.dmasone.identity.payment.domain.PaymentRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * Adapter between the payment repository port and JPA.
 */
@Repository
public class JpaPaymentRepository implements PaymentRepository {

    private final SpringDataPaymentJpaRepository jpaRepository;

    public JpaPaymentRepository(SpringDataPaymentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public PaymentAttempt save(PaymentAttempt paymentAttempt) {
        return jpaRepository.save(PaymentJpaEntity.from(paymentAttempt)).toDomain();
    }

    @Override
    public Optional<PaymentAttempt> findByOrderId(UUID orderId) {
        return jpaRepository.findByOrderId(orderId).map(PaymentJpaEntity::toDomain);
    }
}
