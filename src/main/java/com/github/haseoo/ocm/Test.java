package com.github.haseoo.ocm;

import com.github.haseoo.ocm.api.annotation.CsvColumn;
import com.github.haseoo.ocm.api.annotation.CsvEntity;
import com.github.haseoo.ocm.api.annotation.CsvTransient;
import lombok.Data;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Set;

@Data
@CsvEntity
class Foo {
    @CsvTransient
    private Integer x;
    @CsvColumn(name = "yyyyy")
    private transient String y;
    private LocalDate z;
    private Set<Integer> test;
}

public class Test {
    public static void main(String[] args) throws IOException {
    }
}
