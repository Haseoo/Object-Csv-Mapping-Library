package com.github.haseoo.ocm.test;

import com.github.haseoo.ocm.api.CsvMapper;
import com.github.haseoo.ocm.api.exceptions.CsvMappingException;
import com.github.haseoo.ocm.test.data.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;

public class Test2 {
    public static void main(String[] args) throws CsvMappingException, IOException {
        var mapper = new CsvMapper("C:\\Users\\dawid\\Desktop\\test", ";");
        mapper.registerConverter(Chip.class, new ChipConverter());
        var out = mapper.filesToList(Worker.class);
        var mapper2 = new CsvMapper("C:\\Users\\dawid\\Desktop\\test2", ";");
        mapper2.registerConverter(Chip.class, new ChipConverter());
        mapper2.listToFiles(out);
    }
}
