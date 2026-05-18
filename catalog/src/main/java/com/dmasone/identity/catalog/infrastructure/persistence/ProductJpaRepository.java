package com.dmasone.identity.catalog.infrastructure.persistence;

import com.dmasone.identity.catalog.application.ProductView;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data repository owned by catalog infrastructure. Other modules call
 * catalog application services instead of depending on this repository.
 */
public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from CatalogProduct p where p.id = :id")
    Optional<ProductJpaEntity> findByIdForUpdate(@Param("id") Long id);

    @Query("""
            select new com.dmasone.identity.catalog.application.ProductView(
                p.id,
                p.sku,
                p.name,
                p.price,
                p.availableQuantity
            )
            from CatalogProduct p
            order by p.id
            """)
    List<ProductView> findAllProductViews();

    @Query("""
            select new com.dmasone.identity.catalog.application.ProductView(
                p.id,
                p.sku,
                p.name,
                p.price,
                p.availableQuantity
            )
            from CatalogProduct p
            where p.id = :id
            """)
    Optional<ProductView> findProductViewById(@Param("id") Long id);
}
