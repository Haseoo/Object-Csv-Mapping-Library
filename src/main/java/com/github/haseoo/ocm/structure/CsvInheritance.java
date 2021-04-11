package com.github.haseoo.ocm.structure;

import lombok.Data;
import lombok.Value;

import java.util.List;

@Value
public class CsvInheritance<T> {
    CsvEntity<? super T> baseEntity;
    List<CsvEntity<? extends T>> derivedEntities;

    public static <T> CsvInheritance<T> getInstance(Class<T> clazz) {
        return new CsvInheritance<T>(null, null); //TODO
    }
}
