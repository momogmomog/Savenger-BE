package com.momo.savanger.api.transaction.constraints;

import com.momo.savanger.api.transaction.TransactionService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionNotRevisedValidator implements
        ConstraintValidator<TransactionNotRevised, Long> {

    private final TransactionService transactionService;

    @Override
    public void initialize(TransactionNotRevised constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintValidatorContext) {
        return this.transactionService.existsByIdAndRevisedFalse(id);
    }
}
