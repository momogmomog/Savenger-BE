package com.momo.savanger.api.recurringRule;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RecurringRuleService {

    Optional<LocalDateTime> getNextOccurrence(
            String recurringRule,
            LocalDateTime originalStartDate,
            LocalDateTime currentNextDate
    );

}
