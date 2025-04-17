package com.momo.savanger.api.category;

import com.momo.savanger.api.budget.constraints.CanEditBudget;
import com.momo.savanger.constraints.LengthName;
import com.momo.savanger.constraints.MinValueZero;
import com.momo.savanger.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CreateCategoryDto {

    @NotNull
    @LengthName
    private String categoryName;

    @MinValueZero
    private BigDecimal budgetCap;

    @NotNull
    @CanEditBudget
    private Long budgetId;

}
