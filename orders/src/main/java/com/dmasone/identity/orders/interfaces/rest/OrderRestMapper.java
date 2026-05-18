package com.dmasone.identity.orders.interfaces.rest;

import com.dmasone.identity.orders.application.OrderResponse;
import com.dmasone.identity.orders.application.PlaceOrderCommand;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper generated at compile time for the order REST boundary.
 */
@Mapper
public interface OrderRestMapper {

    PlaceOrderCommand toCommand(PlaceOrderRequest request);

    OrderDto toDto(OrderResponse response);
}
