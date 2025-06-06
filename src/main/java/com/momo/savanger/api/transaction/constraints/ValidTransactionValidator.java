package com.momo.savanger.api.transaction.constraints;

import com.momo.savanger.api.transaction.TransactionService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidTransactionValidator implements
        ConstraintValidator<ValidTransaction, Long> {

    private final TransactionService transactionService;

    @Override
    public boolean isValid(Long id,
            ConstraintValidatorContext constraintValidatorContext) {

        return this.transactionService.isTransactionValid(id);

    }

    @Override
    public void initialize(ValidTransaction constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
}
