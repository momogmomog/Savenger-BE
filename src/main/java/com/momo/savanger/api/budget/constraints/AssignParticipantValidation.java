package com.momo.savanger.api.budget.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = AssignParticipantValidationValidator.class)
public @interface AssignParticipantValidation {

    String message() default "The participant cannot be added or edited";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean requireUserAssigned();

}
