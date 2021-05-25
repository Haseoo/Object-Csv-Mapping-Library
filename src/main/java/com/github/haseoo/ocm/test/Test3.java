package com.github.haseoo.ocm.test;

import com.github.haseoo.ocm.api.CsvMapper;
import com.github.haseoo.ocm.api.exceptions.CsvMappingException;
import com.github.haseoo.ocm.test.data.SimpleDataClass;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Test3 {
    public static void main(String[] args) throws CsvMappingException, IOException {
        var data = new ArrayList<SimpleDataClass>();
        data.add(new SimpleDataClass(null, "test", 5, LocalDateTime.of(2021, 1, 1, 3, 5)));
        data.add(new SimpleDataClass(1L, null, 5, LocalDateTime.of(2019, 1, 1, 3, 5)));
        data.add(new SimpleDataClass(2137L, "test2", null, LocalDateTime.of(2021, 1, 1, 3, 5)));
        data.add(new SimpleDataClass(5L, "test2", 14, null));
        var mapper = new CsvMapper("", ";");
        var files = mapper.listToCsvInMemoryFile(data);
        System.out.println(files);
        var out = mapper.filesToList(SimpleDataClass.class, files);
        System.out.println(out.containsAll(data));
    }
}
