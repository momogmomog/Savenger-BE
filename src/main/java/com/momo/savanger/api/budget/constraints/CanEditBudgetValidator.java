package com.momo.savanger.api.budget.constraints;

import com.momo.savanger.api.budget.BudgetService;
import com.momo.savanger.api.user.User;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;

@RequiredArgsConstructor
public class CanEditBudgetValidator implements ConstraintValidator<CanEditBudget, Long> {

    private final BudgetService budgetService;

    @Override
    public boolean isValid(Long budgetId, ConstraintValidatorContext constraintValidatorContext) {

        if (budgetId == null) {
            return true;
        }

        final User user = (User) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        return this.budgetService.isUserPermitted(user, budgetId);
    }

    @Override
    public void initialize(CanEditBudget constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
}
