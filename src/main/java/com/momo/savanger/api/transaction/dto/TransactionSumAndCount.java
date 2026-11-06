package com.momo.savanger.api.transaction.dto;

import java.math.BigDecimal;
import java.util.List;

public record TransactionSumAndCount(
        BigDecimal sum,
        Long count,
        List<TransactionBasicDto> transactions
) {
}
