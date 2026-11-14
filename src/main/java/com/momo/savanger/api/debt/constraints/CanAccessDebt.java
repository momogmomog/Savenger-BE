package com.momo.savanger.api.debt.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Constraint(validatedBy = CanAccessDebtValidator.class)
public @interface CanAccessDebt {

    String message() default "You do not have access to this debt";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
