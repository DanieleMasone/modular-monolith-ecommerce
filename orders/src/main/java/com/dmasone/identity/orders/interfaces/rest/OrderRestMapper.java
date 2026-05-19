package com.dmasone.identity.orders.interfaces.rest;

import com.dmasone.identity.orders.application.OrderResponse;
import com.dmasone.identity.orders.application.PlaceOrderCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper generated at compile time for the order REST boundary.
 */
@Mapper
public interface OrderRestMapper {

    @Mapping(target = "productId", source = "request.productId")
    @Mapping(target = "quantity", source = "request.quantity")
    @Mapping(target = "idempotencyKey", source = "idempotencyKey")
    PlaceOrderCommand toCommand(PlaceOrderRequest request, String idempotencyKey);

    OrderDto toDto(OrderResponse response);
}
