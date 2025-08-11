package com.momo.savanger.constraints;

import com.momo.savanger.api.util.ValidationUtil;
import com.momo.savanger.constants.ValidationMessages;
import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OneMustBeNullValidator
        implements ConstraintValidator<OneMustBeNull, Object> {

    private String fieldOneName;
    private String fieldTwoName;

    @Override
    public void initialize(OneMustBeNull constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.fieldOneName = constraintAnnotation.fieldOne();
        this.fieldTwoName = constraintAnnotation.fieldTwo();
    }

    @Override
    public boolean isValid(Object dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }

        final Class<?> cls = dto.getClass();
        try {
            final Field fieldOne = cls.getDeclaredField(this.fieldOneName);
            final Field fieldTwo = cls.getDeclaredField(this.fieldTwoName);

            boolean isFieldOneNull;
            try {
                fieldOne.setAccessible(true);
                isFieldOneNull = fieldOne.get(dto) == null;
                if (isFieldOneNull) {
                    return true;
                }
            } finally {
                fieldOne.setAccessible(false);
            }

            try {
                fieldTwo.setAccessible(true);
                if (fieldTwo.get(dto) != null) {
                    return ValidationUtil.fail(context, fieldOneName, String.format(
                            ValidationMessages.ONE_FIELD_SHOULD_BE_NULL, fieldOneName,
                            fieldTwoName));
                }
            } finally {
                fieldTwo.setAccessible(false);
            }
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            log.error("Error during OneMustBeNullValidator", ex);
            throw ApiException.with(ApiErrorCode.ERR_0001);
        }

        return true;
    }
}
