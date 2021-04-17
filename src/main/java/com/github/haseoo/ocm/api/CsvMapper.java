package com.github.haseoo.ocm.api;

import com.github.haseoo.ocm.api.annotation.*;
import com.github.haseoo.ocm.api.exceptions.CsvMappingException;
import com.github.haseoo.ocm.internal.MappingContext;
import com.github.haseoo.ocm.internal.utils.ReflectionUtils;
import com.github.haseoo.ocm.structure.entities.CsvEntityClass;
import com.github.haseoo.ocm.structure.entities.fields.CsvField;
import com.github.haseoo.ocm.structure.entities.fields.CsvValueField;
import com.github.haseoo.ocm.structure.resolvers.object.EntityObjectToRowResolver;
import com.github.haseoo.ocm.internal.utils.ObjectToStringResolverContext;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


public class CsvMapper {
    private final MappingContext mappingContext = new MappingContext("basePath", "");
    public<T> List<T> mapFormFile(File file, Class<T> entityClass) throws IOException {
        try (
                Reader reader = new FileReader(file);
                CSVReader csvReader = new CSVReader(reader);
        ) {
            csvReader.readAll();
        }
        return new ArrayList<>();
    }

    public <T> void arrayToCsv(List<T> objects) throws CsvMappingException {
        ObjectToStringResolverContext resolverContext = new ObjectToStringResolverContext();
        resolverContext.addObjectsToResolve(objects);
        while(resolverContext.objectsToResolve()) {
            var object = resolverContext.getObjectsToResolve();
            var objectType = object.getClass();
            if (resolverContext.isObjectAlreadyResolved(objectType, object)) {
                continue;
            }
            getCsvEntityClass(objectType, object, resolverContext);
            resolverContext.registerResolvedObject(objectType, object);
        }
        resolverContext.DEBUG_accessRegisteredEntityClasses();
    }

    private CsvEntityClass getCsvEntityClass(Class<?> type, Object object, ObjectToStringResolverContext resolverContext) throws CsvMappingException {
        if(type == null || type.getAnnotation(CsvEntity.class) == null || type.equals(Object.class)) {
            return null;
        }
        if (resolverContext.isClassEntityRegistered(type)) {
            return resolverContext.getRegisteredEntityClass(type);
        }
        var csvEntityClass = new CsvEntityClass(type);

        try {
            fillEntityClass(csvEntityClass, object);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new CsvMappingException("Getter method not present or invalid", e);
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
                                 Object entityObject)
            throws NoSuchMethodException,
                InvocationTargetException,
                IllegalAccessException
    {
        var type = entityClass.getType();
        List<Field> objectFields = ReflectionUtils.getNonTransientFields(type.getDeclaredFields());
        for (Field objectField : objectFields) {
            var fieldName = objectField.getName();
            Object fieldValue = type.getMethod(ReflectionUtils.getGetterName(fieldName)).invoke(entityObject);
            CsvField csvField;
            csvField = getCsvField(objectField);
            if(objectField.isAnnotationPresent(CsvId.class)) {
                entityClass.setId(new CsvValueField(mappingContext.getConverterContext(), objectField));
            }
            entityClass.getFields().add(csvField);
        }

    }

    private CsvField getCsvField(Field objectField) {
        CsvField csvField;
        if (objectField.isAnnotationPresent(CsvOneToOne.class)){
            csvField = null; //TODO

        } else if (objectField.isAnnotationPresent(CsvManyToOne.class)) {
            csvField = null; //TODO

        } else if (objectField.isAnnotationPresent(CsvManyToOne.class)) {
            csvField = null; //TODO
        } else if (objectField.isAnnotationPresent(CsvManyToMany.class)) {
            csvField = null; //TODO
        } else {
            csvField = new CsvValueField(mappingContext.getConverterContext(), objectField);
        }
        return csvField;
    }


}
