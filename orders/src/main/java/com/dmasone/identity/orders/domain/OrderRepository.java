package com.dmasone.identity.orders.domain;

import java.util.Optional;
import java.util.UUID;

/**
 * Persistence port for the order aggregate. Application services depend on
 * this port rather than Spring Data repositories.
 */
public interface OrderRepository {

    CustomerOrder save(CustomerOrder order);

    Optional<CustomerOrder> findById(UUID orderId);
}
