package com.momo.savanger.api.transaction;

import com.momo.savanger.api.transaction.dto.CreateTransactionDto;
import com.momo.savanger.api.transaction.dto.EditTransactionDto;
import com.momo.savanger.api.transaction.dto.TransactionDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    Transaction toTransaction(CreateTransactionDto createCategoryDto);

    Transaction toTransaction(EditTransactionDto editTransactionDto);

    TransactionDto toTransactionDto(Transaction transaction);
}
