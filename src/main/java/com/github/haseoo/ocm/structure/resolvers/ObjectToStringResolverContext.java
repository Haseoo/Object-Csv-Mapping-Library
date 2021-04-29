package com.github.haseoo.ocm.structure.resolvers;

import com.github.haseoo.ocm.api.exceptions.CsvMappingException;
import com.github.haseoo.ocm.api.exceptions.IdFiledNotFound;
import com.github.haseoo.ocm.structure.entities.CsvEntityClass;
import com.github.haseoo.ocm.structure.files.CsvEntityFile;
import org.apache.commons.lang3.NotImplementedException;

import java.util.*;

import static java.util.stream.Collectors.toList;

public class ObjectToStringResolverContext implements EntityIdResolver {
    private final Map<Class<?>, CsvEntityClass> registeredEntityClasses = new HashMap<>();
    private final Map<Class<?>, List<Object>> resolvedObjects = new HashMap<>();
    private final LinkedList<Object> objectsToResolve = new LinkedList<>();
    private final Map<Class<?>, CsvEntityFile> csvClassFileAssociation = new HashMap<>();


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
        if (!resolvedObjects.containsKey(type)) {
            resolvedObjects.put(type, new ArrayList<>());
        }
        resolvedObjects.get(type).add(object);
    }

    public boolean containsClassOfResolvedObjects(Class<?> type) {
        return resolvedObjects.containsKey(type);
    }

    public boolean isObjectAlreadyResolved(Class<?> type, Object obj) {
        var typeObject = resolvedObjects.get(type);
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
        return idField.toCsvStringValue(object);
    }

    public List<CsvEntityFile> resolveToFileInfo() {
        var csvFileInfos = new ArrayList<CsvEntityFile>();
        var baseEntityClasses = registeredEntityClasses
                .values()
                .stream()
                .filter(ec -> ec.getBaseClass() == null)
                .collect(toList());
        for (var baseEntityClass : baseEntityClasses) {
            var entityClasses = getEntityAllSubEntities(baseEntityClass);
            entityClasses.add(baseEntityClass);
            var objects = new HashMap<CsvEntityClass, List<Object>>();
            for (CsvEntityClass entityClass : entityClasses) {
                var entityObjects = resolvedObjects.get(entityClass.getType());
                if (entityObjects != null) {
                    objects.put(entityClass, entityObjects);
                }
            }
            var csvFileInfo = new CsvEntityFile(baseEntityClass, objects);
            csvFileInfos.add(csvFileInfo);
            entityClasses.forEach(entityClass -> csvClassFileAssociation.put(entityClass.getType(), csvFileInfo));
        }
        return csvFileInfos;
    }

    public Optional<String> getClassFileName(Class<?> type) {
        if (!csvClassFileAssociation.containsKey(type)) {
            return Optional.empty();
        }
        return Optional.ofNullable(csvClassFileAssociation.get(type).getName());
    }

    private List<CsvEntityClass> getEntityAllSubEntities(CsvEntityClass csvEntityClass) {
        var subEntities = new ArrayList<>(csvEntityClass.getSubClasses());
        for (CsvEntityClass subClass : csvEntityClass.getSubClasses()) {
            subEntities.addAll(getEntityAllSubEntities(subClass));
        }
        return subEntities;
    }
}
