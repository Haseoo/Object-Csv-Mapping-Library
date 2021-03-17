package com.github.haseoo.ocm.internal.converter;

import com.github.haseoo.ocm.api.converter.TypeConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeConverter implements TypeConverter<LocalDateTime> {
    @Override
    public LocalDateTime convertToType(String value, String formatter) {
        return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(formatter));
    }

    @Override
    public String convertToString(LocalDateTime value, String formatter) {
        return value.format(DateTimeFormatter.ofPattern(formatter));
    }
}
