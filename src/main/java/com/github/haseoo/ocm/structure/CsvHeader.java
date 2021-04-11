package com.github.haseoo.ocm.structure;

import com.github.haseoo.ocm.structure.enums.HeaderType;
import lombok.Value;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Value
public class CsvHeader {
    List<CsvColumn> headerColumns;

    public static CsvHeader getInstance(List<Field> fields) {
        final var columns = new ArrayList<CsvColumn>(fields.size());
        IterateThoughtFeels(fields,
                (field, type) -> columns.add(CsvColumn.getInstance(field, fields.indexOf(field), type)));
        return new CsvHeader(columns);
    }

    public static CsvHeader getInstance(List<Field> fields, String className) {
        final var columns = new ArrayList<CsvColumn>(fields.size());
        IterateThoughtFeels(fields,
                (field, type) -> columns.add(CsvColumn.getInstance(field, fields.indexOf(field),  type, className)));
        return new CsvHeader(columns);
    }

    private static void IterateThoughtFeels(List<Field> fields,
                                            BiConsumer<Field, HeaderType> valueHandler) {
        for(Field field : fields) {
            if(!Collection.class.isAssignableFrom(field.getType())) {
                valueHandler.accept(field, HeaderType.VALUE);
            } else {
                System.out.println("Collection");
            }
        }
    }
}
