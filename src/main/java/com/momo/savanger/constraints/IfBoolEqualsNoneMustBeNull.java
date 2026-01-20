package com.momo.savanger.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = IfBoolEqualsNoneMustBeNullValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface IfBoolEqualsNoneMustBeNull {

    String message() default "Field %s is required when field '{boolField}' is '{valueToCheck}'";

    String boolField();

    boolean valueToCheck();

    String[] fields();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {

        IfBoolEqualsNoneMustBeNull[] value();
    }
}
