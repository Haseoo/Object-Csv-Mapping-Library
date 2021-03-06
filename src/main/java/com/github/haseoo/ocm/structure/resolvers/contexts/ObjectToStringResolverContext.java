package com.github.haseoo.ocm.structure.resolvers.contexts;

import com.github.haseoo.ocm.api.exceptions.CsvMappingException;
import com.github.haseoo.ocm.api.exceptions.IdFiledNotFoundException;
import com.github.haseoo.ocm.structure.entities.CsvEntityClass;
import com.github.haseoo.ocm.structure.files.CsvEntityFile;
import com.github.haseoo.ocm.structure.resolvers.EntityClassResolver;
import com.github.haseoo.ocm.structure.resolvers.EntityIdResolver;
import org.apache.commons.lang3.NotImplementedException;

import java.util.*;

import static java.util.stream.Collectors.toList;

public class ObjectToStringResolverContext implements EntityIdResolver, EntityClassResolver {
    private final Map<Class<?>, CsvEntityClass> registeredEntityClasses = new HashMap<>();
    private final Map<Class<?>, Set<Object>> resolvedObjects = new HashMap<>();
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
        resolvedObjects.computeIfAbsent(type, key -> new HashSet<>());
        resolvedObjects.get(type).add(object);
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
    public Object getObjectById(String id, Class<?> type) {
        throw new NotImplementedException("Invalid usage");
    }

    @Override
    public String getObjectId(Object object, Class<?> type) throws CsvMappingException {
        var entityClass = getRegisteredEntityClass(type);
        if (entityClass == null) {
            throw new CsvMappingException("Entity not present");
        }
        var idField = entityClass.getId().orElseThrow(() -> new IdFiledNotFoundException(type));
        return idField.toCsvStringValue(object);
    }

    public List<CsvEntityFile> resolveToFileInfo(String basePath) {
        var csvFileInfos = new ArrayList<CsvEntityFile>();
        var baseEntityClasses = registeredEntityClasses
                .values()
                .stream()
                .filter(ec -> ec.getBaseClass() == null)
                .collect(toList());
        for (var baseEntityClass : baseEntityClasses) {
            var entityClasses = getEntityAllSubEntities(baseEntityClass);
            entityClasses.add(baseEntityClass);
            var objects = new HashMap<CsvEntityClass, Set<Object>>();
            for (CsvEntityClass entityClass : entityClasses) {
                var entityObjects = resolvedObjects.get(entityClass.getType());
                if (entityObjects != null) {
                    objects.put(entityClass, entityObjects);
                }
            }
            var csvFileInfo = new CsvEntityFile(baseEntityClass, basePath, objects);
            csvFileInfos.add(csvFileInfo);
            entityClasses.forEach(entityClass -> csvClassFileAssociation.put(entityClass.getType(), csvFileInfo));
        }
        return csvFileInfos;
    }

    public Optional<String> getClassFileName(Class<?> type) {
        if (!csvClassFileAssociation.containsKey(type)) {
            return Optional.empty();
        }
        return Optional.of(csvClassFileAssociation.get(type).getName());
    }

    private List<CsvEntityClass> getEntityAllSubEntities(CsvEntityClass csvEntityClass) {
        var subEntities = new ArrayList<>(csvEntityClass.getSubClasses());
        for (CsvEntityClass subClass : csvEntityClass.getSubClasses()) {
            subEntities.addAll(getEntityAllSubEntities(subClass));
        }
        return subEntities;
    }
}
