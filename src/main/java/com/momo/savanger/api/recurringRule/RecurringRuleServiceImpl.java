package com.momo.savanger.api.recurringRule;

import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.TimeZone;
import lombok.RequiredArgsConstructor;
import org.dmfs.jems2.iterable.First;
import org.dmfs.rfc5545.DateTime;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;
import org.dmfs.rfc5545.recur.RecurrenceRule;
import org.dmfs.rfc5545.recurrenceset.OfRule;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecurringRuleServiceImpl implements RecurringRuleService {


    @Override
    public LocalDateTime convertRecurringRuleToDate(String recurringRule, LocalDateTime startDate) {

        try {
            final RecurrenceRule rule = new RecurrenceRule(recurringRule);

            final long startMillis = startDate.atZone(ZoneId.systemDefault()).toInstant()
                    .toEpochMilli();

            final DateTime date = new DateTime(TimeZone.getDefault(), startMillis);

            DateTime newDate = null;

            for (DateTime occurrence :
                    new First<>(2, new OfRule(rule, date))
            ) {

                newDate = occurrence;

                if (!this.getLocalDateFromDateTime(date)
                        .isEqual(this.getLocalDateFromDateTime(newDate))) {
                    break;
                }
            }

            return LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(newDate.getTimestamp()),
                    ZoneId.systemDefault()
            );
        } catch (InvalidRecurrenceRuleException exception) {
            throw ApiException.with(ApiErrorCode.ERR_0017);
        }
    }


    private LocalDate getLocalDateFromDateTime(DateTime date) {

        return Instant.ofEpochMilli(date.getTimestamp())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
