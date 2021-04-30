package com.github.haseoo.ocm.structure.entities.fields;

import com.github.haseoo.ocm.api.annotation.CsvManyToOne;
import com.github.haseoo.ocm.api.annotation.CsvOneToMany;
import com.github.haseoo.ocm.api.exceptions.ClassIsNotAnCsvEntity;
import com.github.haseoo.ocm.api.exceptions.ConstraintViolationException;
import com.github.haseoo.ocm.api.exceptions.FieldIsNotACollectionException;
import com.github.haseoo.ocm.api.exceptions.RelationEndNotPresentException;
import com.github.haseoo.ocm.internal.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.function.Consumer;

import static com.github.haseoo.ocm.internal.utils.ReflectionUtils.*;

public final class CsvManyToOneField implements CsvField {
    private final CsvRelation beginRelation;
    private final CsvRelation endRelation;

    private CsvManyToOneField(Field beginRelationField,
                              Class<?> beginRelationFieldType,
                              Field endRelationField) {
        this.beginRelation = new CsvRelation(beginRelationFieldType, beginRelationField);
        this.endRelation = new CsvRelation(endRelationField);
    }

    @Override
    public String toCsvStringValue(Object value) {
        throw new AssertionError("Many to one has no string representation, check appendToFile() first");
    }

    @Override
    public Object toObjectValue(String value) {
        throw new AssertionError("This relation is resolved by one-to-many side!");
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
        return false;
    }

    @Override
    public void validateAndAddToContext(Object entityObject,
                                        Consumer<Object> appendObject) throws
            NoSuchMethodException,
            InvocationTargetException,
            IllegalAccessException,
            ConstraintViolationException {
        var relationEndObjects = (Collection<?>) endRelation.getType()
                .getMethod(ReflectionUtils.getGetterName(getFieldName()))
                .invoke(entityObject);
        if (relationEndObjects != null) {
            for (Object relationEndObject : relationEndObjects) {
                var relationBeginFromEndObject = beginRelation.getType()
                        .getMethod(ReflectionUtils.getGetterName(endRelation.getFieldName()))
                        .invoke(relationEndObject);
                if (relationBeginFromEndObject != entityObject) {
                    throw new ConstraintViolationException(beginRelation.getType(), endRelation.getFieldName());
                }
            }
            relationEndObjects.forEach(appendObject);
        }
    }

    public static CsvManyToOneField newInstance(Class<?> relationBeginEntityType,
                                                Field relationBeginField) throws
            FieldIsNotACollectionException,
            RelationEndNotPresentException, ClassIsNotAnCsvEntity {
        var fieldAnnotation = relationBeginField.getAnnotation(CsvManyToOne.class);
        validateCollectionRelation(relationBeginField.getType(), fieldAnnotation.fieldName());
        var relationEndEntityType = ReflectionUtils.getActualTypeArgument(relationBeginField);
        validateRelationClass(relationEndEntityType);
        Field endRelationField = getRelationField(relationEndEntityType,
                fieldAnnotation.fieldName(),
                fieldAnnotation.annotationType());
        if (!endRelationField.isAnnotationPresent(CsvOneToMany.class) ||
                endRelationField.getType() != relationBeginEntityType) {
            throw new RelationEndNotPresentException(CsvOneToMany.class,
                    relationEndEntityType,
                    fieldAnnotation.fieldName());
        }
        return new CsvManyToOneField(relationBeginField,
                relationEndEntityType,
                endRelationField);
    }
}
