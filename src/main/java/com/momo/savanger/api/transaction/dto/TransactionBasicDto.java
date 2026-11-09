package com.momo.savanger.api.transaction.dto;

import com.momo.savanger.api.transaction.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionBasicDto(
        Long id,
        BigDecimal amount,
        TransactionType type,
        LocalDateTime dateCreated,
        Boolean revised) {

}
