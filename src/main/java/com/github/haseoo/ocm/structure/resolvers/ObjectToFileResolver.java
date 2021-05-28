package com.github.haseoo.ocm.structure.resolvers;

import com.github.haseoo.ocm.api.exceptions.CsvMappingException;
import com.github.haseoo.ocm.internal.MappingContext;
import com.github.haseoo.ocm.structure.entities.CsvEntityClass;
import com.github.haseoo.ocm.structure.files.CsvEntityFile;
import com.github.haseoo.ocm.structure.files.CsvFile;
import com.github.haseoo.ocm.structure.resolvers.contexts.ObjectToStringResolverContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public final class ObjectToFileResolver<T> {
    private final MappingContext mappingContext;
    private final ObjectToStringResolverContext resolverContext = new ObjectToStringResolverContext();

    public ObjectToFileResolver(MappingContext mappingContext, List<T> objects) {
        resolverContext.addObjectsToResolve(objects);
        this.mappingContext = mappingContext;
    }

    public List<CsvFile> resolve() throws CsvMappingException {
        while (resolverContext.objectsToResolve()) {
            var object = resolverContext.getObjectsToResolve();
            var objectType = object.getClass();
            if (resolverContext.isObjectAlreadyResolved(objectType, object)) {
                continue;
            }
            var entityClass = CsvEntityClass.getInstance(objectType,
                    resolverContext,
                    resolverContext,
                    mappingContext);
            handleRelatedObjects(object, objectType, entityClass);
            resolverContext.registerResolvedObject(objectType, object);
        }
        var csvFileInfos = resolverContext.resolveToFileInfo(mappingContext.getBasePath());
        var csvFiles = new ArrayList<CsvFile>();
        for (CsvEntityFile csvFileInfo : csvFileInfos) {
            CsvFile csvFile = prepareCsvFile(csvFileInfo);
            csvFiles.add(csvFile);
        }
        return csvFiles;
    }

    private CsvFile prepareCsvFile(CsvEntityFile csvFileInfo) throws CsvMappingException {
        var headerWithRelations = csvFileInfo.getHeader(resolverContext::getClassFileName);
        var headerWithoutRelations = csvFileInfo.getHeader();
        var csvFile = CsvFile.forObjects(csvFileInfo, headerWithoutRelations, headerWithRelations);
        for (Map<String, String> row : csvFileInfo.getValues()) {
            csvFile.addRow(row);
        }
        return csvFile;
    }

    private void handleRelatedObjects(Object object,
                                      Class<?> objectType,
                                      CsvEntityClass entityClass) throws CsvMappingException {
        if (entityClass != null) {
            try {
                entityClass.addRelatedFieldsToContext(object, resolverContext::addObjectToResolve);
            } catch (CsvMappingException e) {
                throw new CsvMappingException(String.format("While parsing relations of object of type %s",
                        objectType.getCanonicalName()), e);
            }
        }
    }


}
