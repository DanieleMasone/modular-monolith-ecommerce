package com.dmasone.identity.catalog.domain;

import com.dmasone.identity.sharedkernel.domain.DomainException;

/**
 * Raised by catalog application services when callers reference an unknown
 * product id.
 */
public final class ProductNotFoundException extends DomainException {

    public ProductNotFoundException(Long productId) {
        super("PRODUCT_NOT_FOUND", "Product " + productId + " was not found");
    }
}
