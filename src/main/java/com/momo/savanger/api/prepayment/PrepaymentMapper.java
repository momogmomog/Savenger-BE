package com.momo.savanger.api.prepayment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PrepaymentMapper {

    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "completed", ignore = true)
    @Mapping(target = "remainingAmount", ignore = true)
    Prepayment toPrepayment(CreatePrepaymentDto dto);

    PrepaymentDto toPrepaymentDto(Prepayment prepayment);
}
