package com.dmasone.identity.catalog.application;

/**
 * Public application service contract used by other modules to reserve stock.
 * Consumers do not receive repository access, preserving catalog ownership of
 * product state.
 */
public interface StockReservationService {

    /**
     * Reserve catalog stock for a product.
     *
     * @param productId catalog product id
     * @param quantity positive quantity to reserve
     */
    void reserveStock(Long productId, int quantity);
}
