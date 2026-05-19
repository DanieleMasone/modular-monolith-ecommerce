package com.dmasone.identity.orders.interfaces.rest;

import com.dmasone.identity.orders.application.OrderQueryService;
import com.dmasone.identity.orders.application.OrderResponse;
import com.dmasone.identity.orders.application.PlaceOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.MediaType;
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
    private final OrderRestMapper orderRestMapper;

    public OrderController(
            PlaceOrderService placeOrderService,
            OrderQueryService orderQueryService,
            OrderRestMapper orderRestMapper
    ) {
        this.placeOrderService = placeOrderService;
        this.orderQueryService = orderQueryService;
        this.orderRestMapper = orderRestMapper;
    }

    @Operation(
            summary = "Place an order",
            description = "Reserves catalog stock, persists the order, and publishes an internal order placed event."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Order placed",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderDto.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Insufficient stock", content = @Content)
    })
    @PostMapping
    public ResponseEntity<OrderDto> placeOrder(@Valid @RequestBody PlaceOrderRequest request) {
        OrderResponse response = placeOrderService.placeOrder(
                orderRestMapper.toCommand(request)
        );
        OrderDto body = orderRestMapper.toDto(response);
        return ResponseEntity
                .created(URI.create("/api/orders/" + response.id()))
                .body(body);
    }

    @Operation(
            summary = "Find an order",
            description = "Returns a persisted order without exposing payment implementation details."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Order found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderDto.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
    })
    @GetMapping("/{id}")
    public OrderDto findOrder(@PathVariable UUID id) {
        return orderRestMapper.toDto(orderQueryService.findById(id));
    }
}
