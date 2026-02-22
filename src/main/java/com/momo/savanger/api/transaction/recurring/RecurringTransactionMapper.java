package com.momo.savanger.api.transaction.recurring;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RecurringTransactionMapper {

    @Mapping(target = "nextDate", ignore = true)
    RecurringTransaction toRecurringTransaction(CreateRecurringTransactionDto dto);

    RecurringTransactionDto toRecurringTransactionDto(RecurringTransaction transaction);

    void mergeIntoRecurringTransaction(
            CreateRecurringTransactionDto dto,
            @MappingTarget RecurringTransaction transaction
    );
}
