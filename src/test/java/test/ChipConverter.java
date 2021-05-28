package test;

import com.github.haseoo.ocm.api.converter.TypeConverter;
import test.data.Chip;

import java.util.UUID;

public class ChipConverter implements TypeConverter<Chip> {
    @Override
    public Chip convertToTypeObject(String value, String formatter) {
        return new Chip(UUID.fromString(value));
    }

    @Override
    public String convertToString(Chip value, String formatter) {
        return value.getUuid().toString();
    }
}
