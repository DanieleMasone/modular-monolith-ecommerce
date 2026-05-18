package com.dmasone.identity.catalog.interfaces.rest;

import com.dmasone.identity.catalog.application.ProductQueryService;
import com.dmasone.identity.catalog.application.ProductView;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Read-only product API. Catalog writes remain behind application services so
 * stock mutations are driven by explicit use cases rather than generic CRUD.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductQueryService productQueryService;

    public ProductController(ProductQueryService productQueryService) {
        this.productQueryService = productQueryService;
    }

    @GetMapping
    public List<ProductView> products() {
        return productQueryService.findAll();
    }
}
