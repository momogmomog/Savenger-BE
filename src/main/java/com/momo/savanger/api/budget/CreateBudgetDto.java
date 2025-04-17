package com.momo.savanger.api.budget;

import com.momo.savanger.constants.Lengths;
import com.momo.savanger.constants.ValidationMessages;
import com.momo.savanger.constraints.LengthName;
import com.momo.savanger.constraints.MinValueZero;
import com.momo.savanger.constraints.NotNull;
import com.momo.savanger.constraints.RRule;
import com.momo.savanger.converter.DateTimeConverter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class CreateBudgetDto {

    @NotNull
    @LengthName
    private String budgetName;

    @NotNull
    @Length(min = 1, max = Lengths.MAX_VARCHAR, message = ValidationMessages.TEXT_MUST_BE_BETWEEN)
    @RRule
    private String recurringRule;

    @NotNull
    @DateTimeConverter
    private LocalDateTime dateStarted;

    @NotNull
    @DateTimeConverter
    private LocalDateTime dueDate;

    @NotNull
    private Boolean active;

    @MinValueZero
    private BigDecimal balance;

    @MinValueZero
    private BigDecimal budgetCap;

    @NotNull
    private Boolean autoRevise;

}
