package com.github.haseoo.ocm;

import com.github.haseoo.ocm.api.annotation.CsvColumn;
import com.github.haseoo.ocm.api.annotation.CsvEntity;
import com.github.haseoo.ocm.api.annotation.CsvFormatter;
import com.github.haseoo.ocm.api.annotation.CsvTransient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@CsvEntity(name = "TestName")

public class Foo extends BFoo {
    @CsvTransient
    private Integer first;
    private transient String second;
    private int third;
    private String testString;
    private BigDecimal testBigDecimal;
    private final int[] testArray = {1, 2, 3};
    @CsvFormatter("dd.MM.yyyy")
    @CsvColumn(name = "Custom_NAME")
    private final LocalDate ld = LocalDate.now();
    private Double testDouble;

    public Foo(Long letsgo, Integer first, String second, int third, String testString, BigDecimal testBigDecimal, Double testDouble) {
        super(letsgo);
        this.first = first;
        this.second = second;
        this.third = third;
        this.testString = testString;
        this.testBigDecimal = testBigDecimal;
        this.testDouble = testDouble;
    }
}
