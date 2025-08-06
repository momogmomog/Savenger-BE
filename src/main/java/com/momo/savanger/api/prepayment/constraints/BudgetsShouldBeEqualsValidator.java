package com.momo.savanger.api.prepayment.constraints;

import com.momo.savanger.api.prepayment.CreatePrepaymentDto;
import com.momo.savanger.api.transaction.recurring.RecurringTransactionDto;
import com.momo.savanger.api.transaction.recurring.RecurringTransactionMapper;
import com.momo.savanger.api.transaction.recurring.RecurringTransactionService;
import com.momo.savanger.api.util.ValidationUtil;
import com.momo.savanger.constants.ValidationMessages;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BudgetsShouldBeEqualsValidator implements
        ConstraintValidator<BudgetsShouldBeEquals, CreatePrepaymentDto> {

    private final RecurringTransactionService recurringTransactionService;

    private final RecurringTransactionMapper recurringTransactionMapper;

    @Override
    public void initialize(BudgetsShouldBeEquals constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(CreatePrepaymentDto dto,
            ConstraintValidatorContext constraintValidatorContext) {
        if (dto.getBudgetId() == null) {
            return true;
        }

        RecurringTransactionDto recurringTransaction = null;

        if (dto.getRecurringTransaction() != null) {
            recurringTransaction = recurringTransactionMapper.toRecurringTransactionDto(
                    dto.getRecurringTransaction());
        } else if (dto.getRecurringTransactionId() != null) {
            recurringTransaction = recurringTransactionMapper.toRecurringTransactionDto(
                    this.recurringTransactionService.findById(dto.getRecurringTransactionId())
            );
        }

        if (recurringTransaction != null) {
            if (!recurringTransaction.getBudgetId().equals(dto.getBudgetId())) {
                return ValidationUtil.fail(
                        constraintValidatorContext,
                        "recurringTransaction.budgetId",
                        ValidationMessages.BUDGETS_SHOULD_BE_EQUALS
                );
            }
        }

        return true;
    }
}
