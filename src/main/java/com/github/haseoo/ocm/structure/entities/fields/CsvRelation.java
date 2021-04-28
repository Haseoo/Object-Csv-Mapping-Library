package com.github.haseoo.ocm.structure.entities.fields;

import com.github.haseoo.ocm.api.annotation.CsvColumn;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.lang.reflect.Field;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CsvRelation {
    String fieldName;
    String columnName;
    Class<?> type;

    public CsvRelation(Field field) {
        this(field.getType(), field);
    }

    public CsvRelation(Class<?> type, Field field) {
        this(field.getName(),
                field.isAnnotationPresent(CsvColumn.class) ?
                        field.getAnnotation(CsvColumn.class).name() :
                        field.getName(),
                type);
    }
}
