package com.github.haseoo.ocm.structure.entities.fields;

import com.github.haseoo.ocm.api.exceptions.ConstraintViolationException;
import com.github.haseoo.ocm.api.exceptions.CsvMappingException;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.function.Consumer;

public interface CsvField {
    String toCsvStringValue(Object entityObject) throws CsvMappingException;

    Object toObjectValue(Map<String, String> row) throws CsvMappingException;

    String getFieldName();

    String getColumnName();

    Class<?> getFieldType();

    boolean appendToFile();

    void validateAndAddToContext(Object entityObject,
                                 Consumer<Object> appendObject) throws
            NoSuchMethodException,
            InvocationTargetException,
            IllegalAccessException,
            ConstraintViolationException;
}
