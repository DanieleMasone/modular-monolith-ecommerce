package com.dmasone.identity.catalog.infrastructure.persistence;

import com.dmasone.identity.catalog.domain.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;

/**
 * Persistence representation of catalog products. The entity is kept in the
 * infrastructure package so JPA mapping choices do not leak into the product
 * stock rule.
 */
@Entity(name = "CatalogProduct")
@Table(name = "catalog_products")
public class ProductJpaEntity {

    @Id
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity;

    @Version
    private long version;

    protected ProductJpaEntity() {
    }

    public ProductJpaEntity(Long id, String sku, String name, BigDecimal price, int availableQuantity) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.price = price;
        this.availableQuantity = availableQuantity;
    }

    public void apply(Product product) {
        this.sku = product.sku();
        this.name = product.name();
        this.price = product.price();
        this.availableQuantity = product.availableQuantity();
    }

    public Long getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }
}
