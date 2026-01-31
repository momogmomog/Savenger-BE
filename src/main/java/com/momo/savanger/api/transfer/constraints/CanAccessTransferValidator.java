package com.momo.savanger.api.transfer.constraints;

import com.momo.savanger.api.budget.BudgetService;
import com.momo.savanger.api.transfer.Transfer;
import com.momo.savanger.api.transfer.TransferService;
import com.momo.savanger.api.user.User;
import com.momo.savanger.api.util.SecurityUtils;
import com.momo.savanger.api.util.ValidationUtil;
import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CanAccessTransferValidator implements ConstraintValidator<CanAccessTransfer, Long> {

    private final TransferService transferService;

    private final BudgetService budgetService;

    @Override
    public void initialize(CanAccessTransfer constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long transferId, ConstraintValidatorContext constraintValidatorContext) {

        if (transferId == null) {
            return true;
        }

        Optional<ApiException> exception = ApiException.tryCatch(
                ApiErrorCode.ERR_0018,
                () -> this.transferService.getById(transferId)
        );

        if (exception.isPresent()) {
            return ValidationUtil.fail(constraintValidatorContext,
                    "transferId",
                    exception.get().getMessage()
            );
        }

        final Transfer transfer = this.transferService.getById(transferId);

        final User user = SecurityUtils.getCurrentUser();

        return this.budgetService.isUserPermitted(user, transfer.getReceiverBudgetId())
                && this.budgetService.isUserPermitted(user, transfer.getSourceBudgetId());
    }
}
