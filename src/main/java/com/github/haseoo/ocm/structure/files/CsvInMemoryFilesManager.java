package com.github.haseoo.ocm.structure.files;

import com.github.haseoo.ocm.api.InMemoryCsvFile;
import com.github.haseoo.ocm.structure.entities.CsvEntityClass;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class CsvInMemoryFilesManager implements CsvFilesManager {
    private final String delimiter;
    private final List<InMemoryCsvFile> files;

    @Override
    public CsvFile getFileForEntity(CsvEntityClass entityClass) throws IOException {
        var inMemoryFile = files.stream()
                .filter(file -> file.getName()
                        .equals(entityClass.getName() + ".csv"))
                .findFirst();
        if (inMemoryFile.isPresent()) {
            return CsvFile.forInMemoryFile(inMemoryFile.get(), delimiter.charAt(0));
        } else {
            return null;
        }
    }
}
