package com.github.haseoo.ocm.api.exceptions;

public class IdFiledNotFoundException extends CsvMappingException {
    public IdFiledNotFoundException(Class<?> clazz) {
        super(String.format("Class %s has no field marked as id", clazz.getCanonicalName()));
    }
}
