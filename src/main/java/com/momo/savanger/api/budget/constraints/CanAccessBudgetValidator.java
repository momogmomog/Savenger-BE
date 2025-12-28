package com.momo.savanger.api.budget.constraints;

import com.momo.savanger.api.budget.BudgetService;
import com.momo.savanger.api.user.User;
import com.momo.savanger.api.util.SecurityUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CanAccessBudgetValidator implements ConstraintValidator<CanAccessBudget, Long> {

    private final BudgetService budgetService;

    private boolean onlyEnabled;

    @Override
    public boolean isValid(Long budgetId, ConstraintValidatorContext constraintValidatorContext) {

        if (budgetId == null) {
            return true;
        }

        final User user = SecurityUtils.getCurrentUser();

        return this.budgetService.isUserPermitted(user, budgetId, this.onlyEnabled);
    }

    @Override
    public void initialize(CanAccessBudget constraintAnnotation) {
        this.onlyEnabled = constraintAnnotation.onlyEnabled();
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
}
