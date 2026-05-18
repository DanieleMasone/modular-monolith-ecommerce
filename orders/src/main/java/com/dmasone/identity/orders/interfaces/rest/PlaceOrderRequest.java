package com.dmasone.identity.orders.interfaces.rest;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * HTTP request model for order placement. Validation failures are converted by
 * the application-level REST exception handler into stable error responses.
 */
public record PlaceOrderRequest(
        @NotNull Long productId,
        @NotNull @Min(1) Integer quantity
) {
}
