package com.momo.savanger.api.prepayment.constraints;

import com.momo.savanger.api.prepayment.CreatePrepaymentDto;
import com.momo.savanger.api.transaction.recurring.RecurringTransactionService;
import com.momo.savanger.api.util.ValidationUtil;
import com.momo.savanger.constants.ValidationMessages;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidPrepaymentDtoValidator implements
        ConstraintValidator<ValidPrepaymentDto, CreatePrepaymentDto> {

    private final RecurringTransactionService recurringTransactionService;

    @Override
    public void initialize(ValidPrepaymentDto constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(CreatePrepaymentDto dto,
            ConstraintValidatorContext constraintValidatorContext) {
        if (dto.getBudgetId() == null) {
            return true;
        }

        final Set<Long> budgetIds = new HashSet<>();
        budgetIds.add(dto.getBudgetId());

        if (dto.getRecurringTransaction() != null) {
            budgetIds.add(dto.getRecurringTransaction().getBudgetId());
        } else if (dto.getRecurringTransactionId() != null) {

            final boolean rTransactionExists = this.recurringTransactionService
                    .recurringTransactionExists(
                            dto.getRecurringTransactionId(),
                            dto.getBudgetId()
                    );
            if (!rTransactionExists) {
                return ValidationUtil.fail(
                        constraintValidatorContext,
                        "recurringTransactionId",
                        ValidationMessages.R_TRANSACTION_ID_NOT_EXIST
                );
            }
        }

        if (budgetIds.size() > 1) {
            return ValidationUtil.fail(
                    constraintValidatorContext,
                    "recurringTransaction.budgetId",
                    ValidationMessages.BUDGETS_SHOULD_BE_EQUAL
            );
        }

        return true;
    }
}
