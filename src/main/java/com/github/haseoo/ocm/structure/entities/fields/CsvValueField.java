package com.github.haseoo.ocm.structure.entities.fields;

import com.github.haseoo.ocm.api.annotation.CsvColumn;
import com.github.haseoo.ocm.api.annotation.CsvFormatter;
import com.github.haseoo.ocm.api.exceptions.CsvMappingException;
import com.github.haseoo.ocm.internal.ConverterContext;

import java.lang.reflect.Field;

public class CsvValueField implements CsvField {
    private final Class<?> fieldType;
    private final String fieldName;
    private final String columnName;
    private final String formatter;

    private final ConverterContext converterContext;

    public CsvValueField(ConverterContext converterContext, Field field) {
        this.converterContext = converterContext;
        fieldName = field.getName();
        this.fieldType = field.getType();
        var columnAnnotation = field.getAnnotation(CsvColumn.class);
        columnName = columnAnnotation != null ? columnAnnotation.name() : fieldName;
        var formatterAnnotation = field.getAnnotation(CsvFormatter.class);
        formatter = formatterAnnotation != null ? formatterAnnotation.value() : null;
    }

    @Override
    public String toCsvStringValue(Object value) throws CsvMappingException {
        return converterContext.convertToString(fieldType, value, formatter);
    }

    @Override
    public Object toObjectValue(String value) throws CsvMappingException {
        return converterContext.convertToObject(fieldType, value, formatter);
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String getColumnName() {
        return columnName;
    }

    @Override
    public Class<?> getFieldType() {
        return fieldType;
    }


}
