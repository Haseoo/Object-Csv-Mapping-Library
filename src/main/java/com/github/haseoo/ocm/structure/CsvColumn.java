package com.github.haseoo.ocm.structure;

import com.github.haseoo.ocm.api.annotation.CsvFormatter;
import com.github.haseoo.ocm.structure.enums.HeaderType;
import lombok.Value;

import java.lang.reflect.Field;

@Value
public class CsvColumn {
    String colName;
    String fieldName;
    Class<?> javaClass;
    String formatter;
    HeaderType headerType;
    int index;

    public static CsvColumn getInstance(Field field, int index, HeaderType type) {
        return new CsvColumn(getColName(field), field.getName(), field.getType(), getFormatter(field), type, index);
    }

    public static CsvColumn getInstance(Field field, int index, HeaderType type,  String className) {
        return new CsvColumn(String.format("%s$%s", getColName(field), className), field.getName(), field.getType(), getFormatter(field), type, index);
    }
    private static String getFormatter(Field field) {
        var formatAnnotation = field.getDeclaredAnnotation(CsvFormatter.class);
        if (formatAnnotation == null) {
            return null;
        }
        return formatAnnotation.value();
    }
    private static String getColName(Field field) {
        var columnAnnotation = field.getDeclaredAnnotation(com.github.haseoo.ocm.api.annotation.CsvColumn.class);
        if (columnAnnotation == null) {
            return field.getName();
        }
        return columnAnnotation.name();
    }
}
