package com.momo.savanger.api.transfer.transferTransaction.constraints;

import com.momo.savanger.api.budget.BudgetService;
import com.momo.savanger.api.transfer.Transfer;
import com.momo.savanger.api.transfer.TransferService;
import com.momo.savanger.api.transfer.transferTransaction.TransferTransaction;
import com.momo.savanger.api.transfer.transferTransaction.TransferTransactionService;
import com.momo.savanger.api.user.User;
import com.momo.savanger.api.util.SecurityUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CanAccessTransferTransactionValidator implements
        ConstraintValidator<CanAccessTransferTransaction, Long> {

    private final TransferService transferService;

    private final BudgetService budgetService;

    private final TransferTransactionService transferTransactionService;

    @Override
    public void initialize(CanAccessTransferTransaction constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long transferTransactionId,
            ConstraintValidatorContext constraintValidatorContext) {

        if (transferTransactionId == null) {
            return false;
        }

        final TransferTransaction transferTransaction = this.transferTransactionService.getTransferTransaction(
                transferTransactionId);

        final Transfer transfer = this.transferService.getById(transferTransaction.getTransferId());

        final User user = SecurityUtils.getCurrentUser();

        return this.budgetService.isUserPermitted(user, transfer.getReceiverBudgetId())
                && this.budgetService.isUserPermitted(user, transfer.getSourceBudgetId());
    }
}
