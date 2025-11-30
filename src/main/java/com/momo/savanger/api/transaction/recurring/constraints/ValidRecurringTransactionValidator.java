package com.momo.savanger.api.transaction.recurring.constraints;


import com.momo.savanger.api.budget.BudgetService;
import com.momo.savanger.api.transaction.recurring.RecurringTransaction;
import com.momo.savanger.api.transaction.recurring.RecurringTransactionService;
import com.momo.savanger.api.user.User;
import com.momo.savanger.api.util.SecurityUtils;
import com.momo.savanger.api.util.ValidationUtil;
import com.momo.savanger.constants.ValidationMessages;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidRecurringTransactionValidator implements
        ConstraintValidator<ValidRecurringTransaction, Long> {

    private final RecurringTransactionService recurringTransactionService;

    private final BudgetService budgetService;

    @Override
    public void initialize(ValidRecurringTransaction constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintValidatorContext) {

        RecurringTransaction rTransaction = this.recurringTransactionService.findByIdIfExists(id)
                .orElse(null);

        if (rTransaction == null) {
            return ValidationUtil.fail(constraintValidatorContext, null,
                    ValidationMessages.R_TRANSACTION_ID_NOT_EXIST);
        }

        User user = SecurityUtils.getCurrentUser();

        if (rTransaction.getCompleted()) {
            return ValidationUtil.fail(constraintValidatorContext, null,
                    ValidationMessages.R_TRANSACTION_IS_COMPLETED);
        }

        if (budgetService.isUserPermitted(user, rTransaction.getBudgetId())) {
            return ValidationUtil.fail(constraintValidatorContext, null,
                    ValidationMessages.ACCESS_DENIED);
        }

        return true;

    }
}
