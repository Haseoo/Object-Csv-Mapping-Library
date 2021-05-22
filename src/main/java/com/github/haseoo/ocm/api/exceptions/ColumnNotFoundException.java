package com.github.haseoo.ocm.api.exceptions;

public class ColumnNotFoundException extends CsvMappingException {
    public ColumnNotFoundException(String columnName) {
        super(String.format("Column with name %s not found.", columnName));
    }
}
