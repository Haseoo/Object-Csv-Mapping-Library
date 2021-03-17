package com.github.haseoo.ocm.structure.cell;

import com.github.haseoo.ocm.internal.MappingContext;
import lombok.Value;

@Value
public class CsvDirectType<T> implements CsvData {
    MappingContext mappingContext;
    String value;
    String formatter;
    Class<T> clazz;
    @Override
    public Object resolveToObject() {
        var obj = mappingContext.convertToObject(clazz, value, formatter);
        return clazz.cast(obj);
    }
}
