package com.momo.savanger.api.debt;

import com.momo.savanger.api.revision.CreateRevisionDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DebtMapper {

    Debt toDebt(CreateDebtDto dto);

    DebtDto toDebtDto(Debt debt);
}
