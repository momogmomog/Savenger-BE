package com.momo.savanger.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.dmfs.rfc5545.recur.RecurrenceRule;
import org.dmfs.rfc5545.recur.RecurrenceRule.RfcMode;

public class RRuleValidator implements ConstraintValidator<RRule, String> {


    private RecurrenceRule rRule;

    @Override
    public void initialize(RRule constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return true;
        }

        try {
            new RecurrenceRule(s, RfcMode.RFC2445_STRICT);
        } catch (Exception e) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(
                            String.format("RRule failed with: %s", e.getMessage()))
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
