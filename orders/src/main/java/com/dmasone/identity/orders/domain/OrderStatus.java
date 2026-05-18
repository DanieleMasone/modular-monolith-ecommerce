package com.dmasone.identity.orders.domain;

/**
 * Lifecycle state owned by the orders module. Payment may react to an order,
 * but it does not directly mutate this aggregate in the first version.
 */
public enum OrderStatus {
    PLACED
}
