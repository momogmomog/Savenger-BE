package com.momo.savanger.api.budget.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = IsBudgetOwnerValidator.class)
public @interface IsBudgetOwner {

    String message() default "User is not a budget owner";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
