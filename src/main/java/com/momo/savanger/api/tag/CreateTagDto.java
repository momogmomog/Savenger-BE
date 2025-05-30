package com.momo.savanger.api.tag;

import com.momo.savanger.api.budget.constraints.CanAccessBudget;
import com.momo.savanger.constraints.LengthName;
import com.momo.savanger.constraints.MinValueZero;
import com.momo.savanger.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CreateTagDto {

    @NotNull
    @LengthName
    private String tagName;

    @MinValueZero
    private BigDecimal budgetCap;

    @NotNull
    @CanAccessBudget
    private Long budgetId;

}
