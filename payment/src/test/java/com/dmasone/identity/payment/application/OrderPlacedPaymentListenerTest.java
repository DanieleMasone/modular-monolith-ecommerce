package com.dmasone.identity.payment.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.dmasone.identity.orders.application.events.OrderPlacedEvent;
import com.dmasone.identity.payment.domain.PaymentAttempt;
import com.dmasone.identity.payment.domain.PaymentRepository;
import java.time.Clock;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OrderPlacedPaymentListenerTest {

    @Test
    void delegatesOrderPlacedEventToPaymentService() {
        RecordingPaymentService paymentService = new RecordingPaymentService();
        OrderPlacedPaymentListener listener = new OrderPlacedPaymentListener(paymentService);
        OrderPlacedEvent event = new OrderPlacedEvent(UUID.randomUUID(), 1L, 2);

        listener.onOrderPlaced(event);

        assertThat(paymentService.handledEvent).isEqualTo(event);
    }

    private static final class RecordingPaymentService extends PaymentService {

        private OrderPlacedEvent handledEvent;

        private RecordingPaymentService() {
            super(new UnusedPaymentRepository(), new SimulatedPaymentGateway(), Clock.systemUTC());
        }

        @Override
        public PaymentResult authorize(OrderPlacedEvent event) {
            handledEvent = event;
            return null;
        }
    }

    private static final class UnusedPaymentRepository implements PaymentRepository {

        @Override
        public PaymentAttempt save(PaymentAttempt paymentAttempt) {
            throw new UnsupportedOperationException("Not used by this listener test");
        }

        @Override
        public Optional<PaymentAttempt> findByOrderId(UUID orderId) {
            throw new UnsupportedOperationException("Not used by this listener test");
        }
    }
}
