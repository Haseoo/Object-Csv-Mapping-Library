package com.github.haseoo.ocm.api;

import com.github.haseoo.ocm.api.exceptions.CsvMappingException;
import com.github.haseoo.ocm.internal.MappingContext;
import com.github.haseoo.ocm.structure.files.CsvFile;
import com.github.haseoo.ocm.structure.resolvers.ObjectToFileResolver;

import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;


public class CsvMapper {
    private final MappingContext mappingContext;

    public CsvMapper(String basePath, String delimiter) {
        mappingContext = new MappingContext(basePath, delimiter);
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
