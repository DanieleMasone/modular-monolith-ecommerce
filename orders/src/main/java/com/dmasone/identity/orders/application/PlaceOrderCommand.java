package com.dmasone.identity.orders.application;

/**
 * Command object for the order placement use case. REST request models are
 * translated into this command so validation and use-case orchestration stay
 * independent from HTTP details.
 */
public record PlaceOrderCommand(Long productId, int quantity) {
}
