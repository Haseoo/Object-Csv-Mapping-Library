package com.github.haseoo.ocm.structure;

import lombok.Value;

import java.util.List;

@Value
public class CsvEntity<T> {
    String name;
    Class<T> entityClass;
    List<CsvHeader> headers;
}
