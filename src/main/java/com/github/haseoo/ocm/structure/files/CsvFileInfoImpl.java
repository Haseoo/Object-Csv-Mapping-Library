package com.github.haseoo.ocm.structure.files;

import com.github.haseoo.ocm.structure.entities.CsvClass;
import lombok.Value;

@Value
public class CsvFileInfoImpl implements CsvFileInfo {
    public CsvFileInfoImpl(CsvClass csvClass, String filePath) {
        this.filePath = filePath;
        fileName = csvClass.getName();
    }

    String filePath;
    String fileName;
    @Override
    public String getFilePath() {
        return filePath;
    }

    @Override
    public String getName() {
        return fileName;
    }
}
