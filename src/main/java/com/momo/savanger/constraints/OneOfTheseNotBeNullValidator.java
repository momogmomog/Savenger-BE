package com.momo.savanger.constraints;

import com.momo.savanger.constants.ValidationMessages;
import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OneOfTheseNotBeNullValidator implements
        ConstraintValidator<OneOfTheseNotBeNull, Object> {

    private String[] fields;

    @Override
    public void initialize(OneOfTheseNotBeNull constraintAnnotation) {
        this.fields = constraintAnnotation.fields();
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object dto, ConstraintValidatorContext constraintValidatorContext) {
        if (dto == null) {
            return true;
        }

        final Class<?> cls = dto.getClass();

        try {
            Field field;

            for (String fieldName : this.fields) {
                field = cls.getDeclaredField(fieldName);

                try {
                    field.setAccessible(true);
                    if (field.get(dto) != null) {
                        return true;
                    }
                } finally {
                    field.setAccessible(false);
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            log.error("Error during OneMustBeNullValidator", ex);
            throw ApiException.with(ApiErrorCode.ERR_0001);
        }

        return this.fail(constraintValidatorContext, this.fields[0],
                String.format(ValidationMessages.FIELDS_CANNOT_BE_NULL,
                        Arrays.stream(this.fields).toList()));
    }

    private boolean fail(ConstraintValidatorContext context, String field, String msg) {
        context.buildConstraintViolationWithTemplate(msg)
                .addPropertyNode(field)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();

        return false;

    }
}
