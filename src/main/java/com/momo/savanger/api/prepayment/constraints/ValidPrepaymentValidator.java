package com.momo.savanger.api.prepayment.constraints;

import com.momo.savanger.api.budget.BudgetService;
import com.momo.savanger.api.prepayment.Prepayment;
import com.momo.savanger.api.prepayment.PrepaymentService;
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
public class ValidPrepaymentValidator implements
        ConstraintValidator<ValidPrepayment, Long> {

    private final PrepaymentService prepaymentService;

    private final BudgetService budgetService;

    private boolean allowCompleted;

    @Override
    public void initialize(ValidPrepayment constraintAnnotation) {
        this.allowCompleted = constraintAnnotation.allowCompleted();
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintValidatorContext) {

        final Prepayment prepayment = this.prepaymentService.findByIdIfExists(
                        id,
                        this.allowCompleted
                )
                .orElse(null);

        if (prepayment == null) {
            return ValidationUtil.fail(constraintValidatorContext, null,
                    ValidationMessages.PREPAYMENT_DOES_NOT_EXIST);
        }

        final User user = SecurityUtils.getCurrentUser();

        if (!budgetService.isUserPermitted(user, prepayment.getBudgetId())) {
            return ValidationUtil.fail(constraintValidatorContext, null,
                    ValidationMessages.ACCESS_DENIED);
        }

        return true;
    }
}
