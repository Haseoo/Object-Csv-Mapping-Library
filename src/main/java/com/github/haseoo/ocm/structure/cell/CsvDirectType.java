package com.github.haseoo.ocm.structure.cell;

import com.github.haseoo.ocm.internal.MappingContext;
import lombok.Value;

@Value
public class CsvDirectType<T> implements CsvData {
    MappingContext mappingContext;
    String stringValue;
    T objectValue;
    String formatter;
    Class<T> clazz;

    @Override
    public Object resolveToObject() {
        if (stringValue == null) {
            return objectValue;
        }
        var obj = mappingContext.convertToObject(clazz, stringValue, formatter);
        return clazz.cast(obj);
    }

    @Override
    public String resolveString() {
        if (objectValue == null) {
            return stringValue;
        }
        return mappingContext.convertToString(clazz, objectValue, formatter);
    }

    public static<T> CsvDirectType<T> getInstance(MappingContext mappingContext,
                                               Class<T> clazz,
                                               T obj,
                                               String formatter) {
        return new CsvDirectType<>(mappingContext,
                null,
                obj,
                formatter,
                clazz);
    }

    public static<T> CsvDirectType<T> getInstance(MappingContext mappingContext,
                                                  Class<T> clazz,
                                                  String string,
                                                  String formatter) {
        return new CsvDirectType<>(mappingContext,
                string,
                null,
                formatter,
                clazz);
    }
}
