package com.dmasone.identity.catalog.interfaces.rest;

import com.dmasone.identity.catalog.application.ProductQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final ProductRestMapper productRestMapper;

    public ProductController(ProductQueryService productQueryService, ProductRestMapper productRestMapper) {
        this.productQueryService = productQueryService;
        this.productRestMapper = productRestMapper;
    }

    @Operation(
            summary = "List products",
            description = "Returns the catalog read model used by customers when selecting products."
    )
    @ApiResponse(responseCode = "200", description = "Products returned")
    @GetMapping
    public List<ProductResponse> products() {
        return productRestMapper.toResponses(productQueryService.findAll());
    }

    @Operation(
            summary = "Find a product",
            description = "Returns one catalog read model projection by product id."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product found",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ProductResponse product(@PathVariable Long id) {
        return productRestMapper.toResponse(productQueryService.findById(id));
    }
}
