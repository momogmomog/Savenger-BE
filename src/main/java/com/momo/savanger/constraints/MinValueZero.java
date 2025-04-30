package com.momo.savanger.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = MinValueZeroValidator.class)
public @interface MinValueZero {

    String message() default "Value cannot be less than zero";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
