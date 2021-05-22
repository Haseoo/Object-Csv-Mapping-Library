package com.github.haseoo.ocm.structure.entities.fields;

import com.github.haseoo.ocm.api.annotation.CsvManyToMany;
import com.github.haseoo.ocm.api.exceptions.*;
import com.github.haseoo.ocm.internal.utils.ReflectionUtils;
import com.github.haseoo.ocm.structure.resolvers.EntityIdResolver;
import org.apache.commons.lang3.NotImplementedException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

import static com.github.haseoo.ocm.internal.utils.ReflectionUtils.*;

public final class CsvManyToManyField implements CsvField {
    private final EntityIdResolver entityIdResolver;
    private final CsvRelation beginRelation;
    private final CsvRelation endRelation;
    private final String delimiter;

    private CsvManyToManyField(EntityIdResolver entityIdResolver,
                               String delimiter,
                               Field beginRelationField,
                               Class<?> beginRelationFieldType,
                               Field endRelationField,
                               Class<?> endRelationFieldType) {
        this.entityIdResolver = entityIdResolver;
        this.beginRelation = new CsvRelation(beginRelationFieldType, beginRelationField);
        this.endRelation = new CsvRelation(endRelationFieldType, endRelationField);
        this.delimiter = delimiter;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String toCsvStringValue(Object entityObject) throws CsvMappingException {
        var value = ReflectionUtils.getFieldValue(entityObject, getFieldName());
        var valueAsList = (Collection<Object>) value;
        var stringIds = new ArrayList<String>();
        for (Object obj : valueAsList) {
            stringIds.add(entityIdResolver.getObjectId(obj, beginRelation.getType()));
        }
        return "[" + String.join(delimiter, stringIds) + "]";
    }

    @Override
    public Object toObjectValue(Map<String, String> fields) throws CsvMappingException {
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

    public static CsvManyToManyField newInstance(Class<?> relationBeginEntityType,
                                                 Field relationBeginField,
                                                 EntityIdResolver resolverContext,
                                                 String delimiter) throws
            FieldIsNotACollectionException,
            RelationEndNotPresentException, ClassIsNotAnCsvEntity {
        var fieldAnnotation = relationBeginField.getAnnotation(CsvManyToMany.class);
        validateCollectionRelation(relationBeginField.getType(), fieldAnnotation.fieldName());
        var relationEndEntityType = ReflectionUtils.getActualTypeArgument(relationBeginField);
        validateRelationClass(relationEndEntityType);
        Field endRelationField = getRelationField(relationEndEntityType,
                fieldAnnotation.fieldName(),
                fieldAnnotation.annotationType());
        var endRelationFieldCollectionType = ReflectionUtils.getActualTypeArgument(endRelationField);
        if (!endRelationField.isAnnotationPresent(CsvManyToMany.class) ||
                endRelationFieldCollectionType != relationBeginEntityType) {
            throw new RelationEndNotPresentException(fieldAnnotation.annotationType(),
                    relationEndEntityType,
                    fieldAnnotation.fieldName());
        }
        return new CsvManyToManyField(resolverContext,
                delimiter,
                relationBeginField,
                relationEndEntityType,
                endRelationField,
                relationBeginEntityType);
    }
}
