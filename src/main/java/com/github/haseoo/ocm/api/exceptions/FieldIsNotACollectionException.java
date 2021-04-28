package com.github.haseoo.ocm.api.exceptions;

public class FieldIsNotACollectionException extends CsvMappingException {
    public FieldIsNotACollectionException(Class<?> containingClass, String fieldName) {
        super(String.format("Field %s of class %s is not a collection.", fieldName, containingClass.getCanonicalName()));
    }
}
