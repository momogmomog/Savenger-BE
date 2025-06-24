package com.momo.savanger.api.debt;

import com.momo.savanger.api.revision.CreateRevisionDto;
import com.momo.savanger.api.transaction.Transaction;
import com.momo.savanger.api.transaction.dto.EditTransactionDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DebtMapper {

    Debt toDebt(CreateDebtDto dto);

    DebtDto toDebtDto(Debt debt);

    Debt mergeIntoDebt(CreateDebtDto dto,@MappingTarget Debt debt);
}
