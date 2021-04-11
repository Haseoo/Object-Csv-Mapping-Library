package com.github.haseoo.ocm.structure;

import com.github.haseoo.ocm.structure.enums.HeaderType;
import lombok.Value;

import java.lang.reflect.Field;

@Value
public class CsvColumn {
    String rowName;
    String fieldName;
    Class<?> javaClass;
    HeaderType headerType;
    int index;

    public static CsvColumn getInstance(Field field, int index, HeaderType type) {
        return new CsvColumn(field.getName(), field.getName(), field.getType(), type, index);
    }

    public static CsvColumn getInstance(Field field, int index, HeaderType type,  String className) {
        return new CsvColumn(String.format("%s$%s", field.getName(), className), field.getName(), field.getType(), type, index);
    }
}
