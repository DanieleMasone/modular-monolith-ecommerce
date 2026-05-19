package com.dmasone.identity.orders.infrastructure.persistence;

import com.dmasone.identity.orders.domain.CustomerOrder;
import com.dmasone.identity.orders.domain.OrderRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * Adapter that translates between the order repository port and Spring Data
 * JPA. This keeps repository access module-local.
 */
@Repository
public class JpaOrderRepository implements OrderRepository {

    private final SpringDataOrderJpaRepository jpaRepository;

    public JpaOrderRepository(SpringDataOrderJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public CustomerOrder save(CustomerOrder order) {
        return jpaRepository.save(OrderJpaEntity.from(order)).toDomain();
    }

    @Override
    public Optional<CustomerOrder> findById(UUID orderId) {
        return jpaRepository.findById(orderId).map(OrderJpaEntity::toDomain);
    }

    @Override
    public Optional<CustomerOrder> findByIdempotencyKey(String idempotencyKey) {
        return jpaRepository.findByIdempotencyKey(idempotencyKey).map(OrderJpaEntity::toDomain);
    }
}
