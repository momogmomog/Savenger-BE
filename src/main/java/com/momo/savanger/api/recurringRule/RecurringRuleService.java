package com.momo.savanger.api.recurringRule;

import java.time.LocalDateTime;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;

public interface RecurringRuleService {

    LocalDateTime convertRecurringRuleToDate(String recurringRule, LocalDateTime startDate)
            throws InvalidRecurrenceRuleException;

}
