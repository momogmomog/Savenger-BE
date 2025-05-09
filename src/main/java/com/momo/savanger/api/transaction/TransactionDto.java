package com.momo.savanger.api.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class TransactionDto {

    private Long id;

    private TransactionType type;

    private BigDecimal amount;

    private LocalDateTime date;

    private String comment;

    private Boolean revised;

    private Long userId;

    private Long categoryId;

    private Long budgetId;
}
