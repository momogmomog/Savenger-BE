package com.momo.savanger.api.debt.constraints;

import com.momo.savanger.api.debt.CreateDebtDto;
import com.momo.savanger.constants.ValidationMessages;
import com.momo.savanger.constraints.ValidationFail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidDebtDtoValidator implements ConstraintValidator<ValidDebtDto, CreateDebtDto> {

    private final ValidationFail validationFail;

    @Override
    public boolean isValid(CreateDebtDto debtDto,
            ConstraintValidatorContext constraintValidatorContext) {

        if (debtDto.getLenderBudgetId() == null || debtDto.getReceiverBudgetId() == null) {
            return true;
        }

        if (debtDto.getLenderBudgetId().equals(debtDto.getReceiverBudgetId())) {
            return this.validationFail.fail(constraintValidatorContext, "lenderBudgetId",
                    ValidationMessages.BUDGETS_SHOULD_BE_DIFFERENT);
        }

        return true;
    }

    @Override
    public void initialize(ValidDebtDto constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

}
