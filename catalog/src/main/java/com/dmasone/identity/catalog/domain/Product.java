package com.dmasone.identity.catalog.domain;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Catalog aggregate that owns stock reservation rules. The JPA entity is kept
 * outside the domain package so the rule can be tested without persistence
 * concerns and reused by application services.
 */
public final class Product {

    private final Long id;
    private final String sku;
    private final String name;
    private final BigDecimal price;
    private int availableQuantity;

    private Product(Long id, String sku, String name, BigDecimal price, int availableQuantity) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.sku = Objects.requireNonNull(sku, "sku must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.price = Objects.requireNonNull(price, "price must not be null");
        if (availableQuantity < 0) {
            throw new IllegalArgumentException("availableQuantity must not be negative");
        }
        this.availableQuantity = availableQuantity;
    }

    public static Product restore(Long id, String sku, String name, BigDecimal price, int availableQuantity) {
        return new Product(id, sku, name, price, availableQuantity);
    }

    public void reserve(int quantity) {
        if (quantity <= 0) {
            throw new InvalidStockReservationException(quantity);
        }
        if (availableQuantity < quantity) {
            throw new InsufficientStockException(id);
        }
        availableQuantity -= quantity;
    }

    public Long id() {
        return id;
    }

    public String sku() {
        return sku;
    }

    public String name() {
        return name;
    }

    public BigDecimal price() {
        return price;
    }

    public int availableQuantity() {
        return availableQuantity;
    }
}
