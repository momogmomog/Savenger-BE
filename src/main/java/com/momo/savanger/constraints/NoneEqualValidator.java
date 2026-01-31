package com.momo.savanger.constraints;

import com.momo.savanger.api.util.ValidationUtil;
import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NoneEqualValidator implements ConstraintValidator<NoneEqual, Object> {

    private String[] fields;
    private String message;

    @Override
    public void initialize(NoneEqual constraintAnnotation) {
        this.fields = constraintAnnotation.fields();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        try {
            Set<Object> seenValues = new HashSet<>();

            for (String fieldName : fields) {
                Object fieldValue = getFieldValue(value, fieldName);

                if (fieldValue == null) {
                    return true;
                }

                if (!seenValues.add(fieldValue)) {
                    return ValidationUtil.fail(context, fieldName, this.message);
                }
            }
        } catch (Exception e) {
            log.error("Error during NoneEqualValidator", e);
            throw ApiException.with(ApiErrorCode.ERR_0001);
        }

        return true;
    }

    private Object getFieldValue(Object object, String fieldName) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object);
    }
}