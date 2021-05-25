package com.github.haseoo.ocm.api.exceptions;

public class CollectionNotInitializedException extends CsvMappingException {
    public CollectionNotInitializedException(String collectionFieldName, Class<?> clazz) {
        super(String.format("Collection with name %s in class %s is not initialized in default constructor.",
                collectionFieldName, clazz.getCanonicalName()));
    }
}
