package com.momo.savanger.api.transaction;

import com.momo.savanger.api.transaction.dto.CreateTransactionDto;
import com.momo.savanger.api.transaction.dto.EditTransactionDto;
import com.momo.savanger.api.transaction.dto.TransactionDto;
import com.momo.savanger.api.transaction.recurring.RecurringTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    Transaction toTransaction(CreateTransactionDto createCategoryDto);

    CreateTransactionDto toCreateTransactionDto(RecurringTransaction recurringTransaction);

    TransactionDto toTransactionDto(Transaction transaction);

    Transaction mergeIntoTransaction(EditTransactionDto dto,
            @MappingTarget Transaction transaction);
}
