package com.momo.savanger.constraints;


import static com.momo.savanger.constraints.ConditionallyRequiredStrategy.WHEN_COND_FIELD_IS_NOT_NULL;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ConditionallyRequiredValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ConditionallyRequireds.class)
public @interface ConditionallyRequired {

    /**
     * The field that the validation targets.
     */
    String targetField();

    /**
     * The field used to determine if target field is required or not.
     */
    String conditionField();

    ConditionallyRequiredStrategy strategy() default WHEN_COND_FIELD_IS_NOT_NULL;

    String message() default "Message is overridden in validator.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
