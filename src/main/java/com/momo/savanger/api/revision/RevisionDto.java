package com.momo.savanger.api.revision;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class RevisionDto {

    private Long id;

    private LocalDateTime revisionDate;

    private LocalDateTime budgetStartDate;

    private BigDecimal balance;

    private BigDecimal budgetCap;

    private BigDecimal expensesAmount;

    private BigDecimal earningsAmount;

    private Boolean autoRevise;

    private String comment;

    private Long budgetId;
}
