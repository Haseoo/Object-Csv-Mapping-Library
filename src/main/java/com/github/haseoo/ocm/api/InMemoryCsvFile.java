package com.github.haseoo.ocm.api;

import com.github.haseoo.ocm.structure.files.CsvFileInfo;
import lombok.Value;


@Value
public class InMemoryCsvFile implements CsvFileInfo {
    String fileName;
    String[] data;

    @Override
    public String toString() {
        var builder = new StringBuilder();
        builder.append("InMemoryCsvFile:");
        builder.append(String.format("%nfileName:%s%n", fileName));
        builder.append("data:\n");
        for (String datum : data) {
            builder.append(datum);
            builder.append('\n');
        }
        return builder.toString();
    }

    @Override
    public String getFilePath() {
        return fileName;
    }

    @Override
    public String getName() {
        return fileName;
    }
}