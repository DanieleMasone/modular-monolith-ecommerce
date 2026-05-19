package com.dmasone.identity.orders.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository hidden behind the orders repository port.
 */
interface SpringDataOrderJpaRepository extends JpaRepository<OrderJpaEntity, UUID> {

    Optional<OrderJpaEntity> findByIdempotencyKey(String idempotencyKey);
}
