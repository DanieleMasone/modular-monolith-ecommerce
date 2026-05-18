package com.dmasone.identity.payment.interfaces.rest;

import com.dmasone.identity.payment.application.PaymentService;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Optional read endpoint for payment results, useful in tests and demos to
 * show that the event listener reacted to order placement.
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentRestMapper paymentRestMapper;

    public PaymentController(PaymentService paymentService, PaymentRestMapper paymentRestMapper) {
        this.paymentService = paymentService;
        this.paymentRestMapper = paymentRestMapper;
    }

    @GetMapping("/{orderId}")
    public PaymentDto findByOrderId(@PathVariable UUID orderId) {
        return paymentRestMapper.toDto(paymentService.findByOrderId(orderId));
    }
}
