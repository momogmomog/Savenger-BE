package com.momo.savanger.api.budget.constraints;

import com.momo.savanger.api.budget.BudgetService;
import com.momo.savanger.api.user.User;
import com.momo.savanger.api.util.SecurityUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CanEditBudgetValidator implements ConstraintValidator<CanEditBudget, Long> {

    private final BudgetService budgetService;

    @Override
    public boolean isValid(Long budgetId, ConstraintValidatorContext constraintValidatorContext) {

        if (budgetId == null) {
            return true;
        }

        User user = SecurityUtils.getCurrentUser();

        return this.budgetService.isUserPermitted(user, budgetId);
    }

    @Override
    public void initialize(CanEditBudget constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
}
