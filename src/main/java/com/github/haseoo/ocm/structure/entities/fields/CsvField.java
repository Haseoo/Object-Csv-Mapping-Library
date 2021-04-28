package com.github.haseoo.ocm.structure.entities.fields;

import com.github.haseoo.ocm.api.exceptions.ConstraintViolationException;
import com.github.haseoo.ocm.api.exceptions.CsvMappingException;
import com.github.haseoo.ocm.internal.utils.ObjectToStringResolverContext;

import java.lang.reflect.InvocationTargetException;

public interface CsvField {
    String toCsvStringValue(Object value) throws CsvMappingException;

    Object toObjectValue(String value) throws CsvMappingException;

    String getFieldName();

    String getColumnName();

    Class<?> getFieldType();

    boolean appendToFile();

    void validateAndAddToContext(Object entityObject,
                                 ObjectToStringResolverContext context) throws
            NoSuchMethodException,
            InvocationTargetException,
            IllegalAccessException,
            ConstraintViolationException;
}
