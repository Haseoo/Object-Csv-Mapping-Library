package com.github.haseoo.ocm.structure.entities.fields;

import com.github.haseoo.ocm.api.exceptions.CsvMappingException;

import java.util.regex.Pattern;

public interface CsvField {
    String toCsvStringValue(Object value) throws CsvMappingException;
    Object toObjectValue(String value) throws CsvMappingException;
    String getFieldName();
    String getColumnName();
    Class<?> getFieldType();

    static String getTypeSuffix(Class<?> type) {
        return String.format("{@%s}", type.getCanonicalName());
    }
}
