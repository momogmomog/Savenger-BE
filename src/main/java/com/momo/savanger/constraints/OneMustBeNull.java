package com.momo.savanger.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = OneMustBeNullValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface OneMustBeNull {

    String fieldOne();

    String fieldTwo();

    String message() default "Either {fieldOne} or {fieldTwo} fields can be populated, but not both.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
