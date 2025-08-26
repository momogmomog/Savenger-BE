package com.momo.savanger.api.prepayment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class PrepaymentDto {

    private Long id;

    private BigDecimal amount;

    private String name;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    private LocalDateTime paidUntil;

    private Boolean completed;

    private BigDecimal remainingAmount;

    private Long budgetId;

}
