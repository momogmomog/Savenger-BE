package com.momo.savanger.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

public class MinValueZeroValidator implements ConstraintValidator<MinValueZero, BigDecimal> {

    @Override
    public void initialize(MinValueZero constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(BigDecimal bigDecimal,
            ConstraintValidatorContext constraintValidatorContext) {
        if (bigDecimal == null) {
            return true;
        }
        return bigDecimal.compareTo(BigDecimal.ZERO) > 0;
    }
}
