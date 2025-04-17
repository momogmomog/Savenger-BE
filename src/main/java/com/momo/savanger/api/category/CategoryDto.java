package com.momo.savanger.api.category;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class CategoryDto {

    private Long id;

    private String categoryName;

    private BigDecimal budgetCap;

    private Long budgetId;
}
