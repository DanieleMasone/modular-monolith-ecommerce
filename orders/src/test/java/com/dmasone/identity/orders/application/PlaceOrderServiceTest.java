package com.dmasone.identity.orders.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.dmasone.identity.catalog.application.StockReservationService;
import com.dmasone.identity.orders.application.events.OrderPlacedEvent;
import com.dmasone.identity.orders.domain.CustomerOrder;
import com.dmasone.identity.orders.domain.InvalidOrderException;
import com.dmasone.identity.orders.domain.OrderRepository;
import com.dmasone.identity.orders.domain.OrderStatus;
import com.dmasone.identity.sharedkernel.domain.DomainEvent;
import com.dmasone.identity.sharedkernel.domain.EventPublisher;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlaceOrderServiceTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-05-18T08:00:00Z"), ZoneOffset.UTC);

    @Mock
    private StockReservationService stockReservationService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private EventPublisher eventPublisher;

    private PlaceOrderService placeOrderService;

    @BeforeEach
    void setUp() {
        placeOrderService = new PlaceOrderService(stockReservationService, orderRepository, eventPublisher, FIXED_CLOCK);
    }

    @Test
    void placesOrderReservesStockAndPublishesEvent() {
        when(orderRepository.save(any(CustomerOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = placeOrderService.placeOrder(new PlaceOrderCommand(1L, 2));

        assertThat(response.productId()).isEqualTo(1L);
        assertThat(response.quantity()).isEqualTo(2);
        assertThat(response.status()).isEqualTo(OrderStatus.PLACED);
        assertThat(response.createdAt()).isEqualTo(FIXED_CLOCK.instant());
        verify(stockReservationService).reserveStock(1L, 2);

        ArgumentCaptor<DomainEvent> eventCaptor = ArgumentCaptor.forClass(DomainEvent.class);
        verify(eventPublisher).publish(eventCaptor.capture());
        assertThat(eventCaptor.getValue())
                .isInstanceOfSatisfying(OrderPlacedEvent.class, event -> {
                    assertThat(event.orderId()).isEqualTo(response.id());
                    assertThat(event.productId()).isEqualTo(1L);
                    assertThat(event.quantity()).isEqualTo(2);
                });
    }

    @Test
    void rejectsInvalidQuantityBeforeReservingStock() {
        assertThatThrownBy(() -> placeOrderService.placeOrder(new PlaceOrderCommand(1L, 0)))
                .isInstanceOf(InvalidOrderException.class)
                .hasMessage("Order quantity must be greater than zero");

        verifyNoInteractions(stockReservationService, orderRepository, eventPublisher);
    }
}
