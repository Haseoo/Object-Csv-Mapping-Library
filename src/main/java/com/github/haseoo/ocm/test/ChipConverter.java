package com.github.haseoo.ocm.test;

import com.github.haseoo.ocm.api.converter.TypeConverter;
import com.github.haseoo.ocm.test.data.Chip;

import java.util.UUID;

public class ChipConverter implements TypeConverter<Chip> {
    @Override
    public Chip convertToType(String value, String formatter) {
        return new Chip(UUID.fromString(value));
    }

    @Override
    public String convertToString(Chip value, String formatter) {
        return value.getUuid().toString();
    }
}
