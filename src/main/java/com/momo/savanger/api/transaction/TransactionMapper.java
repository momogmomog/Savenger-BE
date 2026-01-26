package com.momo.savanger.api.transaction;

import com.momo.savanger.api.transaction.dto.CreateTransactionDto;
import com.momo.savanger.api.transaction.dto.CreateTransactionServiceDto;
import com.momo.savanger.api.transaction.dto.EditTransactionDto;
import com.momo.savanger.api.transaction.dto.TransactionDtoDetailed;
import com.momo.savanger.api.transaction.dto.TransactionDtoSimple;
import com.momo.savanger.api.transaction.dto.TransactionSearchResponseDto;
import com.momo.savanger.api.transaction.recurring.RecurringTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    CreateTransactionServiceDto toCreateServiceDto(CreateTransactionDto createCategoryDto);

    CreateTransactionServiceDto toCreateServiceDto(RecurringTransaction recurringTransaction);

    Transaction toTransaction(CreateTransactionServiceDto createCategoryDto);

    TransactionSearchResponseDto toTransactionDto(Transaction transaction);

    TransactionDtoSimple toSimpleTransactionDto(Transaction transaction);

    TransactionDtoDetailed toTransactionDtoDetailed(Transaction transaction);

    Transaction mergeIntoTransaction(EditTransactionDto dto,
            @MappingTarget Transaction transaction);
}
