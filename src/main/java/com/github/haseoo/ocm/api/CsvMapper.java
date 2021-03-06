package com.github.haseoo.ocm.api;

import com.github.haseoo.ocm.api.converter.TypeConverter;
import com.github.haseoo.ocm.api.exceptions.CsvMappingException;
import com.github.haseoo.ocm.internal.MappingContext;
import com.github.haseoo.ocm.structure.files.CsvFile;
import com.github.haseoo.ocm.structure.files.CsvInMemoryFilesManager;
import com.github.haseoo.ocm.structure.files.CsvOnDiskFilesManager;
import com.github.haseoo.ocm.structure.resolvers.FileToObjectResolver;
import com.github.haseoo.ocm.structure.resolvers.ObjectToFileResolver;

import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;


public class CsvMapper {
    private final MappingContext mappingContext;

    public CsvMapper(String basePath, String delimiter) {
        mappingContext = new MappingContext(basePath, delimiter);

    }

    public CsvMapper(String delimiter) {
        mappingContext = new MappingContext("", delimiter);
    }

    public <T> void registerConverter(Class<T> clazz, TypeConverter<T> converter) {
        mappingContext.registerConverter(clazz, converter);
    }

    public <T> List<T> filesToList(Class<T> clazz) throws CsvMappingException, IOException {
        var filesManager = new CsvOnDiskFilesManager(mappingContext.getBasePath(), mappingContext.getSplitter());
        var resolver = new FileToObjectResolver<T>(mappingContext, clazz, filesManager);
        return resolver.resolve();
    }

    public <T> List<T> filesToList(Class<T> clazz, List<InMemoryCsvFile> files) throws CsvMappingException, IOException {
        var filesManager = new CsvInMemoryFilesManager(mappingContext.getSplitter(), files);
        var resolver = new FileToObjectResolver<T>(mappingContext, clazz, filesManager);
        return resolver.resolve();
    }

    public <T> List<InMemoryCsvFile> listToCsvInMemoryFile(List<T> objects) throws CsvMappingException {
        var resolver = new ObjectToFileResolver<>(mappingContext, objects);
        var csvFiles = resolver.resolve();
        return csvFiles.stream().map(csvFile -> csvFile.toInMemoryFile(mappingContext.getSplitter())).collect(toList());
    }

    public <T> void listToFiles(List<T> objects) throws CsvMappingException, IOException {
        var resolver = new ObjectToFileResolver<>(mappingContext, objects);
        var csvFiles = resolver.resolve();
        for (CsvFile csvFile : csvFiles) {
            csvFile.writeFile(mappingContext.getSplitter());
        }
    }


}
