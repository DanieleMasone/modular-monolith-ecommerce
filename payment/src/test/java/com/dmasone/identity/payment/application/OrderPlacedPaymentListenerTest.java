package com.dmasone.identity.payment.application;

import static org.mockito.Mockito.verify;

import com.dmasone.identity.orders.application.events.OrderPlacedEvent;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderPlacedPaymentListenerTest {

    @Mock
    private PaymentService paymentService;

    @Test
    void delegatesOrderPlacedEventToPaymentService() {
        OrderPlacedPaymentListener listener = new OrderPlacedPaymentListener(paymentService);
        OrderPlacedEvent event = new OrderPlacedEvent(UUID.randomUUID(), 1L, 2);

        listener.onOrderPlaced(event);

        verify(paymentService).authorize(event);
    }
}
