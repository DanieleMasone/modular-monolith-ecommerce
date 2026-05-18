package com.dmasone.identity.catalog.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ProductTest {

    @Test
    void reservesAvailableStock() {
        Product product = Product.restore(1L, "SKU-1", "Keyboard", BigDecimal.valueOf(149), 10);

        product.reserve(3);

        assertThat(product.availableQuantity()).isEqualTo(7);
    }

    @Test
    void rejectsInsufficientStock() {
        Product product = Product.restore(1L, "SKU-1", "Keyboard", BigDecimal.valueOf(149), 2);

        assertThatThrownBy(() -> product.reserve(3))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessage("Insufficient stock for product 1");
        assertThat(product.availableQuantity()).isEqualTo(2);
    }

    @Test
    void rejectsNonPositiveReservationQuantity() {
        Product product = Product.restore(1L, "SKU-1", "Keyboard", BigDecimal.valueOf(149), 2);

        assertThatThrownBy(() -> product.reserve(0))
                .isInstanceOf(InvalidStockReservationException.class)
                .hasMessage("Quantity must be greater than zero: 0");
    }
}
