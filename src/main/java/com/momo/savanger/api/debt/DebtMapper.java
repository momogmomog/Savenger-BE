package com.momo.savanger.api.debt;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DebtMapper {

    Debt toDebt(CreateDebtDto dto);

    DebtDto toDebtDto(Debt debt);

    void mergeIntoDebt(CreateDebtDto dto, @MappingTarget Debt debt);
}
