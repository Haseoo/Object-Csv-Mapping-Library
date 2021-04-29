package com.github.haseoo.ocm.internal.converter;

import com.github.haseoo.ocm.api.converter.TypeConverter;

import java.util.UUID;

public class UUIDConverter implements TypeConverter<UUID> {
    @Override
    public UUID convertToType(String value, String formatter) {
        return UUID.fromString(value);
    }

    @Override
    public String convertToString(UUID value, String formatter) {
        return value.toString();
    }
}
