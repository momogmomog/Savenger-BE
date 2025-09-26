package com.momo.savanger.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = OneMustNotBeNullValidator.class)
public @interface OneMustNotBeNull {

    String[] fields();

    String message() default "One of these fields should be not null";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
