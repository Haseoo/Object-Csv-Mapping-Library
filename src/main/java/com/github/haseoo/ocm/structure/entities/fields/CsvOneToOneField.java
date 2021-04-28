package com.github.haseoo.ocm.structure.entities.fields;

import com.github.haseoo.ocm.api.annotation.CsvOneToOne;
import com.github.haseoo.ocm.api.exceptions.ConstraintViolationException;
import com.github.haseoo.ocm.api.exceptions.CsvMappingException;
import com.github.haseoo.ocm.internal.utils.ReflectionUtils;
import com.github.haseoo.ocm.structure.resolvers.EntityIdResolver;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

public final class CsvOneToOneField implements CsvField {
    private final EntityIdResolver entityIdResolver;
    private final CsvRelation beginRelation;
    private final CsvRelation endRelation;
    private final boolean appendToFile;

    public CsvOneToOneField(EntityIdResolver entityIdResolver,
                            Field beginRelationField,
                            Field endRelationField) {
        this.entityIdResolver = entityIdResolver;
        this.beginRelation = new CsvRelation(beginRelationField);
        this.endRelation = new CsvRelation(endRelationField);
        appendToFile = beginRelationField.getAnnotation(CsvOneToOne.class).appendToFile();
    }

    @Override
    public String toCsvStringValue(Object value) throws CsvMappingException {
        return entityIdResolver.getObjectId(value, beginRelation.getType());
    }

    @Override
    public Object toObjectValue(String value) throws CsvMappingException {
        return entityIdResolver.getObjectById(value, beginRelation.getType());
    }

    @Override
    public String getFieldName() {
        return beginRelation.getFieldName();
    }

    @Override
    public String getColumnName() {
        return beginRelation.getColumnName();
    }

    @Override
    public Class<?> getFieldType() {
        return beginRelation.getType();
    }

    @Override
    public boolean appendToFile() {
        return appendToFile;
    }

    @Override
    public void validateAndAddToContext(Object entityObject,
                                        Consumer<Object> appendObject) throws
            NoSuchMethodException,
            InvocationTargetException,
            IllegalAccessException,
            ConstraintViolationException {
        var relationEndObject = endRelation.getType()
                .getMethod(ReflectionUtils.getGetterName(beginRelation.getFieldName()))
                .invoke(entityObject);
        if (relationEndObject != null) {
            var relationBeginFromEndObject = beginRelation.getType()
                    .getMethod(ReflectionUtils.getGetterName(endRelation.getFieldName()))
                    .invoke(relationEndObject);
            if (relationBeginFromEndObject != entityObject) {
                throw new ConstraintViolationException(beginRelation.getType(), endRelation.getFieldName());
            }
            appendObject.accept(relationEndObject);
        }
    }
}
