package com.dmasone.identity.payment.application;

import com.dmasone.identity.orders.application.events.OrderPlacedEvent;
import com.dmasone.identity.payment.domain.PaymentStatus;
import org.springframework.stereotype.Component;

/**
 * Deterministic stand-in for an external payment provider. Keeping this as an
 * application component makes the boundary explicit without adding network
 * calls or fake distributed infrastructure.
 */
@Component
public class SimulatedPaymentGateway {

    public PaymentStatus authorize(OrderPlacedEvent event) {
        return event.quantity() > 0 ? PaymentStatus.AUTHORIZED : PaymentStatus.DECLINED;
    }
}
