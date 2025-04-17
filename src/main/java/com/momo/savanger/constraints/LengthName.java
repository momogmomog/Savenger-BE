package com.momo.savanger.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = LengthNameValidator.class)
public @interface LengthName {

    String message() default "Name should be between 1 and 50 characters!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
