package com.github.haseoo.ocm.structure;

import com.github.haseoo.ocm.api.exceptions.CsvMappingException;
import com.github.haseoo.ocm.api.exceptions.IdFiledNotFound;
import com.github.haseoo.ocm.structure.entities.CsvEntityClass;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CsvStringObject {
    Object object;
    CsvEntityClass entityClass;
    Map<String, String> csvFields;

    public static CsvStringObject getInstance(CsvEntityClass entityClass,
                                              Map<String, String> csvFields)
            throws CsvMappingException {
        Object object = null;
        try {
            object = entityClass.getType().getConstructor().newInstance();
        } catch (InstantiationException |
                IllegalAccessException |
                InvocationTargetException |
                NoSuchMethodException e) {
            throw new CsvMappingException(String.format("Could not instantiate entity object of class %s.",
                    entityClass.getType()),
                    e);
        }
        return new CsvStringObject(object, entityClass, csvFields);
    }
}