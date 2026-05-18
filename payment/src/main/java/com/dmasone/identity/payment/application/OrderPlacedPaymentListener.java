package com.dmasone.identity.payment.application;

import com.dmasone.identity.orders.application.events.OrderPlacedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Event listener that starts payment processing after an order transaction has
 * committed. This preserves module decoupling and avoids charging for orders
 * that failed to persist.
 */
@Component
public class OrderPlacedPaymentListener {

    private final PaymentService paymentService;

    public OrderPlacedPaymentListener(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderPlaced(OrderPlacedEvent event) {
        paymentService.authorize(event);
    }
}
