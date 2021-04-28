package com.github.haseoo.ocm.structure.cell;

import com.github.haseoo.ocm.internal.MappingContext;
import com.github.haseoo.ocm.structure.CsvColumn;
import lombok.Value;

@Value
public class CsvDirectType implements CsvData {
    MappingContext mappingContext;
    CsvColumn column;
    String stringValue;
    Object objectValue;
    String formatter;

    @Override
    public Object resolveToObject() {
        if (stringValue == null) {
            return objectValue;
        }
        var obj = mappingContext.convertToObject(column.getJavaClass(), stringValue.replace("\"", ""), formatter);
        return column.getJavaClass().cast(obj);
    }

    @Override
    public String resolveString() {
        if (objectValue == null) {
            return stringValue;
        }
        return mappingContext.convertToString(column.getJavaClass(), objectValue, formatter);
    }

    public static CsvDirectType getInstance(MappingContext mappingContext,
                                            CsvColumn column,
                                            Object obj,
                                            String formatter) {
        return new CsvDirectType(mappingContext,
                column,
                null,
                obj,
                formatter);
    }

    public static CsvDirectType getInstance(MappingContext mappingContext,
                                            CsvColumn column,
                                            String stringValue,
                                            String formatter) {
        return new CsvDirectType(mappingContext,
                column,
                stringValue,
                null,
                formatter);
    }
}
