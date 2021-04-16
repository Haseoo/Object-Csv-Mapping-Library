package com.github.haseoo.ocm.api.exceptions;

import com.github.haseoo.ocm.internal.MappingContext;

public class IdFiledNotFound extends CsvMappingException {
    public IdFiledNotFound(Class<?> clazz) {
        super(String.format("Class %s has not id field", clazz.getCanonicalName()));
    }
}
