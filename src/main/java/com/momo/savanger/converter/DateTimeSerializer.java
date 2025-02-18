package com.momo.savanger.converter;

import com.fasterxml.jackson.databind.util.StdConverter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component
public class DateTimeSerializer extends StdConverter<LocalDateTime, String> {

    @Override
    public String convert(LocalDateTime value) {
        return value.format(DateTimeFormatter.ISO_DATE_TIME);
    }
}
