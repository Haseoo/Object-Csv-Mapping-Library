package com.github.haseoo.ocm.structure;

import com.github.haseoo.ocm.internal.MappingContext;
import com.github.haseoo.ocm.structure.cell.CsvData;
import com.github.haseoo.ocm.structure.enums.HeaderType;
import lombok.Value;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Value
public class CsvRow<T> implements CsvData {
    CsvHeader header;
    MappingContext mappingContext;
    Class<T> clazz;
    T rowObject;
    String[] rowStringValues;

    public static <T> CsvRow<T> getInstance(MappingContext mappingContext,
                                     CsvHeader header,
                                     Class<T> clazz,
                                     T rowObject) {
        return new CsvRow<>(header, mappingContext, clazz, rowObject, null);
    }
    public static  <T> CsvRow<T> getInstance(MappingContext mappingContext,
                                     CsvHeader header,
                                     Class<T> clazz,
                                     String[] values) {
        return new CsvRow<>(header, mappingContext, clazz, null, values);
    }


    @Override
    public Object resolveToObject() {
        if (rowStringValues == null) {
            return rowObject;
        }
        return null; //TODO
    }

    @Override
    public String resolveString() {
        if(rowObject == null) {
            return String.join(mappingContext.getSplitter(), rowStringValues);
        }
        return null; //TODO
    }
}
