package com.github.haseoo.ocm.structure;

import com.github.haseoo.ocm.api.annotation.CsvFormatter;
import com.github.haseoo.ocm.structure.enums.HeaderType;
import lombok.Value;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

@Value
public class CsvColumn {
    String rowName;
    String fieldName;
    Class<?> javaClass;
    String formatter;
    HeaderType headerType;
    int index;

    public static CsvColumn getInstance(Field field, int index, HeaderType type) {
        return new CsvColumn(field.getName(), field.getName(), field.getType(), getFormatter(field), type, index);
    }

    public static CsvColumn getInstance(Field field, int index, HeaderType type,  String className) {
        return new CsvColumn(String.format("%s$%s", field.getName(), className), field.getName(), field.getType(), getFormatter(field), type, index);
    }
    private  static String getFormatter(Field field) {
        return Arrays.stream(field.getDeclaredAnnotations())
                .filter(a -> a.annotationType().equals(CsvFormatter.class))
                .findAny()
                .map(a -> ((CsvFormatter)a).value())
                .orElse(null);
    }
}
