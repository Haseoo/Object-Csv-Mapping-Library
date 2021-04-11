package com.github.haseoo.ocm.structure;

import com.github.haseoo.ocm.internal.MappingContext;
import com.github.haseoo.ocm.internal.utils.ReflectionUtils;
import com.github.haseoo.ocm.structure.cell.CsvData;
import com.github.haseoo.ocm.structure.cell.CsvDirectType;
import com.github.haseoo.ocm.structure.enums.HeaderType;
import lombok.SneakyThrows;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;


@Value
public class CsvRow implements CsvData {
    MappingContext mappingContext;
    CsvHeader header;
    List<CsvData> cells;

    @SneakyThrows
    public static CsvRow getInstance(MappingContext mappingContext,
                                            CsvHeader header,
                                            Object rowObject) {
        final var cells = new ArrayList<CsvData>();

        final var valueTypeColumns = header.getHeaderColumns().stream()
                .filter(c -> c.getHeaderType().equals(HeaderType.VALUE))
                .collect(toList());

        for (CsvColumn valueTypeColumn : valueTypeColumns) {
            var obj  = header.getRowType()
                    .getMethod(ReflectionUtils.getGetterName(valueTypeColumn.getFieldName()))
                    .invoke(rowObject);
            cells.add(CsvDirectType.getInstance(mappingContext, valueTypeColumn, obj, valueTypeColumn.getFormatter()));
        }
        return new CsvRow(mappingContext, header, cells);

    }
    public static CsvRow getInstance(MappingContext mappingContext,
                                     CsvHeader header,
                                     String[] values) {
        return null; //TODO
    }


    @Override
    public Object resolveToObject() {

        return null; //TODO
    }

    @Override
    public String resolveString() {
        return cells.stream().map(CsvData::resolveString).collect(joining(mappingContext.getSplitter()));
    }
}
