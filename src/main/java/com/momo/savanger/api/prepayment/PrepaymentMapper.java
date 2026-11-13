package com.momo.savanger.api.prepayment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PrepaymentMapper {

    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "completed", ignore = true)
    @Mapping(target = "remainingAmount", ignore = true)
    Prepayment toPrepayment(CreatePrepaymentDto dto);

    PrepaymentDto toPrepaymentDto(Prepayment prepayment);

    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "completed", ignore = true)
    @Mapping(target = "remainingAmount", ignore = true)
    void mergeIntoPrepayment(
            CreatePrepaymentDto dto,
            @MappingTarget Prepayment prepayment
    );
}
