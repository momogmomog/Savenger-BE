package com.momo.savanger.api.prepayment.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = IsBudgetBalanceBiggerValidator.class)
public @interface IsBudgetBalanceBigger {

    String message() default "Amount cannot be bigger than budget balance";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
