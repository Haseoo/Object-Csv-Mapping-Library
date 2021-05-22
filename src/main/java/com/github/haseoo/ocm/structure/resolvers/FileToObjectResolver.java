package com.github.haseoo.ocm.structure.resolvers;

import com.github.haseoo.ocm.api.exceptions.CsvMappingException;
import com.github.haseoo.ocm.internal.MappingContext;
import com.github.haseoo.ocm.structure.entities.CsvEntityClass;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public final class FileToObjectResolver<T>  {
    private final MappingContext mappingContext;
    private final FileToObjectResolverContext context;

    public FileToObjectResolver(MappingContext mappingContext, Class<T> clazz) {
        this.mappingContext = mappingContext;
        context = new FileToObjectResolverContext();
        context.addClassToResolve(clazz);
    }

    public List<T> resolve() throws CsvMappingException, IOException {
        var files = new HashSet<File>();
        while(context.classesToResolve()) {
            var clazz = context.getClassToResolve();
            if (context.isClassEntityRegistered(clazz)) {
                continue;
            }
            var classEntity = CsvEntityClass.getInstance(clazz,
                    context,
                    context,
                    mappingContext);
            classEntity.addRelatedFieldsToContext(context::addClassToResolve);
            var file = findEntityFile(classEntity)
                    .orElseThrow(() -> new IOException(String.format("File not for entity %s not exist",
                            classEntity.getName())));
            if (!files.contains(file)) { //IMPORTANT!
                files.add(file);
                instantiateObjectsFromFileInContext(file);
            }
        }
        resolveRelations();
        return getObjectsOfClassAndSubClasses();
    }

    private Optional<File> findEntityFile(CsvEntityClass entityClass) {
        var file = new File(String.format("%s/%s.csv", mappingContext.getBasePath(), entityClass.getName()));
        if (file.exists()) {
            return Optional.of(file);
        }
        if (entityClass.getBaseClass() == null) {
            return Optional.empty();
        }
        return findEntityFile(entityClass.getBaseClass());
    }

    private void instantiateObjectsFromFileInContext(File file) {

    }

    private void resolveRelations() {

    }

    List<T> getObjectsOfClassAndSubClasses() {
        return null;
    }
}
