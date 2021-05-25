package com.github.haseoo.ocm.structure.files;

import com.github.haseoo.ocm.structure.entities.CsvEntityClass;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;

@RequiredArgsConstructor
public class CsvOnDiskFilesManager implements CsvFilesManager {
    private final String basePath;
    private final String delimiter;
    @Override
    public CsvFile getFileForEntity(CsvEntityClass entityClass) throws IOException {
        var file = new File(String.format("%s/%s.csv", basePath, entityClass.getName()));
        if (!file.exists()) {
            return null;
        }
        var fileInfo = new CsvFileOnDiskInfo(entityClass, basePath);
        return CsvFile.forFile(fileInfo, delimiter.charAt(0));
    }
}
