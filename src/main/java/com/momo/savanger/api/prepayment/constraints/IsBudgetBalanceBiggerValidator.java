package com.momo.savanger.api.prepayment.constraints;

import com.momo.savanger.api.budget.BudgetService;
import com.momo.savanger.api.budget.dto.BudgetStatistics;
import com.momo.savanger.api.prepayment.CreatePrepaymentDto;
import com.momo.savanger.api.util.ValidationUtil;
import com.momo.savanger.constants.ValidationMessages;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IsBudgetBalanceBiggerValidator implements
        ConstraintValidator<IsBudgetBalanceBigger, CreatePrepaymentDto> {

    private final BudgetService budgetService;


    @Override
    public void initialize(IsBudgetBalanceBigger constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(CreatePrepaymentDto dto,
            ConstraintValidatorContext constraintValidatorContext) {

        if (dto.getBudgetId() == null) {
            return true;
        }

        final BudgetStatistics statistics = budgetService.getStatistics(dto.getBudgetId());

        if (statistics.getRealBalance().compareTo(dto.getAmount()) < 0) {
            return ValidationUtil.fail(constraintValidatorContext, "amount",
                    ValidationMessages.AMOUNT_CANNOT_BE_BIGGER_THAN_BALANCE);
        }

        return true;
    }
}
