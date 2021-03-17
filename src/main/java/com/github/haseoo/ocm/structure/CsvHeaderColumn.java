package com.github.haseoo.ocm.structure;

import com.github.haseoo.ocm.structure.enums.HeaderType;
import lombok.Value;

@Value
public class CsvHeaderColumn {
    String name;
    Class<?> javaClass;
    HeaderType headerType;
}
