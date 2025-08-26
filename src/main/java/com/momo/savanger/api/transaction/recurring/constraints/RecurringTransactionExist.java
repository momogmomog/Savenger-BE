package com.momo.savanger.api.transaction.recurring.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = RecurringTransactionExistValidator.class)
public @interface RecurringTransactionExist {

    String message() default "Recurring transaction does not exist";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
