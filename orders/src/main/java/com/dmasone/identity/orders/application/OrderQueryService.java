package com.dmasone.identity.orders.application;

import com.dmasone.identity.orders.domain.OrderNotFoundException;
import com.dmasone.identity.orders.domain.OrderRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Query facade for order retrieval. Keeping this separate from placement makes
 * the command and read paths explicit without introducing a second datastore.
 */
@Service
public class OrderQueryService {

    private final OrderRepository orderRepository;

    public OrderQueryService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public OrderResponse findById(UUID orderId) {
        return orderRepository.findById(orderId)
                .map(OrderResponse::from)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}
