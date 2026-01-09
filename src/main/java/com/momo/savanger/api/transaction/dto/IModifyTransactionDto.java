package com.momo.savanger.api.transaction.dto;

import com.momo.savanger.api.transaction.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface IModifyTransactionDto {

    TransactionType getType();

    BigDecimal getAmount();

    LocalDateTime getDateCreated();

    String getComment();

    Long getCategoryId();

    Long getBudgetId();

    List<Long> getTagIds();
}
