package com.github.haseoo.ocm.internal.converter;

import com.github.haseoo.ocm.api.converter.TypeConverter;

public class LongConverter implements TypeConverter<Long> {
    @Override
    public Long convertToType(String value, String formatter) {
        return Long.parseLong(value);
    }

    @Override
    public String convertToString(Long value, String formatter) {
        return value.toString();
    }
}
