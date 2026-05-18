package com.dmasone.identity.catalog.interfaces.rest;

import java.math.BigDecimal;

/**
 * HTTP representation of a catalog product. It is separated from the catalog
 * read projection so REST shape can evolve without changing application
 * services.
 */
public record ProductResponse(
        Long id,
        String sku,
        String name,
        BigDecimal price,
        int availableQuantity
) {
}
