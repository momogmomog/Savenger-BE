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
    public boolean isValid(Budget budget,
            ConstraintValidatorContext constraintValidatorContext) {
        if (budget == null) {
            return true;
        }
        return Objects.equals(budget.getOwnerId(), SecurityUtils.getCurrentUser().getId());
    }
}
