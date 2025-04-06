package com.momo.savanger.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;
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
        try {
            RecurrenceRule recurrenceRule = new RecurrenceRule(s, RfcMode.RFC2445_STRICT);
        } catch (InvalidRecurrenceRuleException e) {
//            constraintValidatorContext.disableDefaultConstraintViolation();
//            constraintValidatorContext.buildConstraintViolationWithTemplate(
//                    e.getMessage()
//            );
            return false;
        }
        return true;
    }
}
