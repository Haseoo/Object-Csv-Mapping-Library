package com.github.haseoo.ocm.internal.converter;

import com.github.haseoo.ocm.api.converter.TypeConverter;

public class StringConverter implements TypeConverter<String> {
    @Override
    public String convertToType(String value, String formatter) {
        return value;
    }

    @Override
    public String convertToString(String value, String formatter) {
        return value;
    }
}
