package com.momo.savanger.api.analytics.dto;

import com.momo.savanger.api.category.CategoryDto;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CategoryAnalytic {

    private final CategoryDto category;

    private final BigDecimal totalIncomes;
    private final BigDecimal totalExpenses;
    private final Long totalTransactions;
}
