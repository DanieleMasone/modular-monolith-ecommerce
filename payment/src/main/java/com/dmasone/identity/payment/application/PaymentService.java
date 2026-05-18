package com.dmasone.identity.payment.application;

import com.dmasone.identity.orders.application.events.OrderPlacedEvent;
import com.dmasone.identity.payment.domain.PaymentAttempt;
import com.dmasone.identity.payment.domain.PaymentNotFoundException;
import com.dmasone.identity.payment.domain.PaymentRepository;
import com.dmasone.identity.payment.domain.PaymentStatus;
import java.time.Clock;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Payment use-case service. It reacts to order placement events, records a
 * simulated authorization result, and keeps the operation idempotent per order.
 */
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final SimulatedPaymentGateway paymentGateway;
    private final Clock clock;

    public PaymentService(PaymentRepository paymentRepository, SimulatedPaymentGateway paymentGateway, Clock clock) {
        this.paymentRepository = paymentRepository;
        this.paymentGateway = paymentGateway;
        this.clock = clock;
    }

    @Transactional
    public PaymentResult authorize(OrderPlacedEvent event) {
        return paymentRepository.findByOrderId(event.orderId())
                .map(PaymentResult::from)
                .orElseGet(() -> createPaymentAttempt(event));
    }

    @Transactional(readOnly = true)
    public PaymentResult findByOrderId(UUID orderId) {
        return paymentRepository.findByOrderId(orderId)
                .map(PaymentResult::from)
                .orElseThrow(() -> new PaymentNotFoundException(orderId));
    }

    private PaymentResult createPaymentAttempt(OrderPlacedEvent event) {
        PaymentStatus status = paymentGateway.authorize(event);
        PaymentAttempt paymentAttempt = PaymentAttempt.create(
                UUID.randomUUID(),
                event.orderId(),
                event.productId(),
                event.quantity(),
                status,
                clock.instant()
        );
        return PaymentResult.from(paymentRepository.save(paymentAttempt));
    }
}
