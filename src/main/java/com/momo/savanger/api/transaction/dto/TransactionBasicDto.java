package com.momo.savanger.api.transaction.dto;

import com.momo.savanger.api.transaction.TransactionType;
import java.math.BigDecimal;

public record TransactionBasicDto(Long id, BigDecimal amount, TransactionType type) {

}
