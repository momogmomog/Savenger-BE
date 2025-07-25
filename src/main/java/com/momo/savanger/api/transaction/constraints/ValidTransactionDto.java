package com.momo.savanger.api.transaction.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = ValidTransactionDtoValidator.class)
public @interface ValidTransactionDto {

    String message() default "Transaction is not valid.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
