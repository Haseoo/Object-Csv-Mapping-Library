package com.github.haseoo.ocm.structure.entities.fields;

import java.util.regex.Pattern;

public interface CsvField {
    String toCsvStringValue(Object value);
    Object toObjectValue(String value);
    String getFieldName();
    String getColumnName();
    Class<?> getFieldType();

    static String getTypeSuffix(Class<?> type) {
        return String.format("{@%s}", type.getCanonicalName());
    }

    static boolean hasStringTypeInfo(String value) {
        return TYPE_PATTERN.matcher(value).find();
    }

    static String removeTypeInfo(String value) {
        return TYPE_PATTERN.matcher(value).replaceAll("");
    }

    static String getTypeString(String value) {
        return value.
                replaceFirst(removeTypeInfo(value), "")
                .replace("@{", "")
                .replace("}", "");
    }

    Pattern TYPE_PATTERN = Pattern.compile("@\\{([^}]*)}");
}
