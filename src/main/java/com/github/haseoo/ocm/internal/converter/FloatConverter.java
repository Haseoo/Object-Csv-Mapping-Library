package com.github.haseoo.ocm.internal.converter;

import com.github.haseoo.ocm.api.converter.TypeConverter;
import lombok.SneakyThrows;

import java.text.NumberFormat;
import java.util.Locale;

public class FloatConverter implements TypeConverter<Float> {

    @SneakyThrows
    @Override
    public Float convertToType(String value, String formatter) {
        if (formatter == null) {
            return Float.parseFloat(value);
        }
        var format = NumberFormat.getInstance(Locale.forLanguageTag(formatter));
        return format.parse(value).floatValue();
    }

    @Override
    public String convertToString(Float value, String formatter) {
        if (formatter == null) {
            return value.toString();
        }
        var format = NumberFormat.getInstance(Locale.forLanguageTag(formatter));
        return format.format(value);
    }
}
