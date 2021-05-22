package com.github.haseoo.ocm.structure.resolvers;

import com.github.haseoo.ocm.internal.MappingContext;
import com.github.haseoo.ocm.structure.entities.CsvEntityClass;

import java.io.File;
import java.util.Optional;

public final class FileToObjectResolver<T>  {
    private final MappingContext mappingContext;

    public FileToObjectResolver(MappingContext mappingContext) {
        this.mappingContext = mappingContext;
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
}
