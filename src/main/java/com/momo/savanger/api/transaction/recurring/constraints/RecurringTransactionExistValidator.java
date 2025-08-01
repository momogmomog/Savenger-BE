package com.momo.savanger.api.transaction.recurring.constraints;

import com.momo.savanger.api.transaction.recurring.RecurringTransactionService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RecurringTransactionExistValidator implements
        ConstraintValidator<RecurringTransactionExist, Long> {

    @Autowired
    private RecurringTransactionService recurringTransactionService;

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

        return this.recurringTransactionService.isRecurringTransactionValid(rTransactionId);
    }
}
