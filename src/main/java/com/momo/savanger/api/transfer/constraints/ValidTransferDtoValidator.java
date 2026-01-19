package com.momo.savanger.api.transfer.constraints;

import com.momo.savanger.api.transfer.dto.CreateTransferDto;
import com.momo.savanger.api.util.ValidationUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidTransferDtoValidator implements
        ConstraintValidator<ValidTransferDto, CreateTransferDto> {

    @Override
    public void initialize(ValidTransferDto constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(CreateTransferDto transferDto,
            ConstraintValidatorContext constraintValidatorContext) {

        if (transferDto.getSourceBudgetId() == null || transferDto.getReceiverBudgetId() == null) {
            return true;
        }

        if (transferDto.getSourceBudgetId().equals(transferDto.getReceiverBudgetId())) {
            return ValidationUtil.fail(constraintValidatorContext, "sourceBudgetId",
                    "Source budget id and receiver budget id should be different.");
        }

        return true;
    }
}
