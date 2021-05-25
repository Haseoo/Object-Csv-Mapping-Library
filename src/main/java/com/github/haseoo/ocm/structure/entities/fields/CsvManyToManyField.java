package com.github.haseoo.ocm.structure.entities.fields;

import com.github.haseoo.ocm.api.annotation.CsvManyToMany;
import com.github.haseoo.ocm.api.exceptions.*;
import com.github.haseoo.ocm.internal.utils.ReflectionUtils;
import com.github.haseoo.ocm.structure.resolvers.EntityIdResolver;

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
    @SuppressWarnings("unchecked")
    public void setObjectField(Object dest, Map<String, String> fields) throws CsvMappingException {
        if (!fields.containsKey(getColumnName())) {
            throw new ColumnNotFoundException(getColumnName());
        }
        var endRelationObjs = getIds(fields);
        var relationBeginFieldValue = ReflectionUtils.getFieldValue(dest, getFieldName());
        if (relationBeginFieldValue == null) {
            throw new CollectionNotInitializedException(getFieldName(), getFieldType());
        }
        var collection = (Collection<Object>) relationBeginFieldValue;
        collection.addAll(endRelationObjs);
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

    private boolean isIdFieldEmpty(String stringId) {
        return stringId.equals("");
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

    private ArrayList<Object> getIds(Map<String, String> fields) throws CsvMappingException {
        String[] idStrings = getStringIds(fields);
        return resolveStringIds(idStrings);
    }

    private ArrayList<Object> resolveStringIds(String[] idStrings) throws CsvMappingException {
        var endRelationObjs = new ArrayList<>();
        for (String stringId : idStrings) {
            if (isIdFieldEmpty(stringId)) {
                continue;
            }
            endRelationObjs.add(entityIdResolver.getObjectById(stringId, getFieldType()));
        }
        return endRelationObjs;
    }

    private String[] getStringIds(Map<String, String> fields) {
        var idStringList = fields.get(getColumnName());
        idStringList = idStringList.substring(1, idStringList.length() - 1); //removes []
        return idStringList.split(delimiter);
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
