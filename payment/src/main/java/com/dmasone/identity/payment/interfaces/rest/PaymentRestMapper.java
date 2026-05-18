package com.dmasone.identity.payment.interfaces.rest;

import com.dmasone.identity.payment.application.PaymentResult;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper generated at compile time for payment REST responses.
 */
@Mapper
public interface PaymentRestMapper {

    PaymentDto toDto(PaymentResult paymentResult);
}
