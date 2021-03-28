package com.github.haseoo.ocm;

import com.github.haseoo.ocm.api.CsvMapper;
import com.github.haseoo.ocm.api.annotation.CsvColumn;
import com.github.haseoo.ocm.api.annotation.CsvEntity;
import com.github.haseoo.ocm.api.annotation.CsvTransient;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

@Data
@CsvEntity
class Foo {
    @CsvTransient
    private Integer x;
    @CsvColumn(name = "yyyyy")
    transient private String y;
    private LocalDate z;
}

public class Test {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello world");
        CsvMapper csvMapper = new CsvMapper();
        Foo[] x = new Foo[3];
        csvMapper.arrayToCsv(x);
    }
}
