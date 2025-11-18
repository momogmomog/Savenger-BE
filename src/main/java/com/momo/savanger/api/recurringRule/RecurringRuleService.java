package com.momo.savanger.api.recurringRule;

import java.time.LocalDateTime;

public interface RecurringRuleService {

    LocalDateTime convertRecurringRuleToDate(String recurringRule, LocalDateTime startDate);

}
