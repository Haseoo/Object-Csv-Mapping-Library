package com.github.haseoo.ocm.internal.utils;

import com.opencsv.CSVReader;
import lombok.Value;

import java.io.*;
import java.util.List;

@Value
public class CsvFileManager {
    String filePath;

    public List<String[]> getRows() throws IOException {
        try (
                Reader reader = new FileReader(new File(filePath));
                CSVReader csvReader = new CSVReader(reader)
        ) {
            return csvReader.readAll();
        }
    }

    public void saveFile(List<String> lines) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }
}
