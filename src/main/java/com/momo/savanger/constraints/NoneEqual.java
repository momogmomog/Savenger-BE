package com.momo.savanger.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NoneEqualValidator.class)
@Documented
public @interface NoneEqual {

    String[] fields();

    String message() default "Fields must not be equal";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
