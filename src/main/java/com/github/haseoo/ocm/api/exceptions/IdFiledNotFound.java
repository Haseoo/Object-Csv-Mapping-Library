package com.github.haseoo.ocm.api.exceptions;

public class IdFiledNotFound extends CsvMappingException {
    public IdFiledNotFound(Class<?> clazz) {
        super(String.format("Class %s has not an id field", clazz.getCanonicalName()));
    }
}
