package com.github.haseoo.ocm.structure.entities.fields;

import com.github.haseoo.ocm.api.annotation.CsvOneToOne;
import com.github.haseoo.ocm.api.exceptions.*;
import com.github.haseoo.ocm.internal.utils.ReflectionUtils;
import com.github.haseoo.ocm.structure.resolvers.EntityIdResolver;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.function.Consumer;

import static com.github.haseoo.ocm.internal.utils.ReflectionUtils.*;

public final class CsvOneToOneField implements CsvField {
    private final EntityIdResolver entityIdResolver;
    private final CsvRelation beginRelation;
    private final CsvRelation endRelation;
    private final boolean appendToFile;

    private CsvOneToOneField(EntityIdResolver entityIdResolver,
                             Field beginRelationField,
                             Field endRelationField) {
        this.entityIdResolver = entityIdResolver;
        this.beginRelation = new CsvRelation(beginRelationField);
        this.endRelation = new CsvRelation(endRelationField);
        appendToFile = beginRelationField.getAnnotation(CsvOneToOne.class).appendToFile();
    }

    @Override
    public String toCsvStringValue(Object entityObject) throws CsvMappingException {
        var value = ReflectionUtils.getFieldValue(entityObject, getFieldName());
        return value != null ? entityIdResolver.getObjectId(value, beginRelation.getType()) : "";
    }

    @Override
    public void setObjectField(Object dest, Map<String, String> fields) throws CsvMappingException {
        if (!fields.containsKey(getColumnName())) {
            throw new ColumnNotFoundException(getColumnName());
        }
        var stringValue = fields.get(getColumnName());
        if (stringValue.equals(""))
            return;
        var endRelationObject = entityIdResolver.getObjectById(stringValue,
                beginRelation.getType());
        setObjectFiled(dest, endRelationObject, getFieldName(), getFieldType());
        setObjectFiled(endRelationObject, dest, endRelation.getFieldName(), endRelation.getType());
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

    public static CsvOneToOneField newInstance(Class<?> relationBeginEntityType,
                                               Field relationBeginField,
                                               EntityIdResolver resolverContext)
            throws ClassIsNotAnCsvEntityException,
            RelationEndNotPresentException {
        var fieldAnnotation = relationBeginField.getAnnotation(CsvOneToOne.class);
        var relationEndEntityType = relationBeginField.getType();
        validateRelationClass(relationEndEntityType);
        Field endRelationField = getRelationField(relationEndEntityType,
                fieldAnnotation.fieldName(),
                fieldAnnotation.annotationType());
        if (!endRelationField.isAnnotationPresent(CsvOneToOne.class) ||
                endRelationField.getType() != relationBeginEntityType) {
            throw new RelationEndNotPresentException(fieldAnnotation.annotationType(),
                    relationEndEntityType,
                    fieldAnnotation.fieldName());
        }
        return new CsvOneToOneField(resolverContext, relationBeginField, endRelationField);
    }
}
