package com.momo.savanger.constraints;

import com.momo.savanger.constants.ValidationMessages;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = RRuleValidator.class)
public @interface RRule {

    String message() default ValidationMessages.INVALID_RRULE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
