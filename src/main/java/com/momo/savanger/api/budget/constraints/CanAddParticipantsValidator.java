package com.momo.savanger.api.budget.constraints;

import com.momo.savanger.api.budget.Budget;
import com.momo.savanger.api.util.SecurityUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CanAddParticipantsValidator implements
        ConstraintValidator<CanAddParticipants, Budget> {

    @Override
    public void initialize(CanAddParticipants constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Budget budgetDto,
            ConstraintValidatorContext constraintValidatorContext) {
        return Objects.equals(budgetDto.getOwnerId(), SecurityUtils.getCurrentUser().getId());
    }
}
