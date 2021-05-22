package com.github.haseoo.ocm.structure.resolvers;

import com.github.haseoo.ocm.api.exceptions.CsvMappingException;
import com.github.haseoo.ocm.api.exceptions.IdFiledNotFound;
import com.github.haseoo.ocm.structure.CsvStringObject;
import com.github.haseoo.ocm.structure.entities.CsvEntityClass;

import java.util.*;

public class FileToObjectResolverContext implements  EntityIdResolver, EntityClassResolver {

    private final Map<Class<?>, CsvEntityClass> resolvedClasses = new HashMap<>();
    private final Map<Class<?>, Map<Object, CsvStringObject>> csvObjects = new HashMap<>();
    private final LinkedList<Class<?>> classesToResolve = new LinkedList<>();

    public void addClassToResolve(Class<?> clazz) {
        classesToResolve.add(clazz);
    }

    public Class<?> getClassToResolve() {
        return classesToResolve.pop();
    }

    @Override
    public boolean isClassEntityRegistered(Class<?> type) {
        return resolvedClasses.containsKey(type);
    }

    @Override
    public CsvEntityClass getRegisteredEntityClass(Class<?> type) {
        return resolvedClasses.get(type);
    }

    @Override
    public void registerEntityClass(CsvEntityClass csvEntityClass) {
        resolvedClasses.put(csvEntityClass.getType(), csvEntityClass);
    }

    @Override
    public Object getObjectById(Object id, Class<?> type) throws CsvMappingException {
        if (!resolvedClasses.containsKey(type)) {
            throw new CsvMappingException(String.format("Entity of class %s not registered", type.getCanonicalName()));
        }
        return getObjectByIdInClassTree(id, resolvedClasses.get(type))
                .orElseThrow(() -> new CsvMappingException(String.format("Object with id %s not found", id)));
    }

    public Optional<Object> getObjectByIdInClassTree(Object id, CsvEntityClass root) {
        if (csvObjects.containsKey(root.getType())) {
            var obj = csvObjects.get(root.getType()).get(id);
            if (obj != null) {
                return Optional.of(obj);
            }
        }
        for (CsvEntityClass subClass : root.getSubClasses()) {
            var obj  = getObjectByIdInClassTree(id, subClass);
            if (obj.isPresent()) {
                return obj;
            }
        }
        return Optional.empty();
    }

    @Override
    public String getObjectId(Object object, Class<?> type) throws CsvMappingException {
        if (!resolvedClasses.containsKey(type)) {
            throw new CsvMappingException(String.format("Entity of class %s not found", type.getCanonicalName()));
        }
        var idField = resolvedClasses.get(type).getId().orElseThrow(() -> new IdFiledNotFound(type));
        return idField.toCsvStringValue(object);
    }
}
