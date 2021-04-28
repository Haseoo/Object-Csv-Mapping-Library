package com.github.haseoo.ocm.api.exceptions;

public class ClassIsNotAnCsvEntity extends CsvMappingException {
    public ClassIsNotAnCsvEntity(Class<?> clazz) {
        super(String.format("Class %s is not an CSV entity", clazz.getCanonicalName()));
    }
}
