package com.momo.savanger.api.util;

import jakarta.validation.ConstraintValidatorContext;

public final class ValidationUtil {

    public static boolean fail(ConstraintValidatorContext context, String field, String msg) {
        context.buildConstraintViolationWithTemplate(msg)
                .addPropertyNode(field)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();

        return false;
    }
}
