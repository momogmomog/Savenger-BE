package com.momo.savanger.api.transaction.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.validation.annotation.Validated;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Constraint(validatedBy = TransactionRevisedValidator.class)
public @interface TransactionRevised {
    String message() default "Transaction is already revised and cannot be edit.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
