package com.momo.savanger.api.debt.constraints;

import com.momo.savanger.api.budget.BudgetService;
import com.momo.savanger.api.debt.CreateDebtDto;
import com.momo.savanger.api.debt.DebtService;
import com.momo.savanger.constants.ValidationMessages;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidDebtDtoValidator implements ConstraintValidator<ValidDebtDto, CreateDebtDto> {

    private final DebtService debtService;

    private final BudgetService budgetService;

    @Override
    public boolean isValid(CreateDebtDto debtDto,
            ConstraintValidatorContext constraintValidatorContext) {

        if (debtDto.getLenderBudgetId() == null || debtDto.getReceiverBudgetId() == null) {
            return true;
        }

        if (debtDto.getLenderBudgetId().equals(debtDto.getReceiverBudgetId())) {
            return this.fail(constraintValidatorContext, "lenderBudgetId",
                    ValidationMessages.BUDGETS_SHOULD_BE_DIFFERENT);
        }

        return true;
    }

    @Override
    public void initialize(ValidDebtDto constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    private boolean fail(ConstraintValidatorContext context, String field, String msg) {
        context.buildConstraintViolationWithTemplate(msg)
                .addPropertyNode(field)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();

        return false;

    }
}
