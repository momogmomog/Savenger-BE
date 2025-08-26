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
public class ValidPrepaymentDtoBudgetIdsValidator implements
        ConstraintValidator<ValidPrepaymentDtoBudgetIds, CreatePrepaymentDto> {

    private final RecurringTransactionService recurringTransactionService;

    @Override
    public void initialize(ValidPrepaymentDtoBudgetIds constraintAnnotation) {
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
            this.recurringTransactionService.findByIdIfExists(dto.getRecurringTransactionId())
                    .ifPresent(rt -> budgetIds.add(rt.getBudgetId()));
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
