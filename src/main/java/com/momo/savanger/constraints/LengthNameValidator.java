package com.momo.savanger.constraints;

import com.momo.savanger.constants.Lengths;
import com.momo.savanger.error.ApiErrorCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LengthNameValidator implements ConstraintValidator<LengthName, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return false;
        }

        return !s.isEmpty() && s.length() <= Lengths.MAX_NAME;
    }

    @Override
    public void initialize(LengthName constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
}
