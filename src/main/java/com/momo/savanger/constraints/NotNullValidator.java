package com.momo.savanger.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotNullValidator implements ConstraintValidator<NotNull, Object> {

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        return object != null;
    }

    @Override
    public void initialize(NotNull constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
}
