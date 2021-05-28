package com.github.haseoo.ocm.internal.converter;

import com.github.haseoo.ocm.api.converter.TypeConverter;

public class IntConverter implements TypeConverter<Integer> {
    @Override
    public Integer convertToTypeObject(String value, String formatter) {
        return Integer.parseInt(value);
    }

    @Override
    public String convertToString(Integer value, String formatter) {
        return value.toString();
    }
}
