package com.momo.savanger.api.budget.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = CanEditBudgetValidator.class)
public @interface CanEditBudget {

    String message() default "Invalid budget ID!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
