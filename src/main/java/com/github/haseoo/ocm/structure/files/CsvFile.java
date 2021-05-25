package com.github.haseoo.ocm.structure.files;

import com.github.haseoo.ocm.api.InMemoryCsvFile;
import com.github.haseoo.ocm.structure.entities.CsvEntityClass;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CsvFile {
    private final CsvFileInfo csvFileInfo;
    private final List<String[]> rows;
    private final Map<String, Integer> header;
    private final Map<String, Integer> headerWithRelations;

    public static CsvFile forObjects(CsvFileInfo csvFileInfo,
                                     Map<String, Integer> header,
                                     Map<String, Integer> headerWithRelations) {
        return new CsvFile(csvFileInfo, new ArrayList<>(), header, headerWithRelations);
    }

    public static CsvFile forFile(CsvFileInfo csvFileInfo, char delimiter) throws IOException {
        var headerWithRelations = new HashMap<String, Integer>();
        var file = new File(csvFileInfo.getFilePath());
        var parser = new CSVParserBuilder().withSeparator(delimiter).build();

        try (var csvReader = new CSVReaderBuilder(Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8))
                .withCSVParser(parser).build()) {
            List<String[]> data = csvReader.readAll();
            if (data.isEmpty()) {
                throw new IOException(String.format("File %s has no content", csvFileInfo.getName()));
            }
            HashMap<String, Integer> headerWithoutRelations = prepareHeader(headerWithRelations, data);
            return new CsvFile(csvFileInfo, data, headerWithoutRelations, headerWithRelations);
        }
    }

    public static CsvFile forInMemoryFile(InMemoryCsvFile inMemoryCsvFile, char delimiter) throws IOException {
        var headerWithRelations = new HashMap<String, Integer>();
        var parser = new CSVParserBuilder().withSeparator(delimiter).build();
        var payload = String.join("\n", inMemoryCsvFile.getData());
        try (var csvReader = new CSVReaderBuilder(new BufferedReader(new StringReader(payload)))
                .withCSVParser(parser).build()) {
            List<String[]> data = csvReader.readAll();
            if (data.isEmpty()) {
                throw new IOException(String.format("File %s has no content", inMemoryCsvFile.getFileName()));
            }
            HashMap<String, Integer> headerWithoutRelations = prepareHeader(headerWithRelations, data);
            return new CsvFile(inMemoryCsvFile, data, headerWithoutRelations, headerWithRelations);
        }
    }

    public String getFileName() {
        return csvFileInfo.getName();
    }

    public Map<String, String> getRow(int rowNum) {
        if (rowNum >= rows.size()) {
            throw new IndexOutOfBoundsException(
                    String.format("Row number %s is greater than size of %s file, which is %s",
                            rowNum,
                            getFileName(),
                            getRowCount()));
        }
        var rowArray = rows.get(rowNum);
        var rowMap = new HashMap<String, String>();
        for (Map.Entry<String, Integer> headerEntry : header.entrySet()) {
            var rowValue = rowArray[headerEntry.getValue()];
            rowMap.put(headerEntry.getKey(), rowValue);
        }
        return rowMap;
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

    public boolean addRow(Map<String, String> row) {
        var stringRow = new String[header.size()];
        for (Map.Entry<String, Integer> headerEntry : header.entrySet()) {
            var cellValue = row.get(headerEntry.getKey());
            stringRow[headerEntry.getValue()] = cellValue != null ? cellValue : "\"\"";
        }
        return addRow(stringRow);
    }

    public void writeFile(String delimiter) throws IOException {
        File file = new File(csvFileInfo.getFilePath());
        try (var br = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            var rowHeader = getHeaderRow(delimiter);
            br.write(rowHeader);
            br.newLine();
            for (String[] row : rows) {
                var rowString = String.join(delimiter, row);
                br.write(rowString);
                br.newLine();
            }
        }
    }

    public InMemoryCsvFile toInMemoryFile(String delimiter) {
        var data = new String[rows.size() + 1];
        data[0] = getHeaderRow(delimiter);
        int index = 1;
        for (String[] row : rows) {
            data[index++] = String.join(delimiter, row);
        }
        return new InMemoryCsvFile(csvFileInfo.getName(), data);
    }

    public boolean hasTypeHeader() {
        return header.containsKey(CsvEntityClass.TYPE_HEADER_NAME);
    }

    private String getHeaderRow(String delimiter) {
        return headerWithRelations.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.joining(delimiter));
    }


    private static String cutHeaderRelation(String header) {
        var splitHeader = header.split("@");
        return splitHeader[0];
    }

    private static HashMap<String, Integer> prepareHeader(HashMap<String, Integer> headerWithRelations, List<String[]> data) {
        String[] headerColumns = data.get(0);
        for (int i = 0; i < headerColumns.length; i++) {
            headerWithRelations.put(headerColumns[i], i);
        }
        data.remove(headerColumns);
        var headerWithoutRelations = new HashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : headerWithRelations.entrySet()) {
            headerWithoutRelations.put(cutHeaderRelation(entry.getKey()), entry.getValue());
        }
        return headerWithoutRelations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CsvFile csvFile = (CsvFile) o;

        return Objects.equals(csvFileInfo, csvFile.csvFileInfo);
    }

    @Override
    public int hashCode() {
        return csvFileInfo != null ? csvFileInfo.hashCode() : 0;
    }
}
