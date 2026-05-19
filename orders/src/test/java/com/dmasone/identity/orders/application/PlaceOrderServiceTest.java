package com.dmasone.identity.orders.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.dmasone.identity.catalog.application.StockReservationService;
import com.dmasone.identity.orders.application.events.OrderPlacedEvent;
import com.dmasone.identity.orders.domain.CustomerOrder;
import com.dmasone.identity.orders.domain.IdempotencyKeyConflictException;
import com.dmasone.identity.orders.domain.InvalidOrderException;
import com.dmasone.identity.orders.domain.OrderRepository;
import com.dmasone.identity.orders.domain.OrderStatus;
import com.dmasone.identity.sharedkernel.domain.DomainEvent;
import com.dmasone.identity.sharedkernel.domain.EventPublisher;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlaceOrderServiceTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-05-18T08:00:00Z"), ZoneOffset.UTC);

    private RecordingStockReservationService stockReservationService;
    private RecordingOrderRepository orderRepository;
    private RecordingEventPublisher eventPublisher;
    private PlaceOrderService placeOrderService;

    @BeforeEach
    void setUp() {
        stockReservationService = new RecordingStockReservationService();
        orderRepository = new RecordingOrderRepository();
        eventPublisher = new RecordingEventPublisher();
        placeOrderService = new PlaceOrderService(stockReservationService, orderRepository, eventPublisher, FIXED_CLOCK);
    }

    @Test
    void placesOrderReservesStockAndPublishesEvent() {
        PlaceOrderResult result = placeOrderService.placeOrder(new PlaceOrderCommand(1L, 2, null));
        OrderResponse response = result.order();

        assertThat(result.replayed()).isFalse();
        assertThat(response.productId()).isEqualTo(1L);
        assertThat(response.quantity()).isEqualTo(2);
        assertThat(response.status()).isEqualTo(OrderStatus.PLACED);
        assertThat(response.createdAt()).isEqualTo(FIXED_CLOCK.instant());
        assertThat(stockReservationService.reservedProductId).isEqualTo(1L);
        assertThat(stockReservationService.reservedQuantity).isEqualTo(2);
        assertThat(orderRepository.savedOrder).isNotNull();

        assertThat(eventPublisher.publishedEvents)
                .singleElement()
                .isInstanceOfSatisfying(OrderPlacedEvent.class, event -> {
                    assertThat(event.orderId()).isEqualTo(response.id());
                    assertThat(event.productId()).isEqualTo(1L);
                    assertThat(event.quantity()).isEqualTo(2);
                });
    }

    @Test
    void rejectsInvalidQuantityBeforeReservingStock() {
        assertThatThrownBy(() -> placeOrderService.placeOrder(new PlaceOrderCommand(1L, 0, null)))
                .isInstanceOf(InvalidOrderException.class)
                .hasMessage("Order quantity must be greater than zero");

        assertThat(stockReservationService.callCount).isZero();
        assertThat(orderRepository.savedOrder).isNull();
        assertThat(eventPublisher.publishedEvents).isEmpty();
    }

    @Test
    void returnsExistingOrderForIdempotentReplay() {
        CustomerOrder existing = CustomerOrder.place(
                UUID.randomUUID(),
                1L,
                2,
                FIXED_CLOCK.instant(),
                "checkout-retry-1"
        );
        orderRepository.existingOrder = existing;

        PlaceOrderResult result = placeOrderService.placeOrder(
                new PlaceOrderCommand(1L, 2, " checkout-retry-1 ")
        );

        assertThat(result.replayed()).isTrue();
        assertThat(result.order().id()).isEqualTo(existing.id());
        assertThat(stockReservationService.callCount).isZero();
        assertThat(orderRepository.savedOrder).isNull();
        assertThat(eventPublisher.publishedEvents).isEmpty();
    }

    @Test
    void rejectsIdempotencyKeyReuseForDifferentRequest() {
        CustomerOrder existing = CustomerOrder.place(
                UUID.randomUUID(),
                1L,
                2,
                FIXED_CLOCK.instant(),
                "checkout-retry-1"
        );
        orderRepository.existingOrder = existing;

        assertThatThrownBy(() -> placeOrderService.placeOrder(new PlaceOrderCommand(2L, 2, "checkout-retry-1")))
                .isInstanceOf(IdempotencyKeyConflictException.class)
                .hasMessage("Idempotency key 'checkout-retry-1' was already used for a different order request");

        assertThat(stockReservationService.callCount).isZero();
        assertThat(orderRepository.savedOrder).isNull();
        assertThat(eventPublisher.publishedEvents).isEmpty();
    }

    private static final class RecordingStockReservationService implements StockReservationService {

        private int callCount;
        private Long reservedProductId;
        private Integer reservedQuantity;

        @Override
        public void reserveStock(Long productId, int quantity) {
            callCount++;
            reservedProductId = productId;
            reservedQuantity = quantity;
        }
    }

    private static final class RecordingOrderRepository implements OrderRepository {

        private CustomerOrder existingOrder;
        private CustomerOrder savedOrder;

        @Override
        public CustomerOrder save(CustomerOrder order) {
            savedOrder = order;
            return order;
        }

        @Override
        public Optional<CustomerOrder> findById(UUID orderId) {
            return Optional.ofNullable(savedOrder)
                    .filter(order -> order.id().equals(orderId));
        }

        @Override
        public Optional<CustomerOrder> findByIdempotencyKey(String idempotencyKey) {
            return Optional.ofNullable(existingOrder)
                    .filter(order -> idempotencyKey.equals(order.idempotencyKey()));
        }
    }

    private static final class RecordingEventPublisher implements EventPublisher {

        private final List<DomainEvent> publishedEvents = new ArrayList<>();

        @Override
        public void publish(DomainEvent event) {
            publishedEvents.add(event);
        }
    }
}
