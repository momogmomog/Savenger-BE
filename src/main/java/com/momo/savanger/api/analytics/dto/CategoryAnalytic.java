package com.momo.savanger.api.analytics.dto;

import com.momo.savanger.api.category.CategoryDto;
import com.momo.savanger.api.transaction.dto.TransactionSumAndCount;
import lombok.Data;

@Data
public class CategoryAnalytic {

    private final CategoryDto category;

    private final TransactionSumAndCount incomes;
    private final TransactionSumAndCount expenses;
}
