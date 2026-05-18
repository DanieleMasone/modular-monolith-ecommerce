package com.dmasone.identity.catalog.infrastructure.persistence;

import com.dmasone.identity.catalog.domain.Product;

/**
 * Translation layer between the JPA entity and the stock-owning domain model.
 */
public final class ProductMapper {

    private ProductMapper() {
    }

    public static Product toDomain(ProductJpaEntity entity) {
        return Product.restore(
                entity.getId(),
                entity.getSku(),
                entity.getName(),
                entity.getPrice(),
                entity.getAvailableQuantity()
        );
    }
}
