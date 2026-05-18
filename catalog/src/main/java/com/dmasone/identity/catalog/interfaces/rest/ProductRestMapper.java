package com.dmasone.identity.catalog.interfaces.rest;

import com.dmasone.identity.catalog.application.ProductView;
import java.util.List;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper generated at compile time for catalog REST responses.
 */
@Mapper
public interface ProductRestMapper {

    ProductResponse toResponse(ProductView productView);

    List<ProductResponse> toResponses(List<ProductView> productViews);
}
