package com.dmasone.identity.catalog.application;

import com.dmasone.identity.catalog.domain.ProductNotFoundException;
import com.dmasone.identity.catalog.infrastructure.persistence.ProductJpaRepository;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Query-side facade for catalog read operations. The service returns immutable
 * projections and uses Redis through Spring Cache when the application is run
 * with the provided infrastructure.
 */
@Service
public class ProductQueryService {

    private final ProductJpaRepository productRepository;

    public ProductQueryService(ProductJpaRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "catalogProducts", key = "'all'")
    public List<ProductView> findAll() {
        return productRepository.findAllProductViews();
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "catalogProduct", key = "#productId")
    public ProductView findById(Long productId) {
        return productRepository.findProductViewById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }
}
