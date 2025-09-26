package com.momo.savanger.api.transaction.recurring;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RecurringTransactionMapper {

    @Mapping(target = "nextDate", ignore = true)
    RecurringTransaction ToRecurringTransaction(CreateRecurringTransactionDto dto);

    RecurringTransactionDto toRecurringTransactionDto(RecurringTransaction transaction);
}
