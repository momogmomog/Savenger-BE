package com.momo.savanger.integration.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.momo.savanger.api.recurringRule.RecurringRuleService;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RecurringRuleServiceIt {

    @Autowired
    private RecurringRuleService recurringRuleService;

    @Test
    public void testConvertRecurringRuleToDate_dailyRecurringRule_shouldReturnNewDate()
            throws InvalidRecurrenceRuleException {

        String recurringRule = "FREQ=DAILY;INTERVAL=1";
        LocalDateTime date = LocalDateTime.of(2025, 11, 6, 13, 27);

        LocalDateTime newDate = this.recurringRuleService.convertRecurringRuleToDate(recurringRule,
                date);

        assertEquals(date.plusDays(1).toLocalDate(), newDate.toLocalDate());
    }

    @Test
    public void testConvertRecurringRuleToDate_weeklyRecurringRule_shouldReturnNewDate()
            throws InvalidRecurrenceRuleException {

        String recurringRule = "FREQ=WEEKLY;INTERVAL=1;BYDAY=MO";

        LocalDateTime date = LocalDateTime.of(2025, 11, 6, 13, 27);

        LocalDateTime newDate = this.recurringRuleService.convertRecurringRuleToDate(recurringRule,
                date);

        LocalDateTime expectedDate = date
                .with(TemporalAdjusters.next(DayOfWeek.MONDAY));

        assertEquals(expectedDate.toLocalDate(), newDate.toLocalDate());
    }

    @Test
    public void testConvertRecurringRuleToDate_monthlyRecurringRule_shouldReturnNewDate()
            throws InvalidRecurrenceRuleException {

        String recurringRule = "FREQ=MONTHLY;INTERVAL=1;BYMONTHDAY=06";

        LocalDateTime date = LocalDateTime.of(2025, 11, 6, 13, 27);

        LocalDateTime newDate = this.recurringRuleService.convertRecurringRuleToDate(recurringRule,
                date);

        LocalDateTime expectedDate = date.plusMonths(1);

        assertEquals(expectedDate.toLocalDate(), newDate.toLocalDate());
    }

    @Test
    public void testConvertRecurringRuleToDate_yearlyRecurringRule_shouldReturnNewDate()
            throws InvalidRecurrenceRuleException {

        String recurringRule = "FREQ=YEARLY;INTERVAL=1;BYMONTH=11;BYMONTHDAY=06";

        LocalDateTime date = LocalDateTime.of(2025, 11, 6, 13, 27);

        LocalDateTime newDate = this.recurringRuleService.convertRecurringRuleToDate(recurringRule,
                date);

        LocalDateTime expectedDate = date.plusYears(1);

        assertEquals(expectedDate.toLocalDate(), newDate.toLocalDate());
    }

}
