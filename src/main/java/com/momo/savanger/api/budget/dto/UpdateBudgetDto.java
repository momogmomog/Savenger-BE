package com.momo.savanger.api.budget.dto;

import com.momo.savanger.constants.Lengths;
import com.momo.savanger.constants.ValidationMessages;
import com.momo.savanger.constraints.LengthName;
import com.momo.savanger.constraints.MinValueZero;
import com.momo.savanger.constraints.NotNull;
import com.momo.savanger.constraints.RRule;
import java.math.BigDecimal;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UpdateBudgetDto {

    @NotNull
    @LengthName
    private String budgetName;

    @NotNull
    @Length(min = 1, max = Lengths.MAX_VARCHAR, message = ValidationMessages.TEXT_MUST_BE_BETWEEN)
    @RRule
    private String recurringRule;

    @NotNull
    private Boolean active;

    @MinValueZero
    private BigDecimal balance;

    @MinValueZero
    private BigDecimal budgetCap;

    @NotNull
    private Boolean autoRevise;

}
