package com.github.haseoo.ocm.api;

import com.github.haseoo.ocm.api.annotation.*;
import com.github.haseoo.ocm.api.exceptions.ClassIsNotAnCsvEntity;
import com.github.haseoo.ocm.api.exceptions.CsvMappingException;
import com.github.haseoo.ocm.api.exceptions.FieldIsNotACollectionException;
import com.github.haseoo.ocm.api.exceptions.RelationEndNotPresentException;
import com.github.haseoo.ocm.internal.MappingContext;
import com.github.haseoo.ocm.internal.utils.ReflectionUtils;
import com.github.haseoo.ocm.structure.entities.CsvEntityClass;
import com.github.haseoo.ocm.structure.entities.fields.*;
import com.github.haseoo.ocm.structure.files.CsvEntityFile;
import com.github.haseoo.ocm.structure.files.CsvFile;
import com.github.haseoo.ocm.structure.resolvers.ObjectToStringResolverContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;


public class CsvMapper {
    private final MappingContext mappingContext;

    public CsvMapper(String basePath, String delimiter) {
        mappingContext = new MappingContext(basePath, delimiter);
    }

    public <T> List<InMemoryCsvFile> listToCstInMemoryFile(List<T> objects) throws CsvMappingException {
        ObjectToStringResolverContext resolverContext = new ObjectToStringResolverContext();
        resolverContext.addObjectsToResolve(objects);
        while (resolverContext.objectsToResolve()) {
            var object = resolverContext.getObjectsToResolve();
            var objectType = object.getClass();
            if (resolverContext.isObjectAlreadyResolved(objectType, object)) {
                continue;
            }
            var entityClass = getCsvEntityClass(objectType, object, resolverContext);
            if (entityClass != null) {
                try {
                    entityClass.addRelatedFieldsToContext(object, resolverContext::addObjectToResolve);
                } catch (CsvMappingException e) {
                    throw new CsvMappingException(String.format("While parsing object of type %s",
                            objectType.getCanonicalName()), e);
                }
            }
            resolverContext.registerResolvedObject(objectType, object);
        }
        var csvFileInfos = resolverContext.resolveToFileInfo();
        var csvFiles = new ArrayList<CsvFile>();
        for (CsvEntityFile csvFileInfo : csvFileInfos) {
            var headerWithRelations = csvFileInfo.getHeader(resolverContext::getClassFileName);
            var headerWithoutRelations = csvFileInfo.getHeader();
            var csvFile = CsvFile.fromObjects(csvFileInfo, headerWithoutRelations, headerWithRelations);
            for (Map<String, String> row : csvFileInfo.getValues()) {
                csvFile.addRow(row);
            }
            csvFiles.add(csvFile);
        }
        return csvFiles.stream().map(csvFile -> csvFile.toInMemoryFile(mappingContext.getSplitter())).collect(toList());
    }

    private CsvEntityClass getCsvEntityClass(Class<?> type,
                                             Object object,
                                             ObjectToStringResolverContext resolverContext) throws CsvMappingException {
        if (type == null || type.getAnnotation(CsvEntity.class) == null || type.equals(Object.class)) {
            return null;
        }
        if (resolverContext.isClassEntityRegistered(type)) {
            return resolverContext.getRegisteredEntityClass(type);
        }
        var csvEntityClass = new CsvEntityClass(type);

        try {
            fillEntityClass(csvEntityClass, resolverContext);
        } catch (CsvMappingException e) {
            throw new CsvMappingException("Invalid entity structure", e);
        }

        var baseType = type.getSuperclass();
        var baseClassEntity = getCsvEntityClass(baseType, object, resolverContext);
        if (baseClassEntity != null) {
            csvEntityClass.setBaseClass(baseClassEntity);
            baseClassEntity.getSubClasses().add(csvEntityClass);
        }
        resolverContext.registerEntityClass(csvEntityClass);
        return csvEntityClass;

    }

    private void fillEntityClass(CsvEntityClass entityClass,
                                 ObjectToStringResolverContext resolverContext) throws CsvMappingException {
        var type = entityClass.getType();
        List<Field> objectFields = ReflectionUtils.getNonTransientFields(type.getDeclaredFields());
        for (Field objectField : objectFields) {
            entityClass.getFields().add(resolveField(type, objectField, resolverContext));
            if (objectField.isAnnotationPresent(CsvId.class)) {
                entityClass.setId(new CsvValueField(mappingContext.getConverterContext(), objectField));
            }
        }

    }

    private CsvField resolveField(Class<?> entityType,
                                  Field entityField,
                                  ObjectToStringResolverContext resolverContext) throws
            ClassIsNotAnCsvEntity,
            FieldIsNotACollectionException,
            RelationEndNotPresentException {
        CsvField csvField;
        if (entityField.isAnnotationPresent(CsvOneToOne.class)) {
            csvField = handleOneToOneRelation(entityType, entityField, resolverContext);
        } else if (entityField.isAnnotationPresent(CsvOneToMany.class)) {
            csvField = handleOneToMany(entityType, entityField, resolverContext);
        } else if (entityField.isAnnotationPresent(CsvManyToOne.class)) {
            csvField = handleManyToOneRelation(entityType, entityField);
        } else if (entityField.isAnnotationPresent(CsvManyToMany.class)) {
            csvField = handleManyToManyRelation(entityType, entityField, resolverContext);
        } else {
            csvField = new CsvValueField(mappingContext.getConverterContext(), entityField);
        }
        return csvField;
    }

    private CsvField handleOneToOneRelation(Class<?> relationBeginEntityType,
                                            Field relationBeginField,
                                            ObjectToStringResolverContext resolverContext)
            throws ClassIsNotAnCsvEntity,
            RelationEndNotPresentException {
        var fieldAnnotation = relationBeginField.getAnnotation(CsvOneToOne.class);
        var relationEndEntityType = relationBeginField.getType();
        validateRelationClass(relationEndEntityType);
        Field endRelationField = getRelationField(relationEndEntityType,
                fieldAnnotation.fieldName(),
                fieldAnnotation.annotationType());
        if (!endRelationField.isAnnotationPresent(CsvOneToOne.class) ||
                endRelationField.getType() != relationBeginEntityType) {
            throw new RelationEndNotPresentException(fieldAnnotation.annotationType(), relationEndEntityType, fieldAnnotation.fieldName());
        }
        return new CsvOneToOneField(resolverContext, relationBeginField, endRelationField);
    }

    private CsvField handleOneToMany(Class<?> relationBeginEntityType,
                                     Field relationBeginField,
                                     ObjectToStringResolverContext resolverContext) throws
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
            throw new RelationEndNotPresentException(fieldAnnotation.annotationType(), relationEndEntityType, fieldAnnotation.fieldName());
        }
        return new CsvOneToManyField(resolverContext,
                relationBeginField,
                endRelationField,
                endRelationFieldCollectionType);
    }

    private CsvField handleManyToOneRelation(Class<?> relationBeginEntityType,
                                             Field relationBeginField) throws
            FieldIsNotACollectionException,
            RelationEndNotPresentException, ClassIsNotAnCsvEntity {
        var fieldAnnotation = relationBeginField.getAnnotation(CsvManyToOne.class);
        validateCollectionRelation(relationBeginField.getType(), fieldAnnotation.fieldName());//
        var relationEndEntityType = ReflectionUtils.getActualTypeArgument(relationBeginField);
        validateRelationClass(relationEndEntityType);
        Field endRelationField = getRelationField(relationEndEntityType,
                fieldAnnotation.fieldName(),
                fieldAnnotation.annotationType());
        if (!endRelationField.isAnnotationPresent(CsvOneToMany.class) ||
                endRelationField.getType() != relationBeginEntityType) {
            throw new RelationEndNotPresentException(CsvOneToMany.class, relationEndEntityType, fieldAnnotation.fieldName());
        }
        return new CsvManyToOneField(relationBeginField,
                relationEndEntityType,
                endRelationField);
    }


    private void validateCollectionRelation(Class<?> relationBeginEntityType, String fieldName) throws FieldIsNotACollectionException {
        if (!ReflectionUtils.isClassCollection(relationBeginEntityType)) {
            throw new FieldIsNotACollectionException(relationBeginEntityType,
                    fieldName);
        }
    }


    private CsvField handleManyToManyRelation(Class<?> relationBeginEntityType,
                                              Field relationBeginField,
                                              ObjectToStringResolverContext resolverContext) throws
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
            throw new RelationEndNotPresentException(fieldAnnotation.annotationType(), relationEndEntityType, fieldAnnotation.fieldName());
        }
        return new CsvManyToManyField(resolverContext,
                mappingContext,
                relationBeginField,
                relationEndEntityType,
                endRelationField,
                relationBeginEntityType);
    }

    private void validateRelationClass(Class<?> relationEndEntityType) throws ClassIsNotAnCsvEntity {
        if (!relationEndEntityType.isAnnotationPresent(CsvEntity.class)) {
            throw new ClassIsNotAnCsvEntity(relationEndEntityType);
        }
    }

    private Field getRelationField(Class<?> relationEndEntityType,
                                   String fieldName,
                                   Class<? extends Annotation> annotationType) throws RelationEndNotPresentException {
        Field endRelationField;
        try {
            endRelationField = relationEndEntityType.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RelationEndNotPresentException(annotationType, relationEndEntityType, fieldName);
        }
        return endRelationField;
    }

}
