package com.momo.savanger.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;

@Component
public class IfBoolEqualsNoneMustBeNullValidator
        implements ConstraintValidator<IfBoolEqualsNoneMustBeNull, Object> {

    private boolean val;
    private String boolField;
    private String[] fields;
    private String messageFormat;

    public void initialize(IfBoolEqualsNoneMustBeNull constraintAnnotation) {
        this.val = constraintAnnotation.valueToCheck();
        this.boolField = constraintAnnotation.boolField();
        this.fields = constraintAnnotation.fields();
        this.messageFormat = constraintAnnotation.message();
    }

    public boolean isValid(Object dto,
            ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        final BeanWrapper dtoWrapper = new BeanWrapperImpl(dto);

        final Object boolVal = dtoWrapper.getPropertyValue(this.boolField);
        if (!Objects.equals(boolVal, this.val)) {
            return true;
        }

        boolean hasErrors = false;
        for (String fieldName : this.fields) {
            final Object fieldValue = dtoWrapper.getPropertyValue(fieldName);
            if (fieldValue == null) {
                hasErrors = true;
                final String messageTemplate = String.format(
                        this.messageFormat,
                        fieldName
                );

                context.buildConstraintViolationWithTemplate(messageTemplate)
                        .addPropertyNode(fieldName)
                        .addConstraintViolation();
            }
        }

        return !hasErrors;
    }
}
