package com.github.haseoo.ocm.structure;

import com.github.haseoo.ocm.structure.enums.HeaderType;
import lombok.Value;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Value
public class CsvHeader {
    List<CsvColumn> headerColumns;
    Class<?> rowType;

    public Map<String, Integer> getColumnMap() {
        return headerColumns.stream().collect(Collectors.toMap(CsvColumn::getColName, CsvColumn::getIndex, (e, r) -> e));
    }

    public static CsvHeader getInstance(List<Field> fields, Class<?> rowType) {
        final var columns = new ArrayList<CsvColumn>(fields.size());
        IterateThoughtFeels(fields,
                (field, type) -> columns.add(CsvColumn.getInstance(field, fields.indexOf(field), type)));
        return new CsvHeader(columns, rowType);
    }

    public static <T> CsvHeader getInstance(List<Field> fields, Class<T> rowType, String className) {
        final var columns = new ArrayList<CsvColumn>(fields.size());
        IterateThoughtFeels(fields,
                (field, type) -> columns.add(CsvColumn.getInstance(field, fields.indexOf(field), type, className)));
        return new CsvHeader(columns, rowType);
    }

    private static void IterateThoughtFeels(List<Field> fields,
                                            BiConsumer<Field, HeaderType> valueHandler) {
        for (Field field : fields) {
            if (!Collection.class.isAssignableFrom(field.getType())) {
                valueHandler.accept(field, HeaderType.VALUE);
            } else {
                System.out.println("Collection");
            }
        }
    }
}
