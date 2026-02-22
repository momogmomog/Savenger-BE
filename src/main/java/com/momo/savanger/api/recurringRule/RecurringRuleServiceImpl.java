package com.momo.savanger.api.recurringRule;

import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.TimeZone;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dmfs.rfc5545.DateTime;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;
import org.dmfs.rfc5545.recur.RecurrenceRule;
import org.dmfs.rfc5545.recur.RecurrenceRuleIterator;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecurringRuleServiceImpl implements RecurringRuleService {

    @Override
    public Optional<LocalDateTime> getNextOccurrence(
            String recurringRule,
            LocalDateTime originalStartDate,
            LocalDateTime currentNextDate
    ) {
       return this.getNextOccurrence(
               recurringRule,
               originalStartDate,
               currentNextDate,
               false
       );
    }

    @Override
    public Optional<LocalDateTime> getNextOccurrence(
            String recurringRule,
            LocalDateTime originalStartDate,
            LocalDateTime currentNextDate,
            boolean allowCurrentNextDate
    ) {
        //TODO: TEST scenarios (skip those that are already tested):
        // Test RRULE with 1 occurrence for a specific date. Happens only once on may 5th 2026 and never repeats. test with few examples.
        // Test RRULE with 3 occurrences max, meaning that passing current next date after the 3rd occurrence will return empty.
        // Test RRULE with end date.
        // Test variety of combinations (days, weeks, years, every x day/week/yer, combine with max occurrence and end date)
        try {
            final RecurrenceRule rule = new RecurrenceRule(recurringRule);

            final long startMillis = originalStartDate.atZone(ZoneId.systemDefault()).toInstant()
                    .toEpochMilli();
            final DateTime startDateTime = new DateTime(TimeZone.getDefault(), startMillis);

            final long currentNextMillis = currentNextDate.atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();

            final RecurrenceRuleIterator iterator = rule.iterator(startDateTime);

            while (iterator.hasNext()) {
                final DateTime next = iterator.nextDateTime();

                final boolean acceptable;
                final long nextTimestamp = next.getTimestamp();
                if (allowCurrentNextDate) {
                    acceptable = nextTimestamp >= currentNextMillis;
                } else {
                    acceptable = nextTimestamp > currentNextMillis;
                }

                if (acceptable) {
                    final LocalDateTime newNextDate = LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(next.getTimestamp()),
                            ZoneId.systemDefault()
                    );
                    return Optional.of(newNextDate);
                }
            }

            return Optional.empty();

        } catch (InvalidRecurrenceRuleException exception) {
            log.error("Error during next occurrence retrieval.", exception);
            throw ApiException.with(ApiErrorCode.ERR_0017);
        }
    }
}
