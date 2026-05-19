package com.dmasone.identity.orders.application;

import com.dmasone.identity.orders.domain.CustomerOrder;

/**
 * Result of the placement use case. It distinguishes a newly created order
 * from an idempotent replay so the REST layer can return precise HTTP
 * semantics without leaking transport concepts into the domain aggregate.
 */
public record PlaceOrderResult(OrderResponse order, boolean replayed) {

    public static PlaceOrderResult placed(CustomerOrder order) {
        return new PlaceOrderResult(OrderResponse.from(order), false);
    }

    public static PlaceOrderResult replayed(CustomerOrder order) {
        return new PlaceOrderResult(OrderResponse.from(order), true);
    }
}
