package com.github.haseoo.ocm.internal.converter;

import com.github.haseoo.ocm.api.converter.TypeConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateConverter implements TypeConverter<LocalDate> {
    @Override
    public LocalDate convertToType(String value, String formatter) {
        return LocalDate.parse(value, DateTimeFormatter.ofPattern(formatter));
    }

    @Override
    public String convertToString(LocalDate value, String formatter) {
        return value.format(DateTimeFormatter.ofPattern(formatter));
    }
}
