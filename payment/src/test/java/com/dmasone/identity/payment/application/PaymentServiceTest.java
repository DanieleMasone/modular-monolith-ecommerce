package com.dmasone.identity.payment.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.dmasone.identity.orders.application.events.OrderPlacedEvent;
import com.dmasone.identity.payment.domain.PaymentAttempt;
import com.dmasone.identity.payment.domain.PaymentRepository;
import com.dmasone.identity.payment.domain.PaymentStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PaymentServiceTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-05-18T08:10:00Z"), ZoneOffset.UTC);

    private RecordingPaymentRepository paymentRepository;
    private RecordingPaymentGateway paymentGateway;
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentRepository = new RecordingPaymentRepository();
        paymentGateway = new RecordingPaymentGateway(PaymentStatus.AUTHORIZED);
        paymentService = new PaymentService(paymentRepository, paymentGateway, FIXED_CLOCK);
    }

    @Test
    void persistsAuthorizedPaymentAttemptForOrderEvent() {
        OrderPlacedEvent event = new OrderPlacedEvent(UUID.randomUUID(), 1L, 2);

        PaymentResult result = paymentService.authorize(event);

        assertThat(result.orderId()).isEqualTo(event.orderId());
        assertThat(result.status()).isEqualTo(PaymentStatus.AUTHORIZED);
        assertThat(result.requestedAt()).isEqualTo(FIXED_CLOCK.instant());
        assertThat(paymentGateway.authorizedEvent).isEqualTo(event);
        assertThat(paymentRepository.savedAttempt).isNotNull();
        assertThat(paymentRepository.savedAttempt.productId()).isEqualTo(1L);
        assertThat(paymentRepository.savedAttempt.quantity()).isEqualTo(2);
    }

    @Test
    void returnsExistingAttemptForDuplicateEvent() {
        OrderPlacedEvent event = new OrderPlacedEvent(UUID.randomUUID(), 1L, 2);
        PaymentAttempt existing = PaymentAttempt.create(
                UUID.randomUUID(),
                event.orderId(),
                1L,
                2,
                PaymentStatus.AUTHORIZED,
                FIXED_CLOCK.instant()
        );
        paymentRepository.existingAttempt = existing;

        PaymentResult result = paymentService.authorize(event);

        assertThat(result.id()).isEqualTo(existing.id());
        assertThat(paymentRepository.savedAttempt).isNull();
        assertThat(paymentGateway.authorizedEvent).isNull();
    }

    private static final class RecordingPaymentRepository implements PaymentRepository {

        private PaymentAttempt existingAttempt;
        private PaymentAttempt savedAttempt;

        @Override
        public PaymentAttempt save(PaymentAttempt paymentAttempt) {
            savedAttempt = paymentAttempt;
            return paymentAttempt;
        }

        @Override
        public Optional<PaymentAttempt> findByOrderId(UUID orderId) {
            return Optional.ofNullable(existingAttempt)
                    .filter(paymentAttempt -> paymentAttempt.orderId().equals(orderId));
        }
    }

    private static final class RecordingPaymentGateway extends SimulatedPaymentGateway {

        private final PaymentStatus status;
        private OrderPlacedEvent authorizedEvent;

        private RecordingPaymentGateway(PaymentStatus status) {
            this.status = status;
        }

        @Override
        public PaymentStatus authorize(OrderPlacedEvent event) {
            authorizedEvent = event;
            return status;
        }
    }
}
