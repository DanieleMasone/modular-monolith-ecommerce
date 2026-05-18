package com.dmasone.identity.catalog.application;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Read projection returned by catalog queries. It deliberately contains only
 * data needed by API callers and cache entries, not the write-side aggregate.
 */
public record ProductView(
        Long id,
        String sku,
        String name,
        BigDecimal price,
        int availableQuantity
) implements Serializable {
}
