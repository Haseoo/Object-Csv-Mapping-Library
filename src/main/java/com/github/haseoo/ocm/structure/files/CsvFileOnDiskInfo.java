package com.github.haseoo.ocm.structure.files;

import com.github.haseoo.ocm.structure.entities.CsvEntityClass;
import lombok.Value;

@Value
public class CsvFileOnDiskInfo implements CsvFileInfo {
    public CsvFileOnDiskInfo(CsvEntityClass csvEntityClass, String filePath) {
        this.filePath = filePath;
        fileName = csvEntityClass.getName();
    }

    String filePath;
    String fileName;

    @Override
    public String getFilePath() {
        return String.format("%s/%s.csv", filePath, fileName);
    }

    @Override
    public String getName() {
        return fileName;
    }
}
