package com.github.haseoo.ocm.structure.entities.fields;

import com.github.haseoo.ocm.api.exceptions.ConstraintViolationException;
import com.github.haseoo.ocm.api.exceptions.CsvMappingException;
import com.github.haseoo.ocm.internal.utils.ObjectToStringResolverContext;
import com.github.haseoo.ocm.internal.utils.ReflectionUtils;
import com.github.haseoo.ocm.structure.resolvers.EntityIdResolver;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public final class CsvOneToManyField implements CsvField {
    private final EntityIdResolver entityIdResolver;
    private final CsvRelation beginRelation;
    private final CsvRelation endRelation;

    public CsvOneToManyField(EntityIdResolver entityIdResolver,
                             Field beginRelationField,
                             Field endRelationField) {
        this.entityIdResolver = entityIdResolver;
        this.beginRelation = new CsvRelation(beginRelationField);
        this.endRelation = new CsvRelation(endRelationField);
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
        return true;
    }

    @Override
    public void validateAndAddToContext(Object entityObject,
                                        ObjectToStringResolverContext context) throws
            NoSuchMethodException,
            InvocationTargetException,
            IllegalAccessException,
            ConstraintViolationException {
        var relationEndObject = beginRelation.getType()
                .getMethod(ReflectionUtils.getGetterName(beginRelation.getFieldName()))
                .invoke(entityObject);
        if (relationEndObject != null) {
            var relationBeginFromEndObject = (Collection<?>) endRelation.getType()
                    .getMethod(ReflectionUtils.getGetterName(endRelation.getFieldName()))
                    .invoke(relationEndObject);
            if (relationBeginFromEndObject.stream().noneMatch(obj -> obj == entityObject)) {
                throw new ConstraintViolationException(beginRelation.getType(), beginRelation.getFieldName());
            }
            context.addObjectToResolve(relationEndObject);
        }
    }
}
