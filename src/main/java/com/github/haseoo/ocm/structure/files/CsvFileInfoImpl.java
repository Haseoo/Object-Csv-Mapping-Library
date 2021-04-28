package com.github.haseoo.ocm.structure.files;

import com.github.haseoo.ocm.structure.entities.CsvEntityClass;
import lombok.Value;

@Value
public class CsvFileInfoImpl implements CsvFileInfo {
    public CsvFileInfoImpl(CsvEntityClass csvEntityClass, String filePath) {
        this.filePath = filePath;
        fileName = csvEntityClass.getName();
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
