package com.github.haseoo.ocm;

import com.github.haseoo.ocm.api.annotation.CsvColumn;
import com.github.haseoo.ocm.api.annotation.CsvEntity;
import com.github.haseoo.ocm.api.annotation.CsvFormatter;
import com.github.haseoo.ocm.api.annotation.CsvTransient;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class BFoo {
    private Long letsgo;
}
