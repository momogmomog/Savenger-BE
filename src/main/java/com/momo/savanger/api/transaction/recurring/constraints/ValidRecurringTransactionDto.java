package com.momo.savanger.api.transaction.recurring.constraints;

import com.momo.savanger.constants.ValidationMessages;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = ValidRecurringTransactionDtoValidator.class)
public @interface ValidRecurringTransactionDto {

    String message() default ValidationMessages.RECURRING_TRANSACTION_DTO_IS_NOT_VALID;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
