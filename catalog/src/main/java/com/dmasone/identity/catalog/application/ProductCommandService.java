package com.dmasone.identity.catalog.application;

import com.dmasone.identity.catalog.domain.Product;
import com.dmasone.identity.catalog.domain.ProductNotFoundException;
import com.dmasone.identity.catalog.infrastructure.persistence.ProductJpaEntity;
import com.dmasone.identity.catalog.infrastructure.persistence.ProductJpaRepository;
import com.dmasone.identity.catalog.infrastructure.persistence.ProductMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Write-side catalog service. It is intentionally narrow: other modules can
 * reserve stock, but cannot update catalog rows or bypass the domain rule.
 */
@Service
public class ProductCommandService implements StockReservationService {

    private final ProductJpaRepository productRepository;

    public ProductCommandService(ProductJpaRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"catalogProducts", "catalogProduct"}, allEntries = true)
    public void reserveStock(Long productId, int quantity) {
        ProductJpaEntity entity = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        Product product = ProductMapper.toDomain(entity);
        product.reserve(quantity);
        entity.apply(product);
    }
}
