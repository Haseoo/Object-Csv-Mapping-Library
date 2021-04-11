package com.github.haseoo.ocm.structure;

import com.github.haseoo.ocm.internal.MappingContext;
import com.github.haseoo.ocm.internal.utils.ReflectionUtils;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

@Value
public class CsvEntity<T> {
    MappingContext mappingContext;
    String name;
    Class<T> entityClass;
    CsvHeader header;
    List<CsvRow> rows;
    CsvInheritance<T> inheritance;

    public List<String> resolveToString() {
        var returnRows = new ArrayList<String>();
        var headerRow = header.getHeaderColumns().stream().map(CsvColumn::getRowName).collect(joining(mappingContext.getSplitter()));
        returnRows.add(headerRow);
        returnRows.addAll(rows.stream().map(CsvRow::resolveString).collect(Collectors.toList()));
        return returnRows;
    }

    public static <T> CsvEntity<T> newInstance(MappingContext mappingContext,
                                               Class<T> clazz,
                                               T[] objects,
                                               boolean putClassName) {
        if (!ReflectionUtils.isClassAnnotated(clazz, com.github.haseoo.ocm.api.annotation.CsvEntity.class)) {
            throw new AssertionError(); //TODO
        }
        CsvEntity<T> csvEntity = mappingContext.getRegisteredEntityOrNull(clazz);
        if (csvEntity == null) {
            final CsvHeader header;
            if (putClassName) {
                header = CsvHeader.getInstance(ReflectionUtils.getNonTransientFields(clazz.getDeclaredFields()),
                        clazz,
                        clazz.getSimpleName());
            } else {
                header = CsvHeader.getInstance(ReflectionUtils.getNonTransientFields(clazz.getDeclaredFields()), clazz);
            }
            final var rows = new ArrayList<CsvRow>();
            for (T object : objects) {
                rows.add(CsvRow.getInstance(mappingContext, header, object));
            }
            csvEntity = new CsvEntity<>(mappingContext,
                    clazz.getSimpleName(),
                    clazz,
                    header,
                    rows,
                    CsvInheritance.getInstance(clazz));

            mappingContext.registerEntity(clazz, csvEntity);
        }
        return csvEntity;
    }
}
