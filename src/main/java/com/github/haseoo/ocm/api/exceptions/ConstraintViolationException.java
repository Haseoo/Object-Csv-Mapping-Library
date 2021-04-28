package com.github.haseoo.ocm.api.exceptions;

public class ConstraintViolationException extends CsvMappingException {

    public ConstraintViolationException(Class<?> beginRelationType, String relationName) {
        super(String.format("Constraint violation of relation %s of class %s",
                relationName,
                beginRelationType.getCanonicalName()));
    }
}
