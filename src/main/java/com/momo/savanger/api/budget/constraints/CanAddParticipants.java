package com.momo.savanger.api.budget.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = CanAddParticipantsValidator.class)
public @interface CanAddParticipants {

    String message() default "User is not allowed to add participants";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
