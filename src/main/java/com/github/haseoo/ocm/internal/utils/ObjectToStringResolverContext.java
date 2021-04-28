package com.github.haseoo.ocm.internal.utils;

import com.github.haseoo.ocm.api.exceptions.CsvMappingException;
import com.github.haseoo.ocm.api.exceptions.IdFiledNotFound;
import com.github.haseoo.ocm.structure.entities.CsvEntityClass;
import com.github.haseoo.ocm.structure.resolvers.EntityIdResolver;
import org.apache.commons.lang3.NotImplementedException;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ObjectToStringResolverContext implements EntityIdResolver {
    private final Map<Class<?>, CsvEntityClass> registeredEntityClasses = new HashMap<>();
    private final Map<Class<?>, List<Object>> resolvedObject = new HashMap<>();
    private final LinkedList<Object> objectsToResolve = new LinkedList<>();


    public void registerEntityClass(CsvEntityClass csvEntityClass) {
        registeredEntityClasses.put(csvEntityClass.getType(), csvEntityClass);
    }

    public boolean isClassEntityRegistered(Class<?> type) {
        return registeredEntityClasses.containsKey(type);
    }

    public CsvEntityClass getRegisteredEntityClass(Class<?> type) {
        return registeredEntityClasses.get(type);
    }

    public void registerResolvedObject(Class<?> type, Object object) {
        if (!resolvedObject.containsKey(type)) {
            resolvedObject.put(type, new ArrayList<>());
        }
        resolvedObject.get(type).add(object);
    }

    public boolean containsClassOfResolvedObjects(Class<?> type) {
        return resolvedObject.containsKey(type);
    }

    public boolean isObjectAlreadyResolved(Class<?> type, Object obj) {
        var typeObject = resolvedObject.get(type);
        if (typeObject == null) {
            return false;
        }
        return typeObject.contains(obj);
    }

    public Object getObjectsToResolve() {
        return objectsToResolve.pop();
    }

    public boolean objectsToResolve() {
        return !objectsToResolve.isEmpty();
    }

    public void addObjectToResolve(Object object) {
        objectsToResolve.add(object);
    }

    public void addObjectsToResolve(Collection<?> objects) {
        objectsToResolve.addAll(objects);
    }


    //DEBUG
    public void DEBUG_accessRegisteredEntityClasses() {
        var vals = registeredEntityClasses.values();
        System.out.println("DEBUG_accessRegisteredEntityClasses");
    }

    @Override
    public Object getObjectById(Object id, Class<?> type) throws CsvMappingException {
        throw new NotImplementedException("TODO!");
    }

    @Override
    public String getObjectId(Object object, Class<?> type) throws CsvMappingException {
        var entityClass = getRegisteredEntityClass(type);
        if (entityClass == null) {
            throw new CsvMappingException("Entity not present");
        }
        var idField = entityClass.getId().orElseThrow(() -> new IdFiledNotFound(type));
        try {
            var idObj = type.getMethod(ReflectionUtils.getGetterName(idField.getFieldName())).invoke(object);
            return idField.toCsvStringValue(idObj);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new CsvMappingException("Getter method not present or invalid", e);
        }
    }
}
