package com.momo.savanger.api.debt.constraints;

import com.momo.savanger.api.budget.BudgetService;
import com.momo.savanger.api.budget.dto.BudgetStatistics;
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

        final BudgetStatistics budgetStatistics = this.budgetService.getStatistics(
                debtDto.getLenderBudgetId());

        if (budgetStatistics.getRealBalance().compareTo(debtDto.getDebtAmount()) < 0) {
            return this.fail(constraintValidatorContext, "amount",
                    ValidationMessages.AMOUNT_IS_TOO_BIG);
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
