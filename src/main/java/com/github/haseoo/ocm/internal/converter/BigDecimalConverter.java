package com.github.haseoo.ocm.internal.converter;

import com.github.haseoo.ocm.api.converter.TypeConverter;
import lombok.SneakyThrows;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class BigDecimalConverter implements TypeConverter<BigDecimal> {

    @SneakyThrows
    @Override
    public BigDecimal convertToTypeObject(String value, String formatter) {
        if (formatter == null) {
            return new BigDecimal(value);
        }
        var format = NumberFormat.getInstance(Locale.forLanguageTag(formatter));
        return new BigDecimal(format.parse(value).byteValue());
    }

    @Override
    public String convertToString(BigDecimal value, String formatter) {
        if (formatter == null) {
            return value.toString();
        }
        var format = NumberFormat.getInstance(Locale.forLanguageTag(formatter));
        return format.format(value);
    }
}
