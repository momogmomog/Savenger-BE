package com.momo.savanger.api.budget.constraints;

import com.momo.savanger.api.budget.BudgetService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ValidBudgetValidator implements ConstraintValidator<ValidBudget, Long> {

    private final BudgetService budgetService;


    @Override
    public boolean isValid(Long budgetId, ConstraintValidatorContext constraintValidatorContext) {
        if (budgetId == null) {
            return true;
        }

        return this.budgetService.isBudgetValid(budgetId);
    }

    @Override
    public void initialize(ValidBudget constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
}
