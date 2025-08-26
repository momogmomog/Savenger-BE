package com.momo.savanger.api.transaction.recurring.constraints;

import com.momo.savanger.api.transaction.recurring.RecurringTransactionService;
import com.momo.savanger.error.ApiException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecurringTransactionExistValidator implements
        ConstraintValidator<RecurringTransactionExist, Long> {

    private final RecurringTransactionService recurringTransactionService;

    @Override
    public void initialize(RecurringTransactionExist constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long rTransactionId,
            ConstraintValidatorContext constraintValidatorContext) {

        if (rTransactionId == null) {
            return true;
        }

        try {
            return this.recurringTransactionService.isRecurringTransactionValid(rTransactionId);
        } catch (ApiException e) {
            return false;
        }
    }
}
