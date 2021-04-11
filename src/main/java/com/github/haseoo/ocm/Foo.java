package com.github.haseoo.ocm;

import com.github.haseoo.ocm.api.annotation.CsvColumn;
import com.github.haseoo.ocm.api.annotation.CsvEntity;
import com.github.haseoo.ocm.api.annotation.CsvFormatter;
import com.github.haseoo.ocm.api.annotation.CsvTransient;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@CsvEntity
@AllArgsConstructor
public class Foo {
    @CsvTransient
    private Integer first;
    private transient String second;
    private int third;
    private String testString;
    private BigDecimal testBigDecimal;
    private final int[] testArray = {1, 2, 3};
    @CsvFormatter("dd.MM.yyyy")
    private final LocalDate ld = LocalDate.now();
    private Double testDouble;
}
