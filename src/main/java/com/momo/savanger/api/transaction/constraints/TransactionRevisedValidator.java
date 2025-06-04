package com.momo.savanger.api.transaction.constraints;

import com.momo.savanger.api.transaction.Transaction;
import com.momo.savanger.api.transaction.TransactionService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionRevisedValidator implements
        ConstraintValidator<TransactionRevised, Long> {

    private final TransactionService transactionService;

    @Override
    public void initialize(TransactionRevised constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintValidatorContext) {
        Transaction transaction = this.transactionService.findById(id);

        return !transaction.getRevised();
    }
}
