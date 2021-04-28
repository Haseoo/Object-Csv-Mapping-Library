package com.github.haseoo.ocm.structure.entities.fields;

import com.github.haseoo.ocm.api.exceptions.ConstraintViolationException;
import com.github.haseoo.ocm.api.exceptions.CsvMappingException;
import com.github.haseoo.ocm.internal.MappingContext;
import com.github.haseoo.ocm.internal.utils.ReflectionUtils;
import com.github.haseoo.ocm.structure.resolvers.EntityIdResolver;
import org.apache.commons.lang3.NotImplementedException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public final class CsvManyToManyField implements CsvField {
    private final EntityIdResolver entityIdResolver;
    private final CsvRelation beginRelation;
    private final CsvRelation endRelation;
    private final MappingContext mappingContext;

    public CsvManyToManyField(EntityIdResolver entityIdResolver,
                              MappingContext mappingContext,
                              Field beginRelationField,
                              Class<?> beginRelationFieldType,
                              Field endRelationField,
                              Class<?> endRelationFieldType) {
        this.entityIdResolver = entityIdResolver;
        this.beginRelation = new CsvRelation(beginRelationFieldType, beginRelationField);
        this.endRelation = new CsvRelation(endRelationFieldType, endRelationField);
        this.mappingContext = mappingContext;
    }

    @Override
    public String toCsvStringValue(Object value) throws CsvMappingException {
        var valueAsList = (Collection<Object>) value;
        var stringIds = new ArrayList<String>();
        for (Object obj : valueAsList) {
            stringIds.add(entityIdResolver.getObjectId(obj, beginRelation.getType()));
        }
        return "[" + String.join(mappingContext.getSplitter(), stringIds) + "]";
    }

    @Override
    public Object toObjectValue(String value) throws CsvMappingException {
        throw new NotImplementedException("TODO"); //TODO
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
        return true;
    }

    @Override
    public void validateAndAddToContext(Object entityObject,
                                        Consumer<Object> appendObject) throws
            NoSuchMethodException,
            InvocationTargetException,
            IllegalAccessException,
            ConstraintViolationException {
        var relationEndObjects = (Collection<?>) endRelation.getType()
                .getMethod(ReflectionUtils.getGetterName(beginRelation.getFieldName()))
                .invoke(entityObject);
        if (relationEndObjects != null) {
            for (Object relationEndObject : relationEndObjects) {
                var relationBeginFromEndObject = (Collection<?>) beginRelation.getType()
                        .getMethod(ReflectionUtils.getGetterName(endRelation.getFieldName()))
                        .invoke(relationEndObject);
                if (relationBeginFromEndObject.stream().noneMatch(obj -> obj == entityObject)) {
                    throw new ConstraintViolationException(beginRelation.getType(), endRelation.getFieldName());
                }
            }
            relationEndObjects.forEach(appendObject);
        }
    }
}
