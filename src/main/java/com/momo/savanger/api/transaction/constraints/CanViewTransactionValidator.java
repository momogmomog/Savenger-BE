package com.momo.savanger.api.transaction.constraints;

import com.momo.savanger.api.transaction.TransactionService;
import com.momo.savanger.api.util.SecurityUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CanViewTransactionValidator implements
        ConstraintValidator<CanViewTransaction, Long> {

    private final TransactionService transactionService;

    @Override
    public boolean isValid(Long id,
            ConstraintValidatorContext constraintValidatorContext) {
        if (id == null) {
            return true;
        }

        return this.transactionService.canViewTransaction(
                id,
                SecurityUtils.getCurrentUser().getId()
        );
    }

    @Override
    public void initialize(CanViewTransaction constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
}
