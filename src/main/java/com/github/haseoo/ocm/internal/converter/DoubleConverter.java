package com.github.haseoo.ocm.internal.converter;

import com.github.haseoo.ocm.api.converter.TypeConverter;
import lombok.SneakyThrows;

import java.text.NumberFormat;
import java.util.Locale;

public class DoubleConverter implements TypeConverter<Double> {

    @SneakyThrows
    @Override
    public Double convertToTypeObject(String value, String formatter) {
        if (formatter == null) {
            return Double.parseDouble(value);
        }
        var format = NumberFormat.getInstance(Locale.forLanguageTag(formatter));
        return format.parse(value).doubleValue();
    }

    @Override
    public String convertToString(Double value, String formatter) {
        if (formatter == null) {
            return value.toString();
        }
        var format = NumberFormat.getInstance(Locale.forLanguageTag(formatter));
        return format.format(value);
    }
}
