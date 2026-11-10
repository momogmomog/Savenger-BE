package com.momo.savanger.api.prepayment.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidPrepaymentValidator.class)
public @interface ValidPrepayment {

    boolean allowCompleted() default false;

    String message() default "Invalid prepayment ID!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
