package com.github.haseoo.ocm.structure.files;

import com.github.haseoo.ocm.structure.entities.CsvEntityClass;

import java.io.IOException;

public interface CsvFilesManager {
    CsvFile getFileForEntity(CsvEntityClass entityClass) throws IOException;
}
