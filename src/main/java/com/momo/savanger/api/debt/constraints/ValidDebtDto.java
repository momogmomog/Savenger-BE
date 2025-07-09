package com.momo.savanger.api.debt.constraints;

import com.momo.savanger.constants.ValidationMessages;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = ValidDebtDtoValidator.class)
public @interface ValidDebtDto {

    String message() default ValidationMessages.DEBT_ALREADY_EXIST;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
