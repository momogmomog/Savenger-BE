package com.momo.savanger.api.debt.constraints;

import com.momo.savanger.api.budget.BudgetService;
import com.momo.savanger.api.debt.Debt;
import com.momo.savanger.api.debt.DebtService;
import com.momo.savanger.api.user.User;
import com.momo.savanger.api.util.SecurityUtils;
import com.momo.savanger.api.util.ValidationUtil;
import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CanAccessDebtValidator implements ConstraintValidator<CanAccessDebt, Long> {

    private final DebtService debtService;

    private final BudgetService budgetService;

    @Override
    public void initialize(CanAccessDebt constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long debtId, ConstraintValidatorContext constraintValidatorContext) {

        if (debtId == null) {
            return true;
        }

        final Optional<ApiException> exception = ApiException.tryCatch(
                ApiErrorCode.ERR_0013,
                () -> this.debtService.findById(debtId)
        );

        if (exception.isPresent()) {
            return ValidationUtil.fail(constraintValidatorContext,
                    "debtId",
                    exception.get().getMessage()
            );
        }

        final Debt debt = this.debtService.findById(debtId);

        final User user = SecurityUtils.getCurrentUser();

        return this.budgetService.isUserPermitted(user, debt.getReceiverBudgetId())
                && this.budgetService.isUserPermitted(user, debt.getLenderBudgetId());
    }
}
