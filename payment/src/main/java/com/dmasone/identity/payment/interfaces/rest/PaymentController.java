package com.dmasone.identity.payment.interfaces.rest;

import com.dmasone.identity.payment.application.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.UUID;
import org.springframework.http.MediaType;
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

    @Operation(
            summary = "Find payment result",
            description = "Returns the payment attempt created by the order placed event listener."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment attempt found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PaymentDto.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Payment attempt not found", content = @Content)
    })
    @GetMapping("/{orderId}")
    public PaymentDto findByOrderId(@PathVariable UUID orderId) {
        return paymentRestMapper.toDto(paymentService.findByOrderId(orderId));
    }
}
