package com.github.haseoo.ocm.structure.entities.fields;

import com.github.haseoo.ocm.api.annotation.CsvColumn;
import com.github.haseoo.ocm.api.annotation.CsvFormatter;
import com.github.haseoo.ocm.api.exceptions.ColumnNotFoundException;
import com.github.haseoo.ocm.api.exceptions.CsvMappingException;
import com.github.haseoo.ocm.internal.ConverterContext;
import com.github.haseoo.ocm.internal.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Consumer;

public final class CsvValueField implements CsvField {
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
    public String toCsvStringValue(Object entityObject) throws CsvMappingException {
        var value = ReflectionUtils.getFieldValue(entityObject, fieldName);
        return value != null ? converterContext.convertToString(fieldType, value, formatter) : "";
    }

    @Override
    public void setObjectField(Object dest, Map<String, String> row) throws CsvMappingException {
        if (!row.containsKey(getColumnName())) {
            throw new ColumnNotFoundException(getColumnName());
        }
        var value = toObjectValue(row.get(getColumnName()));
        ReflectionUtils.setObjectFiled(dest, value, getFieldName(), getFieldType());
    }

    public Object toObjectValue(Map<String, String> row) throws CsvMappingException {
        if (!row.containsKey(getColumnName())) {
            throw new ColumnNotFoundException(getColumnName());
        }
        return toObjectValue(row.get(getColumnName()));
    }

    public Object toObjectValue(String value) throws CsvMappingException {
        return value.equals("") ? null : converterContext.convertToObject(fieldType, value, formatter);
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

    @Override
    public boolean appendToFile() {
        return true;
    }

    @Override
    public void validateAndAddToContext(Object entityObject, Consumer<Object> appendObject) {
        //Not necessary
    }


}
