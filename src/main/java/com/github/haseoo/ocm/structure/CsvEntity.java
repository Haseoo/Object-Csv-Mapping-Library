package com.github.haseoo.ocm.structure;

import com.github.haseoo.ocm.internal.MappingContext;
import com.github.haseoo.ocm.internal.utils.ReflectionUtils;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        final String entityName = getEntityName(clazz).orElse(clazz.getSimpleName());
        CsvEntity<T> csvEntity = mappingContext.getRegisteredEntityOrNull(clazz);
        if (csvEntity == null) {
            final CsvHeader header;
            if (putClassName) {
                header = CsvHeader.getInstance(ReflectionUtils.getNonTransientFields(clazz.getDeclaredFields()),
                        clazz,
                        entityName);
            } else {
                header = CsvHeader.getInstance(ReflectionUtils.getNonTransientFields(clazz.getDeclaredFields()), clazz);
            }
            final var rows = new ArrayList<CsvRow>();
            for (T object : objects) {
                rows.add(CsvRow.getInstance(mappingContext, header, object));
            }
            csvEntity = new CsvEntity<>(mappingContext,
                    entityName,
                    clazz,
                    header,
                    rows,
                    CsvInheritance.getInstance(clazz));

            mappingContext.registerEntity(clazz, csvEntity);
        }
        return csvEntity;
    }

    private static Optional<String> getEntityName(Class<?> clazz) {
        final var entityAnnotation =clazz.getDeclaredAnnotation(com.github.haseoo.ocm.api.annotation.CsvEntity.class);
        if(entityAnnotation == null) {
            throw new AssertionError(); //TODO
        }
        return (entityAnnotation.name().isEmpty()) ? Optional.empty() : Optional.of(entityAnnotation.name());
    }
}
