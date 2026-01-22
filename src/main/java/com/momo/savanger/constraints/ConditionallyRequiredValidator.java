package com.momo.savanger.constraints;


import static com.momo.savanger.api.util.ReflectionUtils.isNullOrEmpty;
import static com.momo.savanger.constraints.ConditionallyRequiredStrategy.WHEN_COND_FIELD_IS_NOT_NULL;
import static com.momo.savanger.constraints.ConditionallyRequiredStrategy.WHEN_COND_FIELD_IS_NULL;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConditionallyRequiredValidator
        implements ConstraintValidator<ConditionallyRequired, Object> {

    private String targetField;
    private String conditionField;
    private ConditionallyRequiredStrategy strategy;

    @Override
    public void initialize(ConditionallyRequired constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.targetField = constraintAnnotation.targetField();
        this.conditionField = constraintAnnotation.conditionField();
        this.strategy = constraintAnnotation.strategy();
    }

    @Override
    public boolean isValid(Object dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }

        final Class<?> cls = dto.getClass();

        try {
            final Field conditionField = cls.getDeclaredField(this.conditionField);
            conditionField.setAccessible(true);
            final Object conditionValue = conditionField.get(dto);
            final boolean condValIsNull = isNullOrEmpty(conditionValue);

            // In these cases condition is not met, skip the validation.
            if ((this.strategy == WHEN_COND_FIELD_IS_NOT_NULL && condValIsNull)
                    || (this.strategy == WHEN_COND_FIELD_IS_NULL && !condValIsNull)) {
                return true;
            }

            final Field targetField = cls.getDeclaredField(this.targetField);
            targetField.setAccessible(true);
            final Object targetValue = targetField.get(dto);

            if (isNullOrEmpty(targetValue)) {
                final String conditionMsg =
                        this.strategy == WHEN_COND_FIELD_IS_NOT_NULL ? "present" : "not present";

                context.disableDefaultConstraintViolation();
                context
                        .buildConstraintViolationWithTemplate(
                                String.format(
                                        "Field '%s' must be present when field '%s' is %s",
                                        this.targetField, this.conditionField, conditionMsg))
                        .addPropertyNode(this.targetField)
                        .addConstraintViolation();

                return false;
            }
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            log.error("Error during ConditionallyRequiredValidator", ex);
            throw new IllegalStateException(ex);
        }

        return true;
    }
}
