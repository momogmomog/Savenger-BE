package com.momo.savanger.api.analytics.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsSummaryDto<T> {

    private BigDecimal incomesSum;

    private BigDecimal expensesSum;

    private BigDecimal total;

    private Long incomesCount;

    private Long expensesCount;

    private Long totalCount;

    private List<T> items;

    public static <T> AnalyticsSummaryDto<T> empty() {
        return new AnalyticsSummaryDto<>(
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                0L,
                0L,
                0L,
                List.of()
        );
    }
}
