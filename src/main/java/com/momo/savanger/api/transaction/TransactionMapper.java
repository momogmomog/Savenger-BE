package com.momo.savanger.api.transaction;

import com.momo.savanger.api.tag.Tag;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    Transaction toTransaction(CreateTransactionDto createCategoryDto);

    TransactionDto toTransactionDto(Transaction transaction);
}
