package com.github.haseoo.ocm.internal.utils;

import com.github.haseoo.ocm.structure.entities.CsvClass;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ObjectToStringResolverContext {
    private final Map<Class<?>, CsvClass> registeredClasses = new HashMap<>();
    private final Map<Class<?>, Map<Object, Object>> objects = new HashMap<>();
    private final LinkedList<Object> objectsToResolve = new LinkedList<>();


    public void registerEntityClass(CsvClass csvClass){
        registeredClasses.put(csvClass.getType(), csvClass);
    }

    public boolean isClassEntityRegistered(Class<?> type) {
        return registeredClasses.containsKey(type);
    }

    public CsvClass getRegisteredEntityClass(Class<?> type) {
        return registeredClasses.get(type);
    }

    public void registerObject(Class<?> type, Object id, Object object) {
        if (!objects.containsKey(type)) {
            objects.put(type, new HashMap<>());
        }
        objects.get(type).put(id, object);
    }

    public boolean containsClassOfObjects(Class<?> type) {
        return objects.containsKey(type);
    }

    public boolean containsObject(Class<?> type, Object id) {
        var typeObject = objects.get(type);
        if (typeObject == null) {
            return false;
        }
        return typeObject.containsKey(id);
    }

    public Object getObjects(Class<?> type, Object id) {
        var typeObject = objects.get(type);
        if (typeObject == null) {
            return null;
        }
        return typeObject.get(id);
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
}
