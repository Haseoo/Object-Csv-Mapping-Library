package com.github.haseoo.ocm.structure.resolvers;

import com.github.haseoo.ocm.api.exceptions.CsvMappingException;
import com.github.haseoo.ocm.internal.MappingContext;
import com.github.haseoo.ocm.internal.utils.ReflectionUtils;
import com.github.haseoo.ocm.structure.entities.CsvEntityClass;
import com.github.haseoo.ocm.structure.entities.CsvStringObject;
import com.github.haseoo.ocm.structure.files.CsvFile;
import com.github.haseoo.ocm.structure.files.CsvFilesManager;
import com.github.haseoo.ocm.structure.resolvers.contexts.FileToObjectResolverContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public final class FileToObjectResolver<T> {
    private final MappingContext mappingContext;
    private final FileToObjectResolverContext context;
    private final CsvFilesManager csvFileManager;
    private final Class<?> returnListType;

    public FileToObjectResolver(MappingContext mappingContext,
                                Class<T> clazz,
                                CsvFilesManager csvFileManager) {
        this.mappingContext = mappingContext;
        this.csvFileManager = csvFileManager;
        returnListType = clazz;
        context = new FileToObjectResolverContext();
        context.addClassToResolve(clazz);
    }

    public List<T> resolve() throws CsvMappingException, IOException {
        var files = new HashSet<CsvFile>();
        while (context.classesToResolve()) {
            var clazz = context.getClassToResolve();
            if (context.isClassEntityRegistered(clazz)) {
                continue;
            }
            var entityClass = CsvEntityClass.getInstance(clazz,
                    context,
                    context,
                    mappingContext);
            entityClass.addRelatedFieldsToContext(context::addClassToResolve);
            var file = findEntityFile(entityClass)
                    .orElseThrow(() -> new IOException(String.format("File for entity %s not exist",
                            entityClass.getName())));
            if (!files.contains(file)) { //IMPORTANT!
                files.add(file);
                try {
                    instantiateObjectsFromFileInContext(file, entityClass);
                } catch (ClassNotFoundException e) {
                    throw new CsvMappingException(String.format("While parsing file %s", file), e);
                }
            }
        }
        resolveFields();
        return getObjectsOfClassAndSubClasses();
    }

    private Optional<CsvFile> findEntityFile(CsvEntityClass entityClass) throws IOException {
        var file = csvFileManager.getFileForEntity(entityClass);
        if (file != null) {
            return Optional.of(file);
        }
        if (entityClass.getBaseClass() == null) {
            return Optional.empty();
        }
        return findEntityFile(entityClass.getBaseClass());
    }

    private void instantiateObjectsFromFileInContext(CsvFile file,
                                                     CsvEntityClass entityClass)
            throws CsvMappingException,
            ClassNotFoundException {
        for (int i = 0; i < file.getRowCount(); i++) {
            var row = file.getRow(i);
            Class<?> rowType;
            if (file.hasTypeHeader()) {
                rowType = ReflectionUtils.getClassByName(row.get(CsvEntityClass.TYPE_HEADER_NAME));
                if (!context.isClassEntityRegistered(rowType)) {
                    onUnregisteredSubClassOccurred(entityClass, rowType);
                }
            } else {
                rowType = entityClass.getType();
            }
            context.registerObject(rowType, row);
        }
    }

    private void onUnregisteredSubClassOccurred(CsvEntityClass entityClass,
                                                Class<?> rowType) throws CsvMappingException {
        var newEntityClass = CsvEntityClass.getInstance(rowType,
                context,
                context,
                mappingContext);
        context.registerEntityClass(entityClass);
        newEntityClass.addRelatedFieldsToContext(context::addClassToResolve);
    }

    private void resolveFields() throws CsvMappingException {
        for (CsvStringObject registeredObject : context.getAllRegisteredObjects()) {
            registeredObject.getEntityClass().fillObjectFields(registeredObject.getObject(),
                    registeredObject.getCsvRow());
        }
    }

    @SuppressWarnings("unchecked")
    private List<T> getObjectsOfClassAndSubClasses() {
        var returnListClassEntity = context.getRegisteredEntityClass(returnListType);
        var returnList = new ArrayList<T>();
        var objectList = new ArrayList<>();
        appendObjectOfEntityToList(returnListClassEntity, objectList);
        for (Object o : objectList) {
            returnList.add((T) o);
        }
        return returnList;
    }

    private void appendObjectOfEntityToList(CsvEntityClass entityClass, List<Object> list) {
        list.addAll(context.getObjectOfClass(entityClass.getType()));
        for (CsvEntityClass subClass : entityClass.getSubClasses()) {
            appendObjectOfEntityToList(subClass, list);
        }
    }
}
