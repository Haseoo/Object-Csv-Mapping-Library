package com.github.haseoo.ocm.structure;

import com.github.haseoo.ocm.internal.MappingContext;
import com.github.haseoo.ocm.internal.utils.ReflectionUtils;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
public class CsvEntity<T> {
    String name;
    Class<T> entityClass;
    CsvHeader header;
    List<CsvRow> data;
    CsvInheritance<T> inheritance;

    public static <T> CsvEntity<T> newInstance(MappingContext mappingContext,
                                               Class<T> clazz,
                                               Object[] objects,
                                               boolean putClassName) {
        CsvEntity<T> csvEntity = mappingContext.getRegisteredEntityOrNull(clazz);
        if (csvEntity == null) {
            if (putClassName) {
                csvEntity = new CsvEntity<>(clazz.getSimpleName(),
                        clazz,
                        CsvHeader.getInstance(ReflectionUtils.getNonTransientFields(clazz.getDeclaredFields()),
                                clazz.getSimpleName()),
                        new ArrayList<>(),
                        CsvInheritance.getInstance(clazz));
            } else {
                csvEntity = new CsvEntity<>(clazz.getSimpleName(),
                        clazz,
                        CsvHeader.getInstance(ReflectionUtils.getNonTransientFields(clazz.getDeclaredFields())),
                        new ArrayList<>(),
                        CsvInheritance.getInstance(clazz));
            }
            mappingContext.registerEntity(clazz, csvEntity);
        }
        return csvEntity;
    }
}
