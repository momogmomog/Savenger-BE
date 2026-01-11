package com.momo.savanger.api.transfer.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Constraint(validatedBy = CanAccessTransferValidator.class)
public @interface CanAccessTransfer {

    String message() default "You do not have access to this transfer";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
