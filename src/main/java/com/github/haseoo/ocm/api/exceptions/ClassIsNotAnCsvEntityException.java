package com.github.haseoo.ocm.api.exceptions;

public class ClassIsNotAnCsvEntityException extends CsvMappingException {
    public ClassIsNotAnCsvEntityException(Class<?> clazz) {
        super(String.format("Class %s is not an CSV entity", clazz.getCanonicalName()));
    }
}
