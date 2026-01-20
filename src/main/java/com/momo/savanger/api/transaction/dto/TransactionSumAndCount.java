package com.momo.savanger.api.transaction.dto;

import java.math.BigDecimal;

public record TransactionSumAndCount(BigDecimal sum, Long count) {

}
