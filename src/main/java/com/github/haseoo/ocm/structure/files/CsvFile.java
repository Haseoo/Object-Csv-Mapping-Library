package com.github.haseoo.ocm.structure.files;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CsvFile {
    private final CsvFileInfo csvFileInfo;
    private final List<String[]> rows;
    private final Map<String, Integer> header;

    public static CsvFile FromObjects(CsvFileInfo csvFileInfo, Map<String, Integer> header) {
        return new CsvFile(csvFileInfo, new ArrayList<>(), header);
    }

    public static CsvFile FromFile(CsvFileInfo csvFileInfo, char delimiter) throws IOException {
        var header = new HashMap<String, Integer>();
        var file = new File(csvFileInfo.getFilePath());
        var parser = new CSVParserBuilder().withSeparator(delimiter).build();

        try (var csvReader = new CSVReaderBuilder(Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8))
                .withCSVParser(parser).build()) {
            List<String[]> data = csvReader.readAll();
            if (data.isEmpty()) {
                throw new AssertionError("Header not found"); //TODO
            }
            String[] headerColumns = data.get(0);
            for (int i = 0; i < headerColumns.length; i++) {
                header.put(headerColumns[i], i);
            }
            data.remove(headerColumns);
            return new CsvFile(csvFileInfo, data, header);
        }
    }

    public String getFileName() {
        return csvFileInfo.getName();
    }

    public String getCell(int rowNum, String colName) {
        if (!header.containsKey(colName)) {
            throw new AssertionError(String.format("File %s does not contain %s column", getFileName(), colName)); //TODO
        }
        return getRow(rowNum)[header.get(colName)];
    }

    public String[] getRow(int rowNum) {
        if (rowNum >= rows.size()) {
            throw new IndexOutOfBoundsException(
                    String.format("Row number %s is greater than size of %s file, which is %s",
                            rowNum,
                            getFileName(),
                            getRowCount()));
        }
        return rows.get(rowNum);
    }

    public int getRowCount() {
        return rows.size();
    }

    public boolean addRow(String[] row) {
        if (row.length != header.size()) {
            return false;
        }
        return rows.add(row);
    }

    public void writeFile(char delimiter) throws IOException {
        File file = new File(csvFileInfo.getFilePath());
        try (var br = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            var rowHeader = header.keySet().stream().collect(Collectors.joining(Character.toString(delimiter)));
            br.write(rowHeader);
            br.newLine();
            for (String[] row : rows) {
                var rowString = String.join(Character.toString(delimiter), row);
                br.write(rowString);
                br.newLine();
            }
        }
    }
}
