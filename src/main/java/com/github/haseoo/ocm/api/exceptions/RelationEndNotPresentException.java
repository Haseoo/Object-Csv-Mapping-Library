package com.github.haseoo.ocm.api.exceptions;

import java.lang.annotation.Annotation;

public class RelationEndNotPresentException extends CsvMappingException {
    public RelationEndNotPresentException(Class<? extends Annotation> annotation,
                                          Class<?> invalidType,
                                          String fieldNameThatIsNotPresent) {
        super(String.format("Field with name %s and annotation %s in not present in class %s",
                fieldNameThatIsNotPresent,
                annotation.getCanonicalName(),
                invalidType.getCanonicalName()));
    }
}
