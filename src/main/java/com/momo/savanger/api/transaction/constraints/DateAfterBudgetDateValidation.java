package com.momo.savanger.api.transaction.constraints;

import com.momo.savanger.api.budget.BudgetService;
import com.momo.savanger.api.transaction.dto.CreateTransactionDto;
import com.momo.savanger.api.util.ValidationUtil;
import com.momo.savanger.constants.ValidationMessages;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DateAfterBudgetDateValidation implements
        ConstraintValidator<DateAfterBudgetDate, CreateTransactionDto> {

    private final BudgetService budgetService;

    @Override
    public void initialize(DateAfterBudgetDate constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(CreateTransactionDto dto,
            ConstraintValidatorContext constraintValidatorContext) {

        if (dto.getBudgetId() == null || dto.getDateCreated() == null) {
            return true;
        }

        if (!this.budgetService.isBudgetDateBefore(
                dto.getBudgetId(),
                dto.getDateCreated())
        ) {
            return ValidationUtil.fail(
                    constraintValidatorContext,
                    "dateCreated",
                    ValidationMessages.TRANSACTION_DATE_IS_INVALID
            );
        }

        return true;
    }
}
