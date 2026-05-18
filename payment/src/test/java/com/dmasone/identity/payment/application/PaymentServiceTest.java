package com.dmasone.identity.payment.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-05-18T08:10:00Z"), ZoneOffset.UTC);

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private SimulatedPaymentGateway paymentGateway;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(paymentRepository, paymentGateway, FIXED_CLOCK);
    }

    @Test
    void persistsAuthorizedPaymentAttemptForOrderEvent() {
        OrderPlacedEvent event = new OrderPlacedEvent(UUID.randomUUID(), 1L, 2);
        when(paymentRepository.findByOrderId(event.orderId())).thenReturn(Optional.empty());
        when(paymentGateway.authorize(event)).thenReturn(PaymentStatus.AUTHORIZED);
        when(paymentRepository.save(any(PaymentAttempt.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResult result = paymentService.authorize(event);

        assertThat(result.orderId()).isEqualTo(event.orderId());
        assertThat(result.status()).isEqualTo(PaymentStatus.AUTHORIZED);
        assertThat(result.requestedAt()).isEqualTo(FIXED_CLOCK.instant());

        ArgumentCaptor<PaymentAttempt> attemptCaptor = ArgumentCaptor.forClass(PaymentAttempt.class);
        verify(paymentRepository).save(attemptCaptor.capture());
        assertThat(attemptCaptor.getValue().productId()).isEqualTo(1L);
        assertThat(attemptCaptor.getValue().quantity()).isEqualTo(2);
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
        when(paymentRepository.findByOrderId(event.orderId())).thenReturn(Optional.of(existing));

        PaymentResult result = paymentService.authorize(event);

        assertThat(result.id()).isEqualTo(existing.id());
        verify(paymentRepository, never()).save(any(PaymentAttempt.class));
        verify(paymentGateway, never()).authorize(event);
    }
}
