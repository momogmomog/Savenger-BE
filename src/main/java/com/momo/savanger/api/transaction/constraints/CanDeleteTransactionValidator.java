package com.momo.savanger.api.transaction.constraints;

import com.momo.savanger.api.transaction.TransactionService;
import com.momo.savanger.api.user.User;
import com.momo.savanger.api.util.SecurityUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CanDeleteTransactionValidator implements
        ConstraintValidator<CanDeleteTransaction, Long> {

    private final TransactionService transactionService;

    @Override
    public void initialize(CanDeleteTransaction constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintValidatorContext) {

        if (id == null) {
            return true;
        }

        final User user = SecurityUtils.getCurrentUser();

        return this.transactionService.canDeleteTransaction(id, user);
    }

}
