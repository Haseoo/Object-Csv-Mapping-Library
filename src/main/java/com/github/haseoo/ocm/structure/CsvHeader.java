package com.github.haseoo.ocm.structure;

import lombok.Value;

import java.util.List;

@Value
public class CsvHeader {
    List<CsvHeaderColumn> headerColumns;
}
