package com.github.haseoo.ocm.structure.entities.fields;

import com.github.haseoo.ocm.api.annotation.CsvManyToOne;
import com.github.haseoo.ocm.api.annotation.CsvOneToMany;
import com.github.haseoo.ocm.api.exceptions.*;
import com.github.haseoo.ocm.internal.utils.ReflectionUtils;
import com.github.haseoo.ocm.structure.resolvers.EntityIdResolver;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

import static com.github.haseoo.ocm.internal.utils.ReflectionUtils.getRelationField;
import static com.github.haseoo.ocm.internal.utils.ReflectionUtils.validateRelationClass;

public final class CsvOneToManyField implements CsvField {
    private final EntityIdResolver entityIdResolver;
    private final CsvRelation beginRelation;
    private final CsvRelation endRelation;

    private CsvOneToManyField(EntityIdResolver entityIdResolver,
                              Field beginRelationField,
                              Field endRelationField,
                              Class<?> endRelationType) {
        this.entityIdResolver = entityIdResolver;
        this.beginRelation = new CsvRelation(beginRelationField);
        this.endRelation = new CsvRelation(endRelationType, endRelationField);
    }

    @Override
    public String toCsvStringValue(Object entityObject) throws CsvMappingException {
        var value = ReflectionUtils.getFieldValue(entityObject, getFieldName());
        return value != null ? entityIdResolver.getObjectId(value, beginRelation.getType()) : "";
    }

    @Override
    public Object toObjectValue(Map<String, String> fields) throws CsvMappingException {
        return entityIdResolver.getObjectById(fields.get(getColumnName()), beginRelation.getType());
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
        var relationEndObject = endRelation.getType()
                .getMethod(ReflectionUtils.getGetterName(beginRelation.getFieldName()))
                .invoke(entityObject);
        if (relationEndObject != null) {
            var relationBeginFromEndObject = (Collection<?>) beginRelation.getType()
                    .getMethod(ReflectionUtils.getGetterName(endRelation.getFieldName()))
                    .invoke(relationEndObject);
            if (relationBeginFromEndObject.stream().noneMatch(obj -> obj == entityObject)) {
                throw new ConstraintViolationException(beginRelation.getType(), endRelation.getFieldName());
            }
            appendObject.accept(relationEndObject);
        }
    }

    public static CsvOneToManyField newInstance(Class<?> relationBeginEntityType,
                                                Field relationBeginField,
                                                EntityIdResolver resolverContext) throws
            ClassIsNotAnCsvEntity,
            RelationEndNotPresentException,
            FieldIsNotACollectionException {
        var fieldAnnotation = relationBeginField.getAnnotation(CsvOneToMany.class);
        var relationEndEntityType = relationBeginField.getType();
        validateRelationClass(relationEndEntityType);
        Field endRelationField = getRelationField(relationEndEntityType,
                fieldAnnotation.fieldName(),
                fieldAnnotation.annotationType());
        if (!ReflectionUtils.isClassCollection(endRelationField.getType())) {
            throw new FieldIsNotACollectionException(relationEndEntityType,
                    fieldAnnotation.fieldName());
        }
        var endRelationFieldCollectionType = ReflectionUtils.getActualTypeArgument(endRelationField);
        if (!endRelationField.isAnnotationPresent(CsvManyToOne.class) ||
                endRelationFieldCollectionType != relationBeginEntityType) {
            throw new RelationEndNotPresentException(fieldAnnotation.annotationType(),
                    relationEndEntityType,
                    fieldAnnotation.fieldName());
        }
        return new CsvOneToManyField(resolverContext,
                relationBeginField,
                endRelationField,
                endRelationFieldCollectionType);
    }
}
