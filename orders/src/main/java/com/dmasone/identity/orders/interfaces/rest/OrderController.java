package com.dmasone.identity.orders.interfaces.rest;

import com.dmasone.identity.orders.application.OrderQueryService;
import com.dmasone.identity.orders.application.OrderResponse;
import com.dmasone.identity.orders.application.PlaceOrderCommand;
import com.dmasone.identity.orders.application.PlaceOrderService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Order API exposing placement and lookup workflows. The controller delegates
 * to use-case services and does not reach into catalog or payment internals.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final PlaceOrderService placeOrderService;
    private final OrderQueryService orderQueryService;

    public OrderController(PlaceOrderService placeOrderService, OrderQueryService orderQueryService) {
        this.placeOrderService = placeOrderService;
        this.orderQueryService = orderQueryService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody PlaceOrderRequest request) {
        OrderResponse response = placeOrderService.placeOrder(
                new PlaceOrderCommand(request.productId(), request.quantity())
        );
        return ResponseEntity
                .created(URI.create("/api/orders/" + response.id()))
                .body(response);
    }

    @GetMapping("/{id}")
    public OrderResponse findOrder(@PathVariable UUID id) {
        return orderQueryService.findById(id);
    }
}
