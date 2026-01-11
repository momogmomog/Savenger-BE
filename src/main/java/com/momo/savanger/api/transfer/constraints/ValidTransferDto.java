package com.momo.savanger.api.transfer.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = ValidTransferDtoValidator.class)
public @interface ValidTransferDto {

    String message() default "Source budget id and receiver budget id should be different.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
