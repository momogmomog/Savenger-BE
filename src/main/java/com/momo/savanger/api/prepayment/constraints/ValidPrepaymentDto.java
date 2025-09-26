package com.momo.savanger.api.prepayment.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = ValidPrepaymentDtoValidator.class)
public @interface ValidPrepaymentDto {

    String message() default "BudgetId in RecurringTransaction should be equals to BudgetId in Prepayment";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
