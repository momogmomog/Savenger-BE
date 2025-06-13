package com.momo.savanger.api.revision;

import com.momo.savanger.api.budget.constraints.CanAccessBudget;
import com.momo.savanger.constants.Lengths;
import com.momo.savanger.constants.ValidationMessages;
import com.momo.savanger.constraints.MinValueZero;
import com.momo.savanger.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class CreateRevisionDto {

    @MinValueZero
    private BigDecimal balance;

    @Length(max = Lengths.MAX_VARCHAR, message = ValidationMessages.TEXT_MUST_BE_BETWEEN)
    private String comment;

    @NotNull
    @CanAccessBudget
    private Long budgetId;

}
