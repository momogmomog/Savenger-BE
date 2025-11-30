package com.momo.savanger.api.transaction.recurring.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidRecurringTransactionValidator.class)
public @interface ValidRecurringTransaction {

    String message() default "Invalid recurring transaction ID!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
