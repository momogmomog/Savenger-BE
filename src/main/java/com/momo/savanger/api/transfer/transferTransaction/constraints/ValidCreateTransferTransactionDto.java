package com.momo.savanger.api.transfer.transferTransaction.constraints;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = ValidCreateTransferTransactionDtoValidation.class)
public @interface ValidCreateTransferTransactionDto {

    String message() default "Category is not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
