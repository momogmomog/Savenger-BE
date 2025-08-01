package com.momo.savanger.api.prepayment;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PrepaymentMapper {

    Prepayment toPrepayment(CreatePrepaymentDto dto);

    PrepaymentDto toPrepaymentDto(Prepayment prepayment);
}
