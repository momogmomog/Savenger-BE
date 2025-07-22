package com.momo.savanger.api.transaction.recurring;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RecurringTransactionMapper {

    RecurringTransaction ToRecurringTransaction(CreateRecurringTransactionDto dto);
}
