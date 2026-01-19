package com.momo.savanger.api.analytics.dto;

import com.momo.savanger.api.tag.TagDto;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class TagAnalytic {

    private final TagDto tag;

    private final BigDecimal totalIncomes;
    private final BigDecimal totalExpenses;
    private final Long totalTransactions;
}
