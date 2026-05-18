package com.dmasone.identity.orders.application;

import com.dmasone.identity.catalog.application.StockReservationService;
import com.dmasone.identity.orders.application.events.OrderPlacedEvent;
import com.dmasone.identity.orders.domain.CustomerOrder;
import com.dmasone.identity.orders.domain.OrderRepository;
import com.dmasone.identity.sharedkernel.domain.EventPublisher;
import java.time.Clock;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Primary order placement use case. It coordinates module boundaries by asking
 * catalog to reserve stock through an application service, saving the order,
 * and then publishing an internal event for payment.
 */
@Service
public class PlaceOrderService {

    private final StockReservationService stockReservationService;
    private final OrderRepository orderRepository;
    private final EventPublisher eventPublisher;
    private final Clock clock;

    public PlaceOrderService(
            StockReservationService stockReservationService,
            OrderRepository orderRepository,
            EventPublisher eventPublisher,
            Clock clock
    ) {
        this.stockReservationService = stockReservationService;
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public OrderResponse placeOrder(PlaceOrderCommand command) {
        CustomerOrder order = CustomerOrder.place(
                UUID.randomUUID(),
                command.productId(),
                command.quantity(),
                clock.instant()
        );

        stockReservationService.reserveStock(order.productId(), order.quantity());

        CustomerOrder saved = orderRepository.save(order);
        eventPublisher.publish(new OrderPlacedEvent(saved.id(), saved.productId(), saved.quantity()));

        return OrderResponse.from(saved);
    }
}
